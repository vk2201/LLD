package advance.lld.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class TrafficApproach {

    @Getter
    private final String id;
    @Getter
    @Setter
    private Direction direction;
    @Getter
    private TrafficSensor sensor;
    @Getter
    private TrafficSignal signal;
    private int vehicleCount;

    public TrafficApproach(
            String id,
            Direction direction,
            TrafficSignal signal,
            TrafficSensor sensor
    ) {
        this.id = Objects.requireNonNull(id);
        this.direction = Objects.requireNonNull(direction);
        this.signal = Objects.requireNonNull(signal);
        this.sensor = Objects.requireNonNull(sensor);
    }
}
