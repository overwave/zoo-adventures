package dev.overtow.service.renderer;

import dev.overtow.service.window.Window;
import dev.overtow.util.injection.Bind;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjglb.engine.Scene;
import org.lwjglb.engine.SceneLight;
import org.lwjglb.engine.Utils;
import org.lwjglb.engine.graph.*;
import org.lwjglb.engine.graph.anim.AnimGameItem;
import org.lwjglb.engine.graph.anim.AnimatedFrame;
import org.lwjglb.engine.graph.lights.DirectionalLight;
import org.lwjglb.engine.graph.lights.PointLight;
import org.lwjglb.engine.graph.lights.SpotLight;
import org.lwjglb.engine.items.GameItem;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;

@Bind
public class RendererImpl implements Renderer {

    private static final int MAX_POINT_LIGHTS = 5;

    private static final int MAX_SPOT_LIGHTS = 5;

    private final Transformation transformation;

    private ShadowMap shadowMap;

    private ShaderProgram depthShaderProgram;

    private ShaderProgram sceneShaderProgram;

    private final float specularPower;

    public RendererImpl() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init() throws Exception {
        shadowMap = new ShadowMap();

        setupDepthShader();
        setupSceneShader();
    }

    public void render(Window window, Camera camera, Scene scene) {
        clear();

        // Render depth map before view ports has been set up
        renderDepthMap(scene);

        glViewport(0, 0, window.getWidth(), window.getHeight());

        // Update projection matrix once per render cycle
        window.updateProjectionMatrix();

        renderScene(window, camera, scene);
//        renderAxes(window, camera);
        renderCrossHair(window);
    }

    private void setupDepthShader() throws Exception {
        depthShaderProgram = new ShaderProgram();
        depthShaderProgram.createVertexShader(Utils.loadResource("/shaders/depth_vertex.vert"));
        depthShaderProgram.createFragmentShader(Utils.loadResource("/shaders/depth_fragment.frag"));
        depthShaderProgram.link();

        depthShaderProgram.createUniform("isInstanced");
        depthShaderProgram.createUniform("jointsMatrix");
        depthShaderProgram.createUniform("modelLightViewNonInstancedMatrix");
        depthShaderProgram.createUniform("orthoProjectionMatrix");
    }

    private void setupSceneShader() throws Exception {
        // Create shader
        sceneShaderProgram = new ShaderProgram();
        sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/scene_vertex.vert"));
        sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/scene_fragment.frag"));
        sceneShaderProgram.link();

        // Create uniforms for modelView and projection matrices
        sceneShaderProgram.createUniform("projectionMatrix");
        sceneShaderProgram.createUniform("modelViewNonInstancedMatrix");
        sceneShaderProgram.createUniform("texture_sampler");
        sceneShaderProgram.createUniform("normalMap");
        // Create uniform for material
        sceneShaderProgram.createMaterialUniform("material");
        // Create lighting related uniforms
        sceneShaderProgram.createUniform("specularPower");
        sceneShaderProgram.createUniform("ambientLight");
        sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        sceneShaderProgram.createDirectionalLightUniform("directionalLight");

        // Create uniforms for shadow mapping
        sceneShaderProgram.createUniform("shadowMap");
        sceneShaderProgram.createUniform("orthoProjectionMatrix");
        sceneShaderProgram.createUniform("modelLightViewNonInstancedMatrix");

        // Create uniform for joint matrices
        sceneShaderProgram.createUniform("jointsMatrix");

        sceneShaderProgram.createUniform("isInstanced");
        sceneShaderProgram.createUniform("numCols");
        sceneShaderProgram.createUniform("numRows");

        sceneShaderProgram.createUniform("selectedNonInstanced");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    private void renderDepthMap(Scene scene) {
        // Setup view port to match the texture size
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShaderProgram.bind();

        DirectionalLight light = scene.getSceneLight().getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        float lightAngleX = (float) Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float) Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;
        Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

        depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);

        renderNonInstancedMeshes(scene, depthShaderProgram, null, lightViewMatrix);

        // Unbind
        depthShaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void renderScene(Window windowKek, Camera camera, Scene scene) {
        sceneShaderProgram.bind();

        Matrix4f projectionMatrix = windowKek.getProjectionMatrix();
        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
        sceneShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();
        Matrix4f viewMatrix = camera.getViewMatrix();

        SceneLight sceneLight = scene.getSceneLight();
        renderLights(viewMatrix, sceneLight);

        sceneShaderProgram.setUniform("texture_sampler", 0);
        sceneShaderProgram.setUniform("normalMap", 1);
        sceneShaderProgram.setUniform("shadowMap", 2);

        renderNonInstancedMeshes(scene, sceneShaderProgram, viewMatrix, lightViewMatrix);

        sceneShaderProgram.unbind();
    }

