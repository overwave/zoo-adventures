package org.lwjglb.engine.loaders.newloader;

import org.lwjglb.engine.graph.Texture;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    
    public Texture getTexture(String path) throws IOException {
        Texture texture = texturesMap.get(path);
        if ( texture == null ) {
            texture = new Texture(path);
            texturesMap.put(path, texture);
        }
        return texture;
    }
}
