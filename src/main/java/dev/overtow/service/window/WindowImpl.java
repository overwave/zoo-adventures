package dev.overtow.service.window;

import dev.overtow.math.Vector2;
import dev.overtow.service.config.Config;
import dev.overtow.service.settings.Settings;
import dev.overtow.util.ErrorCallback;
import dev.overtow.util.injection.Bind;
import dev.overtow.util.injection.Destroyable;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

@Bind
public class WindowImpl implements Window, Destroyable {

    private final long windowHandle;
    //    private boolean resized;
    private String title;
    private Vector2 size;
    private Vector2 windowPosition;
    GLFWErrorCallback errCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback fbCallback;
    GLFWWindowPosCallback posCallback;

    public WindowImpl(Config config, Settings settings) {
        title = config.getString("window.title");
        size = Vector2.of(config.getInteger("window.width"), config.getInteger("window.height"));

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
        glfwWindowHint(GLFW_SAMPLES, settings.getMsaaLevel());

        windowHandle = glfwCreateWindow(getWidth(), getHeight(), "The Zoo Adventures", NULL, NULL);
        if (windowHandle == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(windowHandle, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (action != GLFW_RELEASE) {
                    return;
                }

                if (key == GLFW_KEY_ESCAPE) {
                    glfwSetWindowShouldClose(window, true);
                }
            }
        });
        glfwSetFramebufferSizeCallback(windowHandle, fbCallback = new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                if (width > 0 && height > 0 && (getWidth() != width || getHeight() != height)) {
                    size = Vector2.of(width, height);
                }
            }
        });
        glfwSetWindowPosCallback(windowHandle, posCallback = new GLFWWindowPosCallback() {
            @Override
            public void invoke(long window, int xpos, int ypos) {
                windowPosition = Vector2.of(xpos, ypos);
            }
        });

        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        assert videoMode != null;
        glfwSetWindowPos(windowHandle, (videoMode.width() - getWidth()) / 2, (videoMode.height() - getHeight()) / 2);
        glfwMakeContextCurrent(windowHandle);
        glfwSwapInterval(1);
        glfwShowWindow(windowHandle);

        try (MemoryStack frame = MemoryStack.stackPush()) {
            IntBuffer posX = frame.mallocInt(1);
            IntBuffer posY = frame.mallocInt(1);
            glfwGetFramebufferSize(windowHandle, posX, posY);
            size = Vector2.of(posX.get(0), posY.get(0));
        }
    }
//            glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
//        glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
//        });
//            glfwMaximizeWindow(windowHandle);

    @Override
    public int getWidth() {
        return (int) size.getX();
    }

    @Override
    public int getHeight() {
        return (int) size.getY();
    }

    @Override
    public Vector2 getSize() {
        return size;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
        glfwSetWindowTitle(windowHandle, title);
    }

    @Override
    public float getAspectRatio() {
        return size.getX() / size.getY();
    }

    @Override
    public long getWindowHandle() {
        return windowHandle;
    }

    @Override
    public Vector2 getMousePosition() {
        try (MemoryStack frame = MemoryStack.stackPush()) {
            DoubleBuffer posX = frame.mallocDouble(1);
            DoubleBuffer posY = frame.mallocDouble(1);
            glfwGetCursorPos(windowHandle, posX, posY);

            return Vector2.of((float) posX.get(0), (float) posY.get(0));
        }
    }

    @Override
    public boolean windowShouldClose() {
        return glfwWindowShouldClose(windowHandle);
    }

    @Override
    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }

    @Override
    public void destroy() {
        errCallback.free();
        keyCallback.free();
        fbCallback.free();
        posCallback.free();
        glfwDestroyWindow(windowHandle);
        glfwTerminate();
    }

    @Override
    public void pollEvents() {
        glfwPollEvents();
    }

    @Override
    public void swapBuffers() {
        glfwSwapBuffers(windowHandle);
    }
}
