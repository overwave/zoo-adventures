package dev.overtow.glsl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import dev.overtow.glsl.shader.Shader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class Converter {
    public Converter(List<Class<? extends Shader>> classes) {
        for (Class<?> clazz : classes) {
            File javaFile = classToFile(clazz);

            convertToGlsl(javaFile);
        }
    }

    private File classToFile(Class<?> clazz) {
        return new File("src\\main\\java\\" + clazz.getCanonicalName().replace('.', '\\') + ".java");
    }

    private CompilationUnit parseJava(File javaFile) {
        try {
            return StaticJavaParser.parse(javaFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void convertToGlsl(File javaFile) {
        CompilationUnit compilationUnit = parseJava(javaFile);

//        compilationUnit.
    }
}
