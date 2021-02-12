package dev.overtow.service.renderer;

import dev.overtow.util.injection.Bind;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

@Bind(Renderer.DEBUG)
public class DebuggingRenderer implements Renderer {

    @Override
    public long glfwCreateWindow(int width, int height, CharSequence title, long monitor, long share) {
        System.out.printf("Called glfwCreateWindow, args: width=%d, title=%s, monitor=%d, share=%d%n", width, title, monitor, share);
        return GLFW.glfwCreateWindow(width, height, title, monitor, share);
    }

    @Override
    public void glfwDefaultWindowHints() {
        System.out.printf("Called glfwDefaultWindowHints%n");
        GLFW.glfwDefaultWindowHints();
    }

    @Override
    public void glfwDestroyWindow(long windowHandle) {
        System.out.printf("Called glfwDestroyWindow, args: windowHandle=%d%n", windowHandle);
        GLFW.glfwDestroyWindow(windowHandle);
    }

    @Override
    public long glfwGetPrimaryMonitor() {
        System.out.printf("Called glfwGetPrimaryMonitor%n");
        return GLFW.glfwGetPrimaryMonitor();
    }

    @Override
    public GLFWVidMode glfwGetVideoMode(long monitorHandle) {
        System.out.printf("Called glfwGetVideoMode, args: monitorHandle=%d%n", monitorHandle);
        return GLFW.glfwGetVideoMode(monitorHandle);
    }

    @Override
    public boolean glfwInit() {
        System.out.printf("Called glfwInit%n");
        return GLFW.glfwInit();
    }

    @Override
    public void glfwMakeContextCurrent(long windowHandle) {
        System.out.printf("Called glfwMakeContextCurrent, args: windowHandle=%d%n", windowHandle);
        GLFW.glfwMakeContextCurrent(windowHandle);
    }

    @Override
    public void glfwPollEvents() {
        System.out.printf("Called glfwPollEvents%n");
        GLFW.glfwPollEvents();
    }

    @Override
    public GLFWErrorCallback glfwSetErrorCallback(GLFWErrorCallbackI callback) {
        System.out.printf("Called glfwSetErrorCallback, args: callback=%s%n", callback);
        return GLFW.glfwSetErrorCallback(callback);
    }

    @Override
    public GLFWFramebufferSizeCallback glfwSetFramebufferSizeCallback(long windowHandle, GLFWFramebufferSizeCallbackI callback) {
        System.out.printf("Called glfwSetFramebufferSizeCallback, args: windowHandle=%d, callback=%s%n", windowHandle, callback);
        return GLFW.glfwSetFramebufferSizeCallback(windowHandle, callback);
    }

    @Override
    public GLFWKeyCallback glfwSetKeyCallback(long windowHandle, GLFWKeyCallbackI callback) {
        System.out.printf("Called glfwSetKeyCallback, args: windowHandle=%d, callback=%s%n", windowHandle, callback);
        return GLFW.glfwSetKeyCallback(windowHandle, callback);
    }

    @Override
    public void glfwSetWindowPos(long windowHandle, int xPosition, int yPosition) {
        System.out.printf("Called glfwSetWindowPos, args: windowHandle=%d, xPosition=%d, yPosition=%d%n", windowHandle, xPosition, yPosition);
        GLFW.glfwSetWindowPos(windowHandle, xPosition, yPosition);
    }

    @Override
    public void glfwSetWindowShouldClose(long windowHandle, boolean value) {
        System.out.printf("Called glfwSetWindowShouldClose, args: windowHandle=%d, value=%b%n", windowHandle, value);
        GLFW.glfwSetWindowShouldClose(windowHandle, value);
    }

    @Override
    public void glfwShowWindow(long windowHandle) {
        System.out.printf("Called glfwShowWindow, args: windowHandle=%d%n", windowHandle);
        GLFW.glfwShowWindow(windowHandle);
    }

    @Override
    public void glfwSwapBuffers(long windowHandle) {
        System.out.printf("Called glfwSwapBuffers, args: windowHandle=%d%n", windowHandle);
        GLFW.glfwSwapBuffers(windowHandle);
    }

    @Override
    public void glfwSwapInterval(int interval) {
        System.out.printf("Called glfwSwapInterval, args: interval=%d%n", interval);
        GLFW.glfwSwapInterval(interval);
    }

    @Override
    public void glfwTerminate() {
        System.out.printf("Called glfwTerminate%n");
        GLFW.glfwTerminate();
    }

    @Override
    public void glfwWindowHint(int hint, int value) {
        System.out.printf("Called glfwWindowHint, args: hint=%d, value=%d%n", hint, value);
        GLFW.glfwWindowHint(hint, value);
    }

