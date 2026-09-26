package advance.lld.model;

import java.util.concurrent.atomic.AtomicInteger;

public class TrafficSensor {
    private final AtomicInteger vehicleCount = new AtomicInteger();

    public void updateVehicleCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException(
                    "Vehicle count cannot be negative."
            );
        }

        vehicleCount.set(count);
    }
    public int getVehicleCount() {
        return vehicleCount.get();
    }
}
