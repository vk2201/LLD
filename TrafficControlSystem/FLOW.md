# Traffic Control System — LLD Flow Diagrams

**Patterns:** State (`RedState` / `GreenState` / `YellowState`) · Strategy (`FixedTimeStrategy` / `DensityBasedStrategy`) · Scheduler (`ScheduledExecutorService`, one per intersection)

**One-line summary:** each `Intersection` has a scheduler thread that calls `scheduledTick()` every N ms. The `SignalController` decides which approach turns GREEN (using the strategy), and each `TrafficSignal` counts its own ticks through the state pattern. Only one approach is ever non-RED.

## 1. Big picture

```mermaid
flowchart TD
    TCS[TrafficControlSystem<br/>Map id → Intersection] -->|startAll / stopAll| I
    subgraph I[Intersection INT-1 · ReentrantLock]
        direction TB
        SCH[SignalScheduler<br/>1 thread, fixed rate] -->|every tickMs| ST[scheduledTick]
        ST --> C[SignalController.tick]
        C -->|who is next?| STR{{SignalControlStrategy}}
        C --> A1[Approach NORTH<br/>Signal + Sensor]
        C --> A2[Approach SOUTH]
        C --> A3[Approach EAST]
        C --> A4[Approach WEST]
    end
    STR -.-> F[FixedTime: round-robin]
    STR -.-> D[DensityBased: max vehicleCount,<br/>skip last served]
    EXT[Sensors / Operator] -->|updateTraffic · requestEmergencyPriority · setStrategy| I
```

## 2. Class diagram

```mermaid
classDiagram
    TrafficControlSystem "1" --> "*" Intersection
    Intersection "1" --> "1..4" TrafficApproach : EnumMap~Direction~
    Intersection --> SignalController
    Intersection --> SignalScheduler
    SignalScheduler ..> Intersection : calls scheduledTick()
    SignalController --> SignalControlStrategy
    SignalControlStrategy <|.. FixedTimeStrategy
    SignalControlStrategy <|.. DensityBasedStrategy
    TrafficApproach --> Direction
    TrafficApproach --> TrafficSignal
    TrafficApproach --> TrafficSensor
    TrafficSignal --> SignalState
    SignalState <|.. RedState
    SignalState <|.. GreenState
    SignalState <|.. YellowState

    class TrafficControlSystem {
        -ConcurrentHashMap intersections
        +addIntersection(i)
        +startAll()
        +stopAll()
    }
    class Intersection {
        -ReentrantLock lock
        -volatile boolean running
        -boolean stopRequested
        +addApproach(a)
        +start()
        +scheduledTick()
        +updateTraffic(dir, count)
        +requestEmergencyPriority(dir)
        +setStrategy(s)
        +stopSafely()
    }
    class SignalController {
        -currentApproach
        -lastCompletedApproach
        -pendingEmergencyApproach
        -boolean stopRequested
        +tick(approaches)
        +requestEmergency(approaches, dir)
        +requestSafeStop()
        +isIdle()
    }
    class SignalScheduler {
        -ScheduledExecutorService executor
        +start()
        +stop()
    }
    class TrafficSignal {
        -SignalState state = Red
        -int greenTicks
        -int yellowTicks
        -int elapsedTicks
        +getColor() sync
        +transition() sync
        +tickAndIsPhaseComplete() sync
        +restartCurrentPhaseTimer()
    }
    class SignalState {
        <<interface>>
        +color() SignalColor
        +next() SignalState
        +durationTicks(green, yellow) int
    }
    class SignalControlStrategy {
        <<interface>>
        +selectNextApproach(approaches, lastCompleted)
    }
    class TrafficSensor {
        -AtomicInteger vehicleCount
        +updateVehicleCount(n)
    }
```

## 3. Signal state (State pattern)

```mermaid
stateDiagram-v2
    direction LR
    [*] --> RED
    RED --> GREEN : controller picks it (next())
    GREEN --> YELLOW : greenTicks elapsed / emergency / safe stop
    YELLOW --> RED : yellowTicks elapsed
```

| State  | `durationTicks` | `next()` |
|--------|-----------------|----------|
| RED    | 0: stays RED until the controller picks it | GREEN |
| GREEN  | greenTicks      | YELLOW |
| YELLOW | yellowTicks     | RED |

**Safety rule:** the code never goes GREEN → RED directly, and it never makes a signal GREEN while another one is not RED (`ensureAllOtherSignalsAreRed` throws if that would happen).

## 4. What happens on each tick (`SignalController.tick`)

