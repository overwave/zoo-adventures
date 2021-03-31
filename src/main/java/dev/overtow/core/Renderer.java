package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.Uniform;
import dev.overtow.service.meshlibrary.MeshLibrary;
import dev.overtow.util.Utils;
import dev.overtow.util.injection.Injector;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.nio.ByteBuffer;
import java.util.List;

import static dev.overtow.core.shader.uniform.Uniform.Name.*;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL11C.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL13.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL43C.*;

public class Renderer {
    private final GLCapabilities capabilities;
    private final ShaderProgram generalShader;
    private final ShaderProgram waterShader;
    private final ShaderProgram depthShader;
    private final MeshLibrary meshLibrary;
    private final HudRenderer hudRenderer;
//    GLDebugMessageCallback debugProc;


    int fbo;
    int depthTexture;
    int shadowMapSize = 2048;
    private final Vector3f cameraPosition;
    private final Vector3f cameraRotation;
    private final Matrix4f biasMatrix;

    public Renderer() {
        capabilities = GL.createCapabilities();

        // TODO CREATE PROPER LOGGER
//        if (capabilities.OpenGL43) {
//            apiLog("[GL] Using OpenGL 4.3 for error logging.");
//            debugProc = GLDebugMessageCallback.create((source, type, id, severity, length, message, userParam) -> {
//                if (severity == GL_DEBUG_SEVERITY_NOTIFICATION) {
//                    return;
//                }
//                System.out.println("[LWJGL] OpenGL debug message");
//                printDetail(System.out, "ID", String.format("0x%X", id));
//                printDetail(System.out, "Source", getDebugSource(source));
//                printDetail(System.out, "Type", getDebugType(type));
//                printDetail(System.out, "Severity", getDebugSeverity(severity));
//                printDetail(System.out, "Message", GLDebugMessageCallback.getMessage(length, message));
//            });
//            glDebugMessageCallback(debugProc, NULL);
//            if ((glGetInteger(GL_CONTEXT_FLAGS) & GL_CONTEXT_FLAG_DEBUG_BIT) == 0) {
//                apiLog("[GL] Warning: A non-debug context may not produce any debug output.");
//                glEnable(GL_DEBUG_OUTPUT);
//            }
//        } else {
//            throw new RuntimeException("opengl 4.3!");
//        }

        glEnable(GL_CULL_FACE);
        glEnable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.2f, 0.3f, 0.4f, 1.0f);

        depthShader = new DepthShaderProgram();
        generalShader = new GeneralShaderProgram();
        generalShader.executeWithProgram(shader -> {
            shader.set(Uniform.Name.DEPTH_TEXTURE, 0);
            shader.set(Uniform.Name.TEXTURE_SAMPLER, 1);
        });
        waterShader = new WaterShaderProgram();
        waterShader.executeWithProgram(shader -> shader.set(Uniform.Name.TEXTURE_SAMPLER, 1));

        createDepthTexture();
        createFbo();
        meshLibrary = Injector.getInstance(MeshLibrary.class);
        meshLibrary.get(Mesh.Id.CUBE_TOWER);
        meshLibrary.get(Mesh.Id.POOL);

        hudRenderer = new HudRenderer();

