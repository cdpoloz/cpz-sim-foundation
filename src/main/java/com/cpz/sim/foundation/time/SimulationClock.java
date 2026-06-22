package com.cpz.sim.foundation.time;

import java.time.Duration;
import java.util.Objects;

/**
 * @author CPZ
 */
public class SimulationClock {

    private final Duration timeStep;
    private long currentIndex;
    private Duration elapsedTime;

    public SimulationClock(Duration timeStep) {
        this.timeStep = Objects.requireNonNull(timeStep, "timeStep cannot be null");
        if (timeStep.isZero() || timeStep.isNegative()) throw new IllegalArgumentException("timeStep must be positive");
        reset();
    }

    public void reset() {
        currentIndex = 0;
        elapsedTime = Duration.ZERO;
    }

    public SimulationTick advance() {
        if (currentIndex == Long.MAX_VALUE) throw new IllegalStateException("Maximum simulation step reached");
        currentIndex++;
        elapsedTime = elapsedTime.plus(timeStep);
        return currentTick();
    }

    public SimulationTick currentTick() {
        return new SimulationTick(currentIndex, elapsedTime, timeStep);
    }

    // <editor-fold defaultstate="collapsed" desc="*** getter & setter ***">
    public Duration getTimeStep() {
        return timeStep;
    }

    public long getCurrentIndex() {
        return currentIndex;
    }

    public Duration getElapsedTime() {
        return elapsedTime;
    }
    // </editor-fold>
}
