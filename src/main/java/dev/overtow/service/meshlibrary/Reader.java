package dev.overtow.service.meshlibrary;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Reader {
    String read(String path) throws FileNotFoundException, IOException;
}
