package dev.overtow.core.shader.uniform;

import org.lwjglb.engine.graph.Material;
import org.lwjglb.engine.graph.lights.PointLight;
import org.lwjglb.engine.graph.lights.SpotLight;

import java.util.function.Consumer;
import java.util.function.Function;

import static org.lwjgl.opengl.GL20.*;

public class MaterialUniform implements ValueUniform<Material> {

    private final String namePrefix;
    private Material value;

    private int ambient;
    private int diffuse;
    private int specular;
    private int hasTexture;
    private int hasNormalMap;
    private int reflectance;

    public MaterialUniform(String name) {
        namePrefix = name;
    }

    public void setValue(Material value) {
        if (value.equals(this.value)) {
            return;
        }
        this.value = value;

        glUniform3f(ambient, value.getAmbientColour().x(), value.getAmbientColour().y(), value.getAmbientColour().z());
        glUniform3f(diffuse, value.getDiffuseColour().x(), value.getDiffuseColour().y(), value.getDiffuseColour().z());
        glUniform3f(specular, value.getSpecularColour().x(), value.getSpecularColour().y(), value.getSpecularColour().z());
        glUniform1i(hasTexture, value.isTextured() ? 1 : 0);
        glUniform1i(hasNormalMap, value.hasNormalMap() ? 1 : 0);
        glUniform1f(reflectance, value.getReflectance());
    }

    @Override
    public void locate(Function<String, Integer> function) {
        ambient = function.apply(namePrefix + ".ambient");
        diffuse = function.apply(namePrefix + ".diffuse");
        specular = function.apply(namePrefix + ".specular");
        hasTexture = function.apply(namePrefix + ".hasTexture");
        hasNormalMap = function.apply(namePrefix + ".hasNormalMap");
        reflectance = function.apply(namePrefix + ".reflectance");
    }
}
