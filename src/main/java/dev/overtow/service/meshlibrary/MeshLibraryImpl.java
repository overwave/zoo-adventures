package dev.overtow.service.meshlibrary;

import dev.overtow.service.meshloader.MeshLoader;
import dev.overtow.util.injection.Bind;
import dev.overtow.core.Mesh;

import java.util.HashMap;
import java.util.Map;

@Bind
public class MeshLibraryImpl implements MeshLibrary {

    private final MeshLoader meshLoader;
    private final Map<Mesh.Id, Mesh> meshes;

    public MeshLibraryImpl(MeshLoader meshLoader) {
        this.meshLoader = meshLoader;
        this.meshes = new HashMap<>();
    }

    @Override
    public Mesh get(Mesh.Id id) {
        return meshes.computeIfAbsent(id, key -> meshLoader.load(id.getPath()));
    }
}
