package dev.overtow.service.window;

import dev.overtow.service.config.Config;
import dev.overtow.util.ErrorCallback;
import dev.overtow.util.injection.Bind;
import dev.overtow.util.injection.Injector;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

@Bind
public class Window implements AutoCloseable {
    //    private final WindowKek windowHandle;
//    private static final float FOV = (float) Math.toRadians(60.0f);
//
//    private static final float Z_NEAR = 0.01f;
//
//    private static final float Z_FAR = 1000.f;

    private long windowHandle;
//    private boolean resized;
//    private Matrix4f projectionMatrix;

    private String title;
    private int height;
    private int width;

//    private DoubleBuffer posX;
//    private DoubleBuffer posY;
    GLFWErrorCallback errCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback fbCallback;

    public Window() {
        Config config = Injector.getInstance(Config.class);
        title = config.getString("window.title");
        width = config.getInteger("window.width");
        height = config.getInteger("window.height");

//        posX = MemoryUtil.memAllocDouble(1);
//        posY = MemoryUtil.memAllocDouble(1);
//
//        projectionMatrix = new Matrix4f();
//        init();
        errCallback = new ErrorCallback();
        glfwSetErrorCallback(errCallback);

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        windowHandle = glfwCreateWindow(width, height, "Shadow Mapping Demo", NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

//        glfwSetKeyCallback(windowHandle, keyCallback = new GLFWKeyCallback() {
//            @Override
//            public void invoke(long window, int key, int scancode, int action, int mods) {
//                if (action != GLFW_RELEASE)
//                    return;
//
//                if (key == GLFW_KEY_ESCAPE) {
//                    glfwSetWindowShouldClose(window, true);
//                }
//            }
//        });
        glfwSetFramebufferSizeCallback(windowHandle, fbCallback = new GLFWFramebufferSizeCallback() {
            public void invoke(long window, int width, int height) {
                if (width > 0 && height > 0 && (Window.this.width != width || Window.this.height != height)) {
                    Window.this.width = width;
                    Window.this.height = height;
                }
            }
        });

        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        assert videoMode != null;
        glfwSetWindowPos(windowHandle, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);
        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(0);
        glfwShowWindow(windowHandle);

        try (MemoryStack frame = MemoryStack.stackPush()) {
            IntBuffer posX = frame.mallocInt(1);
            IntBuffer posY = frame.mallocInt(1);
            glfwGetFramebufferSize(windowHandle, posX, posY);
            width = posX.get(0);
            height = posY.get(0);
        }
    }

//    public void init() {
//        // Setup an error callback. The default implementation
//        // will print the error message in System.err.
//        GLFWErrorCallback.createPrint(System.err).set();
//
//        // Initialize GLFW. Most GLFW functions will not work before doing this.
//        if (!glfwInit()) {
//            throw new IllegalStateException("Unable to initialize GLFW");
//        }
//
//        glfwDefaultWindowHints(); // optional, the current window hints are already the default
//        glfwWindowHint(GLFW_VISIBLE, GL_FALSE); // the window will stay hidden after creation
//        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE); // the window will be resizable
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
//        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
//        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_COMPAT_PROFILE);
//
//        boolean maximized = false;
//        // If no size has been specified set it to maximized state
//        if (width == 0 || height == 0) {
//            // Set up a fixed width and height so window initialization does not fail
//            width = 100;
//            height = 100;
//            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
//            maximized = true;
//        }
//
//        // Create the window
//        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);
//        if (windowHandle == NULL) {
//            throw new RuntimeException("Failed to create the GLFW window");
//        }
//
//        // Setup resize callback
//        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
//            this.width = width;
//            this.height = height;
//            this.resized = true;
//        });
//
//        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
//        glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> {
//            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
//                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
//            }
//        });
//
//        if (maximized) {
//            glfwMaximizeWindow(windowHandle);
//        } else {
//            // Get the resolution of the primary monitor
//            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
//            // Center our window
//            glfwSetWindowPos(
//                    windowHandle,
//                    (vidmode.width() - width) / 2,
//                    (vidmode.height() - height) / 2
//            );
//        }
//
//        // Make the OpenGL context current
//        glfwMakeContextCurrent(windowHandle);
//
//        // Enable v-sync
//        glfwSwapInterval(1);
//
//        // Make the window visible
//        glfwShowWindow(windowHandle);
//
//        GL.createCapabilities();
//
//        // Set the clear color
//        glClearColor(1, 1, 1, 1);
//        glEnable(GL_DEPTH_TEST);
//        glEnable(GL_STENCIL_TEST);
////        if (opts.showTriangles) {
////            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
////        }
//
//        glEnable(GL_BLEND);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_BACK);
//        glfwWindowHint(GLFW_SAMPLES, 4);
//    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public long getWindowHandle() {
        return windowHandle;
    }

    public Vector2f getMousePosition() {
        try (MemoryStack frame = MemoryStack.stackPush()) {
            DoubleBuffer posX = frame.mallocDouble(1);
            DoubleBuffer posY = frame.mallocDouble(1);
            glfwGetCursorPos(windowHandle, posX, posY);
            int x = (int) posX.get(0);
            int y = (int) posY.get(0);
            return new Vector2f(x, y);
        }
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    public void update() {
//        glfwSwapBuffers(windowHandle);
//        glfwPollEvents();
    }

    public void restoreState() {
//        glEnable(GL_DEPTH_TEST);
//        glEnable(GL_STENCIL_TEST);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_BACK);
    }

    public Matrix4f getProjectionMatrix() {
//        return projectionMatrix;
        return null;
    }

    public void setWindowTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(windowHandle, title);
    }

    public Matrix4f updateProjectionMatrix() {
        float aspectRatio = (float) width / (float) height;
//        return projectionMatrix.setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
        return null;
    }


    @Override
    public void close() throws Exception {
        errCallback.free();
        keyCallback.free();
        fbCallback.free();
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }

    public void pollEvents() {
        glfwPollEvents();
    }

    public void swapBuffers() {
        glfwSwapBuffers(windowHandle);
    }
}
