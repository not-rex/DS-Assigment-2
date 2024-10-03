package com.weatherApp;

public class LamportClock {
    private int time;

    public LamportClock() {
        this.time = 0;
    }

    // Increments the local Lamport clock.
    public synchronized void tick() {
        time++;
    }

    // Updates the local Lamport clock based on the received timestamp.
    public synchronized void update(int receivedTime) {
        time = Math.max(time, receivedTime) + 1;
    }

    // Retrieves the current Lamport clock value.
    public synchronized int getTime() {
        return time;
    }
}
