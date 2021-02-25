package dev.overtow.glsl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import dev.overtow.core.shader.uniform.Uniform.Name;
import dev.overtow.glsl.shader.Shader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        HashMap<String, String> inOutMap = convertFields(compilationUnit);

        System.out.println(inOutMap);
    }

    private HashMap<String, String> convertFields(CompilationUnit compilationUnit) {
        HashMap<String, String> inOutMap = new HashMap<>();
        final boolean[] hasParentShader = {false};

        new VoidVisitorAdapter<Map<String, String>>() {
            @Override
            public void visit(FieldDeclaration fd, Map<String, String> inOutMap) {

                boolean isDefinition = fd.hasModifier(Modifier.Keyword.FINAL) && fd.hasModifier(Modifier.Keyword.STATIC);
                if (isDefinition) {
                    VariableDeclarator variable = fd.getVariable(0);
                    String variableName = variable.getNameAsString();
                    String initializer = variable.getInitializer().orElseThrow().toString();

                    System.out.println("#define " + variableName + " " + initializer);
                    return;
                }

                boolean isParentShaderLink = isParentShaderLink(fd.getVariable(0));
                if (isParentShaderLink) {
                    hasParentShader[0] = true;
                    return;
                }

                boolean isUniform = fd.getAnnotationByClass(Uniform.class).isPresent();
                if (isUniform) {
                    String type = uncapitalize(fd.getVariable(0).getTypeAsString());

                    SingleMemberAnnotationExpr annotationExpr = fd.getAnnotationByClass(Uniform.class)
                            .orElseThrow()
                            .asSingleMemberAnnotationExpr();

                    Name name = Name.valueOf(annotationExpr.getMemberValue().toString());

                    System.out.printf("uniform %s %s;%n", type, name.get());
                    return;
                }

                boolean isInput = fd.getAnnotationByClass(Input.class).isPresent();
                if (isInput) {
                    VariableDeclarator variable = fd.getVariable(0);

                    String type = uncapitalize(variable.getTypeAsString());

                    if (hasParentShader[0]) {
                        String parentShaderLinkName = variable.getInitializer().orElseThrow().asFieldAccessExpr().getNameAsString();
                        inOutMap.put(variable.getNameAsString(), parentShaderLinkName);

                        System.out.printf("in %s %s;%n", type, parentShaderLinkName);
                        return;
                    } else {
                        NodeList<MemberValuePair> annotationValues = fd.getAnnotationByClass(Input.class)
                                .orElseThrow()
                                .asNormalAnnotationExpr()
                                .getPairs();

                        for (MemberValuePair annotationValue : annotationValues) {
                            if (annotationValue.getNameAsString().equals("location")) {
                                int location = Integer.parseInt(annotationValue.getValue().toString());
                                String name = variable.getNameAsString();

                                System.out.printf("layout (location=%d) in %s %s;%n", location, type, name);
                                return;
                            }
                        }
                        throw new IllegalStateException();
                    }
                }

                boolean isOutput = fd.getAnnotationByClass(Output.class).isPresent();
                if (isOutput) {
                    boolean isHidden = fd.getAnnotationByClass(Output.class)
                            .orElseThrow()
                            .toNormalAnnotationExpr()
                            .stream()
                            .map(NormalAnnotationExpr::getPairs)
                            .flatMap(List::stream)
                            .anyMatch(pair -> pair.getNameAsString().equals("shown") && pair.getValue().toString().equals("false"));
                    if (isHidden) {
                        return;
                    }

                    VariableDeclarator variable = fd.getVariable(0);

                    String type = uncapitalize(variable.getTypeAsString());
                    String name = variable.getNameAsString();

                    System.out.printf("out %s %s;%n", type, name);
                    return;
                }

                throw new IllegalArgumentException();
            }
        }.visit(compilationUnit, inOutMap);

        return inOutMap;
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
