package com.cpz.sim.foundation.engine;

import com.cpz.sim.foundation.time.SimulationClock;
import com.cpz.sim.foundation.time.SimulationTick;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author CPZ
 */
class SimulationEngineIntegrationTest {

    @Test
    void shouldCoordinateSystemsThroughSharedState() {
        SimulationClock clock = new SimulationClock(Duration.ofSeconds(1));
        SimulationEngine engine = new SimulationEngine(clock);
        SharedValue sharedValue = new SharedValue();
        IncrementingSystem producer = new IncrementingSystem(sharedValue);
        ObservingSystem observer = new ObservingSystem(sharedValue);
        engine.register(producer);
        engine.register(observer);
        SimulationTick finalTick = engine.step(3);
        assertEquals(3, finalTick.index());
        assertEquals(Duration.ofSeconds(3), finalTick.elapsedTime());
        assertEquals(30, sharedValue.value);
        assertEquals(List.of(new Observation(1, 10), new Observation(2, 20), new Observation(3, 30)), observer.observations);
    }

    private static class SharedValue {
        private int value;
    }


    private record IncrementingSystem(SharedValue sharedValue) implements Simulatable {

        @Override
        public void update(SimulationTick tick) {
            sharedValue.value += 10;
        }
    }

    private static class ObservingSystem implements Simulatable {

        private final SharedValue sharedValue;
        private final List<Observation> observations = new ArrayList<>();

        private ObservingSystem(SharedValue sharedValue) {
            this.sharedValue = sharedValue;
        }

        @Override
        public void update(SimulationTick tick) {
            observations.add(new Observation(tick.index(), sharedValue.value));
        }
    }

    private record Observation(long tickIndex, int value) {
    }
}

