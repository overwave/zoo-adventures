package dev.overtow.core;

import dev.overtow.core.shader.ShaderProgram;
import dev.overtow.core.shader.uniform.Uniform;
import dev.overtow.service.memory.MemoryManager;
import dev.overtow.service.meshlibrary.MeshLibrary;
import dev.overtow.util.injection.Injector;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageCallback;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
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
import static org.lwjgl.opengl.GL43C.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL43C.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL43C.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL43C.GL_LINEAR;
import static org.lwjgl.opengl.GL43C.GL_NONE;
import static org.lwjgl.opengl.GL43C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL43C.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL43C.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL43C.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL43C.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL43C.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL43C.glBindTexture;
import static org.lwjgl.opengl.GL43C.glClear;
import static org.lwjgl.opengl.GL43C.glDrawBuffer;
import static org.lwjgl.opengl.GL43C.glGenTextures;
import static org.lwjgl.opengl.GL43C.glReadBuffer;
import static org.lwjgl.opengl.GL43C.glTexImage2D;
import static org.lwjgl.opengl.GL43C.glTexParameteri;
import static org.lwjgl.opengl.GL43C.glViewport;
import static org.lwjgl.opengl.GL43C.*;

public class Renderer {
    private final GLCapabilities capabilities;
    private final ShaderProgram generalShader;
    private final ShaderProgram depthShader;
    private final MeshLibrary meshLibrary;
    private final Hud hud;
    private final Window window;
    GLDebugMessageCallback debugProc;


    int fbo;
    int depthTexture;
    int shadowMapSize = 2048;

    public Renderer(Window window) {
        capabilities = GL.createCapabilities();
        this.window = window;

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
        generalShader.draw(shader -> {
            shader.set(Uniform.Name.DEPTH_TEXTURE, 0);
            shader.set(Uniform.Name.TEXTURE_SAMPLER, 1);
        });

        createDepthTexture();
        createFbo();
        meshLibrary = Injector.getInstance(MeshLibrary.class);
        meshLibrary.get(Mesh.Id.CUBE);
        meshLibrary.get(Mesh.Id.POOL);

        hud = new Hud(Injector.getInstance(MemoryManager.class), window);
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
        List<Actor> actors = scene.getActors();

        drawDepthMap(actors, scene);

        drawScene(actors, scene);

        drawHud(window);
    }

    private void drawDepthMap(List<Actor> actors, Scene scene) {
        depthShader.draw(shader -> {
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

    private void drawScene(List<Actor> actors, Scene scene) {
        Vector3f cameraPosition = new Vector3f(0f, 27, 00f);
        Vector3f cameraRotation = new Vector3f(90, 0, 0);
        Matrix4f camera = new Matrix4f();
        camera.setPerspective((float) Math.toRadians(45.0f), (float) 1600 / 900, 0.1f, 50.0f)
                .rotateX((float) Math.toRadians(cameraRotation.x))
                .rotateY((float) Math.toRadians(cameraRotation.y))
                .translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z);
        Matrix4f biasMatrix = new Matrix4f(
                0.5f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f
        );

        generalShader.draw(shader -> {
            shader.set(VIEW_PROJECTION_MATRIX, camera);
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


                Matrix4f matrix4f = new Matrix4f().translationRotateScale(
                        actor.getPosition(),
                        actor.getRotation(),
                        new Vector3f(actor.getScale()));
                shader.set(MODEL_MATRIX, matrix4f);
                glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
            }


            glBindVertexArray(0);

            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, 0);

            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, 0);
        });
    }

    private void drawHud(Window window) {
        hud.render();

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_STENCIL_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
    }
