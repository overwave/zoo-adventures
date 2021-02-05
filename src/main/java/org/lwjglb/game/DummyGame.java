package org.lwjglb.game;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL11;
import org.lwjglb.engine.*;
import org.lwjglb.engine.graph.*;
import org.lwjglb.engine.graph.lights.DirectionalLight;
import org.lwjglb.engine.items.GameItem;
import org.lwjglb.engine.loaders.newloader.StaticMeshesLoader;
import org.lwjglb.engine.sound.SoundBuffer;
import org.lwjglb.engine.sound.SoundListener;
import org.lwjglb.engine.sound.SoundManager;
import org.lwjglb.engine.sound.SoundSource;

import static org.lwjgl.glfw.GLFW.*;

public class DummyGame implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final SoundManager soundMgr;

    private final Camera camera;

    private Scene scene;

    private Hud hud;

    private static final float CAMERA_POS_STEP = 0.10f;

//    private Terrain terrain;

    private float angleInc;

    private float lightAngle;

//    private FlowParticleEmitter particleEmitter;

    private MouseBoxSelectionDetector selectDetector;

    private boolean leftButtonPressed;

    private enum Sounds {
        MUSIC, BEEP, FIRE
    }

    ;

    private GameItem[] gameItems;

    public DummyGame() {
        renderer = new Renderer();
        hud = new Hud();
        soundMgr = new SoundManager();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        angleInc = 0;
        lightAngle = 45;
    }

    @Override
    public void init(Window window) throws Exception {
        hud.init(window);
        renderer.init(window);
        soundMgr.init();

        leftButtonPressed = false;

        scene = new Scene();

        float reflectance = 1f;

        float blockScale = 0.5f;
        float skyBoxScale = 100.0f;
        float extension = 2.0f;

        float startx = extension * (-skyBoxScale + blockScale);
        float startz = extension * (skyBoxScale - blockScale);
        float starty = -1.0f;
        float inc = blockScale * 2;

        float posx = startx;
        float posz = startz;
//        float incy = 0.0f;

        selectDetector = new MouseBoxSelectionDetector();

//        ByteBuffer buf;
//        int width;
//        int height;
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            IntBuffer w = stack.mallocInt(1);
//            IntBuffer h = stack.mallocInt(1);
//            IntBuffer channels = stack.mallocInt(1);
//
//            buf = stbi_load("textures/grassblock.png", w, h, channels, 4);
//            if (buf == null) {
//                throw new Exception("Image file not loaded: " + stbi_failure_reason());
//            }
//
//            width = w.get();
//            height = h.get();
//        }

        int width = 10;
        int height = 10;
        int instances = 100;
//        int instances = height * width;


        Mesh[] houseMesh = StaticMeshesLoader.load("models\\cube2/c8.obj", "models\\cube2/");
//        Mesh[] houseMesh = StaticMeshesLoader.load("/src\\main\\resources\\models\\cube/c.obj", "");
//        Mesh[] houseMesh = StaticMeshesLoader.load("C:\\Users\\overw\\IdeaProjects\\lwjglbook\\chapter27\\c27-p1\\src\\main\\resources\\models/cube/c.obj", "");

        Mesh mesh = houseMesh[0];
//        Texture texture = new Texture("textures/terrain_textures-2.png");
//        Material material = new Material(texture, reflectance);
//        mesh.setMaterial(material);
        gameItems = new GameItem[instances];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
//                if (i!=j) continue;
//                if (i!=4) continue;
                GameItem gameItem = new GameItem(mesh);
                gameItem.setScale(1);
//                int rgb = HeightMapMesh.getRGB(i, j, width, buf);
//                incy = rgb / (10 * 255 * 255);
                gameItem.setPosition(j, 0, i);
                int textPos = Math.random() > 0.5f ? 0 : 1;
                gameItem.setTextPos(textPos);
//                gameItems[0] = gameItem;
                gameItems[i * width + j] = gameItem;

                posx += inc;
            }
            posx = startx;
            posz -= inc;
        }
        scene.setGameItems(gameItems);

        // Particles
        int maxParticles = 200;
        Vector3f particleSpeed = new Vector3f(0, 1, 0);
        particleSpeed.mul(2.5f);
        long ttl = 4000;
        long creationPeriodMillis = 300;
        float range = 0.2f;
        float scale = 1.0f;
