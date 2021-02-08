import dev.overtow.service.engine.Engine;
import dev.overtow.util.injection.Injector;

public class Main {

    public static void main(String[] args) {
        Injector.init();

        Engine engine = Injector.getInstance(Engine.class);

        engine.start();
    }
}