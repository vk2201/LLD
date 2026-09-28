package advance.lld;

import advance.lld.model.*;
import advance.lld.strategy.DensityBasedStrategy;
import advance.lld.strategy.FixedTimeStrategy;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Intersection intersection = new Intersection(
                "INT-1",
                new FixedTimeStrategy(),
                1000
        );

        intersection.addApproach(
                new TrafficApproach("N", Direction.NORTH, new TrafficSignal(3,1), new TrafficSensor())
        );
        intersection.addApproach(
                new TrafficApproach("E", Direction.EAST, new TrafficSignal(3,1), new TrafficSensor())
        );
        intersection.addApproach(
                new TrafficApproach("S", Direction.SOUTH, new TrafficSignal(3,1), new TrafficSensor())
        );
        intersection.addApproach(
                new TrafficApproach("W", Direction.WEST, new TrafficSignal(3,1), new TrafficSensor())
        );

        TrafficControlSystem system = new TrafficControlSystem();
        system.addIntersection(intersection);

        // Sensor updates can happen asynchronously.
        intersection.updateTraffic(Direction.NORTH, 8);
        intersection.updateTraffic(Direction.EAST, 3);
        intersection.updateTraffic(Direction.SOUTH, 5);
        intersection.updateTraffic(Direction.WEST, 2);

        //intersection.start();
        system.startAll();

        Thread.sleep(4500);

        System.out.println("\n--- Emergency vehicle from WEST ---");
        intersection.requestEmergencyPriority(Direction.WEST);

        Thread.sleep(4000);

        System.out.println("\n--- Switching to density-based strategy ---");
        intersection.setStrategy(new DensityBasedStrategy());
        intersection.updateTraffic(Direction.NORTH, 4);
        intersection.updateTraffic(Direction.EAST, 14);
        intersection.updateTraffic(Direction.SOUTH, 7);
        intersection.updateTraffic(Direction.WEST, 1);

        Thread.sleep(4000);

        System.out.println("\n--- Safe stop requested ---");
        system.startAll();

        Thread.sleep(2500);
        System.out.println("Intersection running = " + intersection.isRunning());
    }
}