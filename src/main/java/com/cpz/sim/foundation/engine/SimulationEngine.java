package com.cpz.sim.foundation.engine;

import com.cpz.sim.foundation.time.SimulationClock;
import com.cpz.sim.foundation.time.SimulationTick;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Coordinates registered simulation systems using a logical clock.
 * <p>
 * Systems are updated sequentially in registration order.
 * Every system receives the same tick during a simulation step.
 *
 * @author CPZ
 */
public class SimulationEngine {

    private final SimulationClock clock;
    private final List<Simulatable> systems = new ArrayList<>();

    public SimulationEngine(SimulationClock clock) {
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    /**
     * Registers a system at the end of the execution order.
     */
    public void register(Simulatable system) {
        systems.add(Objects.requireNonNull(system, "system cannot be null"));
    }

    public SimulationTick step() {
        SimulationTick tick = clock.advance();
        for (Simulatable system : systems) system.update(tick);
        return tick;
    }

    public SimulationTick step(int count) {
        if (count <= 0) throw new IllegalArgumentException("count must be positive");
        SimulationTick tick = clock.currentTick();
        for (int i = 0; i < count; i++) tick = step();
        return tick;
    }

    public void reset() {
        clock.reset();
        for (Simulatable system : systems) system.reset();
    }

    public SimulationTick currentTick() {
        return clock.currentTick();
    }

    public int systemCount() {
        return systems.size();
    }

}
