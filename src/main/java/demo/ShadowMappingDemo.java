package demo;
/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import dev.overtow.service.renderer.Renderer;
import dev.overtow.util.injection.Injector;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public class ShadowMappingDemo {
    private static final Vector3f[] boxes = {
            new Vector3f(-5.0f, -0.1f, -5.0f), new Vector3f(5.0f, 0.0f, 5.0f),
            new Vector3f(-0.5f, 0.0f, -0.5f), new Vector3f(0.5f, 1.0f, 0.5f),
            new Vector3f(-2.5f, 0.0f, -1.5f), new Vector3f(-1.5f, 1.0f, -0.5f),
            new Vector3f(-2.5f, 0.0f, 1.5f), new Vector3f(-1.5f, 1.0f, 2.5f),
            new Vector3f(1.5f, 0.0f, 1.5f), new Vector3f(2.5f, 1.0f, 2.5f),
            new Vector3f(1.5f, 0.0f, -2.5f), new Vector3f(2.5f, 1.0f, -1.5f)
    };

    private static final Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);

    static int shadowMapSize = 1024;
    static Vector3f lightPosition = new Vector3f(6.0f, 3.0f, 6.0f);
    static Vector3f lightLookAt = new Vector3f(0.0f, 1.0f, 0.0f);
    static Vector3f cameraPosition = new Vector3f(-3.0f, 6.0f, 6.0f);
    static Vector3f cameraLookAt = new Vector3f(0.0f, 0.0f, 0.0f);
    static float lightDistance = 10.0f;
    static float lightHeight = 4.0f;

    private static final boolean DEBUG_MODE = false;
    private static final Renderer renderer = Injector.getInstance(Renderer.class, DEBUG_MODE ? Renderer.DEBUG : "");

    long window;
    int width = 1200;
    int height = 800;

    int vao;
    int vbo;
    int shadowProgram;
    int shadowProgramVPUniform;
    int normalProgram;
    int normalProgramBiasUniform;
    int normalProgramVPUniform;
    int normalProgramLVPUniform;
    int normalProgramLightPosition;
    int normalProgramLightLookAt;
    int fbo;
    int depthTexture;
    int samplerLocation;

    FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    Matrix4f light = new Matrix4f();
    Matrix4f camera = new Matrix4f();
    Matrix4f biasMatrix = new Matrix4f(
            0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f
    );

    GLCapabilities caps;
    GLFWErrorCallback errCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback fbCallback;
    Callback debugProc;

    void init() throws IOException {
        renderer.glfwSetErrorCallback(errCallback = new GLFWErrorCallback() {
            GLFWErrorCallback delegate = GLFWErrorCallback.createPrint(System.err);

            public void invoke(int error, long description) {
                if (error == GLFW_VERSION_UNAVAILABLE)
                    System.err.println("This demo requires OpenGL 3.0 or higher.");
                delegate.invoke(error, description);
            }

            @Override
            public void free() {
                delegate.free();
            }
        });

        if (!renderer.glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        renderer.glfwDefaultWindowHints();
        renderer.glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        renderer.glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        renderer.glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        renderer.glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = renderer.glfwCreateWindow(width, height, "Shadow Mapping Demo", NULL, NULL);
        if (window == NULL) {
            throw new AssertionError("Failed to create the GLFW window");
        }

        renderer.glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action != GLFW_RELEASE)
                    return;

                if (key == GLFW_KEY_ESCAPE) {
                    renderer.glfwSetWindowShouldClose(window, true);
                }
            }
        });
        renderer.glfwSetFramebufferSizeCallback(window, fbCallback = new GLFWFramebufferSizeCallback() {
            public void invoke(long window, int width, int height) {
                if (width > 0 && height > 0 && (ShadowMappingDemo.this.width != width || ShadowMappingDemo.this.height != height)) {
                    ShadowMappingDemo.this.width = width;
                    ShadowMappingDemo.this.height = height;
                }
            }
        });

        GLFWVidMode vidmode = renderer.glfwGetVideoMode(renderer.glfwGetPrimaryMonitor());
        renderer.glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        renderer.glfwMakeContextCurrent(window);
        renderer.glfwSwapInterval(0);
        renderer.glfwShowWindow(window);

        try (MemoryStack frame = MemoryStack.stackPush()) {
            IntBuffer framebufferSize = frame.mallocInt(2);
            renderer.nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
            width = framebufferSize.get(0);
            height = framebufferSize.get(1);
        }

        caps = GL.createCapabilities();
        debugProc = GLUtil.setupDebugMessageCallback();

        /* Set some GL states */
        renderer.glEnable(GL_CULL_FACE);
        renderer.glEnable(GL_DEPTH_TEST);
        renderer.glClearColor(0.2f, 0.3f, 0.4f, 1.0f);

        /* Create all needed GL resources */
        createVao();
        createShadowProgram();
        initShadowProgram();
        createNormalProgram();
        initNormalProgram();
        createDepthTexture();
        createFbo();
    }

    /**
     * Create the texture storing the depth values of the light-render.
     */
    void createDepthTexture() {
        depthTexture = renderer.glGenTextures();
        renderer.glBindTexture(GL_TEXTURE_2D, depthTexture);
        renderer.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        renderer.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        renderer.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        renderer.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        renderer.glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, shadowMapSize, shadowMapSize, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE,
                (ByteBuffer) null);
        renderer.glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Create the FBO to render the depth values of the light-render into the
     * depth texture.
     */
    void createFbo() {
        fbo = renderer.glGenFramebuffers();
        renderer.glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        renderer.glBindTexture(GL_TEXTURE_2D, depthTexture);
        renderer.glDrawBuffer(GL_NONE);
        renderer.glReadBuffer(GL_NONE);
        renderer.glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
        int fboStatus = renderer.glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw new AssertionError("Could not create FBO: " + fboStatus);
        }
        renderer.glBindTexture(GL_TEXTURE_2D, 0);
        renderer.glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Creates a VAO for the scene with some boxes.
     */
    void createVao() {
        vao = renderer.glGenVertexArrays();
        int vbo = renderer.glGenBuffers();
        renderer.glBindVertexArray(vao);
        renderer.glBindBuffer(GL_ARRAY_BUFFER, vbo);
        ByteBuffer bb = BufferUtils.createByteBuffer(boxes.length * 4 * (3 + 3) * 6 * 6);
        FloatBuffer fv = bb.asFloatBuffer();
        for (int i = 0; i < boxes.length; i += 2) {
            boxToVertices(boxes[i], boxes[i + 1], fv);
        }
        renderer.glBufferData(GL_ARRAY_BUFFER, bb, GL_STATIC_DRAW);
        renderer.glEnableVertexAttribArray(0);
        renderer.glVertexAttribPointer(0, 3, GL_FLOAT, false, 4 * (3 + 3), 0L);
        renderer.glEnableVertexAttribArray(1);
        renderer.glVertexAttribPointer(1, 3, GL_FLOAT, false, 4 * (3 + 3), 4 * 3);
        renderer.glBindBuffer(GL_ARRAY_BUFFER, 0);
        renderer.glBindVertexArray(0);
    }

    static int createShader(String resource, int type) throws IOException {
        int shader = renderer.glCreateShader(type);

        ByteBuffer source = ioResourceToByteBuffer(resource, 8192);

        PointerBuffer strings = BufferUtils.createPointerBuffer(1);
        IntBuffer lengths = BufferUtils.createIntBuffer(1);

        strings.put(0, source);
        lengths.put(0, source.remaining());

        renderer.glShaderSource(shader, strings, lengths);
        renderer.glCompileShader(shader);
        int compiled = renderer.glGetShaderi(shader, GL_COMPILE_STATUS);
        String shaderLog = renderer.glGetShaderInfoLog(shader);
        if (shaderLog.trim().length() > 0) {
            System.err.println(shaderLog);
        }
        if (compiled == 0) {
            throw new AssertionError("Could not compile shader");
        }
        return shader;
    }

    void createShadowProgram() throws IOException {
        shadowProgram = renderer.glCreateProgram();
        int vshader = createShader("data/shader/shadowMapping-vs.glsl", GL_VERTEX_SHADER);
        int fshader = createShader("data/shader/shadowMapping-fs.glsl", GL_FRAGMENT_SHADER);
        renderer.glAttachShader(shadowProgram, vshader);
        renderer.glAttachShader(shadowProgram, fshader);
        renderer.glBindAttribLocation(shadowProgram, 0, "position");
        renderer.glLinkProgram(shadowProgram);
        int linked = renderer.glGetProgrami(shadowProgram, GL_LINK_STATUS);
        String programLog = renderer.glGetProgramInfoLog(shadowProgram);
        if (programLog.trim().length() > 0) {
            System.err.println(programLog);
        }
        if (linked == 0) {
            throw new AssertionError("Could not link program");
        }
    }

    void initShadowProgram() {
        renderer.glUseProgram(shadowProgram);
        shadowProgramVPUniform = renderer.glGetUniformLocation(shadowProgram, "viewProjectionMatrix");
        renderer.glUseProgram(0);
    }

    void createNormalProgram() throws IOException {
        normalProgram = renderer.glCreateProgram();
        int vshader = createShader("data/shader/shadowMappingShade-vs.glsl", GL_VERTEX_SHADER);
        int fshader = createShader("data/shader/shadowMappingShade-fs.glsl", GL_FRAGMENT_SHADER);
        renderer.glAttachShader(normalProgram, vshader);
        renderer.glAttachShader(normalProgram, fshader);
        renderer.glBindAttribLocation(normalProgram, 0, "position");
        renderer.glBindAttribLocation(normalProgram, 1, "normal");
        renderer.glLinkProgram(normalProgram);
        int linked = renderer.glGetProgrami(normalProgram, GL_LINK_STATUS);
        String programLog = renderer.glGetProgramInfoLog(normalProgram);
        if (programLog.trim().length() > 0) {
            System.err.println(programLog);
        }
        if (linked == 0) {
            throw new AssertionError("Could not link program");
        }
    }

    void initNormalProgram() {
        renderer.glUseProgram(normalProgram);
        samplerLocation = renderer.glGetUniformLocation(normalProgram, "depthTexture");
        normalProgramBiasUniform = renderer.glGetUniformLocation(normalProgram, "biasMatrix");
        normalProgramVPUniform = renderer.glGetUniformLocation(normalProgram, "viewProjectionMatrix");
        normalProgramLVPUniform = renderer.glGetUniformLocation(normalProgram, "lightViewProjectionMatrix");
        normalProgramLightPosition = renderer.glGetUniformLocation(normalProgram, "lightPosition");
        normalProgramLightLookAt = renderer.glGetUniformLocation(normalProgram, "lightLookAt");
        renderer.glUniform1i(samplerLocation, 0);
        renderer.glUseProgram(0);
    }

    /**
     * Update the camera MVP matrix.
     */
    void update() {
        /* Update light */
        double alpha = System.currentTimeMillis() / 1000.0 * 0.5;
        float x = (float) Math.sin(alpha);
        float z = (float) Math.cos(alpha);
        lightPosition.set(lightDistance * x, 3 + (float) Math.sin(alpha), lightDistance * z);
        light.setPerspective((float) Math.toRadians(45.0f), 1.0f, 0.1f, 60.0f)
                .lookAt(lightPosition, lightLookAt, UP);

        /* Update camera */
        camera.setPerspective((float) Math.toRadians(45.0f), (float) width / height, 0.1f, 30.0f)
                .lookAt(cameraPosition, cameraLookAt, UP);
    }

    /**
     * Render the shadow map into a depth texture.
     */
    void renderShadowMap() {
        renderer.glUseProgram(shadowProgram);

        /* Set MVP matrix of the "light camera" */
        renderer.glUniformMatrix4fv(shadowProgramVPUniform, false, light.get(matrixBuffer));

        renderer.glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        renderer.glViewport(0, 0, shadowMapSize, shadowMapSize);
        /* Only clear depth buffer, since we don't have a color draw buffer */
        renderer.glClear(GL_DEPTH_BUFFER_BIT);
        renderer.glBindVertexArray(vao);
        renderer.glDrawArrays(GL_TRIANGLES, 0, 6 * 6 * boxes.length);
        renderer.glBindVertexArray(0);
        renderer.glBindFramebuffer(GL_FRAMEBUFFER, 0);

        renderer.glUseProgram(0);
    }

    /**
     * Render the scene normally, with sampling the previously rendered depth
     * texture.
     */
    void renderNormal() {
        renderer.glUseProgram(normalProgram);

        /* Set MVP matrix of camera */
        renderer.glUniformMatrix4fv(normalProgramVPUniform, false, camera.get(matrixBuffer));
        /* Set MVP matrix that was used when doing the light-render */
        renderer.glUniformMatrix4fv(normalProgramLVPUniform, false, light.get(matrixBuffer));
        /* The bias-matrix used to convert to NDC coordinates */
        renderer.glUniformMatrix4fv(normalProgramBiasUniform, false, biasMatrix.get(matrixBuffer));
        /* Light position and lookat for normal lambertian computation */
        renderer.glUniform3f(normalProgramLightPosition, lightPosition.x, lightPosition.y, lightPosition.z);
        renderer.glUniform3f(normalProgramLightLookAt, lightLookAt.x, lightLookAt.y, lightLookAt.z);

        renderer.glViewport(0, 0, width, height);
        /* Must clear both color and depth, since we are re-rendering the scene */
        renderer.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        renderer.glBindTexture(GL_TEXTURE_2D, depthTexture);
        renderer.glBindVertexArray(vao);
        renderer.glDrawArrays(GL_TRIANGLES, 0, 6 * 6 * boxes.length);
        renderer.glBindVertexArray(0);
        renderer.glBindTexture(GL_TEXTURE_2D, 0);

        renderer.glUseProgram(0);
    }

    int counter = 0;

    void loop() {
        while (!renderer.glfwWindowShouldClose(window) && counter < 10) {
            renderer.glfwPollEvents();

            update();
            renderShadowMap();
            renderNormal();

            renderer.glfwSwapBuffers(window);
            if (DEBUG_MODE) {
                counter++;
            }
        }
    }

    void run() {
        try {
            init();
            loop();

            if (debugProc != null)
                debugProc.free();

            errCallback.free();
            keyCallback.free();
            fbCallback.free();
            renderer.glfwDestroyWindow(window);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            renderer.glfwTerminate();
        }
    }

    public static void main(String[] args) {
        new ShadowMappingDemo().run();
    }


    public static ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;
        try (InputStream source = new FileInputStream(resource)) {
            byte[] src = source.readAllBytes();
            buffer = BufferUtils.createByteBuffer(src.length + 1);
            buffer.put(src);
            buffer.flip();
        }
        return buffer;
    }

    public static void boxToVertices(Vector3f min, Vector3f max, FloatBuffer fv) {
        /* Front face */
        fv.put(min.x).put(min.y).put(max.z).put(0.0f).put(0.0f).put(1.0f);
        fv.put(max.x).put(min.y).put(max.z).put(0.0f).put(0.0f).put(1.0f);
        fv.put(max.x).put(max.y).put(max.z).put(0.0f).put(0.0f).put(1.0f);
        fv.put(max.x).put(max.y).put(max.z).put(0.0f).put(0.0f).put(1.0f);
        fv.put(min.x).put(max.y).put(max.z).put(0.0f).put(0.0f).put(1.0f);
        fv.put(min.x).put(min.y).put(max.z).put(0.0f).put(0.0f).put(1.0f);
        /* Back face */
        fv.put(max.x).put(min.y).put(min.z).put(0.0f).put(0.0f).put(-1.0f);
        fv.put(min.x).put(min.y).put(min.z).put(0.0f).put(0.0f).put(-1.0f);
        fv.put(min.x).put(max.y).put(min.z).put(0.0f).put(0.0f).put(-1.0f);
        fv.put(min.x).put(max.y).put(min.z).put(0.0f).put(0.0f).put(-1.0f);
        fv.put(max.x).put(max.y).put(min.z).put(0.0f).put(0.0f).put(-1.0f);
        fv.put(max.x).put(min.y).put(min.z).put(0.0f).put(0.0f).put(-1.0f);
        /* Left face */
        fv.put(min.x).put(min.y).put(min.z).put(-1.0f).put(0.0f).put(0.0f);
        fv.put(min.x).put(min.y).put(max.z).put(-1.0f).put(0.0f).put(0.0f);
        fv.put(min.x).put(max.y).put(max.z).put(-1.0f).put(0.0f).put(0.0f);
        fv.put(min.x).put(max.y).put(max.z).put(-1.0f).put(0.0f).put(0.0f);
        fv.put(min.x).put(max.y).put(min.z).put(-1.0f).put(0.0f).put(0.0f);
        fv.put(min.x).put(min.y).put(min.z).put(-1.0f).put(0.0f).put(0.0f);
        /* Right face */
        fv.put(max.x).put(min.y).put(max.z).put(1.0f).put(0.0f).put(0.0f);
        fv.put(max.x).put(min.y).put(min.z).put(1.0f).put(0.0f).put(0.0f);
        fv.put(max.x).put(max.y).put(min.z).put(1.0f).put(0.0f).put(0.0f);
        fv.put(max.x).put(max.y).put(min.z).put(1.0f).put(0.0f).put(0.0f);
        fv.put(max.x).put(max.y).put(max.z).put(1.0f).put(0.0f).put(0.0f);
        fv.put(max.x).put(min.y).put(max.z).put(1.0f).put(0.0f).put(0.0f);
        /* Top face */
        fv.put(min.x).put(max.y).put(max.z).put(0.0f).put(1.0f).put(0.0f);
        fv.put(max.x).put(max.y).put(max.z).put(0.0f).put(1.0f).put(0.0f);
        fv.put(max.x).put(max.y).put(min.z).put(0.0f).put(1.0f).put(0.0f);
        fv.put(max.x).put(max.y).put(min.z).put(0.0f).put(1.0f).put(0.0f);
        fv.put(min.x).put(max.y).put(min.z).put(0.0f).put(1.0f).put(0.0f);
        fv.put(min.x).put(max.y).put(max.z).put(0.0f).put(1.0f).put(0.0f);
        /* Bottom face */
        fv.put(min.x).put(min.y).put(min.z).put(0.0f).put(-1.0f).put(0.0f);
        fv.put(max.x).put(min.y).put(min.z).put(0.0f).put(-1.0f).put(0.0f);
        fv.put(max.x).put(min.y).put(max.z).put(0.0f).put(-1.0f).put(0.0f);
        fv.put(max.x).put(min.y).put(max.z).put(0.0f).put(-1.0f).put(0.0f);
        fv.put(min.x).put(min.y).put(max.z).put(0.0f).put(-1.0f).put(0.0f);
        fv.put(min.x).put(min.y).put(min.z).put(0.0f).put(-1.0f).put(0.0f);
    }
}