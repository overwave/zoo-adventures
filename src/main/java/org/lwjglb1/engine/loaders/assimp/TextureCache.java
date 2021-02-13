package org.lwjglb1.engine.loaders.assimp;

import java.util.HashMap;
import java.util.Map;
import org.lwjglb1.engine.graph.Texture;

public class TextureCache {

    private static TextureCache INSTANCE;

    private Map<String, Texture> texturesMap;
    
    private TextureCache() {
        texturesMap = new HashMap<>();
    }
    
    public static synchronized TextureCache getInstance() {
        if ( INSTANCE == null ) {
            INSTANCE = new TextureCache();
        }
        return INSTANCE;
    }
    
    public Texture getTexture(String path)  {
        Texture texture = texturesMap.get(path);
        if ( texture == null ) {
            try {
                texture = new Texture(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            texturesMap.put(path, texture);
        }
        return texture;
    }
}
