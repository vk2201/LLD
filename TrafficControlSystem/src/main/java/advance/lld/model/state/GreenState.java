package advance.lld.model.state;

import advance.lld.model.SignalColor;
import advance.lld.model.TrafficSignal;

public class GreenState implements SignalState {
    @Override
    public SignalColor color() {
        return SignalColor.GREEN;
    }

    @Override
    public SignalState next() {
        return new YellowState();
    }

    @Override
    public int durationTicks(int greenTicks, int yellowTicks) {
        return greenTicks;
    }
}
