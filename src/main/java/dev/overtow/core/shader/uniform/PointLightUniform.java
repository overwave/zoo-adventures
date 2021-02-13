package dev.overtow.core.shader.uniform;

import org.lwjglb1.engine.graph.lights.PointLight;

import java.util.function.Function;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3f;

public class PointLightUniform implements ValueUniform<PointLight> {

    private final String namePrefix;
    private PointLight value;

    private int colorId;
    private int positionId;
    private int intensityId;
    private int attenuationConstantId;
    private int attenuationLinearId;
    private int attenuationExponentId;

    public PointLightUniform(String name) {
        namePrefix = name;
    }

    public void setValue(PointLight value) {
//        if (value.equals(this.value)) {
//            return;
//        }
//        this.value = value;

        glUniform3f(colorId, value.getColor().x(), value.getColor().y(), value.getColor().z());
        glUniform3f(positionId, value.getPosition().x(), value.getPosition().y(), value.getPosition().z());
        glUniform1f(intensityId, value.getIntensity());

        PointLight.Attenuation attenuation = value.getAttenuation();
        glUniform1f(attenuationConstantId, attenuation.getConstant());
        glUniform1f(attenuationLinearId, attenuation.getLinear());
        glUniform1f(attenuationExponentId, attenuation.getExponent());
    }

    @Override
    public void locate(Function<String, Integer> function) {
        colorId = function.apply(namePrefix + "colour");
        positionId = function.apply(namePrefix + "position");
        intensityId = function.apply(namePrefix + "intensity");
        attenuationConstantId = function.apply(namePrefix + "att.constant");
        attenuationLinearId = function.apply(namePrefix + "att.linear");
        attenuationExponentId = function.apply(namePrefix + "att.exponent");
    }
}
