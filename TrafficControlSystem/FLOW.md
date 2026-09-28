# Traffic Control System — LLD Flow Diagrams

> ⚠️ **Work in progress.** Only `TrafficSensor` has an implementation. The other classes are empty stubs. The diagrams below show the design the class and package names point to, so you can use them as a blueprint while you finish the code.

**Patterns (planned):** State (`RedState` / `GreenState` / `YellowState`) · Strategy (`FixedTimeStrategy` / `DensityBasedStrategy`)

## 1. Class diagram (planned)

```mermaid
classDiagram
    TrafficControlSystem "1" --> "*" Intersection
    Intersection "1" --> "*" TrafficApproach
    TrafficApproach --> Direction
    TrafficApproach --> TrafficSignal
    TrafficApproach --> TrafficSensor
    TrafficSignal --> SignalState : current state
    SignalState <|.. RedState
    SignalState <|.. GreenState
    SignalState <|.. YellowState
    Intersection --> SignalController
    SignalController --> SignalControlStrategy
    SignalControlStrategy <|.. FixedTimeStrategy
    SignalControlStrategy <|.. DensityBasedStrategy
    SignalScheduler --> SignalController : tick

    class Direction { <<enum>> NORTH; SOUTH; EAST; WEST }
    class TrafficSensor {
        -AtomicInteger vehicleCount
        +updateVehicleCount(n)
        +getVehicleCount()
    }
    class SignalState {
        <<interface>>
        +next(signal)
        +getColor() SignalColor
    }
    class SignalControlStrategy {
        <<interface>>
        +nextGreen(approaches) TrafficApproach
        +greenDuration(approach) int
    }
```

## 2. Signal state machine

```mermaid
stateDiagram-v2
    direction LR
    [*] --> RED
    RED --> GREEN : controller picks this approach
    GREEN --> YELLOW : green time over
    YELLOW --> RED : yellow time over
```

## 3. Control loop (planned)

```mermaid
sequenceDiagram
    participant SCH as SignalScheduler
    participant C as SignalController
    participant ST as SignalControlStrategy
    participant SE as TrafficSensor
    participant SG as TrafficSignal

    loop every cycle
        SCH->>C: tick()
        C->>SE: getVehicleCount() per approach
        C->>ST: nextGreen(approaches) / greenDuration
        Note over ST: Fixed: round-robin, same time<br/>Density: most vehicles, longer green
        C->>SG: current GREEN → YELLOW → RED
        C->>SG: chosen approach RED → GREEN
    end
```

## 4. Revision points

- **Safety rule:** an intersection has at most one conflicting approach on GREEN at a time. The controller turns the current signal RED before it sets the next one GREEN.
- **Strategy swap:** `FixedTimeStrategy` suits predictable traffic, and `DensityBasedStrategy` reads the sensor counts. You can change strategy at runtime.
- **Concurrency:** the sensor count uses `AtomicInteger` because sensors update from other threads. The scheduler can run on a `ScheduledExecutorService`.
