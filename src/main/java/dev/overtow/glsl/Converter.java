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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        convertMethods(compilationUnit, inOutMap);
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

    private void convertMethods(CompilationUnit compilationUnit, Map<String, String> inOutMap) {
        new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration md, Void arg) {
                printMethodSignature(md);
                printMethodBody(md, inOutMap);
                System.out.println("}");
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

        System.out.printf("%s %s(%s) {%n", returnType, name, args);
    }

    private void printMethodBody(MethodDeclaration md, Map<String, String> inOutMap) {
        BlockStmt methodBody = md.getBody().orElseThrow();
        for (Statement statement : methodBody.getStatements()) {
            System.out.println(convertStatement(statement, inOutMap));
        }
    }

    private String convertStatement(Statement statement, Map<String, String> inOutMap) {
        if (statement == null) {
            return "";
        }
        if (statement.isReturnStmt()) {
            ReturnStmt returnStmt = statement.asReturnStmt();
            return String.format("return %s;", convertExpression(returnStmt.getExpression().orElse(null), inOutMap));
        } else if (statement.isExpressionStmt()) {
            ExpressionStmt expressionStmt = statement.asExpressionStmt();
            return convertExpression(expressionStmt.getExpression(), inOutMap) + ";";
        } else if (statement.isIfStmt()) {
            IfStmt ifStmt = statement.asIfStmt();

            String clause = convertExpression(ifStmt.getCondition(), inOutMap);
            String body = convertStatement(ifStmt.getThenStmt(), inOutMap);

            String code = String.format("if (%s) %s", clause, body);
            code += ifStmt.getElseStmt().map(st -> String.format(" else %s", convertStatement(st, inOutMap))).orElse("");

            return code;
        } else if (statement.isBlockStmt()) {
            BlockStmt blockStmt = statement.asBlockStmt();

            String body = blockStmt.getStatements().stream()
                    .map(st -> convertStatement(st, inOutMap))
                    .collect(Collectors.joining(";\n"));
            return String.format("{%n%s%n}", body);
        } else {
            System.out.println("!!!");
        }

        throw new IllegalStateException();
    }

    private String convertExpression(Expression expression, Map<String, String> inOutMap) {
        if (expression == null) {
            return "";
        }

        if (expression.isMethodCallExpr()) {
            MethodCallExpr callExpr = expression.asMethodCallExpr();

            String methodName = callExpr.getNameAsString();
            if (methodName.equals("multiply")) {
                return convertExpression(new BinaryExpr(callExpr.getScope().orElseThrow(), callExpr.getArgument(0), BinaryExpr.Operator.MULTIPLY), inOutMap);
            }
            if (methodName.equals("divide")) {
                return convertExpression(new BinaryExpr(callExpr.getScope().orElseThrow(), callExpr.getArgument(0), BinaryExpr.Operator.DIVIDE), inOutMap);
            }
            if (methodName.equals("plus")) {
                return convertExpression(new BinaryExpr(callExpr.getScope().orElseThrow(), callExpr.getArgument(0), BinaryExpr.Operator.PLUS), inOutMap);
            }
            if (methodName.equals("minus")) {
                return convertExpression(new BinaryExpr(callExpr.getScope().orElseThrow(), callExpr.getArgument(0), BinaryExpr.Operator.MINUS), inOutMap);
            }

            String arguments = callExpr.getArguments().stream()
                    .map(a -> convertExpression(a, inOutMap))
                    .collect(Collectors.joining(", "));
            return callExpr.getNameAsString() + "(" + arguments + ")";
        } else if (expression.isAssignExpr()) {
            AssignExpr assignExpr = expression.asAssignExpr();
            return assignExpr.getTarget().toString() + " = " + convertExpression(assignExpr.getValue(), inOutMap);
        } else if (expression.isEnclosedExpr()) {
            return "(" + convertExpression(expression.asEnclosedExpr().getInner(), inOutMap) + ")";
        } else if (expression.isFieldAccessExpr()) {
            FieldAccessExpr fieldAccessExpr = expression.asFieldAccessExpr();
            return convertExpression(fieldAccessExpr.getScope(), inOutMap) + "." + fieldAccessExpr.getNameAsString();
        } else if (expression.isNameExpr()) {
            String name = expression.asNameExpr().getNameAsString();
            return inOutMap.getOrDefault(name, name);
        } else if (expression.isVariableDeclarationExpr()) {
            return expression.asVariableDeclarationExpr().getVariables().stream()
                    .map(variable -> uncapitalize(variable.getTypeAsString().replace("double", "float")) + " " + variable.getNameAsString() +
                            variable.getInitializer().map(ex -> " = " + convertExpression(ex, inOutMap)).orElse(""))
                    .collect(Collectors.joining("; "));
        } else if (expression.isBinaryExpr()) {
            BinaryExpr binaryExpr = expression.asBinaryExpr();
            return convertExpression(binaryExpr.getLeft(), inOutMap) + " " + binaryExpr.getOperator().asString() + " " + convertExpression(binaryExpr.getRight(), inOutMap);
        } else if (expression.isDoubleLiteralExpr() || expression.isIntegerLiteralExpr()) {
            return expression.toString();
        } else {
            System.out.println("!!!");
        }
        return expression.toString();
    }

    private String uncapitalize(String word) {
        return word.substring(0, 1).toLowerCase() + word.substring(1);
    }
}
