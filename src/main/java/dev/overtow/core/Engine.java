package dev.overtow.core;

import dev.overtow.math.Vector2;
import dev.overtow.service.reader.Reader;
import dev.overtow.service.window.Window;
import dev.overtow.util.injection.Injector;

import java.io.IOException;

public class Engine {
    private final Window window;
    private final Renderer renderer;
    private final Scene scene;
//    private final SoundManager soundMgr;
//    private MouseBoxSelectionDetector selectDetector;
//    private boolean leftButtonPressed;
//    private final MouseInput mouseInput;

    public Engine() {
        window = Injector.getInstance(Window.class);
        renderer = new Renderer();
        scene = new Scene(readLevel(17));


//        soundMgr = new SoundManager();
//            soundMgr.init();
//            leftButtonPressed = false;
//            selectDetector = new MouseBoxSelectionDetector();
//            this.soundMgr.init();
//            this.soundMgr.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
    }

    public void start() {
        while (!window.windowShouldClose()) {
            window.pollEvents();

            Vector2 scaledMousePosition = window.getMousePosition().divide(window.getSize());
            scene.update(scaledMousePosition);

            renderer.render(scene);

            window.swapBuffers();
        }
    }

    private Level readLevel(int number) {
        Reader reader = Injector.getInstance(Reader.class);
        try {
            String levelContent = reader.read("data/level/level_" + number + ".dat");
            return new Level(levelContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//        if (window.isKeyPressed(GLFW_KEY_Z)) {
//            cameraInc.y = -1;
//        if (mouseInput.isRightButtonPressed()) {
//        soundMgr.updateListenerPosition(camera);
//        SoundBuffer buffBack = new SoundBuffer("/sounds/background.ogg");
//        soundMgr.addSoundBuffer(buffBack);
//        SoundSource sourceBack = new SoundSource(true, true);
//        sourceBack.setBuffer(buffBack.getBufferId());
//        soundMgr.addSoundSource(Sounds.MUSIC.toString(), sourceBack);
//        sourceFire.play();
//        soundMgr.setListener(new SoundListener(new Vector3f()));
}
