package demo;
/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */

import dev.overtow.glsl.Converter;
import dev.overtow.glsl.shader.SceneFragmentShader;
import dev.overtow.glsl.shader.SceneVertexShader;
import dev.overtow.glsl.shader.ShadowFragmentShader;
import dev.overtow.glsl.shader.ShadowVertexShader;
import dev.overtow.service.meshloader.MeshLoader;
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
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.MemoryStack;
import org.lwjglb.engine.graph.Mesh;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL30C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL30C.GL_CULL_FACE;
import static org.lwjgl.opengl.GL30C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL30C.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL30C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL30C.GL_LINEAR;
import static org.lwjgl.opengl.GL30C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL30C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL30C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL30C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL30C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL30C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL30C.glBindTexture;
import static org.lwjgl.opengl.GL30C.glClear;
import static org.lwjgl.opengl.GL30C.glClearColor;
import static org.lwjgl.opengl.GL30C.glEnable;
import static org.lwjgl.opengl.GL30C.glGenTextures;
import static org.lwjgl.opengl.GL30C.glGetInteger;
import static org.lwjgl.opengl.GL30C.glTexImage2D;
import static org.lwjgl.opengl.GL30C.glTexParameteri;
import static org.lwjgl.opengl.GL30C.glViewport;
import static org.lwjgl.opengl.GL30C.*;
import static org.lwjgl.opengl.GL43C.*;
import static org.lwjgl.system.APIUtil.apiLog;
import static org.lwjgl.system.APIUtil.apiUnknownToken;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public class ShadowMappingDemo {

    private static final Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);
    static int shadowMapSize = 1024;
    static float lightHeight = 15.0f;
    static Vector3f lightPosition = new Vector3f(6.0f, lightHeight, 6.0f);
    static Vector3f lightLookAt = new Vector3f(0.5f, 0.0f, 0.5f);
    static Vector3f cameraPosition = new Vector3f(0.5f, 17.0f, 0.5f);
    static Vector3f cameraLookAt = new Vector3f(0.0f, 0.0f, 0.0f);
    static float lightDistance = 25.0f;

    long window;
    int width = 1600;
    int height = 900;

    int vao;
    int vao2;
    int shadowProgram;
    int shadowProgramVPUniform;
    int normalProgram;
    int normalProgramBiasUniform;
    int normalModelMatrixUniform;
    int shadowModelMatrixUniform;
    int normalProgramVPUniform;
    int normalProgramLVPUniform;
    int normalProgramLightPosition;
    int normalProgramLightLookAt;
    int fbo;
    int depthTexture;
    int depthSamplerLocation;
    int textureSamplerLocation;

    FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    Matrix4f light = new Matrix4f();
    Matrix4f camera = new Matrix4f();
    Matrix4f biasMatrix = new Matrix4f(
            0.5f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.5f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.5f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f
    );

    GLCapabilities capabilities;
    GLFWErrorCallback errCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback fbCallback;
    GLDebugMessageCallback debugProc;
    private Mesh mesh;
    private Mesh mesh2;

    void init() throws IOException {
        glfwSetErrorCallback(errCallback = new GLFWErrorCallback() {
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

        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(width, height, "Shadow Mapping Demo", NULL, NULL);
        if (window == NULL) {
            throw new AssertionError("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action != GLFW_RELEASE)
                    return;

                if (key == GLFW_KEY_ESCAPE) {
                    glfwSetWindowShouldClose(window, true);
                }
            }
        });
        glfwSetFramebufferSizeCallback(window, fbCallback = new GLFWFramebufferSizeCallback() {
            public void invoke(long window, int width, int height) {
                if (width > 0 && height > 0 && (ShadowMappingDemo.this.width != width || ShadowMappingDemo.this.height != height)) {
                    ShadowMappingDemo.this.width = width;
                    ShadowMappingDemo.this.height = height;
                }
            }
        });

        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        glfwMakeContextCurrent(window);
        glfwSwapInterval(0);
        glfwShowWindow(window);

        try (MemoryStack frame = MemoryStack.stackPush()) {
            IntBuffer framebufferSize = frame.mallocInt(2);
            nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
            width = framebufferSize.get(0);
            height = framebufferSize.get(1);
        }

        capabilities = GL.createCapabilities();

        if (capabilities.OpenGL43) {
            apiLog("[GL] Using OpenGL 4.3 for error logging.");
            debugProc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
                if (severity == GL_DEBUG_SEVERITY_NOTIFICATION) {
                    return;
                }
                System.out.println("[LWJGL] OpenGL debug message");
                printDetail(System.out, "ID", String.format("0x%X", id));
                printDetail(System.out, "Source", getDebugSource(source));
                printDetail(System.out, "Type", getDebugType(type));
                printDetail(System.out, "Severity", getDebugSeverity(severity));
                printDetail(System.out, "Message", GLDebugMessageCallback.getMessage(length, message));
            });
            glDebugMessageCallback(debugProc, NULL);
            if ((glGetInteger(GL_CONTEXT_FLAGS) & GL_CONTEXT_FLAG_DEBUG_BIT) == 0) {
                apiLog("[GL] Warning: A non-debug context may not produce any debug output.");
                glEnable(GL_DEBUG_OUTPUT);
            }
        } else {
            throw new RuntimeException("opengl 4.3!");
        }


        /* Set some GL states */
        glEnable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.2f, 0.3f, 0.4f, 1.0f);

        /* Create all needed GL resources */
        createVao();
        createShadowProgram();
        initShadowProgram();
        createNormalProgram();
        initNormalProgram();
        createDepthTexture();
        createFbo();
    }

    private static void printDetail(PrintStream stream, String type, String message) {
        stream.printf("\t%s: %s\n", type, message);
    }

    private static String getDebugSource(int source) {
        return switch (source) {
            case GL_DEBUG_SOURCE_API -> "API";
            case GL_DEBUG_SOURCE_WINDOW_SYSTEM -> "WINDOW SYSTEM";
            case GL_DEBUG_SOURCE_SHADER_COMPILER -> "SHADER COMPILER";
            case GL_DEBUG_SOURCE_THIRD_PARTY -> "THIRD PARTY";
            case GL_DEBUG_SOURCE_APPLICATION -> "APPLICATION";
            case GL_DEBUG_SOURCE_OTHER -> "OTHER";
            default -> apiUnknownToken(source);
        };
    }

    private static String getDebugType(int type) {
        return switch (type) {
            case GL_DEBUG_TYPE_ERROR -> "ERROR";
            case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> "DEPRECATED BEHAVIOR";
            case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> "UNDEFINED BEHAVIOR";
            case GL_DEBUG_TYPE_PORTABILITY -> "PORTABILITY";
            case GL_DEBUG_TYPE_PERFORMANCE -> "PERFORMANCE";
            case GL_DEBUG_TYPE_OTHER -> "OTHER";
            case GL_DEBUG_TYPE_MARKER -> "MARKER";
            default -> apiUnknownToken(type);
        };
    }

    private static String getDebugSeverity(int severity) {
        return switch (severity) {
            case GL_DEBUG_SEVERITY_HIGH -> "HIGH";
            case GL_DEBUG_SEVERITY_MEDIUM -> "MEDIUM";
            case GL_DEBUG_SEVERITY_LOW -> "LOW";
            case GL_DEBUG_SEVERITY_NOTIFICATION -> "NOTIFICATION";
            default -> apiUnknownToken(severity);
        };
    }

    /**
     * Create the texture storing the depth values of the light-render.
     */
    void createDepthTexture() {
        depthTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, shadowMapSize, shadowMapSize, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE,
                (ByteBuffer) null);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Create the FBO to render the depth values of the light-render into the
     * depth texture.
     */
    void createFbo() {
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
        int fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw new AssertionError("Could not create FBO: " + fboStatus);
        }
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    /**
     * Creates a VAO for the scene with some boxes.
     */
    void createVao() {
        MeshLoader meshLoader = Injector.getInstance(MeshLoader.class);
        try {
            mesh = meshLoader.load("data/model/cube2/c10.obj");
            mesh2 = meshLoader.load("data/model/pool/pool_final_3.obj");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        vao = mesh.getVaoId();
        vao2 = mesh2.getVaoId();
    }

    static int createShader(String resource, int type) throws IOException {
        int shader = glCreateShader(type);

        ByteBuffer source = ioResourceToByteBuffer(resource);

        PointerBuffer strings = BufferUtils.createPointerBuffer(1);
        IntBuffer lengths = BufferUtils.createIntBuffer(1);

        strings.put(0, source);
        lengths.put(0, source.remaining());

        glShaderSource(shader, strings, lengths);
        glCompileShader(shader);
        int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
        String shaderLog = glGetShaderInfoLog(shader);
        if (shaderLog.trim().length() > 0) {
            System.err.println(shaderLog);
        }
        if (compiled == 0) {
            throw new AssertionError("Could not compile shader");
        }
        return shader;
    }

    void createShadowProgram() throws IOException {
        shadowProgram = glCreateProgram();
        int vshader = createShader("data/shader/target/ShadowVertexShader.glsl", GL_VERTEX_SHADER);
        int fshader = createShader("data/shader/target/ShadowFragmentShader.glsl", GL_FRAGMENT_SHADER);
        glAttachShader(shadowProgram, vshader);
        glAttachShader(shadowProgram, fshader);
        glBindAttribLocation(shadowProgram, 0, "position");
        glLinkProgram(shadowProgram);
        int linked = glGetProgrami(shadowProgram, GL_LINK_STATUS);
        String programLog = glGetProgramInfoLog(shadowProgram);
        if (programLog.trim().length() > 0) {
            System.err.println(programLog);
        }
        if (linked == 0) {
            throw new AssertionError("Could not link program");
        }
    }

    void initShadowProgram() {
        glUseProgram(shadowProgram);
        shadowProgramVPUniform = glGetUniformLocation(shadowProgram, "viewProjectionMatrix");
        shadowModelMatrixUniform = glGetUniformLocation(shadowProgram, "modelMatrix");
        glUseProgram(0);
    }

    void createNormalProgram() throws IOException {
        normalProgram = glCreateProgram();
        int vshader = createShader("data/shader/target/SceneVertexShader.glsl", GL_VERTEX_SHADER);
        int fshader = createShader("data/shader/target/SceneFragmentShader.glsl", GL_FRAGMENT_SHADER);
        glAttachShader(normalProgram, vshader);
        glAttachShader(normalProgram, fshader);
        glBindAttribLocation(normalProgram, 0, "position");
        glBindAttribLocation(normalProgram, 1, "normal");
        glLinkProgram(normalProgram);
        int linked = glGetProgrami(normalProgram, GL_LINK_STATUS);
        String programLog = glGetProgramInfoLog(normalProgram);
        if (programLog.trim().length() > 0) {
            System.err.println(programLog);
        }
        if (linked == 0) {
            throw new AssertionError("Could not link program");
        }
    }

    void initNormalProgram() {
        glUseProgram(normalProgram);
        depthSamplerLocation = glGetUniformLocation(normalProgram, "depthTexture");
        glUniform1i(depthSamplerLocation, 0);
        textureSamplerLocation = glGetUniformLocation(normalProgram, "textureSampler");
        glUniform1i(textureSamplerLocation, 1);

        normalProgramBiasUniform = glGetUniformLocation(normalProgram, "biasMatrix");
        normalProgramVPUniform = glGetUniformLocation(normalProgram, "viewProjectionMatrix");
        normalProgramLVPUniform = glGetUniformLocation(normalProgram, "lightViewProjectionMatrix");
        normalProgramLightPosition = glGetUniformLocation(normalProgram, "lightPosition");
        normalProgramLightLookAt = glGetUniformLocation(normalProgram, "lightLookAt");
        normalModelMatrixUniform = glGetUniformLocation(normalProgram, "modelMatrix");
        glUseProgram(0);
    }

    /**
     * Update the camera MVP matrix.
     */
    void update() {
        /* Update light */
        double alpha = System.currentTimeMillis() / 1000.0 * 0.5;
        float x = (float) Math.sin(alpha);
        float z = (float) Math.cos(alpha);
        lightPosition.set(lightDistance * x, lightHeight + (float) Math.sin(alpha), lightDistance * z);
        light.setPerspective((float) Math.toRadians(45.0f), 1.0f, 0.1f, 60.0f)
                .lookAt(lightPosition, lightLookAt, UP);

        /* Update camera */
        Vector3f position = new Vector3f(0f, 27, 00f);
        Vector3f rotation = new Vector3f(90, 0, 0);
        camera.setPerspective((float) Math.toRadians(45.0f), (float) width / height, 0.1f, 30.0f)
//                .lookAt(cameraPosition, cameraLookAt, UP)
//        .rotateY((float) Math.toRadians(45));
//        camera.identity();
                .rotateX((float) Math.toRadians(rotation.x))
                .rotateY((float) Math.toRadians(rotation.y))
                .translate(-position.x, -position.y, -position.z);
    }

    /**
     * Render the shadow map into a depth texture.
     */
    void renderShadowMap() {
        glUseProgram(shadowProgram);

        /* Set MVP matrix of the "light camera" */
        glUniformMatrix4fv(shadowProgramVPUniform, false, light.get(matrixBuffer));
        glUniformMatrix4fv(shadowModelMatrixUniform, false, new Matrix4f().identity().get(matrixBuffer));

        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glViewport(0, 0, shadowMapSize, shadowMapSize);
        /* Only clear depth buffer, since we don't have a color draw buffer */
        glClear(GL_DEPTH_BUFFER_BIT);
        glBindVertexArray(vao);

        glActiveTexture(GL_TEXTURE0);

        glUniformMatrix4fv(shadowModelMatrixUniform, false, new Matrix4f().identity().get(matrixBuffer));
        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);


        Matrix4f matrix4f = new Matrix4f().translationRotateScale(
                -1, 0, 2.F,
                0, 0, 0, 1,
                1, 1, 1);
        glUniformMatrix4fv(shadowModelMatrixUniform, false, matrix4f.get(matrixBuffer));
        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(vao2);
        glUniformMatrix4fv(shadowModelMatrixUniform, false, new Matrix4f().identity().get(matrixBuffer));
        glDrawElements(GL_TRIANGLES, mesh2.getVertexCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glUseProgram(0);
    }

    /**
     * Render the scene normally, with sampling the previously rendered depth
     * texture.
     */
    void renderNormal() {
        glUseProgram(normalProgram);

        /* Set MVP matrix of camera */
        glUniformMatrix4fv(normalProgramVPUniform, false, camera.get(matrixBuffer));
        /* Set MVP matrix that was used when doing the light-render */
        glUniformMatrix4fv(normalProgramLVPUniform, false, light.get(matrixBuffer));
        /* The bias-matrix used to convert to NDC coordinates */
        glUniformMatrix4fv(normalProgramBiasUniform, false, biasMatrix.get(matrixBuffer));
        glUniformMatrix4fv(normalModelMatrixUniform, false, new Matrix4f().identity().get(matrixBuffer));
        /* Light position and lookat for normal lambertian computation */
        glUniform3f(normalProgramLightPosition, lightPosition.x, lightPosition.y, lightPosition.z);
        glUniform3f(normalProgramLightLookAt, lightLookAt.x, lightLookAt.y, lightLookAt.z);

        glViewport(0, 0, width, height);
        /* Must clear both color and depth, since we are re-rendering the scene */
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        glBindVertexArray(vao);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, depthTexture);

        glActiveTexture(GL_TEXTURE1);
        mesh.getMaterial().getTexture().bind();

        glUniformMatrix4fv(normalModelMatrixUniform, false, new Matrix4f().identity().get(matrixBuffer));
        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        Matrix4f matrix4f = new Matrix4f().translationRotateScale(
                -1, 0, 2.F,
                0, 0, 0, 1,
                1, 1, 1);
        glUniformMatrix4fv(normalModelMatrixUniform, false, matrix4f.get(matrixBuffer));

        glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(vao2);
        mesh2.getMaterial().getTexture().bind();
        glUniformMatrix4fv(normalModelMatrixUniform, false, new Matrix4f().identity().get(matrixBuffer));
        glDrawElements(GL_TRIANGLES, mesh2.getVertexCount(), GL_UNSIGNED_INT, 0);

        glBindVertexArray(0);
        glBindTexture(GL_TEXTURE_2D, 0);

        glUseProgram(0);
    }

    void loop() {
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents();

            update();
            renderShadowMap();
            renderNormal();

            glfwSwapBuffers(window);
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
            glfwDestroyWindow(window);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            glfwTerminate();
        }
    }

    public static void main(String[] args) {
        try {
            Converter.convert(List.of(
                    SceneVertexShader.class, SceneFragmentShader.class,
                    ShadowVertexShader.class, ShadowFragmentShader.class
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new ShadowMappingDemo().run();
    }


    public static ByteBuffer ioResourceToByteBuffer(String resource) throws IOException {
        ByteBuffer buffer;
        try (InputStream source = new FileInputStream(resource)) {
            byte[] src = source.readAllBytes();
            buffer = BufferUtils.createByteBuffer(src.length + 1);
            buffer.put(src);
            buffer.flip();
        }
        return buffer;
    }
}