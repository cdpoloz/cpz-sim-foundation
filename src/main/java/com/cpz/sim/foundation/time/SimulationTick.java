package com.cpz.sim.foundation.time;

import java.time.Duration;
import java.util.Objects;

/**
 * @author CPZ
 */
public record SimulationTick(
        long index,
        Duration elapsedTime,
        Duration deltaTime
) {
    public SimulationTick {
        if (index < 0) throw new IllegalArgumentException("index cannot be negative");
        Objects.requireNonNull(elapsedTime, "elapsedTime cannot be null");
        Objects.requireNonNull(deltaTime, "deltaTime cannot be null");
        if (elapsedTime.isNegative()) throw new IllegalArgumentException("elapsedTime cannot be negative");
        if (deltaTime.isZero() || deltaTime.isNegative())
            throw new IllegalArgumentException("deltaTime must be positive");
    }

    public double deltaSeconds() {
        return deltaTime.toSeconds() + deltaTime.toNanosPart() / 1_000_000_000.0;
    }
}

