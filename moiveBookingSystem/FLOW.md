# Movie Booking System — LLD Flow Diagrams

**Patterns:** Strategy (`PricingStrategy`, `PaymentStrategy`) · Service layer · per-Show lock for concurrency

## 1. End-to-end flow

```mermaid
flowchart TD
    A[Setup: Cinema → Screen → Seats] --> B[Create Movie + Show]
    B --> C[PricingService.initializeShowSeats<br/>Seat + price → ShowSeat]
    C --> D[SearchService.findShowByMovieAndCity]
    D --> E[User picks show + seatIds]
    E --> F[SeatLockService.lockSeats<br/>AVAILABLE → LOCKED, 5 min expiry]
    F --> G[BookingService.createBooking<br/>status = PAYMENT_PENDING]
    G --> H[PaymentService.pay booking, UPI]
    H --> I{strategy.pay?}
    I -- success --> J[confirmBooking<br/>LOCKED → BOOKED<br/>Booking CONFIRMED]
    I -- fail --> K[failBooking<br/>LOCKED → AVAILABLE<br/>Booking FAILED]
```

## 2. Class diagram

```mermaid
classDiagram
    Cinema "1" --> "*" Screen
    Screen "1" --> "*" Seat
    Show --> Movie
    Show --> Screen
    Show "1" --> "*" ShowSeat
    ShowSeat --> Seat
    Booking --> User
    Booking --> Show
    Booking "1" --> "*" ShowSeat
    Booking --> Payment

    class Cinema {
        id
        name
        city
        address
    }
    class Seat {
        id
        row
        col
        SeatType
    }
    class Show {
        id
        startTime
        endTime
        Object lock
    }
    class ShowSeat {
        SeatStatus seatStatus
        double price
        lockedByUserId
        lockExpiryTime
        +lock(userId, expiry)
        +unlock()
        +book()
    }
    class Booking {
        id
        totalAmount
        BookingStatus status
    }
    class Payment {
        id
        txnId
        amount
        PaymentMethod
        PaymentStatus
    }

    class PricingStrategy {
        <<interface>>
        +calculatePrice(show, seat)
    }
    PricingStrategy <|.. DefaultPricingStrategy
    class PaymentStrategy {
        <<interface>>
        +pay() boolean
    }
    PaymentStrategy <|.. UpiPaymentStrategy

    PricingService --> PricingStrategy
    PaymentService --> PaymentStrategy : Map method→strategy
    PaymentService --> BookingService
    BookingService --> SeatLockService
    SearchService --> Show
```

## 3. Booking + payment (sequence)

```mermaid
sequenceDiagram
    actor U as User
    participant L as SeatLockService
    participant BS as BookingService
    participant PS as PaymentService
    participant ST as PaymentStrategy
    participant SH as Show.lock

    U->>L: lockSeats([A1], show, user)
    L->>SH: synchronized(show.lock)
    L->>L: all seats exist & AVAILABLE?
    L->>L: seat.lock(userId, now+5m)
    U->>BS: createBooking(user, [A1], show)
    BS-->>U: Booking(PAYMENT_PENDING, total = Σ price)
    U->>PS: pay(booking, UPI)
    PS->>ST: pay()
    alt success
        PS->>BS: confirmBooking
        BS->>SH: synchronized: all LOCKED? → book()
    else failure
        PS->>BS: failBooking
        BS->>L: unlockSeats → AVAILABLE
    end
```

## 4. State machines

```mermaid
stateDiagram-v2
    direction LR
    state "ShowSeat" as SS {
        [*] --> AVAILABLE
        AVAILABLE --> LOCKED : lockSeats
        LOCKED --> BOOKED : confirmBooking
        LOCKED --> AVAILABLE : failBooking / unlock
    }
```

```mermaid
stateDiagram-v2
    direction LR
    [*] --> PAYMENT_PENDING
    PAYMENT_PENDING --> CONFIRMED : payment success
    PAYMENT_PENDING --> FAILED : payment failed
    PAYMENT_PENDING --> EXPIRED : lock timeout (enum only)
```

## 5. Revision points

- **Why `ShowSeat` and not `Seat`?** A `Seat` is physical and belongs to a screen. A `ShowSeat` is that seat for one particular show, and it holds the status and price.
- **Concurrency:** every change to seat status goes through `synchronized (show.getLock())`. The lock is per show, so different shows never block each other.
- **Two-phase booking:** LOCK (temporary hold with expiry), then PAY, then BOOK or UNLOCK.
- Pricing depends on `SeatType`: BASIC = 200, RECLINER = 350.

<details><summary>Gaps in the current code</summary>

- `lockExpiryTime` is stored but never checked. No expiry job exists, and nothing moves a booking to `EXPIRED`.
- `confirmBooking` doesn't check that `lockedByUserId` matches the booking's user.
- `Booking.payment` is never set.
</details>
