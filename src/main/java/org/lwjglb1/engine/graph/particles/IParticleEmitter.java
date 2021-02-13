package org.lwjglb1.engine.graph.particles;

import java.util.List;
import org.lwjglb1.engine.items.GameItem;

public interface IParticleEmitter {

    void cleanup();
    
    Particle getBaseParticle();
    
    List<GameItem> getParticles();
}