//    private static final int MAX_POINT_LIGHTS = 5;
//
//    private static final int MAX_SPOT_LIGHTS = 5;
//
//    private final Transformation transformation;
//
//    private final ShadowRenderer shadowRenderer;
//
//    private ShaderProgram sceneShaderProgram;
//
//    private ShaderProgram skyBoxShaderProgram;
//
//    private ShaderProgram particlesShaderProgram;
//
//    private final float specularPower;
//
//    private final FrustumCullingFilter frustumFilter;
//
//    private final List<GameItem> filteredItems;
//
//    public Renderer() {
//        transformation = new Transformation();
//        specularPower = 10f;
//        shadowRenderer = new ShadowRenderer();
//        frustumFilter = new FrustumCullingFilter();
//        filteredItems = new ArrayList<>();
//    }
//
//    public void init(Window window) throws Exception {
//        shadowRenderer.init(window);
//        setupSkyBoxShader();
//        setupSceneShader();
//        setupParticlesShader();
//    }
//
//    public void render(Window window, Camera camera, Scene scene, boolean sceneChanged) {
//        clear();
//
//        frustumFilter.updateFrustum(window.getProjectionMatrix(), camera.getViewMatrix());
//        frustumFilter.filter(scene.getGameMeshes());
//
//        // Render depth map before view ports has been set up
//        if (scene.isRenderShadows() && sceneChanged) {
//            shadowRenderer.render(window, scene, camera, transformation, this);
//        }
//
//        glViewport(0, 0, window.getWidth(), window.getHeight());
//
//        // Update projection matrix once per render cycle
//        window.updateProjectionMatrix();
//
//        renderScene(window, camera, scene);
////        renderParticles(window, camera, scene);
//
//        //renderAxes(camera);
//        //renderCrossHair(window);
//    }
//
//    private void setupParticlesShader() throws Exception {
//        particlesShaderProgram = new ShaderProgram();
//        particlesShaderProgram.createVertexShader(Utils.loadFile("data/shader/particles_vertex.vs"));
//        particlesShaderProgram.createFragmentShader(Utils.loadFile("data/shader/particles_fragment.fs"));
//        particlesShaderProgram.link();
//
//        particlesShaderProgram.createUniform("viewMatrix");
//        particlesShaderProgram.createUniform("projectionMatrix");
//        particlesShaderProgram.createUniform("texture_sampler");
//
//        particlesShaderProgram.createUniform("numCols");
//        particlesShaderProgram.createUniform("numRows");
//    }
//
//    private void setupSkyBoxShader() throws Exception {
//        skyBoxShaderProgram = new ShaderProgram();
//        skyBoxShaderProgram.createVertexShader(Utils.loadFile("data/shader/sb_vertex.vs"));
//        skyBoxShaderProgram.createFragmentShader(Utils.loadFile("data/shader/sb_fragment.fs"));
//        skyBoxShaderProgram.link();
//
//        // Create uniforms for projection matrix
//        skyBoxShaderProgram.createUniform("projectionMatrix");
//        skyBoxShaderProgram.createUniform("modelViewMatrix");
//        skyBoxShaderProgram.createUniform("texture_sampler");
//        skyBoxShaderProgram.createUniform("ambientLight");
//        skyBoxShaderProgram.createUniform("colour");
//        skyBoxShaderProgram.createUniform("hasTexture");
//    }
//
//    private void setupSceneShader() throws Exception {
//        // Create shader
//        sceneShaderProgram = new ShaderProgram();
//        sceneShaderProgram.createVertexShader(Utils.loadFile("data/shader/scene_vertex.vs"));
//        sceneShaderProgram.createFragmentShader(Utils.loadFile("data/shader/scene_fragment.fs"));
//        sceneShaderProgram.link();
//
//        // Create uniforms for view and projection matrices
//        sceneShaderProgram.createUniform("viewMatrix");
//        sceneShaderProgram.createUniform("projectionMatrix");
//        sceneShaderProgram.createUniform("texture_sampler");
//        sceneShaderProgram.createUniform("normalMap");
//        // Create uniform for material
//        sceneShaderProgram.createMaterialUniform("material");
//        // Create lighting related uniforms
//        sceneShaderProgram.createUniform("specularPower");
//        sceneShaderProgram.createUniform("ambientLight");
//        sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
//        sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
//        sceneShaderProgram.createDirectionalLightUniform("directionalLight");
//        sceneShaderProgram.createFogUniform("fog");
//
//        // Create uniforms for shadow mapping
//        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
//            sceneShaderProgram.createUniform("shadowMap_" + i);
//        }
//        sceneShaderProgram.createUniform("orthoProjectionMatrix", ShadowRenderer.NUM_CASCADES);
//        sceneShaderProgram.createUniform("modelNonInstancedMatrix");
//        sceneShaderProgram.createUniform("lightViewMatrix", ShadowRenderer.NUM_CASCADES);
//        sceneShaderProgram.createUniform("cascadeFarPlanes", ShadowRenderer.NUM_CASCADES);
//        sceneShaderProgram.createUniform("renderShadow");
//
//        // Create uniform for joint matrices
//        sceneShaderProgram.createUniform("jointsMatrix");
//
//        sceneShaderProgram.createUniform("isInstanced");
//        sceneShaderProgram.createUniform("numCols");
//        sceneShaderProgram.createUniform("numRows");
//
//        sceneShaderProgram.createUniform("selectedNonInstanced");
//    }
//
//    public void clear() {
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
//    }
//
//    private void renderParticles(Window window, Camera camera, Scene scene) {
//        particlesShaderProgram.bind();
//
//        Matrix4f viewMatrix = camera.getViewMatrix();
//        particlesShaderProgram.setUniform("viewMatrix", viewMatrix);
//        particlesShaderProgram.setUniform("texture_sampler", 0);
//        Matrix4f projectionMatrix = window.getProjectionMatrix();
//        particlesShaderProgram.setUniform("projectionMatrix", projectionMatrix);
//
//        IParticleEmitter[] emitters = scene.getParticleEmitters();
//        int numEmitters = emitters != null ? emitters.length : 0;
//
//        glDepthMask(false);
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE);
//
//        for (int i = 0; i < numEmitters; i++) {
//            IParticleEmitter emitter = emitters[i];
//            Mesh mesh = emitter.getBaseParticle().getMesh();
//
//            Texture text = mesh.getMaterial().getTexture();
//            particlesShaderProgram.setUniform("numCols", text.getNumCols());
//            particlesShaderProgram.setUniform("numRows", text.getNumRows());
//
////            mesh.renderList(emitter.getParticles(), true, transformation, viewMatrix);
//        }
//
//        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
//        glDepthMask(true);
//
//        particlesShaderProgram.unbind();
//    }
//
//    public void renderScene(Window window, Camera camera, Scene scene) {
//        sceneShaderProgram.bind();
//
//        Matrix4f viewMatrix = camera.getViewMatrix();
//        Matrix4f projectionMatrix = window.getProjectionMatrix();
//        sceneShaderProgram.setUniform("viewMatrix", viewMatrix);
//        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);
//
//        List<ShadowCascade> shadowCascades = shadowRenderer.getShadowCascades();
//        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
//            ShadowCascade shadowCascade = shadowCascades.get(i);
//            sceneShaderProgram.setUniform("orthoProjectionMatrix", shadowCascade.getOrthoProjMatrix(), i);
//            sceneShaderProgram.setUniform("cascadeFarPlanes", ShadowRenderer.CASCADE_SPLITS[i], i);
//            sceneShaderProgram.setUniform("lightViewMatrix", shadowCascade.getLightViewMatrix(), i);
//        }
//
//        SceneLight sceneLight = scene.getSceneLight();
//        renderLights(viewMatrix, sceneLight);
//
//        sceneShaderProgram.setUniform("texture_sampler", 0);
//        sceneShaderProgram.setUniform("normalMap", 1);
//        int start = 2;
//        for (int i = 0; i < ShadowRenderer.NUM_CASCADES; i++) {
//            sceneShaderProgram.setUniform("shadowMap_" + i, start + i);
//        }
//        sceneShaderProgram.setUniform("renderShadow", scene.isRenderShadows() ? 1 : 0);
//
//        renderNonInstancedMeshes(scene);
//
//        sceneShaderProgram.unbind();
//    }
//
//    private void renderNonInstancedMeshes(Scene scene) {
//        sceneShaderProgram.setUniform("isInstanced", 0);
//
//        // Render each mesh with the associated game Items
//        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
//        for (Mesh mesh : mapMeshes.keySet()) {
//            sceneShaderProgram.setUniform("material", mesh.getMaterial());
//
//            Texture text = mesh.getMaterial().getTexture();
//            if (text != null) {
//                sceneShaderProgram.setUniform("numCols", text.getNumCols());
//                sceneShaderProgram.setUniform("numRows", text.getNumRows());
//            }
//
//            shadowRenderer.bindTextures(GL_TEXTURE2);
//
//            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
//                sceneShaderProgram.setUniform("selectedNonInstanced", gameItem.isSelected() ? 1.0f : 0.0f);
//                Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
//                sceneShaderProgram.setUniform("modelNonInstancedMatrix", modelMatrix);
//                    }
//            );
//        }
//    }
//
//    private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {
//
//        sceneShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
//        sceneShaderProgram.setUniform("specularPower", specularPower);
//
//        // Process Point Lights
//        PointLight[] pointLightList = sceneLight.getPointLightList();
//        int numLights = pointLightList != null ? pointLightList.length : 0;
//        for (int i = 0; i < numLights; i++) {
//            // Get a copy of the point light object and transform its position to view coordinates
//            PointLight currPointLight = new PointLight(pointLightList[i]);
//            Vector3f lightPos = currPointLight.getPosition();
//            Vector4f aux = new Vector4f(lightPos, 1);
//            aux.mul(viewMatrix);
//            lightPos.x = aux.x;
//            lightPos.y = aux.y;
//            lightPos.z = aux.z;
//            sceneShaderProgram.setUniform("pointLights", currPointLight, i);
//        }
//
//        // Process Spot Ligths
//        SpotLight[] spotLightList = sceneLight.getSpotLightList();
//        numLights = spotLightList != null ? spotLightList.length : 0;
//        for (int i = 0; i < numLights; i++) {
//            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
//            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
//            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
//            dir.mul(viewMatrix);
//            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));
//
//            Vector3f lightPos = currSpotLight.getPointLight().getPosition();
//            Vector4f aux = new Vector4f(lightPos, 1);
//            aux.mul(viewMatrix);
//            lightPos.x = aux.x;
//            lightPos.y = aux.y;
//            lightPos.z = aux.z;
//
//            sceneShaderProgram.setUniform("spotLights", currSpotLight, i);
//        }
//
//        // Get a copy of the directional light object and transform its position to view coordinates
//        DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
//        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
//        dir.mul(viewMatrix);
//        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
//        sceneShaderProgram.setUniform("directionalLight", currDirLight);
//    }
//
//
//    public void cleanup() {
//        if (shadowRenderer != null) {
//            shadowRenderer.cleanup();
//        }
//        if (skyBoxShaderProgram != null) {
//            skyBoxShaderProgram.cleanup();
//        }
//        if (sceneShaderProgram != null) {
//            sceneShaderProgram.cleanup();
//        }
//        if (particlesShaderProgram != null) {
//            particlesShaderProgram.cleanup();
//        }
//    }
}