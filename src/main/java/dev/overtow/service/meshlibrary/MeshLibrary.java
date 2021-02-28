package dev.overtow.service.meshlibrary;

import dev.overtow.core.Mesh;

public interface MeshLibrary {
    Mesh get(Mesh.Id id);
}
