package org.lwjglb.game;

import dev.overtow.service.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.primitives.Intersectionf;
import org.joml.primitives.Planef;
import org.joml.primitives.Rayf;
import org.lwjglb.engine.graph.Camera;
import org.lwjglb.engine.items.GameItem;

import java.time.Instant;

public class MouseBoxSelectionDetector extends CameraBoxSelectionDetector {

    private final Matrix4f invProjectionMatrix;
    
    private final Matrix4f invViewMatrix;

    private final Vector3f mouseDir;
    
    private final Vector4f tmpVec;

    public MouseBoxSelectionDetector() {
        super();
        invProjectionMatrix = new Matrix4f();
        invViewMatrix = new Matrix4f();
        mouseDir = new Vector3f();
        tmpVec = new Vector4f();
    }
    
    public boolean selectGameItem(GameItem[] gameItems, Window windowKek, Vector2d mousePos, Camera camera) {
        // Transform mouse coordinates into normalized spaze [-1, 1]
        int wdwWitdh = windowKek.getWidth();
        int wdwHeight = windowKek.getHeight();
        
        float x = (float)(2 * mousePos.x) / (float)wdwWitdh - 1.0f;
        float y = 1.0f - (float)(2 * mousePos.y) / (float)wdwHeight;
        float z = -1.0f;

        invProjectionMatrix.set(windowKek.getProjectionMatrix());
        invProjectionMatrix.invert();
        
        tmpVec.set(x, y, z, 1.0f);
        tmpVec.mul(invProjectionMatrix);
        tmpVec.z = -1.0f;
        tmpVec.w = 0.0f;
        
        Matrix4f viewMatrix = camera.getViewMatrix();
        invViewMatrix.set(viewMatrix);
        invViewMatrix.invert();
        tmpVec.mul(invViewMatrix);
        
        mouseDir.set(tmpVec.x, tmpVec.y, tmpVec.z);

        final String SEARCH_BY_LOGIN_TIME =
                "SELECT profile.username, profile.lastLoginAt " +
                        "FROM accounts WHERE profile.lastLoginAt >= \"%s\" AND profile.lastLoginAt <= \"%s\"";

        String query = String.format(SEARCH_BY_LOGIN_TIME, Instant.now(), Instant.now());

        Rayf clickRay = new Rayf(camera.getPosition(), mouseDir);
        // point is at 0, 1, 0; normal is looking to camera (to +Y)
        Planef boxesSurface = new Planef(new Vector3f(0, 1, 0), new Vector3f(0, 1, 0));
        float length = Intersectionf.intersectRayPlane(clickRay, boxesSurface, 0.0001f); // should be 16
        Vector3f point = new Vector3f(camera.getPosition()).add(mouseDir.mul(length));

        windowKek.setWindowTitle("[" + (int)point.x + "/" + (int)point.z + "]");

        return selectGameItem(gameItems, camera.getPosition(), mouseDir);
    }
}
