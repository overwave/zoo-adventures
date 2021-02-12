package dev.overtow.service.renderer;

import dev.overtow.util.injection.Bind;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Bind
public class LwjglRenderer implements Renderer {
    @Override
    public long glfwCreateWindow(int width, int height, CharSequence title, long monitor, long share) {
        return GLFW.glfwCreateWindow(width, height, title, monitor, share);
    }

    @Override
    public void glfwDefaultWindowHints() {
        GLFW.glfwDefaultWindowHints();
    }

    @Override
    public void glfwDestroyWindow(long windowHandle) {
        GLFW.glfwDestroyWindow(windowHandle);
    }

    @Override
    public long glfwGetPrimaryMonitor() {
        return GLFW.glfwGetPrimaryMonitor();
    }

    @Override
    public GLFWVidMode glfwGetVideoMode(long monitorHandle) {
        return GLFW.glfwGetVideoMode(monitorHandle);
    }

    @Override
    public boolean glfwInit() {
        return GLFW.glfwInit();
    }

    @Override
    public void glfwMakeContextCurrent(long windowHandle) {
        GLFW.glfwMakeContextCurrent(windowHandle);
    }

    @Override
    public void glfwPollEvents() {
        GLFW.glfwPollEvents();
    }

    @Override
    public GLFWErrorCallback glfwSetErrorCallback(GLFWErrorCallbackI callback) {
        return GLFW.glfwSetErrorCallback(callback);
    }

    @Override
    public GLFWFramebufferSizeCallback glfwSetFramebufferSizeCallback(long windowHandle, GLFWFramebufferSizeCallbackI callback) {
        return GLFW.glfwSetFramebufferSizeCallback(windowHandle, callback);
    }

    @Override
    public GLFWKeyCallback glfwSetKeyCallback(long windowHandle, GLFWKeyCallbackI callback) {
        return GLFW.glfwSetKeyCallback(windowHandle, callback);
    }

    @Override
    public void glfwSetWindowPos(long windowHandle, int xPosition, int yPosition) {
        GLFW.glfwSetWindowPos(windowHandle, xPosition, yPosition);
    }

    @Override
    public void glfwSetWindowShouldClose(long windowHandle, boolean value) {
        GLFW.glfwSetWindowShouldClose(windowHandle, value);
    }

    @Override
    public void glfwShowWindow(long windowHandle) {
        GLFW.glfwShowWindow(windowHandle);
    }

    @Override
    public void glfwSwapBuffers(long windowHandle) {
        GLFW.glfwSwapBuffers(windowHandle);
    }

    @Override
    public void glfwSwapInterval(int interval) {
        GLFW.glfwSwapInterval(interval);
    }

    @Override
    public void glfwTerminate() {
        GLFW.glfwTerminate();
    }

    @Override
    public void glfwWindowHint(int hint, int value) {
        GLFW.glfwWindowHint(hint, value);
    }

    @Override
    public boolean glfwWindowShouldClose(long windowHandle) {
        return GLFW.glfwWindowShouldClose(windowHandle);
    }

    @Override
    public void nglfwGetFramebufferSize(long windowHandle, long width, long height) {
        GLFW.nglfwGetFramebufferSize(windowHandle, width, height);
    }

    @Override
    public void glAttachShader(int program, int shader) {
        GL30.glAttachShader(program, shader);
    }

    @Override
    public void glBindAttribLocation(int program, int index, CharSequence name) {
        GL30.glBindAttribLocation(program, index, name);
    }

    @Override
    public void glBindBuffer(int target, int buffer) {
        GL30.glBindBuffer(target, buffer);
    }

    @Override
    public void glBindFramebuffer(int target, int framebuffer) {
        GL30.glBindFramebuffer(target, framebuffer);
    }

    @Override
    public void glBindTexture(int target, int texture) {
        GL30.glBindTexture(target, texture);
    }

