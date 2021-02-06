import dev.overtow.graphics.draw.Engine;
import dev.overtow.graphics.draw.Injector;

public class Main {

    public static void main(String[] args) {
        Injector.init();

        Engine engine = new Engine();

        engine.start();
    }
}