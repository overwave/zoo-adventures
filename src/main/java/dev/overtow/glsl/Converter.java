package dev.overtow.glsl;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import dev.overtow.core.shader.uniform.Uniform.Name;
import dev.overtow.glsl.shader.Shader;
import dev.overtow.glsl.type.Mat4;
import dev.overtow.glsl.type.Sampler2D;
import dev.overtow.glsl.type.Vec2;
import dev.overtow.glsl.type.Vec3;
import dev.overtow.glsl.type.Vec4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class Converter {
    private static final Set<Class<?>> GLSL_TYPE_TOKENS = Set.of(float.class, Vec2.class, Vec3.class, Vec4.class, Mat4.class, Sampler2D.class);
    private static final Set<String> FLOAT_TYPE = Set.of("double", "float");
    private static final Set<String> GLSL_COMPLEX_TYPES = Set.of("Vec2", "Vec3", "Vec4", "Mat4", "Sampler2D");

    private final PrintStream os;
    private final Map<String, String> shaderInOutMap;
    private final Map<String, Class<?>> usedStructs;

    private Converter(PrintStream os) {
        this.os = os;
        this.shaderInOutMap = new HashMap<>();
        this.usedStructs = new HashMap<>();
    }

    public static List<Name> getUsedUniforms(List<Class<? extends Shader>> shaderClasses) {
        return shaderClasses.stream()
                .map(Class::getDeclaredFields)
                .flatMap(Arrays::stream)
                .filter(field -> field.isAnnotationPresent(Uniform.class))
                .map(field -> field.getAnnotation(Uniform.class).value())
                .collect(Collectors.toList());
    }

    public static String convert(Class<? extends Shader> clazz) {
        return Converter.convertToGlsl(clazz);
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
            converter.doConvertToGlsl(compilationUnit, clazz);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return filename;
    }

    private static File classToFile(Class<?> clazz) {
        return new File("src\\main\\java\\" + clazz.getCanonicalName().replace('.', '\\') + ".java");
    }

    public static Map<String, Class<?>> getUsedStructs(Class<? extends Shader> shaderClass) {
        Map<String, Class<?>> resultMap = new HashMap<>();

        for (Field field : shaderClass.getDeclaredFields()) {
            Class<?> fieldType = field.getType();
            if (fieldType.isArray()) {
                fieldType = fieldType.componentType();
            }

            if (Shader.class.isAssignableFrom(fieldType) ||     // link to parent shader
                    Modifier.isStatic(field.getModifiers()) ||  // static definitions
                    GLSL_TYPE_TOKENS.contains(fieldType)) {     // glsl types
                continue;
            }

            resultMap.put(fieldType.getSimpleName(), fieldType);
        }
        return resultMap;
    }

    private void doConvertToGlsl(CompilationUnit compilationUnit, Class<? extends Shader> clazz) {
        os.println("#version 330");

        convertStructs(clazz);
        convertFields(compilationUnit);
        convertMethods(compilationUnit);
    }

    private void convertStructs(Class<? extends Shader> clazz) {
        usedStructs.putAll(getUsedStructs(clazz));

        for (Map.Entry<String, Class<?>> entry : usedStructs.entrySet()) {
            Class<?> struct = entry.getValue();
            os.println("struct " + struct.getSimpleName() + " {");

            for (Field field : struct.getDeclaredFields()) {
                Class<?> fieldType = field.getType();
                String typeName = javaTypeToGlslType(fieldType.getSimpleName());
                os.println(typeName + " " + field.getName() + ";");
            }

            os.println("};");
        }
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

        List<String> packages = new ArrayList<>();
        new VoidVisitorAdapter<List<String>>() {
            @Override
            public void visit(PackageDeclaration pd, List<String> accumulator) {
                accumulator.add(pd.getNameAsString());
            }
        }.visit(compilationUnit, packages);

        new VoidVisitorAdapter<Map<String, String>>() {
            @Override
            public void visit(FieldDeclaration fd, Map<String, String> inOutMap) {

                boolean isDefinition = fd.hasModifier(com.github.javaparser.ast.Modifier.Keyword.FINAL) && fd.hasModifier(com.github.javaparser.ast.Modifier.Keyword.STATIC);
                if (isDefinition) {
                    VariableDeclarator variable = fd.getVariable(0);
                    String variableName = variable.getNameAsString();
                    String initializer = variable.getInitializer().orElseThrow().toString();

                    os.println("#define " + variableName + " " + initializer);
                    return;
                }

                boolean isParentShaderLink = isParentShaderLink(fd.getVariable(0), packages);
                if (isParentShaderLink) {
                    hasParentShader[0] = true;
                    return;
                }

                boolean isUniform = fd.getAnnotationByClass(Uniform.class).isPresent();
                if (isUniform) {
                    String type = javaTypeToGlslType(fd.getVariable(0).getTypeAsString());
                    Optional<AnnotationExpr> arrayAnnotation = fd.getAnnotationByClass(Array.class);

                    String arrayQualifier = arrayAnnotation.isEmpty() ? "" :
                            "[" + convertExpression(arrayAnnotation.get().asSingleMemberAnnotationExpr().getMemberValue()) + "]";

                    SingleMemberAnnotationExpr annotationExpr = fd.getAnnotationByClass(Uniform.class)
                            .orElseThrow()
                            .asSingleMemberAnnotationExpr();

                    Name name = Name.valueOf(annotationExpr.getMemberValue().toString());

                    os.printf("uniform %s %s;%n", type + arrayQualifier, name.get());
                    return;
                }

                boolean isInput = fd.getAnnotationByClass(Input.class).isPresent();
                if (isInput) {
                    VariableDeclarator variable = fd.getVariable(0);

                    String type = javaTypeToGlslType(variable.getTypeAsString());

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

                    String type = javaTypeToGlslType(variable.getTypeAsString());
                    String name = variable.getNameAsString();

                    os.printf("out %s %s;%n", type, name);
                    return;
                }

                throw new IllegalArgumentException();
            }
        }.visit(compilationUnit, shaderInOutMap);
    }

    private boolean isParentShaderLink(VariableDeclarator variable, List<String> packages) {
        String shaderRootPackage = Shader.class.getPackageName();
        List<String> shaderLookUpPackages = new ArrayList<>(packages);
        shaderLookUpPackages.add(shaderRootPackage);

        for (String lookUpPackage : shaderLookUpPackages) {
            try {
                Class<?> clazz = Class.forName(lookUpPackage + "." + variable.getTypeAsString());
                if (Shader.class.isAssignableFrom(clazz)) {
                    return true;
                }
            } catch (ClassNotFoundException ignored) {
            }
        }
        return false;
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
        } else if (type.isClassOrInterfaceType()) {
            returnType = javaTypeToGlslType(type.asString());
        } else {
            throw new IllegalArgumentException();
        }

        String name = md.getNameAsString();
        String args = md.getParameters()
                .stream()
                .map(par -> javaTypeToGlslType(par.getTypeAsString()) + " " + par.getNameAsString())
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
        } else if (statement.isForStmt()) {
            ForStmt forStmt = statement.asForStmt();

            String initialization = forStmt.getInitialization().stream()
                    .map(this::convertExpression)
                    .collect(Collectors.joining(", "));
            String compare = forStmt.getCompare()
                    .map(this::convertExpression)
                    .orElse("");
            String update = forStmt.getUpdate().stream()
                    .map(this::convertExpression)
                    .collect(Collectors.joining(", "));
            String body = convertStatement(forStmt.getBody());

            return String.format("for(%s; %s; %s) %s", initialization, compare, update, body);
        }

        throw new IllegalStateException("unexpected statement: " + statement);
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
            return assignExpr.getTarget().toString() + " " + assignExpr.getOperator().asString() + " " + convertExpression(assignExpr.getValue());
        } else if (expression.isEnclosedExpr()) {
            return "(" + convertExpression(expression.asEnclosedExpr().getInner()) + ")";
        } else if (expression.isFieldAccessExpr()) {
            FieldAccessExpr fieldAccessExpr = expression.asFieldAccessExpr();
            return convertExpression(fieldAccessExpr.getScope()) + "." + fieldAccessExpr.getNameAsString();
        } else if (expression.isNameExpr()) {
            String name = expression.asNameExpr().getNameAsString();
            return shaderInOutMap.getOrDefault(name, name);
        } else if (expression.isVariableDeclarationExpr()) {
            return expression.asVariableDeclarationExpr().getVariables().stream()
                    .map(variable -> javaTypeToGlslType(variable.getTypeAsString()) + " " + variable.getNameAsString() +
                            variable.getInitializer().map(ex -> " = " + convertExpression(ex)).orElse(""))
                    .collect(Collectors.joining("; "));
        } else if (expression.isBinaryExpr()) {
            BinaryExpr binaryExpr = expression.asBinaryExpr();
            return "(" + convertExpression(binaryExpr.getLeft()) + " " + binaryExpr.getOperator().asString() + " " + convertExpression(binaryExpr.getRight()) + ")";
        } else if (expression.isDoubleLiteralExpr() || expression.isIntegerLiteralExpr() || expression.isBooleanLiteralExpr()) {
            return expression.toString();
        } else if (expression.isUnaryExpr()) {
            UnaryExpr unaryExpr = expression.asUnaryExpr();
            String nestedExpr = convertExpression(unaryExpr.getExpression());
            String operator = unaryExpr.getOperator().asString();

            if (unaryExpr.isPostfix()) {
                return nestedExpr + operator;
            } else {
                return operator + nestedExpr;
            }
        } else if (expression.isArrayAccessExpr()) {
            ArrayAccessExpr arrAccExpr = expression.asArrayAccessExpr();
            return "%s[%s]".formatted(convertExpression(arrAccExpr.getName()), convertExpression(arrAccExpr.getIndex()));
        } else {
            throw new IllegalStateException();
        }
    }

    private String javaTypeToGlslType(String type) {
        type = type.replace("[]", "");

        if (FLOAT_TYPE.contains(type)) {
            return "float";
        } else if ("int".equals(type)) {
            return "int";
        } else if (GLSL_COMPLEX_TYPES.contains(type)) {
            return type.substring(0, 1).toLowerCase() + type.substring(1);
        } else if (usedStructs.containsKey(type)) {
            return type;
        } else {
            throw new IllegalArgumentException("Passed illegal type: " + type);
        }
    }
}
