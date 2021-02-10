package dev.overtow.service.meshlibrary;

import dev.overtow.util.injection.Bind;

import java.io.FileInputStream;
import java.io.IOException;

@Bind
public class FileReader implements Reader {
    @Override
    public String read(String path) throws IOException {
        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            return new String(fileInputStream.readAllBytes());
        }
    }
}
