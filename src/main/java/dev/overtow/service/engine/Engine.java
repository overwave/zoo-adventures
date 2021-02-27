package dev.overtow.service.engine;

import demo.ShadowMappingDemo;
import dev.overtow.core.Scene;
import dev.overtow.glsl.Converter;
import dev.overtow.glsl.shader.DepthFragmentShader;
import dev.overtow.glsl.shader.DepthVertexShader;
import dev.overtow.glsl.shader.GeneralFragmentShader;
import dev.overtow.glsl.shader.GeneralVertexShader;
import dev.overtow.service.hud.Hud;
import dev.overtow.service.meshlibrary.MeshLibrary;
import dev.overtow.service.meshloader.MeshLoader;
import dev.overtow.service.window.Window;
import dev.overtow.util.ErrorCallback;
import dev.overtow.util.injection.Bind;
import dev.overtow.util.injection.Injector;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.openal.AL11;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.system.MemoryStack;
import org.lwjglb.engine.MouseInput;
import org.lwjglb.engine.SceneLight;
import org.lwjglb.engine.Timer;
import org.lwjglb.engine.graph.Camera;
import org.lwjglb.engine.graph.Mesh;
import org.lwjglb.engine.graph.Renderer;
import org.lwjglb.engine.graph.lights.DirectionalLight;
import org.lwjglb.engine.items.GameItem;
import org.lwjglb.engine.sound.SoundBuffer;
import org.lwjglb.engine.sound.SoundListener;
import org.lwjglb.engine.sound.SoundManager;
import org.lwjglb.engine.sound.SoundSource;
import org.lwjglb.game.MouseBoxSelectionDetector;

