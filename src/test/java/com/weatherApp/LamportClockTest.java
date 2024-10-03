// LamportClockTest.java
package com.weatherApp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class LamportClockTest {

    @Test
    public void testInitialTime() {
        LamportClock clock = new LamportClock();
        assertEquals(0, clock.getTime(), "Initial time should be 0");
    }

    @Test
    public void testTick() {
        LamportClock clock = new LamportClock();
        clock.tick();
        assertEquals(1, clock.getTime(), "Time after one tick should be 1");
    }

    @Test
    public void testUpdateWithHigherReceivedTime() {
        LamportClock clock = new LamportClock();
        clock.tick(); // time = 1
        clock.update(2); // time = max(1, 2) + 1 = 3
        assertEquals(3, clock.getTime(), "Time after update with higher receivedTime should be 3");
    }

    @Test
    public void testUpdateWithLowerReceivedTime() {
        LamportClock clock = new LamportClock();
        clock.tick(); // time = 1
        clock.update(0); // time = max(1, 0) + 1 = 2
        assertEquals(2, clock.getTime(), "Time after update with lower receivedTime should be 2");
    }

    @Test
    public void testMultipleTicks() {
        LamportClock clock = new LamportClock();
        for (int i = 0; i < 5; i++) {
            clock.tick();
        }
        assertEquals(5, clock.getTime(), "Time after 5 ticks should be 5");
    }

    @Test
    public void testMultipleUpdates() {
        LamportClock clock = new LamportClock();
        clock.update(1); // time = max(0,1) +1 = 2
        assertEquals(2, clock.getTime(), "Time after first update should be 2");

        clock.update(3); // time = max(2,3) +1 = 4
        assertEquals(4, clock.getTime(), "Time after second update should be 4");

        clock.update(2); // time = max(4,2) +1 = 5
        assertEquals(5, clock.getTime(), "Time after third update should be 5");
    }

    @Test
    public void testConcurrentTicks() throws InterruptedException {
        LamportClock clock = new LamportClock();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                clock.tick();
            }
        });
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                clock.tick();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        assertEquals(2000, clock.getTime(), "Time after concurrent ticks should be 2000");
    }

    @Test
    public void testConcurrentUpdates() throws InterruptedException {
        LamportClock clock = new LamportClock();
        clock.tick(); // time = 1

        Thread t1 = new Thread(() -> {
            clock.update(5); // time = max(current,5) +1
        });

        Thread t2 = new Thread(() -> {
            clock.update(3); // time = max(current,3) +1
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        // Final time should be max after both updates
        // After first update (assume t1 runs first): time = max(1,5)+1=6
        // Then t2: time = max(6,3)+1=7
        // Or if t2 runs first: time = max(1,3)+1=4, then t1: max(4,5)+1=6
        // So final time is either 6 or 7, depending on thread execution
        int finalTime = clock.getTime();
        assertTrue(finalTime == 6 || finalTime == 7, "Final time should be either 6 or 7");
    }
}
