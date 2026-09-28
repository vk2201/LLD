package advance.lld;

import advance.lld.model.Direction;
import advance.lld.model.SignalColor;
import advance.lld.model.TrafficApproach;
import advance.lld.model.TrafficSignal;
import advance.lld.strategy.SignalControlStrategy;

import java.util.List;
import java.util.Objects;

public class SignalController {
    private SignalControlStrategy strategy;

    private TrafficApproach currentApproach;
    private TrafficApproach lastCompletedApproach;
    private TrafficApproach pendingEmergencyApproach;

    private boolean stopRequested;

    public SignalController(SignalControlStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    public void tick(List<TrafficApproach> approaches) {
        if (approaches.isEmpty()) {
            return;
        }

        if (currentApproach == null) {
            if (!stopRequested) {
                activateNextApproach(approaches);
            }
            return;
        }

        TrafficSignal signal = currentApproach.getSignal();

        if (!signal.tickAndIsPhaseComplete()) {
            return;
        }

        switch (signal.getColor()) {
            case GREEN:
                transitionCurrentToYellow();
                break;

            case YEllOW:
                completeCurrentApproach();

                if (!stopRequested) {
                    activateNextApproach(approaches);
                }
                break;

            case RED:
                // Defensive fallback. A current approach should normally
                // never remain assigned while RED.
                lastCompletedApproach = currentApproach;
                currentApproach = null;

                if (!stopRequested) {
                    activateNextApproach(approaches);
                }
                break;
        }
    }

    public void requestEmergency(
            List<TrafficApproach> approaches,
            Direction emergencyDirection
    ) {
        TrafficApproach emergencyApproach = approaches.stream()
                .filter(a -> a.getDirection() == emergencyDirection)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No approach for emergency direction: " + emergencyDirection
                ));

        // If emergency direction already has GREEN, keep it GREEN longer.
        if (currentApproach == emergencyApproach
                && currentApproach.getSignal().getColor() == SignalColor.GREEN) {

            currentApproach.getSignal().restartCurrentPhaseTimer();
            System.out.println(
                    "Emergency already on active approach " + emergencyDirection
                            + ". Extending GREEN."
            );
            return;
        }

        pendingEmergencyApproach = emergencyApproach;

        if (currentApproach == null) {
            activateNextApproach(approaches);
            return;
        }

        // Safe preemption: GREEN -> YELLOW.
        // Never GREEN -> RED directly.
        if (currentApproach.getSignal().getColor() == SignalColor.GREEN) {
            transitionCurrentToYellow();
        }
    }

    public void requestSafeStop() {
        stopRequested = true;
        pendingEmergencyApproach = null;

        if (currentApproach == null) {
            return;
        }

        SignalColor color = currentApproach.getSignal().getColor();

        if (color == SignalColor.GREEN) {
            // Start a valid safe shutdown transition.
            transitionCurrentToYellow();
        } else if (color == SignalColor.RED) {
            lastCompletedApproach = currentApproach;
            currentApproach = null;
        }
        // If already YELLOW, let the scheduler finish YELLOW -> RED.
    }

    public void resume() {
        stopRequested = false;
        pendingEmergencyApproach = null;
    }

    public boolean isIdle() {
        return currentApproach == null;
    }

    public void setStrategy(SignalControlStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy);
    }

    private void activateNextApproach(List<TrafficApproach> approaches) {
        TrafficApproach next;

        if (pendingEmergencyApproach != null) {
            next = pendingEmergencyApproach;
            pendingEmergencyApproach = null;
        } else {
            next = strategy.selectNextApproach(
                    approaches,
                    lastCompletedApproach
            );
        }

        if (next == null) {
            return;
        }

        ensureAllOtherSignalsAreRed(approaches, next);

        if (next.getSignal().getColor() != SignalColor.RED) {
            throw new IllegalStateException(
                    "Next approach must be RED before becoming GREEN: "
                            + next.getDirection()
            );
        }

        next.getSignal().transition(); // RED -> GREEN
        currentApproach = next;

        System.out.println(
                "GREEN  -> " + currentApproach.getDirection()
        );
    }

    private void transitionCurrentToYellow() {
        TrafficSignal signal = currentApproach.getSignal();

        if (signal.getColor() != SignalColor.GREEN) {
            return;
        }

        signal.transition(); // GREEN -> YELLOW

        System.out.println(
                "YELLOW -> " + currentApproach.getDirection()
        );
    }

    private void completeCurrentApproach() {
        TrafficSignal signal = currentApproach.getSignal();

        if (signal.getColor() != SignalColor.YEllOW) {
            throw new IllegalStateException(
                    "Expected YELLOW before RED."
            );
        }

        signal.transition(); // YELLOW -> RED

        System.out.println(
                "RED    -> " + currentApproach.getDirection()
        );

        lastCompletedApproach = currentApproach;
        currentApproach = null;
    }

    private void ensureAllOtherSignalsAreRed(
            List<TrafficApproach> approaches,
            TrafficApproach selected
    ) {
        for (TrafficApproach approach : approaches) {
            if (approach != selected
                    && approach.getSignal().getColor() != SignalColor.RED) {

                throw new IllegalStateException(
                        "Safety invariant violated. "
                                + approach.getDirection()
                                + " is not RED while activating "
                                + selected.getDirection()
                );
            }
        }
    }

}