    @Override
    public void glBindVertexArray(int array) {
        GL30.glBindVertexArray(array);
    }

    @Override
    public void glBufferData(int target, ByteBuffer data, int usage) {
        GL30.glBufferData(target, data, usage);
    }

    @Override
    public int glCheckFramebufferStatus(int target) {
        return GL30.glCheckFramebufferStatus(target);
    }

    @Override
    public void glClear(int mask) {
        GL30.glClear(mask);
    }

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        GL30.glClearColor(red, green, blue, alpha);
    }

    @Override
    public void glCompileShader(int shader) {
        GL30.glCompileShader(shader);
    }

    @Override
    public int glCreateProgram() {
        return GL30.glCreateProgram();
    }

    @Override
    public int glCreateShader(int type) {
        return GL30.glCreateShader(type);
    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        GL30.glDrawArrays(mode, first, count);
    }

    @Override
    public void glDrawBuffer(int buffer) {
        GL30.glDrawBuffer(buffer);
    }

    @Override
    public void glEnable(int target) {
        GL30.glEnable(target);
    }

    @Override
    public void glEnableVertexAttribArray(int index) {
        GL30.glEnableVertexAttribArray(index);
    }

    @Override
    public void glFramebufferTexture2D(int target, int attachment, int textTarget, int texture, int level) {
        GL30.glFramebufferTexture2D(target, attachment, textTarget, texture, level);
    }

    @Override
    public int glGenBuffers() {
        return GL30.glGenBuffers();
    }

    @Override
    public int glGenFramebuffers() {
        return GL30.glGenFramebuffers();
    }

    @Override
    public int glGenTextures() {
        return GL30.glGenTextures();
    }

    @Override
    public int glGenVertexArrays() {
        return GL30.glGenVertexArrays();
    }

    @Override
    public String glGetProgramInfoLog(int program, int maxLength) {
        return GL30.glGetProgramInfoLog(program, maxLength);
    }

    @Override
    public String glGetProgramInfoLog(int program) {
        return GL30.glGetProgramInfoLog(program);
    }

    @Override
    public int glGetProgrami(int program, int parameterName) {
        return GL30.glGetProgrami(program, parameterName);
    }

    @Override
    public String glGetShaderInfoLog(int shader) {
        return GL30.glGetShaderInfoLog(shader);
    }

    @Override
    public int glGetShaderi(int shader, int parameterName) {
        return GL30.glGetShaderi(shader, parameterName);
    }

    @Override
    public int glGetUniformLocation(int program, CharSequence name) {
        return GL30.glGetUniformLocation(program, name);
    }

    @Override
    public void glLinkProgram(int program) {
        GL30.glLinkProgram(program);
    }

    @Override
    public void glReadBuffer(int src) {
        GL30.glReadBuffer(src);
    }

    @Override
    public void glShaderSource(int shader, CharSequence string) {
        GL30.glShaderSource(shader, string);
    }

    @Override
    public void glShaderSource(int shader, PointerBuffer strings, IntBuffer length) {
        GL30.glShaderSource(shader, strings, length);
    }

    @Override
    public void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        GL30.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    @Override
    public void glTexParameteri(int target, int parameterName, int param) {
        GL30.glTexParameteri(target, parameterName, param);
    }

    @Override
    public void glUniform1i(int location, int value) {
        GL30.glUniform1i(location, value);
    }

    @Override
    public void glUniform3f(int location, float value0, float value1, float value2) {
        GL30.glUniform3f(location, value0, value1, value2);
    }

    @Override
    public void glUniformMatrix4fv(int location, boolean transpose, FloatBuffer value) {
        GL30.glUniformMatrix4fv(location, transpose, value);
    }

    @Override
    public void glUseProgram(int program) {
        GL30.glUseProgram(program);
    }

    @Override
    public void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        GL30.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    @Override
    public void glViewport(int x, int y, int w, int h) {
        GL30.glViewport(x, y, w, h);
    }
}
