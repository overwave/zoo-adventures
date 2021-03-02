package dev.overtow.glsl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import dev.overtow.core.shader.uniform.Uniform.Name;
import dev.overtow.glsl.shader.Shader;
import dev.overtow.glsl.shader.VertexShader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Converter {

    private final PrintStream os;
    private final Map<String, String> inOutMap;

    private Converter(PrintStream os) {
        this.os = os;
        this.inOutMap = new HashMap<>();
    }

    public static List<Name> getUsedUniforms(List<Class<? extends Shader>> shaderClasses) {
        return shaderClasses.stream()
                .map(Class::getDeclaredFields)
                .flatMap(Arrays::stream)
                .filter(field -> field.isAnnotationPresent(Uniform.class))
                .map(field -> field.getAnnotation(Uniform.class).value())
                .collect(Collectors.toList());
    }

    public static String convert(Class<? extends Shader> classes) {
        return Converter.convertToGlsl(classes);
    }

    public static List<String> convert(List<Class<? extends Shader>> classes) {
        return classes.stream().map(Converter::convertToGlsl).collect(Collectors.toList());
    }

    private static String convertToGlsl(Class<? extends Shader> clazz) {
        File javaFile = classToFile(clazz);
        CompilationUnit compilationUnit = parseJava(javaFile);
        String filename;
        try {
            String fileExtension = (String) clazz.getField("extension").get(null);
            filename = "data/shader/target/%s.%s".formatted(clazz.getSimpleName(), fileExtension);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        try (PrintStream os = new PrintStream(new FileOutputStream(filename))) {
            Converter converter = new Converter(os);
            converter.doConvertToGlsl(compilationUnit);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return filename;
    }

    private void doConvertToGlsl(CompilationUnit compilationUnit) {
        os.println("#version 330");

        convertFields(compilationUnit);
        convertMethods(compilationUnit);
    }

    private static File classToFile(Class<?> clazz) {
        return new File("src\\main\\java\\" + clazz.getCanonicalName().replace('.', '\\') + ".java");
    }

    private static CompilationUnit parseJava(File javaFile) {
        try {
            return StaticJavaParser.parse(javaFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void convertFields(CompilationUnit compilationUnit) {
        final boolean[] hasParentShader = {false};

        new VoidVisitorAdapter<Map<String, String>>() {
            @Override
            public void visit(FieldDeclaration fd, Map<String, String> inOutMap) {

                boolean isDefinition = fd.hasModifier(Modifier.Keyword.FINAL) && fd.hasModifier(Modifier.Keyword.STATIC);
                if (isDefinition) {
                    VariableDeclarator variable = fd.getVariable(0);
                    String variableName = variable.getNameAsString();
                    String initializer = variable.getInitializer().orElseThrow().toString();

                    os.println("#define " + variableName + " " + initializer);
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

                    os.printf("uniform %s %s;%n", type, name.get());
                    return;
                }

                boolean isInput = fd.getAnnotationByClass(Input.class).isPresent();
                if (isInput) {
                    VariableDeclarator variable = fd.getVariable(0);

                    String type = uncapitalize(variable.getTypeAsString());

                    if (hasParentShader[0]) {
                        String parentShaderLinkName = variable.getInitializer().orElseThrow().asFieldAccessExpr().getNameAsString();
                        inOutMap.put(variable.getNameAsString(), parentShaderLinkName);

                        os.printf("in %s %s;%n", type, parentShaderLinkName);
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

                                os.printf("layout (location=%d) in %s %s;%n", location, type, name);
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

                    os.printf("out %s %s;%n", type, name);
                    return;
                }

                throw new IllegalArgumentException();
            }
        }.visit(compilationUnit, inOutMap);
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

    private void convertMethods(CompilationUnit compilationUnit) {
        new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration md, Void arg) {
                printMethodSignature(md);
                printMethodBody(md);
                os.println("}");
            }
        }.visit(compilationUnit, null);
    }

    private void printMethodSignature(MethodDeclaration md) {
        String returnType;
        Type type = md.getType();
        if (type.isPrimitiveType()) {
            returnType = type.asString().replace("double", "float");
        } else if (type.isVoidType()) {
            returnType = "void";
        } else {
            throw new IllegalArgumentException();
        }

        String name = md.getNameAsString();
        String args = md.getParameters()
                .stream()
                .map(par -> uncapitalize(par.getTypeAsString()) + " " + par.getNameAsString())
                .collect(Collectors.joining(", "));

        os.printf("%s %s(%s) {%n", returnType, name, args);
    }

    private void printMethodBody(MethodDeclaration md) {
        BlockStmt methodBody = md.getBody().orElseThrow();
        for (Statement statement : methodBody.getStatements()) {
            os.println(convertStatement(statement));
        }
    }

    private String convertStatement(Statement statement) {
        if (statement == null) {
            return "";
        }
        if (statement.isReturnStmt()) {
            ReturnStmt returnStmt = statement.asReturnStmt();
            return String.format("return %s;", convertExpression(returnStmt.getExpression().orElse(null)));
        } else if (statement.isExpressionStmt()) {
            ExpressionStmt expressionStmt = statement.asExpressionStmt();
            return convertExpression(expressionStmt.getExpression()) + ";";
        } else if (statement.isIfStmt()) {
            IfStmt ifStmt = statement.asIfStmt();

            String clause = convertExpression(ifStmt.getCondition());
            String body = convertStatement(ifStmt.getThenStmt());

            String code = String.format("if (%s) %s", clause, body);
            code += ifStmt.getElseStmt().map(st -> String.format(" else %s", convertStatement(st))).orElse("");

            return code;
        } else if (statement.isBlockStmt()) {
            BlockStmt blockStmt = statement.asBlockStmt();

            String body = blockStmt.getStatements().stream()
                    .map(this::convertStatement)
                    .collect(Collectors.joining(";\n"));
            return String.format("{%n%s%n}", body);
        }

        throw new IllegalStateException();
    }

    private String convertExpression(Expression expression) {
        if (expression == null) {
            return "";
        }

        if (expression.isMethodCallExpr()) {
            MethodCallExpr callExpr = expression.asMethodCallExpr();

            String methodName = callExpr.getNameAsString();
            if (methodName.equals("multiply")) {
                return convertExpression(new BinaryExpr(callExpr.getScope().orElseThrow(), callExpr.getArgument(0), BinaryExpr.Operator.MULTIPLY));
            }
            if (methodName.equals("divide")) {
                return convertExpression(new BinaryExpr(callExpr.getScope().orElseThrow(), callExpr.getArgument(0), BinaryExpr.Operator.DIVIDE));
            }
            if (methodName.equals("plus")) {
                return convertExpression(new BinaryExpr(callExpr.getScope().orElseThrow(), callExpr.getArgument(0), BinaryExpr.Operator.PLUS));
            }
            if (methodName.equals("minus")) {
                return convertExpression(new BinaryExpr(callExpr.getScope().orElseThrow(), callExpr.getArgument(0), BinaryExpr.Operator.MINUS));
            }

            String arguments = callExpr.getArguments().stream()
                    .map(this::convertExpression)
                    .collect(Collectors.joining(", "));
            return callExpr.getNameAsString() + "(" + arguments + ")";
        } else if (expression.isAssignExpr()) {
            AssignExpr assignExpr = expression.asAssignExpr();
            return assignExpr.getTarget().toString() + " = " + convertExpression(assignExpr.getValue());
        } else if (expression.isEnclosedExpr()) {
            return "(" + convertExpression(expression.asEnclosedExpr().getInner()) + ")";
        } else if (expression.isFieldAccessExpr()) {
            FieldAccessExpr fieldAccessExpr = expression.asFieldAccessExpr();
            return convertExpression(fieldAccessExpr.getScope()) + "." + fieldAccessExpr.getNameAsString();
        } else if (expression.isNameExpr()) {
            String name = expression.asNameExpr().getNameAsString();
            return inOutMap.getOrDefault(name, name);
        } else if (expression.isVariableDeclarationExpr()) {
            return expression.asVariableDeclarationExpr().getVariables().stream()
                    .map(variable -> uncapitalize(variable.getTypeAsString().replace("double", "float")) + " " + variable.getNameAsString() +
                            variable.getInitializer().map(ex -> " = " + convertExpression(ex)).orElse(""))
                    .collect(Collectors.joining("; "));
        } else if (expression.isBinaryExpr()) {
            BinaryExpr binaryExpr = expression.asBinaryExpr();
            return convertExpression(binaryExpr.getLeft()) + " " + binaryExpr.getOperator().asString() + " " + convertExpression(binaryExpr.getRight());
        } else if (expression.isDoubleLiteralExpr() || expression.isIntegerLiteralExpr() || expression.isBooleanLiteralExpr()) {
            return expression.toString();
        } else {
            throw new IllegalStateException();
        }
    }

    private String uncapitalize(String word) {
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }
}