    @Override
    public boolean glfwWindowShouldClose(long windowHandle) {
        System.out.printf("Called glfwWindowShouldClose, args: windowHandle=%d%n", windowHandle);
        return GLFW.glfwWindowShouldClose(windowHandle);
    }

    @Override
    public void nglfwGetFramebufferSize(long windowHandle, long width, long height) {
        System.out.printf("Called nglfwGetFramebufferSize, args: windowHandle=%d, width=%d, height=%d%n", windowHandle, width, height);
        GLFW.nglfwGetFramebufferSize(windowHandle, width, height);
    }

    ///////////////////////
    @Override
    public void glAttachShader(int program, int shader) {
        System.out.printf("Called glAttachShader, args: program=%d, shader=%d%n", program, shader);
        GL30.glAttachShader(program, shader);
    }

    @Override
    public void glBindAttribLocation(int program, int index, CharSequence name) {
        System.out.printf("Called glBindAttribLocation, args: program=%d, index=%d, name=%s%n", program, index, name);
        GL30.glBindAttribLocation(program, index, name);
    }

    @Override
    public void glBindBuffer(int target, int buffer) {
        System.out.printf("Called glBindBuffer, args: target=%d, buffer=%d%n", target, buffer);
        GL30.glBindBuffer(target, buffer);
    }

    @Override
    public void glBindFramebuffer(int target, int framebuffer) {
        System.out.printf("Called glBindFramebuffer, args: target=%d, framebuffer=%d%n", target, framebuffer);
        GL30.glBindFramebuffer(target, framebuffer);
    }

    @Override
    public void glBindTexture(int target, int texture) {
        System.out.printf("Called glBindTexture, args: target=%d, texture=%d%n", target, texture);
        GL30.glBindTexture(target, texture);
    }

    @Override
    public void glBindVertexArray(int array) {
        System.out.printf("Called glBindVertexArray, args: array=%d%n", array);
        GL30.glBindVertexArray(array);
    }

    @Override
    public void glBufferData(int target, ByteBuffer data, int usage) {
        System.out.printf("Called glBufferData, args: target=%d, data=%s, usage=%d%n", target, data, usage);
        GL30.glBufferData(target, data, usage);
    }

    @Override
    public int glCheckFramebufferStatus(int target) {
        System.out.printf("Called glCheckFramebufferStatus, args: target=%d%n", target);
        return GL30.glCheckFramebufferStatus(target);
    }

