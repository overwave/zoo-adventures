package dev.overtow.graphics.draw;

import dev.overtow.core.Mesh;
import dev.overtow.math.Vector4;
import dev.overtow.util.Utils;

import java.awt.Color;

public enum BoxType {
    TOWER(new Color(45, 189, 255, 255), Mesh.Id.CUBE_TOWER),
    BARN(new Color(160, 98, 84, 255), Mesh.Id.CUBE_BARN),
    CORN(new Color(255, 200, 66, 255), Mesh.Id.CUBE_CORN),
    WINDMILL(new Color(224, 239, 239, 255), Mesh.Id.CUBE_WINDMILL),


    STARFISH(new Color(137, 255, 190, 255), Mesh.Id.CUBE_TOWER),
    CACTUS(new Color(255, 197, 7, 255), Mesh.Id.CUBE_TOWER),
    FLAMINGO(new Color(165, 255, 91, 255), Mesh.Id.CUBE_TOWER),
    SUN(new Color(66, 229, 255, 255), Mesh.Id.CUBE_TOWER);

    private final Vector4 color;
    private final Mesh.Id meshId;

    BoxType(Color color, Mesh.Id meshId) {
        this.color = Utils.convertColor(color);
        this.meshId = meshId;
    }

    public Vector4 getColor() {
        return color;
    }

    public Mesh.Id getMeshId() {
        return meshId;
    }
}
