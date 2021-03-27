package dev.overtow.service.meshloader;

import dev.overtow.core.Mesh;

public interface MeshLoader {
    Mesh load(String filename, String texturePath);
}