        cameraPosition = new Vector3f(0f, 5, 10);
        cameraRotation = new Vector3f(15, 0, 0);
//        cameraPosition = new Vector3f(0, 21, 0);
//        cameraRotation = new Vector3f(90, 0, 0);
        biasMatrix = new Matrix4f(
                0.5f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f
        );
    }

    void createDepthTexture() {
        depthTexture = glGenTextures();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, shadowMapSize, shadowMapSize, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE,
                (ByteBuffer) null);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    void createFbo() {
        fbo = glGenFramebuffers();
        glActiveTexture(GL_TEXTURE0);
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glDrawBuffer(GL_NONE);
        glReadBuffer(GL_NONE);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);
        int fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw new RuntimeException("Could not create FBO: " + fboStatus);
        }
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void render(Scene scene) {
        restoreState();

        Matrix4f viewProjectionMatrix = createViewProjectionMatrix(scene);

        List<Actor> usualActors = scene.getUsualActors();
        drawDepthMap(usualActors, scene);
        drawScene(viewProjectionMatrix, usualActors, scene);

        List<WaterActor> waterActors = scene.getWater();
        drawWater(viewProjectionMatrix, waterActors, scene);

        hudRenderer.render(scene.getHudElements());
    }

    private Matrix4f createViewProjectionMatrix(Scene scene) {
        Matrix4f viewProjectionMatrix = new Matrix4f();
        viewProjectionMatrix.setPerspective((float) Math.toRadians(45.0f), (float) 1600 / 900, 0.1f, 50.0f)
                .rotateX((float) Math.toRadians(cameraRotation.x))
                .rotateY((float) Math.toRadians(cameraRotation.y))
                .translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
//        viewProjectionMatrix = new Matrix4f().setTranslation(0.3f, 0, 0).mul(viewProjectionMatrix);

//        Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);
//        viewProjectionMatrix.setPerspective((float) Math.toRadians(45), 1.0f, 0.1f, 40.0f)
//                .lookAt(scene.getLightPosition(), new Vector3f(0), UP);
        return viewProjectionMatrix;
    }

    private void restoreState() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }

    private void drawDepthMap(List<Actor> actors, Scene scene) {
        depthShader.executeWithProgram(shader -> {
            shader.set(VIEW_PROJECTION_MATRIX, scene.getLight());

            glActiveTexture(GL_TEXTURE0);
            glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glBindTexture(GL_TEXTURE_2D, depthTexture);
            glViewport(0, 0, shadowMapSize, shadowMapSize);
            glClear(GL_DEPTH_BUFFER_BIT);


            for (Actor actor : actors) {
                Mesh mesh = meshLibrary.get(actor.getMeshId());
                glBindVertexArray(mesh.getVaoId());

                Matrix4f matrix4f = new Matrix4f().translationRotateScale(
                        actor.getPosition(),
                        actor.getRotation(),
                        new Vector3f(actor.getScale()));
                shader.set(MODEL_MATRIX, matrix4f);
                glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
            }

            glBindVertexArray(0);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        });
    }

    private void drawScene(Matrix4f viewProjectionMatrix, List<Actor> actors, Scene scene) {
        generalShader.executeWithProgram(shader -> {
            shader.set(VIEW_PROJECTION_MATRIX, viewProjectionMatrix);
            shader.set(LIGHT_VIEW_PROJECTION_MATRIX, scene.getLight());
            shader.set(BIAS_MATRIX, biasMatrix);
            shader.set(LIGHT_POSITION, scene.getLightPosition());

            glViewport(0, 0, 1600, 900);
            // TODO maybe i can skip color buffer clearance
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);


            for (Actor actor : actors) {
                Mesh mesh = meshLibrary.get(actor.getMeshId());

                glBindVertexArray(mesh.getVaoId());

                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, depthTexture);

                glActiveTexture(GL_TEXTURE1);
                mesh.getMaterial().getTexture().bind();

                shader.set(BACKGROUND_COLOR, actor.getBackgroundColor());

                Matrix4f modelMatrix = new Matrix4f().translationRotateScale(
                        actor.getPosition(),
                        actor.getRotation(),
                        new Vector3f(actor.getScale()));
                shader.set(MODEL_MATRIX, modelMatrix);
                Matrix4f normalMatrix = new Matrix4f(modelMatrix).invert().transpose();
                shader.set(NORMAL_MATRIX, normalMatrix);
                glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
            }


            glBindVertexArray(0);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, 0);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, 0);
        });
    }

    private void drawWater(Matrix4f viewProjectionMatrix, List<WaterActor> actors, Scene scene) {
        waterShader.executeWithProgram(shader -> {
            shader.set(VIEW_PROJECTION_MATRIX, viewProjectionMatrix);
            shader.set(LIGHT_POSITION, scene.getLightPosition());
            shader.set(TIME, Utils.getTime());

            glViewport(0, 0, 1600, 900);

            for (WaterActor actor : actors) {
                shader.setWaves(WAVES, actor.getWaves());
                shader.setRipples(RIPPLES, actor.getRipples());

                Mesh mesh = meshLibrary.get(actor.getMeshId());

                glBindVertexArray(mesh.getVaoId());

                glActiveTexture(GL_TEXTURE1);
                mesh.getMaterial().getTexture().bind();

                shader.set(TEXTURE_MOVING_DIRECTION, actor.getWavesDirection());

                Matrix4f modelMatrix = new Matrix4f().translationRotateScale(
                        actor.getPosition(),
                        actor.getRotation(),
                        new Vector3f(actor.getScale()));
                shader.set(MODEL_MATRIX, modelMatrix);
                Matrix4f normalMatrix = new Matrix4f(modelMatrix).normal();
                shader.set(NORMAL_MATRIX, normalMatrix);
                glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
            }


            glBindVertexArray(0);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, 0);
        });
    }
}
