package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.Uniform;
import dev.overtow.math.Matrix;
import dev.overtow.math.Vector3;
import dev.overtow.service.meshlibrary.MeshLibrary;
import dev.overtow.service.settings.Settings;
import dev.overtow.util.injection.Injector;
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
    private final ShaderProgram depthShader;
    private final MeshLibrary meshLibrary;
    private final HudRenderer hudRenderer;
//    GLDebugMessageCallback debugProc;


    private int fbo;
    private int depthTexture;
    private int shadowMapSize = 2048;
    private final Vector3 shadowsAntialiasingLevel;
    private final Matrix biasMatrix;

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

        createDepthTexture();
        createFbo();
        meshLibrary = Injector.getInstance(MeshLibrary.class);
        meshLibrary.get(Mesh.Id.CUBE_TOWER);
        meshLibrary.get(Mesh.Id.POOL);

        shadowsAntialiasingLevel = calculateShadowsAntialiasingLevel();

        hudRenderer = new HudRenderer();

        biasMatrix = Matrix.of(
                0.5f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f
        );
    }

    private Vector3 calculateShadowsAntialiasingLevel() {
        Settings settings = Injector.getInstance(Settings.class);
        int level = settings.getShadowsAntialiasingLevel();
        return Vector3.of(
                Math.floorDiv(level, -2) + 1,   // sampling offset, from
                Math.floorDiv(level, 2) + 1,    // sampling offset, to, exclusive
                level * level                   // number of samples
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

        Matrix viewProjectionMatrix = scene.getViewProjectionMatrix();

        List<Actor> usualActors = scene.getUsualActors();
        drawDepthMap(usualActors, scene);
        drawScene(viewProjectionMatrix, usualActors, scene);

        hudRenderer.render(scene.getHudElements());
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
            shader.set(VIEW_PROJECTION_MATRIX, scene.getLightViewProjectionMatrix());

            glActiveTexture(GL_TEXTURE0);
            glBindFramebuffer(GL_FRAMEBUFFER, fbo);
            glBindTexture(GL_TEXTURE_2D, depthTexture);
            glViewport(0, 0, shadowMapSize, shadowMapSize);
            glClear(GL_DEPTH_BUFFER_BIT);


            for (Actor actor : actors) {
                Mesh mesh = meshLibrary.get(actor.getMeshId());
                glBindVertexArray(mesh.getVaoId());

                Matrix modelMatrix = Matrix.ofModel(actor.getPosition(), actor.getRotation(), actor.getScale());
                shader.set(MODEL_MATRIX, modelMatrix);
                glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
            }

            glBindVertexArray(0);
            glBindFramebuffer(GL_FRAMEBUFFER, 0);
        });
    }

    private void drawScene(Matrix viewProjectionMatrix, List<Actor> actors, Scene scene) {
        generalShader.executeWithProgram(shader -> {
            shader.set(VIEW_PROJECTION_MATRIX, viewProjectionMatrix);
            shader.set(LIGHT_VIEW_PROJECTION_MATRIX, scene.getLightViewProjectionMatrix());
            shader.set(BIAS_MATRIX, biasMatrix);
            shader.set(LIGHT_POSITION, scene.getLightPosition());
            shader.set(SHADOWS_ANTIALIASING_LEVEL, shadowsAntialiasingLevel);

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
                shader.set(ITEM_SELECTED, actor.isSelected() ? 1f : 0f);

                Matrix modelMatrix = Matrix.ofModel(actor.getPosition(), actor.getRotation(), actor.getScale());
                shader.set(MODEL_MATRIX, modelMatrix);
                Matrix normalMatrix = modelMatrix.normal();
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
}
