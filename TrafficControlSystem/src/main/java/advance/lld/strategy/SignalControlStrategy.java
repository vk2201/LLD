package advance.lld.strategy;

import advance.lld.model.Direction;
import advance.lld.model.TrafficApproach;

import java.util.List;

public interface SignalControlStrategy {
    public TrafficApproach selectNextApproach(
            List<TrafficApproach> approaches,
            TrafficApproach lastCompletedApproach
    );
}