//        Mesh partMesh = OBJLoader.loadMesh("/models/particle.obj", maxParticles);
//        Texture particleTexture = new Texture("textures/particle_anim.png", 4, 4);
//        Material partMaterial = new Material(particleTexture, reflectance);
//        partMesh.setMaterial(partMaterial);
//        Particle particle = new Particle(partMesh, particleSpeed, ttl, 100);
//        particle.setScale(scale);
//        particleEmitter = new FlowParticleEmitter(particle, maxParticles, creationPeriodMillis);
//        particleEmitter.setActive(true);
//        particleEmitter.setPositionRndRange(range);
//        particleEmitter.setSpeedRndRange(range);
//        particleEmitter.setAnimRange(10);
//        this.scene.setParticleEmitters(new FlowParticleEmitter[]{particleEmitter});

        // Shadows
        scene.setRenderShadows(false);

        // Fog
        Vector3f fogColour = new Vector3f(0.5f, 0.5f, 0.5f);
//        scene.setFog(new Fog(false, fogColour, 0.02f));

        // Setup  SkyBox
//        SkyBox skyBox = new SkyBox("/models/skybox.obj", new Vector4f(0.65f, 0.65f, 0.65f, 1.0f));
//        skyBox.setScale(skyBoxScale);
//        scene.setSkyBox(skyBox);

        // Setup Lights
        setupLights();

//        camera.getPosition().x = 0.25f;
//        camera.getPosition().y = 6.5f;
//        camera.getPosition().z = 6.5f;
//        camera.getRotation().x = 25;
//        camera.getRotation().y = -1;

//        stbi_image_free(buf);

        // Sounds
        this.soundMgr.init();
        this.soundMgr.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
        setupSounds();
    }

    private void setupSounds() throws Exception {
//        SoundBuffer buffBack = new SoundBuffer("/sounds/background.ogg");
//        soundMgr.addSoundBuffer(buffBack);
//        SoundSource sourceBack = new SoundSource(true, true);
//        sourceBack.setBuffer(buffBack.getBufferId());
//        soundMgr.addSoundSource(Sounds.MUSIC.toString(), sourceBack);

        SoundBuffer buffBeep = new SoundBuffer("/sounds/beep.ogg");
        soundMgr.addSoundBuffer(buffBeep);
        SoundSource sourceBeep = new SoundSource(false, true);
        sourceBeep.setBuffer(buffBeep.getBufferId());
        soundMgr.addSoundSource(Sounds.BEEP.toString(), sourceBeep);

//        SoundBuffer buffFire = new SoundBuffer("/sounds/fire.ogg");
//        soundMgr.addSoundBuffer(buffFire);
//        SoundSource sourceFire = new SoundSource(true, false);
//        Vector3f pos = particleEmitter.getBaseParticle().getPosition();
//        sourceFire.setPosition(pos);
//        sourceFire.setBuffer(buffFire.getBufferId());
//        soundMgr.addSoundSource(Sounds.FIRE.toString(), sourceFire);
//        sourceFire.play();

        soundMgr.setListener(new SoundListener(new Vector3f()));

//        sourceBack.play();
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        directionalLight.setShadowPosMult(10);
        directionalLight.setOrthoCords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);
        sceneLight.setDirectionalLight(directionalLight);
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            angleInc -= 0.05f;
            soundMgr.playSoundSource(Sounds.BEEP.toString());
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            angleInc += 0.05f;
            soundMgr.playSoundSource(Sounds.BEEP.toString());
        } else {
            angleInc = 0;
        }

    }

    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {
        if (mouseInput.isRightButtonPressed()) {
            // Update camera based on mouse
            Vector2f rotVec = mouseInput.getDisplVec();
//            Vector2f rotVec = new Vector2f();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

//         Update camera position
        Vector3f prevPos = new Vector3f(camera.getPosition());
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
//         Check if there has been a collision. If true, set the y position to
//         the maximum height
        float height = 0;
        if (camera.getPosition().y <= height) {
            camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
        }

        lightAngle += angleInc;
        if (lightAngle < 0) {
            lightAngle = 0;
        } else if (lightAngle > 180) {
            lightAngle = 180;
        }
        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();

//        particleEmitter.update((long) (interval * 1000));

        // Update view matrix
        camera.updateViewMatrix();

        // Update sound listener position;
        soundMgr.updateListenerPosition(camera);

        boolean aux = mouseInput.isLeftButtonPressed();
        if (aux && !this.leftButtonPressed && this.selectDetector.selectGameItem(gameItems, window, mouseInput.getCurrentPos(), camera)) {
            this.hud.incCounter();
        }
        this.leftButtonPressed = aux;
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, scene);
        hud.render(window);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        soundMgr.cleanup();

        scene.cleanup();
        if (hud != null) {
            hud.cleanup();
        }
    }
}
