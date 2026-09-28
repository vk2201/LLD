package advance.lld;

import advance.lld.model.Intersection;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SignalScheduler {
    private ScheduledExecutorService executorService;
    private Intersection intersection;
    private long tickIntervalMillis;

    public SignalScheduler(
            Intersection intersection,
            long tickIntervalMillis
    ) {
        if (tickIntervalMillis <= 0) {
            throw new IllegalArgumentException(
                    "Tick interval must be positive."
            );
        }

        this.intersection = Objects.requireNonNull(intersection);
        this.tickIntervalMillis = tickIntervalMillis;
    }

    public void start() {

        if(executorService != null && !executorService.isShutdown()) {
            return;
        }

        executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(
                    r,
                    "signal-scheduler-" + intersection.getId()
            );
            thread.setDaemon(true);
            return thread;
        });

        executorService.scheduleAtFixedRate(
                intersection::scheduledTick,
                0,
                tickIntervalMillis,
                TimeUnit.MILLISECONDS
        );
    }

    public synchronized void stop() {
        if (executorService == null) {
            return;
        }

        executorService.shutdown();
        executorService = null;
    }

}