    @Override
    public void glClear(int mask) {
        System.out.printf("Called glClear, args: mask=%d%n", mask);
        GL30.glClear(mask);
    }

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        System.out.printf("Called glClearColor, args: red=%f, green=%s, green=%f, alpha=%f%n", red, green, blue, alpha);
        GL30.glClearColor(red, green, blue, alpha);
    }

    @Override
    public void glCompileShader(int shader) {
        System.out.printf("Called glCompileShader, args: shader=%d%n", shader);
        GL30.glCompileShader(shader);
    }

    @Override
    public int glCreateProgram() {
        System.out.printf("Called glCreateProgram%n");
        return GL30.glCreateProgram();
    }

    @Override
    public int glCreateShader(int type) {
        System.out.printf("Called glCreateShader, args: type=%d%n", type);
        return GL30.glCreateShader(type);
    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        System.out.printf("Called glDrawArrays, args: mode=%d, first=%d, count=%d%n", mode, first, count);
        GL30.glDrawArrays(mode, first, count);
    }

    @Override
    public void glDrawBuffer(int buffer) {
        System.out.printf("Called glDrawBuffer, args: buffer=%d%n", buffer);
        GL30.glDrawBuffer(buffer);
    }

    @Override
    public void glEnable(int target) {
        System.out.printf("Called glEnable, args: target=%d%n", target);
        GL30.glEnable(target);
    }

    @Override
    public void glEnableVertexAttribArray(int index) {
        System.out.printf("Called glEnableVertexAttribArray, args: index=%d%n", index);
        GL30.glEnableVertexAttribArray(index);
    }

    @Override
    public void glFramebufferTexture2D(int target, int attachment, int textTarget, int texture, int level) {
        System.out.printf("Called glFramebufferTexture2D, args: target=%d, attachment=%d, textTarget=%d, texture=%d, level=%d%n", target, attachment, textTarget, texture, level);
        GL30.glFramebufferTexture2D(target, attachment, textTarget, texture, level);
    }

    @Override
    public int glGenBuffers() {
        System.out.printf("Called glGenBuffers%n");
        return GL30.glGenBuffers();
    }

    @Override
    public int glGenFramebuffers() {
        System.out.printf("Called glGenFramebuffers%n");
        return GL30.glGenFramebuffers();
    }

    @Override
    public int glGenTextures() {
        System.out.printf("Called glGenTextures%n");
        return GL30.glGenTextures();
    }

    @Override
    public int glGenVertexArrays() {
        System.out.printf("Called glGenVertexArrays%n");
        return GL30.glGenVertexArrays();
    }

    @Override
    public String glGetProgramInfoLog(int program, int maxLength) {
        System.out.printf("Called glGetProgramInfoLog, args: program=%d, maxLength=%d%n", program, maxLength);
        return GL30.glGetProgramInfoLog(program, maxLength);
    }

    @Override
    public String glGetProgramInfoLog(int program) {
        System.out.printf("Called glGetProgramInfoLog, args: program=%d%n", program);
        return GL30.glGetProgramInfoLog(program);
    }

    @Override
    public int glGetProgrami(int program, int parameterName) {
        System.out.printf("Called glGetProgrami, args: program=%d, parameterName=%d%n", program, parameterName);
        return GL30.glGetProgrami(program, parameterName);
    }

    @Override
    public String glGetShaderInfoLog(int shader) {
        System.out.printf("Called glGetShaderInfoLog, args: shader=%d%n", shader);
        return GL30.glGetShaderInfoLog(shader);
    }

    @Override
    public int glGetShaderi(int shader, int parameterName) {
        System.out.printf("Called glGetShaderi, args: shader=%d, parameterName=%d%n", shader, parameterName);
        return GL30.glGetShaderi(shader, parameterName);
    }

    @Override
    public int glGetUniformLocation(int program, CharSequence name) {
        System.out.printf("Called glGetUniformLocation, args: program=%d, name=%s%n", program, name);
        return GL30.glGetUniformLocation(program, name);
    }

    @Override
    public void glLinkProgram(int program) {
        System.out.printf("Called glLinkProgram, args: program=%d%n", program);
        GL30.glLinkProgram(program);
    }

    @Override
    public void glReadBuffer(int src) {
        System.out.printf("Called glReadBuffer, args: src=%d%n", src);
        GL30.glReadBuffer(src);
    }

    @Override
    public void glShaderSource(int shader, CharSequence string) {
        System.out.printf("Called glShaderSource, args: program=%d, string=%s%n", shader, string);
        GL30.glShaderSource(shader, string);
    }

    @Override
    public void glShaderSource(int shader, PointerBuffer strings, IntBuffer length) {
        System.out.printf("Called glShaderSource, args: program=%d, strings=%s, length=%s%n", shader, strings, length);
        GL30.glShaderSource(shader, strings, length);
    }

    @Override
    public void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
        System.out.printf("Called glTexImage2D, args: target=%d, level=%d, internalFormat=%d, width=%d, height=%d, border=%d, format=%d, type=%d, pixels=%s%n", target, level, internalFormat, width, height, border, format, type, pixels);
        GL30.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
    }

    @Override
    public void glTexParameteri(int target, int parameterName, int param) {
        System.out.printf("Called glTexParameteri, args: target=%d, parameterName=%d, param=%d%n", target, parameterName, param);
        GL30.glTexParameteri(target, parameterName, param);
    }

    @Override
    public void glUniform1i(int location, int value) {
        System.out.printf("Called glUniform1i, args: location=%d, value=%d%n", location, value);
        GL30.glUniform1i(location, value);
    }

    @Override
    public void glUniform3f(int location, float value0, float value1, float value2) {
        System.out.printf("Called glUniform3f, args: location=%d, value0=%f, value1=%f, value2=%f%n", location, value0, value1, value2);
        GL30.glUniform3f(location, value0, value1, value2);
    }

    @Override
    public void glUniformMatrix4fv(int location, boolean transpose, FloatBuffer value) {
        System.out.printf("Called glUniformMatrix4fv, args: location=%d, transpose=%b, value=%s%n", location, transpose, value);
        GL30.glUniformMatrix4fv(location, transpose, value);
    }

    @Override
    public void glUseProgram(int program) {
        System.out.printf("Called glUseProgram, args: program=%d%n", program);
        GL30.glUseProgram(program);
    }

    @Override
    public void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
        System.out.printf("Called glVertexAttribPointer, args: index=%d, size=%d, type=%d, normalized=%b, stride=%d, pointer=%d%n", index, size, type, normalized, stride, pointer);
        GL30.glVertexAttribPointer(index, size, type, normalized, stride, pointer);
    }

    @Override
    public void glViewport(int x, int y, int w, int h) {
        System.out.printf("Called glViewport, args: x=%d, y=%d, z=%d, h=%d%n", x, y, w, h);
        GL30.glViewport(x, y, w, h);
    }
}
