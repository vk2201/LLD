package advance.lld.strategy;

import advance.lld.model.TrafficApproach;

import java.util.Comparator;
import java.util.List;

public class DensityBasedStrategy implements SignalControlStrategy {

    @Override
    public TrafficApproach selectNextApproach(
            List<TrafficApproach> approaches,
            TrafficApproach lastCompletedApproach
    ) {
        if (approaches.isEmpty()) {
            return null;
        }

        List<TrafficApproach> candidates = approaches;

        // Small fairness rule: if alternatives exist, do not immediately
        // choose the same approach again.
        if (approaches.size() > 1 && lastCompletedApproach != null) {
            candidates = approaches.stream()
                    .filter(a -> a != lastCompletedApproach)
                    .toList();
        }

        return candidates.stream()
                .max(Comparator.comparingInt(
                        a -> a.getSensor().getVehicleCount()
                ))
                .orElse(null);
    }
}
