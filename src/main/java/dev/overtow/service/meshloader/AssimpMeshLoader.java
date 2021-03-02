package dev.overtow.service.meshloader;

import dev.overtow.core.Material;
import dev.overtow.core.Mesh;
import dev.overtow.core.Texture;
import dev.overtow.service.memory.MemoryManager;
import dev.overtow.util.Utils;
import dev.overtow.util.injection.Bind;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.system.MemoryUtil.*;

@Bind
public class AssimpMeshLoader implements MeshLoader {

    private final MemoryManager memoryManager;
    private final Map<String, Texture> texturesCache;

    public AssimpMeshLoader(MemoryManager memoryManager) {
        this.memoryManager = memoryManager;
        this.texturesCache = new HashMap<>();
    }

    @Override
    public Mesh load(String filename) {
        AIFileIO fileIo = AIFileIO.create().OpenProc((pFileIO, fileName, openMode) -> {
            ByteBuffer data;
            String fileNameUtf8 = memUTF8(fileName);
            try {
                data = memoryManager.readFromFile(fileNameUtf8);
            } catch (IOException e) {
                throw new RuntimeException("Could not open file: " + fileNameUtf8);
            }

            return AIFile.create()
                    .ReadProc((pFile, pBuffer, size, count) -> {
                        long max = Math.min(data.remaining(), size * count);
                        memCopy(memAddress(data) + data.position(), pBuffer, max);
                        return max;
                    })
                    .SeekProc((pFile, offset, origin) -> {
                        if (origin == Assimp.aiOrigin_CUR) {
                            data.position(data.position() + (int) offset);
                        } else if (origin == Assimp.aiOrigin_SET) {
                            data.position((int) offset);
                        } else if (origin == Assimp.aiOrigin_END) {
                            data.position(data.limit() + (int) offset);
                        }
                        return 0;
                    })
                    .FileSizeProc(pFile -> data.limit())
                    .address();
        }).CloseProc((pFileIO, pFile) -> {
            AIFile aiFile = AIFile.create(pFile);

            aiFile.ReadProc().free();
            aiFile.SeekProc().free();
            aiFile.FileSizeProc().free();
        });

        File file = new File(filename);
        AIScene aiScene = aiImportFileEx(filename, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate, fileIo);

        if (aiScene == null) {
            throw new RuntimeException("Error loading model:" + aiGetErrorString());
        }

        int numMaterials = aiScene.mNumMaterials();
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<Material> materials = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            processMaterial(aiMaterial, materials, file.getParent());
        }


        int numMeshes = aiScene.mNumMeshes();
        PointerBuffer aiMeshes = aiScene.mMeshes();

        if (numMeshes != 1 || aiMeshes == null) {
            throw new RuntimeException(String.format("Model %s has not exactly one mesh", filename));
        }

        AIMesh aiMesh = AIMesh.create(aiMeshes.get(0));
        return processMesh(aiMesh, materials);
    }

    private void processMaterial(AIMaterial aiMaterial, List<Material> materials, String texturesDir) {
        AIString path = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, aiTextureType_DIFFUSE, 0, path, (IntBuffer) null, null, null, null, null, null);
        String texturePath = path.dataString();

        Texture texture = null;
        if (texturePath.length() > 0) {
            texture = texturesCache.computeIfAbsent(texturesDir + "/" + texturePath, Texture::new);
        }

        materials.add(new Material(texture));
    }

    private static Mesh processMesh(AIMesh aiMesh, List<Material> materials) {
        List<Float> vertices = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        processVertices(aiMesh, vertices);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, textures);
        processIndices(aiMesh, indices);

        // Texture coordinates may not have been populated. We need at least the empty slots
        if (textures.size() == 0) {
            int numElements = (vertices.size() / 3) * 2;
            for (int i = 0; i < numElements; i++) {
                textures.add(0.0f);
            }
        }

        Mesh mesh = new Mesh(Utils.listToArray(vertices),
                Utils.listToArray(textures),
                Utils.listToArray(normals),
                Utils.listIntToArray(indices)
        );
        Material material;
        int materialIdx = aiMesh.mMaterialIndex();
        if (materialIdx >= 0 && materialIdx < materials.size()) {
            material = materials.get(materialIdx);
        } else {
//            throw new IllegalStateException("no texture present");
            material = null;
        }
        mesh.setMaterial(material);

        return mesh;
    }

    private static void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }

    private static void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    private static void processTextCoords(AIMesh aiMesh, List<Float> textures) {
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();
            textures.add(textCoord.x());
            textures.add(1 - textCoord.y());
        }
    }

    private static void processIndices(AIMesh aiMesh, List<Integer> indices) {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }
}
