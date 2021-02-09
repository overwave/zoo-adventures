package dev.overtow.service.shader;

import dev.overtow.util.injection.Bind;

@Bind
public class ShaderCompilerImpl implements ShaderCompiler {
    @Override
    public Shader compile(String folderPath, Uniform<?>... uniforms) {
        for (Uniform<?> uniform : uniforms) {
            uniform.createUniform(System.out::println);
        }
        return null;
    }
}
