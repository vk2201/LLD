package advance.lld.model.state;

import advance.lld.model.SignalColor;
import advance.lld.model.TrafficSignal;

public interface SignalState {

    SignalColor color();

    SignalState next();
    int durationTicks(int greenTicks, int yellowTicks);
}
