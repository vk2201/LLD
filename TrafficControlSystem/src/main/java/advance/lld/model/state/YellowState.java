package advance.lld.model.state;

import advance.lld.model.SignalColor;

public class YellowState implements SignalState {
    @Override
    public SignalColor color() {
        return SignalColor.YEllOW;
    }

    @Override
    public SignalState next() {
        return new RedState();
    }

    @Override
    public int durationTicks(int greenTicks, int yellowTicks) {
        return yellowTicks;
    }
}
