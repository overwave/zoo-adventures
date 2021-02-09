package dev.overtow.service.shader;

public interface ShaderCompiler {
    Shader compile(String folderPath, Uniform<?>... uniforms);
}
