package dev.overtow.core;

public class Engine {
    private final Window window;
    private final Renderer renderer;
    private final Scene scene;
//    private final SoundManager soundMgr;
//    private MouseBoxSelectionDetector selectDetector;
//    private boolean leftButtonPressed;
//    private final MouseInput mouseInput;

    public Engine() {
        window = new Window();
        renderer = new Renderer(window);
        scene = new Scene();


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

            scene.update();

            renderer.render(scene);

            window.swapBuffers();
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
