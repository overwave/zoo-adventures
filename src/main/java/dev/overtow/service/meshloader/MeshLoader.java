package dev.overtow.service.meshloader;

import org.lwjglb.engine.graph.Mesh;

public interface MeshLoader {
    Mesh load(String filename);
}
