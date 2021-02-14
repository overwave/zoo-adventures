package org.lwjglb1.engine;

public interface IGameLogic {

    void init(Window window) throws Exception;
    
    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput, Window window);
    
    void render(Window window);
    
    void cleanup();
}