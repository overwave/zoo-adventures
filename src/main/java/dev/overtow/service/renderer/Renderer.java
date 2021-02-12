package dev.overtow.service.renderer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface Renderer {
    String DEBUG = "debug";

    void glfwMakeContextCurrent(long windowHandle);

    void glfwPollEvents();

    GLFWErrorCallback glfwSetErrorCallback(GLFWErrorCallbackI callback);

    void glfwDestroyWindow(long windowHandle);

    long glfwGetPrimaryMonitor();

    GLFWVidMode glfwGetVideoMode(long monitorHandle);

    boolean glfwInit();

    long glfwCreateWindow(int width, int height, CharSequence title, long monitor, long share);

    void glfwDefaultWindowHints();

    GLFWFramebufferSizeCallback glfwSetFramebufferSizeCallback(long windowHandle, GLFWFramebufferSizeCallbackI callback);

    GLFWKeyCallback glfwSetKeyCallback(long windowHandle, GLFWKeyCallbackI callback);

    void glfwSetWindowPos(long windowHandle, int xPosition, int yPosition);

    void glfwSetWindowShouldClose(long windowHandle, boolean value);

    void glfwShowWindow(long windowHandle);

    void glfwSwapBuffers(long windowHandle);

    void glfwSwapInterval(int interval);

    void glfwTerminate();

    void glfwWindowHint(int hint, int value);

    boolean glfwWindowShouldClose(long windowHandle);

    void nglfwGetFramebufferSize(long windowHandle, long width, long height);

    void glAttachShader(int program, int shader);

    void glBindAttribLocation(int program, int index, CharSequence name);

    void glBindBuffer(int target, int buffer);

    void glBindFramebuffer(int target, int framebuffer);

    void glBindTexture(int target, int texture);

    void glBindVertexArray(int array);

    void glBufferData(int target, ByteBuffer data, int usage);

    int glCheckFramebufferStatus(int target);

    void glClear(int mask);

    void glClearColor(float red, float green, float blue, float alpha);

    void glCompileShader(int shader);

    int glCreateProgram();

    int glCreateShader(int type);

    void glDrawArrays(int mode, int first, int count);

    void glDrawBuffer(int buffer);

    void glEnable(int target);

    void glEnableVertexAttribArray(int index);

    void glFramebufferTexture2D(int target, int attachment, int textTarget, int texture, int level);

    int glGenBuffers();

    int glGenFramebuffers();

    int glGenTextures();

    int glGenVertexArrays();

    String glGetProgramInfoLog(int program, int maxLength);

    String glGetProgramInfoLog(int program);

    int glGetProgrami(int program, int parameterName);

    String glGetShaderInfoLog(int shader);

    int glGetShaderi(int shader, int parameterName);

    int glGetUniformLocation(int program, CharSequence name);

    void glLinkProgram(int program);

    void glReadBuffer(int src);

    void glShaderSource(int shader, CharSequence string);

    void glShaderSource(int shader, PointerBuffer strings, IntBuffer length);

    void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels);

    void glTexParameteri(int target, int parameterName, int param);

    void glUniform1i(int location, int value);

    void glUniform3f(int location, float value0, float value1, float value2);

    void glUniformMatrix4fv(int location, boolean transpose, FloatBuffer value);

    void glUseProgram(int program);

    void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer);

    void glViewport(int x, int y, int w, int h);
}
