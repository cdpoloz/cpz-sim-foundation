package com.cpz.sim.foundation.time;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author CPZ
 */
class SimulationClockTest {

    @Test
    void startAsZero() {
        SimulationClock clock = new SimulationClock(Duration.ofSeconds(5));
        assertEquals(0, clock.getCurrentIndex());
        assertEquals(Duration.ZERO, clock.getElapsedTime());
    }

    @Test
    void advancesUsingTheConfiguresTimeStep() {
        SimulationClock clock = new SimulationClock(Duration.ofMillis(500));
        SimulationTick first = clock.advance();
        SimulationTick second = clock.advance();
        assertEquals(1, first.index());
        assertEquals(Duration.ofMillis(500), first.elapsedTime());
        assertEquals(2, second.index());
        assertEquals(Duration.ofSeconds(1), second.elapsedTime());
        assertEquals(Duration.ofMillis(500), second.deltaTime());
    }

    @Test
    void resetsTheClock() {
        SimulationClock clock = new SimulationClock(Duration.ofSeconds(1));
        clock.advance();
        clock.advance();
        clock.reset();
        assertEquals(0, clock.getCurrentIndex());
        assertEquals(Duration.ZERO, clock.getElapsedTime());
    }

    @Test
    void rejectsInvalidTimeSteps() {
        assertThrows(NullPointerException.class, () -> new SimulationClock(null));
        assertThrows(IllegalArgumentException.class, () -> new SimulationClock(Duration.ZERO));
        assertThrows(IllegalArgumentException.class, () -> new SimulationClock(Duration.ofSeconds(-1)));
    }

    @Test
    void convertsDeltaTimeToFractionalSeconds() {
        SimulationTick tick = new SimulationTick(1, Duration.ofMillis(250), Duration.ofMillis(250));
        assertEquals(0.25, tick.deltaSeconds(), 1.0e-12);
    }
}
