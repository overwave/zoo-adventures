package dev.overtow.glsl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import dev.overtow.core.shader.uniform.Uniform.Name;
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

        System.out.println("#version 330");
        System.out.println();

        new FieldVisitor().visit(compilationUnit, null);
    }


    private static class FieldVisitor extends VoidVisitorAdapter<Void> {

        @Override
        public void visit(FieldDeclaration fd, Void arg) {
            boolean isDefinition = fd.hasModifier(Modifier.Keyword.FINAL) && fd.hasModifier(Modifier.Keyword.STATIC);
            if (isDefinition) {
                printAsDefinition(fd);
                return;
            }

            boolean isParentShaderLink = isParentShaderLink(fd.getVariable(0));
            if (isParentShaderLink) {
                System.out.println();
                return;
            }

            boolean isUniform = fd.getAnnotationByClass(Uniform.class).isPresent();
            if (isUniform) {
                printAsUniform(fd);
                return;
            }

            boolean isInput = fd.getAnnotationByClass(Input.class).isPresent();
            if (isInput) {
                printAsInput(fd);
                return;
            }

            boolean isOutput = fd.getAnnotationByClass(Output.class).isPresent();
            if (isOutput) {
                printAsOutput(fd);
                return;
            }

            System.out.println(fd);
        }

        private void printAsDefinition(FieldDeclaration fd) {
            VariableDeclarator variable = fd.getVariable(0);
            String variableName = variable.getNameAsString();
            String initializer = variable.getInitializer().orElseThrow().toString();

            System.out.println("#define " + variableName + " " + initializer);
        }

        private void printAsUniform(FieldDeclaration fd) {
            String type = uncapitalize(fd.getVariable(0).getTypeAsString());

            SingleMemberAnnotationExpr annotationExpr = fd.getAnnotationByClass(Uniform.class)
                    .orElseThrow()
                    .asSingleMemberAnnotationExpr();

            Name name = Name.valueOf(annotationExpr.getMemberValue().toString());

            System.out.printf("uniform %s %s;%n", type, name.get());
        }

        private void printAsInput(FieldDeclaration fd) {
            VariableDeclarator variable = fd.getVariable(0);
            String type = uncapitalize(variable.getTypeAsString());
            Expression initializer = variable.getInitializer().orElseThrow();

            System.out.println(initializer);
//            Name name = Name.valueOf(annotationExpr.getMemberValue().toString());
//
//            System.out.printf("uniform %s %s;%n", type, name.get());
        }

        private void printAsOutput(FieldDeclaration fd) {
//            String type = uncapitalize(fd.getVariable(0).getTypeAsString());
//
//            SingleMemberAnnotationExpr annotationExpr = fd.getAnnotationByClass(Output.class)
//                    .orElseThrow()
//                    .asSingleMemberAnnotationExpr();
//
//            Name name = Name.valueOf(annotationExpr.getMemberValue().toString());
//
//            System.out.printf("out %s %s;%n", type, name.get());
        }

        private boolean isParentShaderLink(VariableDeclarator variable) {
            String shaderPackageName = Shader.class.getPackageName();

            try {
                Class<?> clazz = Class.forName(shaderPackageName + "." + variable.getTypeAsString());
                return Shader.class.isAssignableFrom(clazz);
            } catch (ClassNotFoundException e) {
                return false;
            }
        }

        private String uncapitalize(String word) {
            return word.substring(0, 1).toLowerCase() + word.substring(1);
        }
    }
}
