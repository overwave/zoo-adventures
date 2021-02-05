import dev.overtow.graphics.draw.Injector;
import org.lwjglb.engine.GameEngine;
import org.lwjglb.engine.IGameLogic;
import org.lwjglb.engine.Window;
import org.lwjglb.game.DummyGame;

public class Main {

    public static void main(String[] args) {
        dev.overtow.graphics.draw.Window window = Injector.getInstance(dev.overtow.graphics.draw.Window.class);
        System.out.println(window.getHeight());

        try {
            boolean vSync = true;
            IGameLogic gameLogic = new DummyGame();
            Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = true;
            opts.showFps = true;
            opts.compatibleProfile = true;
//            opts.showTriangles = true;
            opts.antialiasing = true;
            GameEngine gameEng = new GameEngine("GAME", vSync, opts, gameLogic);
            gameEng.run();
        } catch (Exception excp) {
            excp.printStackTrace();
            System.exit(-1);
        }
    }
}