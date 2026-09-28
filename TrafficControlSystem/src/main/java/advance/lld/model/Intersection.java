package advance.lld.model;

import advance.lld.SignalController;
import advance.lld.SignalScheduler;
import advance.lld.strategy.SignalControlStrategy;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Intersection {
    @Getter
    private final String id;
    private final Map<Direction, TrafficApproach> approaches =
            new EnumMap<>(Direction.class);
    private SignalController controller;
    private SignalScheduler scheduler;
    private final ReentrantLock lock = new ReentrantLock();
    private volatile boolean running;
    private boolean stopRequested;

    public Intersection(
            String id,
            SignalControlStrategy strategy,
            long tickIntervalMillis
    ) {
        this.id = Objects.requireNonNull(id);
        this.controller = new SignalController(strategy);
        this.scheduler = new SignalScheduler(this, tickIntervalMillis);
    }

    public void addApproach(TrafficApproach approach) {
        lock.lock();
        try {
            if (running) {
                throw new IllegalStateException(
                        "Cannot add approaches while intersection is running."
                );
            }

            TrafficApproach old = approaches.putIfAbsent(
                    approach.getDirection(),
                    approach
            );

            if (old != null) {
                throw new IllegalArgumentException(
                        "Approach already configured for " + approach.getDirection()
                );
            }
        } finally {
            lock.unlock();
        }
    }

    public void start() {
        lock.lock();
        try {
            if (running) {
                return;
            }

            if (approaches.isEmpty()) {
                throw new IllegalStateException("Intersection has no approaches.");
            }

            stopRequested = false;
            controller.resume();
            running = true;
            scheduler.start();
        } finally {
            lock.unlock();
        }
    }


    /* Called only by this intersection's scheduler. */
    public void scheduledTick() {
        lock.lock();
        try {
            if (!running) {
                return;
            }

            controller.tick(new ArrayList<>(approaches.values()));

            if (stopRequested && controller.isIdle()) {
                running = false;
                scheduler.stop();
                System.out.println("[" + id + "] stopped safely. All signals are RED.");
            }
        } catch (RuntimeException ex) {
            System.err.println("[" + id + "] tick failed: " + ex.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void updateTraffic(Direction direction, int vehicleCount) {
        TrafficApproach approach = getApproach(direction);
        approach.getSensor().updateVehicleCount(vehicleCount);
    }

    public void requestEmergencyPriority(Direction direction) {
        lock.lock();
        try {
            ensureRunning();
            controller.requestEmergency(
                    new ArrayList<>(approaches.values()),
                    direction
            );
        } finally {
            lock.unlock();
        }
    }

    public void setStrategy(SignalControlStrategy strategy) {
        lock.lock();
        try {
            controller.setStrategy(strategy);
        } finally {
            lock.unlock();
        }
    }

    public void stopSafely() {
        lock.lock();
        try {
            if (!running || stopRequested) {
                return;
            }

            stopRequested = true;
            controller.requestSafeStop();

            // If nothing was active, we can stop immediately.
            if (controller.isIdle()) {
                running = false;
                scheduler.stop();
                System.out.println("[" + id + "] stopped safely. All signals are RED.");
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isRunning() {
        return running;
    }

    private TrafficApproach getApproach(Direction direction) {
        TrafficApproach approach = approaches.get(direction);
        if (approach == null) {
            throw new IllegalArgumentException(
                    "No approach configured for " + direction
            );
        }
        return approach;
    }

    private void ensureRunning() {
        if (!running) {
            throw new IllegalStateException(
                    "Intersection " + id + " is not running."
            );
        }
    }
}
