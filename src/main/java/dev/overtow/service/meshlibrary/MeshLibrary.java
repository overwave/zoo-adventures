package dev.overtow.service.meshlibrary;

import org.lwjglb.engine.graph.Mesh;

import java.io.IOException;

public interface MeshLibrary {
    Mesh get(Mesh.Id id);
}
