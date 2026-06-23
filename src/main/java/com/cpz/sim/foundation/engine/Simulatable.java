package com.cpz.sim.foundation.engine;

import com.cpz.sim.foundation.time.SimulationTick;

/**
 * @author CPZ
 */
public interface Simulatable {

    void update(SimulationTick tick);

    default void reset() {
    }
}
