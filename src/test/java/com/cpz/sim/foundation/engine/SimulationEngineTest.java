package com.cpz.sim.foundation.engine;

import com.cpz.sim.foundation.time.SimulationClock;
import com.cpz.sim.foundation.time.SimulationTick;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author CPZ
 */
class SimulationEngineTest {

    private static final Duration DELTA_TIME = Duration.ofMillis(100);

    private static SimulationEngine createEngine() {
        return new SimulationEngine(new SimulationClock(Duration.ofMillis(100)));
    }

    @Test
    void shouldRejectNullClock() {
        assertThrows(NullPointerException.class, () -> new SimulationEngine(null));
    }

    @Test
    void shouldRegisterSystems() {
        SimulationEngine engine = createEngine();
        engine.register(new FakeSystem());
        engine.register(new FakeSystem());
        assertEquals(2, engine.systemCount());
    }

    @Test
    void shouldRejectNullSystem() {
        SimulationEngine engine = createEngine();
        assertThrows(NullPointerException.class, () -> engine.register(null));
    }

    @Test
    void shouldAdvanceOneStep() {
        SimulationEngine engine = createEngine();
        SimulationTick tick = engine.step();
        assertEquals(1, tick.index());
        assertEquals(Duration.ofMillis(100), tick.elapsedTime());
        assertEquals(Duration.ofMillis(100), tick.deltaTime());
    }

    @Test
    void shouldUpdateRegisteredSystemWhenStepping() {
        SimulationEngine engine = createEngine();
        FakeSystem system = new FakeSystem();
        engine.register(system);
        SimulationTick tick = engine.step();
        assertEquals(1, system.updateCount);
        assertSame(tick, system.lastTick);
    }

    @Test
    void shouldSendSameTickToAllSystemsInSameStep() {
        SimulationEngine engine = createEngine();
        FakeSystem firstSystem = new FakeSystem();
        FakeSystem secondSystem = new FakeSystem();
        engine.register(firstSystem);
        engine.register(secondSystem);
        SimulationTick tick = engine.step();
        assertSame(tick, firstSystem.lastTick);
        assertSame(tick, secondSystem.lastTick);
    }

    @Test
    void shouldUpdateSystemsInRegistrationOrder() {
        SimulationEngine engine = createEngine();
        List<String> executionOrder = new ArrayList<>();
        engine.register(new OrderedSystem("first", executionOrder));
        engine.register(new OrderedSystem("second", executionOrder));
        engine.register(new OrderedSystem("third", executionOrder));
        engine.step();
        assertEquals(List.of("first", "second", "third"), executionOrder);
    }

    @Test
    void sholdAdvanceMultipleSteps() {
        SimulationEngine engine = createEngine();
        FakeSystem system = new FakeSystem();
        engine.register(system);
        SimulationTick tick = engine.step(5);
        assertEquals(5, tick.index());
        assertEquals(Duration.ofMillis(500), tick.elapsedTime());
        assertEquals(5, system.updateCount);
        assertEquals(5, engine.currentTick().index());
    }

    @Test
    void shouldRejectInvalidStepCount() {
        SimulationEngine engine = createEngine();
        assertThrows(IllegalArgumentException.class, () -> engine.step(0));
        assertThrows(IllegalArgumentException.class, () -> engine.step(-1));
    }

    @Test
    void shouldResetClockAndSystems() {
        SimulationEngine engine = createEngine();
        FakeSystem firstSystem = new FakeSystem();
        FakeSystem secondSystem = new FakeSystem();
        engine.register(firstSystem);
        engine.register(secondSystem);
        engine.step(3);
        engine.reset();
        assertEquals(0, engine.currentTick().index());
        assertEquals(Duration.ZERO, engine.currentTick().elapsedTime());
        assertEquals(1, firstSystem.resetCount);
        assertEquals(1, secondSystem.resetCount);
    }

    private static class FakeSystem implements Simulatable {

        private int updateCount;
        private int resetCount;
        private SimulationTick lastTick;

        @Override
        public void update(SimulationTick tick) {
            updateCount++;
            lastTick = tick;
        }

        @Override
        public void reset() {
            resetCount++;
        }
    }

    private record OrderedSystem(String name, List<String> executionOrder) implements Simulatable {

        @Override
        public void update(SimulationTick tick) {
            executionOrder.add(name);
        }
    }

}