    private void renderNonInstancedMeshes(Scene scene, ShaderProgram shader, Matrix4f viewMatrix, Matrix4f lightViewMatrix) {
        sceneShaderProgram.setUniform("isInstanced", 0);

        // Render each mesh with the associated game Items
        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            if (viewMatrix != null) {
                shader.setUniform("material", mesh.getMaterial());
                glActiveTexture(GL_TEXTURE2);
                glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
            }

            Texture text = mesh.getMaterial().getTexture();
            if (text != null) {
                sceneShaderProgram.setUniform("numCols", text.getNumCols());
                sceneShaderProgram.setUniform("numRows", text.getNumRows());
            }

            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
                sceneShaderProgram.setUniform("selectedNonInstanced", gameItem.isSelected() ? 1.0f : 0.0f);
                Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
                if (viewMatrix != null) {
                    Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
                    sceneShaderProgram.setUniform("modelViewNonInstancedMatrix", modelViewMatrix);
                }
                Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(modelMatrix, lightViewMatrix);
                sceneShaderProgram.setUniform("modelLightViewNonInstancedMatrix", modelLightViewMatrix);

                if (gameItem instanceof AnimGameItem) {
                    AnimGameItem animGameItem = (AnimGameItem) gameItem;
                    AnimatedFrame frame = animGameItem.getCurrentFrame();
                    shader.setUniform("jointsMatrix", frame.getJointMatrices());
                }
            }
            );
        }
    }

    private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {

        sceneShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
        sceneShaderProgram.setUniform("specularPower", specularPower);

        // Process Point Lights
        PointLight[] pointLightList = sceneLight.getPointLightList();
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            sceneShaderProgram.setUniform("pointLights", currPointLight, i);
        }

        // Process Spot Ligths
        SpotLight[] spotLightList = sceneLight.getSpotLightList();
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

            Vector3f lightPos = currSpotLight.getPointLight().getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            sceneShaderProgram.setUniform("spotLights", currSpotLight, i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector4f dir = new Vector4f(new Vector3f(6, 10, 4), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        sceneShaderProgram.setUniform("directionalLight", currDirLight);
    }

    private void renderCrossHair(Window windowKek) {
        glPushMatrix();
        glLoadIdentity();

        float inc = 0.05f;
        glLineWidth(2.0f);

        glBegin(GL_LINES);

        glColor3f(1.0f, 1.0f, 1.0f);

        // Horizontal line
        glVertex3f(-inc, 0.0f, 0.0f);
        glVertex3f(+inc, 0.0f, 0.0f);
        glEnd();

        // Vertical line
        glBegin(GL_LINES);
        glVertex3f(0.0f, -inc, 0.0f);
        glVertex3f(0.0f, +inc, 0.0f);
        glEnd();

        glPopMatrix();
    }

    /**
     * Renders the three axis in space (For debugging purposes only
     *
     * @param camera
     */
    private void renderAxes(Window windowKek, Camera camera) {
        glPushMatrix();
        glLoadIdentity();
        float rotX = camera.getRotation().x;
        float rotY = camera.getRotation().y;
        float rotZ = 0;
        glRotatef(rotX, 1.0f, 0.0f, 0.0f);
        glRotatef(rotY, 0.0f, 1.0f, 0.0f);
        glRotatef(rotZ, 0.0f, 0.0f, 1.0f);
        glLineWidth(2.0f);

        glBegin(GL_LINES);
        // X Axis
        glColor3f(1.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(1.0f, 0.0f, 0.0f);
        // Y Axis
        glColor3f(0.0f, 1.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 1.0f, 0.0f);
        // Z Axis
        glColor3f(1.0f, 1.0f, 1.0f);
        glVertex3f(0.0f, 0.0f, 0.0f);
        glVertex3f(0.0f, 0.0f, 1.0f);
        glEnd();

        glPopMatrix();
    }

    public void cleanup() {
        if (shadowMap != null) {
            shadowMap.cleanup();
        }
        if (depthShaderProgram != null) {
            depthShaderProgram.cleanup();
        }
        if (sceneShaderProgram != null) {
            sceneShaderProgram.cleanup();
        }
    }
}
