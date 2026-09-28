package advance.lld.strategy;

import advance.lld.model.TrafficApproach;

import java.util.List;

public class FixedTimeStrategy implements SignalControlStrategy {

    @Override
    public TrafficApproach selectNextApproach(
            List<TrafficApproach> approaches,
            TrafficApproach lastCompletedApproach
    ) {
        if (approaches.isEmpty()) {
            return null;
        }

        if (lastCompletedApproach == null) {
            return approaches.get(0);
        }

        int index = approaches.indexOf(lastCompletedApproach);

        if (index == -1) {
            return approaches.get(0);
        }

        return approaches.get((index + 1) % approaches.size());
    }
}
