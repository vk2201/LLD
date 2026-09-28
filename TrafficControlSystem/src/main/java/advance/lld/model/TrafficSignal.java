package advance.lld.model;

import advance.lld.model.state.RedState;
import advance.lld.model.state.SignalState;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class TrafficSignal {
    private SignalState state = new RedState();
    private final int redTicks;
    private final int yellowTicks;
    private int elapsedTicks;

    public TrafficSignal(int redTicks, int yellowTicks) {
        this.redTicks = redTicks;
        this.yellowTicks = yellowTicks;
    }

    public synchronized SignalColor getColor() {
        return state.color();
    }

    public synchronized void transition() {
        state = state.next();
        elapsedTicks = 0;
    }

    public synchronized boolean tickAndIsPhaseComplete() {
        int requiredTicks = state.durationTicks(redTicks, yellowTicks);

        if (requiredTicks <= 0) {
            return true;
        }
        elapsedTicks++;
        return elapsedTicks >= requiredTicks;
    }

    public synchronized void restartCurrentPhaseTimer() {
        elapsedTicks = 0;
    }
}