import java.io.Closeable;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11C.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11C.glClearColor;
import static org.lwjgl.opengl.GL11C.glEnable;
import static org.lwjgl.opengl.GL11C.glGetInteger;
import static org.lwjgl.opengl.GL30C.GL_CONTEXT_FLAGS;
import static org.lwjgl.opengl.GL43C.*;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_OUTPUT;
import static org.lwjgl.system.APIUtil.apiLog;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public class Engine  {
    private final Window window;
    private final Renderer renderer;
    private final Scene scene;
//
//    private static final float MOUSE_SENSITIVITY = 0.2f;
//
//    private final Vector3f cameraInc;
//
//    private final SoundManager soundMgr;
//
//    private final Camera camera;
//
//    private final Hud hud;
//
//    private final MeshLoader meshLoader;
//
//    private Scene scene;
//
//    private static final float CAMERA_POS_STEP = 0.10f;
//
//    private float angleInc;
//
//    private float lightAngle;
//
//    private MouseBoxSelectionDetector selectDetector;
//
//    private boolean leftButtonPressed;
//
//    public static final int TARGET_UPS = 30;
//
//    private final Timer timer;
//
//    private final MouseInput mouseInput;
//
//    private List<GameItem> gameItems;
//    /////////////////////////////////////
//    private final long startTime;

    public Engine() {

        try {
            Converter.convert(List.of(
                    GeneralVertexShader.class, GeneralFragmentShader.class,
                    DepthVertexShader.class, DepthFragmentShader.class
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        window = new Window();
        renderer = new Renderer();
        scene = new Scene();


//        this.window = window;
//        this.hud = hud;
//        this.meshLoader = meshLoader;
//
//        soundMgr = new SoundManager();
//        camera = new Camera();
//        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
//        angleInc = 0;
//        lightAngle = 45;
//        mouseInput = new MouseInput();
//        timer = new Timer();
//        startTime = System.nanoTime();
//
//        timer.init();
////        mouseInput.init(this.window);
//        try {
//            soundMgr.init();
//
//            leftButtonPressed = false;
//
//            SceneLight sceneLight = setupLights();
//
//            scene = new Scene(window, camera, sceneLight);
//
//            selectDetector = new MouseBoxSelectionDetector();
//
//            int width = 10;
//            int height = 10;
//
//            Mesh meshCube = meshLoader.load("data/model/cube2/c9.obj");
//            Mesh meshPool = meshLoader.load("data/model/pool/pool_final_3.obj");
//            gameItems = new ArrayList<>();
//            for (int i = 0; i < height; i++) {
//                for (int j = 0; j < width; j++) {
//                    if (i > 0 && i < 9) {
//                        if (j > 0 && j < 9) {
//                            continue;
//                        }
//                    }
//
//                    GameItem gameItem = new GameItem(meshCube);
//                    gameItem.setPosition(j, 3, i);
//                    gameItems.add(gameItem);
//                }
//            }
//            GameItem gameItem = new GameItem(meshPool);
//            gameItem.setPosition(5, 0, 5);
//            gameItems.add(gameItem);
//
//
//            scene.setGameItems(gameItems);
//
//
//            this.soundMgr.init();
//            this.soundMgr.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
//            setupSounds();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    public void start() {
        while (!window.windowShouldClose()) {
            window.pollEvents();

            scene.update();

            renderer.render(scene);

            window.swapBuffers();
        }
//        try {
//            float elapsedTime;
//            float accumulator = 0f;
//            float interval = 1f / TARGET_UPS;
//
//            while (!window.windowShouldClose()) {
//                elapsedTime = timer.getElapsedTime();
//                accumulator += elapsedTime;
//
//                input();
//
////                while (accumulator >= interval) {
////                    update();
////                    counter++;
////                    accumulator -= interval;
////                }
//                if (accumulator > interval) {
//                    update();
//                    accumulator = 0;
//                }
//                render();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            cleanup();
//        }
    }

    private void input() {
//        mouseInput.input(window);
//        cameraInc.set(0, 0, 0);
//        if (window.isKeyPressed(GLFW_KEY_W)) {
//            cameraInc.z = -1;
//        } else if (window.isKeyPressed(GLFW_KEY_S)) {
//            cameraInc.z = 1;
//        }
//        if (window.isKeyPressed(GLFW_KEY_A)) {
//            cameraInc.x = -1;
//        } else if (window.isKeyPressed(GLFW_KEY_D)) {
//            cameraInc.x = 1;
//        }
//        if (window.isKeyPressed(GLFW_KEY_Z)) {
//            cameraInc.y = -1;
//        } else if (window.isKeyPressed(GLFW_KEY_X)) {
//            cameraInc.y = 1;
//        }
//        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
//            angleInc -= 0.05f;
//            soundMgr.playSoundSource(Sounds.BEEP.toString());
//        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
//            angleInc += 0.05f;
//            soundMgr.playSoundSource(Sounds.BEEP.toString());
//        } else {
//            angleInc = 0;
//        }

    }

    private void update() {
//        scene.update();
//
//
//        if (mouseInput.isRightButtonPressed()) {
//            // Update camera based on mouse
//            Vector2f rotVec = mouseInput.getDisplVec();
////            Vector2f rotVec = new Vector2f();
//            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
//        }
//
////         Update camera position
//        Vector3f prevPos = new Vector3f(camera.getPosition());
//        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
////         Check if there has been a collision. If true, set the y position to
////         the maximum height
//        float height = 0;
//        if (camera.getPosition().y <= height) {
//            camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
//        }
//
//        lightAngle += angleInc;
//        if (lightAngle < 0) {
//            lightAngle = 0;
//        } else if (lightAngle > 180) {
//            lightAngle = 180;
//        }
//        float zValue = (float) Math.cos(Math.toRadians(lightAngle));
//        float yValue = (float) Math.sin(Math.toRadians(lightAngle));
//        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
//        lightDirection.x = 0;
//        lightDirection.y = yValue;
//        lightDirection.z = zValue;
//        lightDirection.normalize();
//
////        camera.updateViewMatrix();
//
//        soundMgr.updateListenerPosition(camera);
//
//        boolean aux = mouseInput.isLeftButtonPressed();
////        if (aux && !this.leftButtonPressed && this.selectDetector.selectGameItem(gameItems, window, mouseInput.getCurrentPos(), camera)) {
////            this.hud.incCounter();
////        }
//        this.leftButtonPressed = aux;
    }

    private void render() {
//        scene.draw();
////        renderer.render(window, camera, scene);
//        hud.render();
//        window.update();
    }

    private enum Sounds {
        MUSIC, BEEP, FIRE
    }

    private void setupSounds() throws Exception {
//        SoundBuffer buffBack = new SoundBuffer("/sounds/background.ogg");
//        soundMgr.addSoundBuffer(buffBack);
//        SoundSource sourceBack = new SoundSource(true, true);
//        sourceBack.setBuffer(buffBack.getBufferId());
//        soundMgr.addSoundSource(Sounds.MUSIC.toString(), sourceBack);

//        SoundBuffer buffBeep = new SoundBuffer("data/sounds/beep.ogg");
//        soundMgr.addSoundBuffer(buffBeep);
//        SoundSource sourceBeep = new SoundSource(false, true);
//        sourceBeep.setBuffer(buffBeep.getBufferId());
//        soundMgr.addSoundSource(Sounds.BEEP.toString(), sourceBeep);

//        SoundBuffer buffFire = new SoundBuffer("/sounds/fire.ogg");
//        soundMgr.addSoundBuffer(buffFire);
//        SoundSource sourceFire = new SoundSource(true, false);
//        sourceFire.setPosition(pos);
//        sourceFire.setBuffer(buffFire.getBufferId());
//        soundMgr.addSoundSource(Sounds.FIRE.toString(), sourceFire);
//        sourceFire.play();

//        soundMgr.setListener(new SoundListener(new Vector3f()));

//        sourceBack.play();
    }

    private SceneLight setupLights() {
//        SceneLight sceneLight = new SceneLight();
////        scene.setSceneLight(sceneLight);
//
//        // Ambient Light
//        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
//
//        // Directional Light
//        float lightIntensity = 1.f;
//        Vector3f lightDirection = new Vector3f(0, 1, 1);
//        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
////        directionalLight.setShadowPosMult(10);
////        directionalLight.setOrthoCords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);
//        sceneLight.setDirectionalLight(directionalLight);
//
        return null;
    }
}
