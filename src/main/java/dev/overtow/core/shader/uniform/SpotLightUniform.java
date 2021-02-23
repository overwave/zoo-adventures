package dev.overtow.core.shader.uniform;

import org.lwjglb.engine.graph.lights.PointLight;
import org.lwjglb.engine.graph.lights.SpotLight;

import java.util.function.Function;

import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3f;

public class SpotLightUniform implements ValueUniform<SpotLight> {

    private final String namePrefix;
    private SpotLight value;

    private int pointLightColorId;
    private int pointLightPositionId;
    private int pointLightIntensityId;
    private int pointLightAttenuationConstantId;
    private int pointLightAttenuationLinearId;
    private int pointLightAttenuationExponentId;
    private int coneDirectionId;
    private int cutoffId;

    public SpotLightUniform(String name) {
        namePrefix = name;
    }

    public void setValue(SpotLight value) {
//        if (value.equals(this.value)) {
//            return;
//        }
//        this.value = value;

        PointLight pointLight = value.getPointLight();
        glUniform3f(pointLightColorId, pointLight.getColor().x(), pointLight.getColor().y(), pointLight.getColor().z());
        glUniform3f(pointLightPositionId, pointLight.getPosition().x(), pointLight.getPosition().y(), pointLight.getPosition().z());
        glUniform1f(pointLightIntensityId, pointLight.getIntensity());

        PointLight.Attenuation attenuation = pointLight.getAttenuation();
        glUniform1f(pointLightAttenuationConstantId, attenuation.getConstant());
        glUniform1f(pointLightAttenuationLinearId, attenuation.getLinear());
        glUniform1f(pointLightAttenuationExponentId, attenuation.getExponent());

        glUniform3f(coneDirectionId, value.getConeDirection().x(), value.getConeDirection().y(), value.getConeDirection().z());
        glUniform1f(cutoffId, value.getCutOff());
    }

    @Override
    public void locate(Function<String, Integer> function) {
        pointLightColorId = function.apply(namePrefix + ".pl.colour");
        pointLightPositionId = function.apply(namePrefix + ".pl.position");
        pointLightIntensityId = function.apply(namePrefix + ".pl.intensity");
        pointLightAttenuationConstantId = function.apply(namePrefix + ".pl.att.constant");
        pointLightAttenuationLinearId = function.apply(namePrefix + ".pl.att.linear");
        pointLightAttenuationExponentId = function.apply(namePrefix + ".pl.att.exponent");
        coneDirectionId = function.apply(namePrefix + ".conedir");
        cutoffId = function.apply(namePrefix + ".cutoff");
    }
}