```mermaid
flowchart TD
    T([tick]) --> Q{currentApproach == null?}
    Q -- yes --> S1{stopRequested?}
    S1 -- no --> ACT[activateNextApproach]
    S1 -- yes --> E([idle])
    Q -- no --> P{signal.tickAndIsPhaseComplete?}
    P -- no --> E2([wait])
    P -- yes --> COL{color}
    COL -- GREEN --> Y[transition → YELLOW]
    COL -- YELLOW --> R[transition → RED<br/>lastCompleted = current<br/>current = null]
    R --> S2{stopRequested?}
    S2 -- no --> ACT
    ACT --> EM{pendingEmergency?}
    EM -- yes --> N1[next = emergency approach]
    EM -- no --> N2[next = strategy.selectNextApproach]
    N1 --> SAFE[ensureAllOtherSignalsAreRed]
    N2 --> SAFE
    SAFE --> G[next: RED → GREEN<br/>current = next]
```

### Example timeline from `Main` (tick = 1 s, green = 3 ticks, yellow = 1 tick, FixedTime)

```mermaid
gantt
    dateFormat s
    axisFormat %S
    section NORTH
    GREEN :n1, 0, 3s
    YELLOW :n2, after n1, 1s
    section SOUTH
    GREEN :s1, after n2, 3s
    YELLOW :s2, after s1, 1s
    section EAST
    GREEN :e1, after s2, 3s
```

Round-robin follows `EnumMap` order: **NORTH → SOUTH → EAST → WEST**.

## 5. Emergency preemption

```mermaid
sequenceDiagram
    actor Op as Emergency (WEST)
    participant I as Intersection (lock)
    participant C as SignalController
    participant Cur as Current signal (e.g. NORTH)
    participant W as WEST signal

    Op->>I: requestEmergencyPriority(WEST)
    I->>C: requestEmergency(approaches, WEST)
    alt WEST already GREEN
        C->>W: restartCurrentPhaseTimer() → longer GREEN
    else another approach active
        C->>C: pendingEmergency = WEST
        opt current is GREEN
            C->>Cur: transition GREEN → YELLOW (safe preempt)
        end
        Note over C: next ticks: YELLOW → RED
        C->>W: activateNext picks pendingEmergency → GREEN
    else nothing active
        C->>W: activate now → GREEN
    end
```

## 6. Safe stop

```mermaid
sequenceDiagram
    participant TCS as TrafficControlSystem
    participant I as Intersection
    participant C as SignalController
    participant S as SignalScheduler
    TCS->>I: stopAll → stopSafely()
    I->>C: requestSafeStop (clears pending emergency)
    alt current GREEN
        C->>C: GREEN → YELLOW
    end
    loop scheduler ticks
        I->>C: tick → YELLOW → RED, current = null (no new GREEN)
        I->>I: stopRequested && controller.isIdle()?
    end
    I->>S: stop() · running = false
    Note over I: "stopped safely. All signals are RED."
```

## 7. Strategies

```mermaid
flowchart LR
    subgraph Fixed[FixedTimeStrategy]
        F1[last == null → approaches 0] --> F2[else index of last + 1 mod n]
    end
    subgraph Density[DensityBasedStrategy]
        D1[drop lastCompleted if n > 1] --> D2[max by sensor.vehicleCount]
    end
```

The strategy can be swapped at runtime with `intersection.setStrategy(...)`, under the lock. It takes effect the next time a signal is picked.

## 8. Revision points

- **Threading:**
  - **One scheduler thread per intersection:** each has its own single-thread executor (daemon), so intersections run independently.
  - **One lock per intersection:** a `ReentrantLock` guards everything the intersection does (tick, emergency, strategy, stop), so `SignalController` itself doesn't need to be thread-safe.
  - **Sensors don't take the lock:** they update through `AtomicInteger`.
  - **`running` is `volatile`:** so `isRunning()` can read it without taking the lock.
- **Ticks, not sleeps:** each signal counts `elapsedTicks` against its phase length, which keeps the design deterministic and easy to test.
- **The controller owns the choice of the next GREEN.** A RED signal never turns itself GREEN (its duration is 0).
- **Emergency is preemption, not a hard cut:** the current signal goes GREEN → YELLOW → RED, then the emergency approach goes GREEN.
- **Safe stop:** let the current cycle finish to all-RED, then shut down the executor.
- **Failures:** `scheduledTick` catches `RuntimeException`. Otherwise one exception would silently stop `scheduleAtFixedRate` forever.

<details><summary>Gaps in the current code</summary>

- `Main`'s "Safe stop requested" step calls `system.startAll()`. It should be `stopAll()`, so the demo never stops.
- The `TrafficSignal(redTicks, yellowTicks)` constructor: `redTicks` is actually used as the **green** duration (`GreenState.durationTicks` returns the first argument). Rename it to `greenTicks`.
- The density strategy only chooses **who** goes next. Green time stays fixed and doesn't grow with vehicle count.
- `SignalColor.YEllOW` has a typo. `TrafficApproach.vehicleCount` is unused (the sensor holds the count).
- `SignalScheduler.start()` is not `synchronized` but `stop()` is. That's safe today only because both are called under the intersection lock.
- `read.txt` TODOs: move timings into a timing object, and allow starting a single intersection from `TrafficControlSystem`.
</details>
