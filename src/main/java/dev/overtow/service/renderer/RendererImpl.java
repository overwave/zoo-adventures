package dev.overtow.service.renderer;

import dev.overtow.util.injection.Bind;

@Bind
public class RendererImpl implements Renderer {

//    public void render(Window window, Camera camera, Scene scene) {
//        clear();
//
//        // Render depth map before view ports has been set up
//        renderDepthMap(scene);
//
//        glViewport(0, 0, window.getWidth(), window.getHeight());
//
//        // Update projection matrix once per render cycle
//        window.updateProjectionMatrix();
//
//        renderScene(window, camera, scene);
////        renderAxes(window, camera);
//        renderCrossHair(window);
//    }

//    private void renderCrossHair(Window windowKek) {
//        glPushMatrix();
//        glLoadIdentity();
//
//        float inc = 0.05f;
//        glLineWidth(2.0f);
//
//        glBegin(GL_LINES);
//
//        glColor3f(1.0f, 1.0f, 1.0f);
//
//        // Horizontal line
//        glVertex3f(-inc, 0.0f, 0.0f);
//        glVertex3f(+inc, 0.0f, 0.0f);
//        glEnd();
//
//        // Vertical line
//        glBegin(GL_LINES);
//        glVertex3f(0.0f, -inc, 0.0f);
//        glVertex3f(0.0f, +inc, 0.0f);
//        glEnd();
//
//        glPopMatrix();
//    }


//    public void cleanup() {
//        if (shadowMap != null) {
//            shadowMap.cleanup();
//        }
//        if (depthShaderProgram != null) {
//            depthShaderProgram.cleanup();
//        }
//        if (sceneShaderProgram != null) {
//            sceneShaderProgram.cleanup();
//        }
//    }
}
