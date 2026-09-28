package advance.lld.model.state;

import advance.lld.model.SignalColor;
import advance.lld.model.TrafficSignal;

public class RedState implements SignalState {
    @Override
    public SignalColor color() {
        return SignalColor.RED;
    }

    @Override
    public SignalState next() {
        return new GreenState();
    }

    @Override
    public int durationTicks(int greenTicks, int yellowTicks) {
        // RED signals that are not selected simply stay RED.
        // The controller explicitly selects when RED -> GREEN happens.
        return 0;
    }
}
