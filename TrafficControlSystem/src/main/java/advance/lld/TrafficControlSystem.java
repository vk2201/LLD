package advance.lld;

import advance.lld.model.Intersection;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class TrafficControlSystem {
    private final Map<String, Intersection> intersections = new ConcurrentHashMap<>();

    public void addIntersection(Intersection intersection) {
        Objects.requireNonNull(intersection);

        if (intersections.putIfAbsent(intersection.getId(), intersection) != null) {
            throw new IllegalArgumentException(
                    "Intersection already exists: " + intersection.getId()
            );
        }
    }

    public Intersection getIntersection(String id) {
        Intersection intersection = intersections.get(id);
        if (intersection == null) {
            throw new IllegalArgumentException("Unknown intersection: " + id);
        }
        return intersection;
    }

    public void startAll() {
        for (Intersection intersection
                : intersections.values()) {
            intersection.start();
        }
    }

    public void stopAll() {
        for (Intersection intersection
                : intersections.values()) {
            intersection.stopSafely();
        }
    }
}
