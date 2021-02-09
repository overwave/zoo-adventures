package dev.overtow.service.reader;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface Reader {
    String read(String path) throws FileNotFoundException, IOException;
}
