package dev.overtow.util;

import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.GLFW_VERSION_UNAVAILABLE;

public class ErrorCallback extends GLFWErrorCallback {
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
}
