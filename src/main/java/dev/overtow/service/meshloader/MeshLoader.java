package dev.overtow.service.meshloader;

import org.lwjglb.engine.graph.Mesh;

import java.io.IOException;

public interface MeshLoader {
    Mesh load(String modelFilename) throws IOException;
}
