package dev.overtow.core;

import dev.overtow.service.config.Config;
import dev.overtow.util.ErrorCallback;
import dev.overtow.util.injection.Bind;
import dev.overtow.util.injection.Injector;
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

@Bind
public class Window implements AutoCloseable {

    private long windowHandle;
    //    private boolean resized;
    private String title;
    private int height;
    private int width;
    GLFWErrorCallback errCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback fbCallback;

    public Window() {
        Config config = Injector.getInstance(Config.class);
        title = config.getString("window.title");
        width = config.getInteger("window.width");
        height = config.getInteger("window.height");

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
//            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
//        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
//        });
//            glfwMaximizeWindow(windowHandle);

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
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

    @Override
    public void close() {
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
