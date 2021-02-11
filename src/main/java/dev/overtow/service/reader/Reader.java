package dev.overtow.service.reader;

import java.io.IOException;

public interface Reader {
    String read(String path) throws IOException;
}
