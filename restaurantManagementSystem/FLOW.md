# Restaurant Management System — LLD Flow Diagrams

**Patterns:** Decorator (bill) · Observer (order item → waiter) · Strategy (payment) · per-Table lock

## 1. End-to-end flow

```mermaid
flowchart TD
    A[Restaurant → Branch<br/>Menu, Tables, Chefs, Waiters] --> B{Customer type}
    B -- reservation --> R[TableAllocationService.reserveTable<br/>capacity + time-overlap check]
    R --> CI[checkIn → Reservation CHECKED_IN<br/>table.occupy]
    B -- walk-in --> W[allocateWalkIn<br/>first AVAILABLE table that fits]
    W --> O
    CI --> O[OrderService.getOrCreateOrder<br/>one active order per table]
    O --> AI[addItem → OrderItem PLACED<br/>waiter registered as observer]
    AI --> P[Chef: prepareOrderItem → PREPARING]
    P --> RD[markOrderItemReady → READY<br/>notify Waiter 🔔]
    RD --> BL[BillingService.getBill<br/>ServiceCharge wraps BaseOrderAmount]
    BL --> PAY[PaymentService.makePayment<br/>split payments allowed]
    PAY --> ST{remaining == 0?}
    ST -- no --> PP[PARTIALLY_PAID] --> PAY
    ST -- yes --> PD[PAID] --> REL[releaseTable → AVAILABLE]
```

## 2. Class diagram

```mermaid
classDiagram
    Restaurant "1" --> "*" Branch
    Branch --> Menu
    Menu "1" --> "*" MenuItem
    Branch "1" --> "*" Table
    Branch "1" --> "*" Chef
    Branch "1" --> "*" Waiter
    Person <|-- Customer
    Person <|-- Employee
    Employee <|-- Chef
    Employee <|-- Waiter
    Table "1" --> "*" Reservations
    Table --> Order : activeOrder
    Order "1" --> "*" OrderItem
    OrderItem --> MenuItem
    Order --> Bill
    Bill "1" --> "*" Payment

    class OrderItemObserver {
        <<interface>>
        +updateOnOrderItem()
    }
    OrderItemObserver <|.. Waiter
    OrderItem --> OrderItemObserver : observers

    class BillComponent {
        <<interface>>
        +calculate() Double
    }
    BillComponent <|.. BaseOrderAmount
    BillComponent <|.. BillDecorator
    BillDecorator <|-- ServiceChargeDecorator
    BillDecorator o--> BillComponent : wraps

    class PaymentStrategy {
        <<interface>>
        +process(amount, method) Payment
    }
    PaymentStrategy <|.. CashPaymentStrategy
    PaymentStrategy <|.. CardPaymentStrategy
    PaymentStrategy <|.. UpiPaymentStrategy

    class Table {
        status
        capacity
        Object lock
        +occupy()
        +release()
    }
    class Bill {
        totalAmount
        BillStatus
        +addPayment() sync
        +getRemainingAmount() sync
    }
```

## 3. Decorator: how the bill is calculated

```mermaid
flowchart LR
    SC["ServiceChargeDecorator(5%)"] -->|calculate = inner + charge| BO["BaseOrderAmount(order)"]
    BO -->|Σ price × qty| OI[OrderItems]
```

`new ServiceChargeDecorator(new BaseOrderAmount(order), 5.0)`. To stack more charges, keep wrapping: `new GstDecorator(new ServiceCharge(new Base(...)))`.

## 4. Observer: order item ready

```mermaid
sequenceDiagram
    participant OS as OrderService
    participant OI as OrderItem
    participant W as Waiter (observer)
    OS->>OI: addItem → addObserver(waiter)
    OS->>OI: prepareOrderItem → startPreparing()
    OS->>OI: markOrderItemReady → markOrderItemPrepared()
    OI->>OI: status = READY
    OI->>W: notifyObserver → updateOnOrderItem()
```

## 5. State machines

```mermaid
stateDiagram-v2
    direction LR
    [*] --> AVAILABLE
    AVAILABLE --> OCCUPIED : occupy (walk-in / check-in)
    OCCUPIED --> AVAILABLE : release
    AVAILABLE --> OUT_OF_SERVICE
```

```mermaid
stateDiagram-v2
    direction LR
    [*] --> PLACED
    PLACED --> PREPARING : chef starts
    PREPARING --> READY : notify waiter
    READY --> SERVED
```

```mermaid
stateDiagram-v2
    direction LR
    [*] --> OPEN
    OPEN --> PARTIALLY_PAID : payment < remaining
    OPEN --> PAID : payment == remaining
    PARTIALLY_PAID --> PAID
```

## 6. Revision points

- **Concurrency:** each `Table` has its own `lock` object. Allocation, order creation, and billing all run inside `synchronized(table.getLock())`. `Bill.addPayment` is `synchronized`.
- **Split payments:** `makePayment` rejects any amount above `remaining`. The bill is `PAID` once `remaining == 0`.
- **Reservation overlap:** two bookings overlap when `newFrom < existingEnd && newTo > existingStart`.

<details><summary>Gaps in the current code</summary>

- `ServiceChargeDecorator` does `+ pct/100`. It should be `+ inner * pct/100`.
- `getBill` doesn't store the bill on the order, so the "return existing bill" check never hits.
- `reserveTable` reserves **every** eligible table instead of the first, and a new reservation's `status` is `null`.
- `Branch.addChef/addWaiter` are empty, and `Waiter.updateOnOrderItem` does nothing.
</details>
