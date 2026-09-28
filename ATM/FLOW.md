# ATM — LLD Flow Diagrams

**Patterns:** State (`AtmState`) · Strategy (`DispenseStrategy`) · Service layer (Bank / Withdrawal / Deposit)

## 1. State machine (the core of this design)

```mermaid
stateDiagram-v2
    [*] --> IdleState
    IdleState --> CardInsertedState : insertCard(card)
    CardInsertedState --> AuthenticatedState : authenticate(pin) OK
    CardInsertedState --> CardInsertedState : wrong pin, attempts++
    CardInsertedState --> IdleState : attempts > 3, block
    CardInsertedState --> IdleState : ejectCard()
    AuthenticatedState --> AuthenticatedState : withdraw / deposit / balanceEnquiry / changePin
    AuthenticatedState --> IdleState : ejectCard()
```

| Operation        | Idle | CardInserted | Authenticated |
|------------------|:----:|:------------:|:-------------:|
| insertCard       | ✅   | ❌           | ❌            |
| authenticate     | ❌   | ✅           | ❌ (already)  |
| withdraw/deposit | ❌   | ❌           | ✅            |
| ejectCard        | ❌   | ✅           | ✅            |

`ATM` holds the current `atmState` and passes every call to it: `atm.withdraw(x)` becomes `atmState.withdraw(this, x)`.

## 2. Class diagram

```mermaid
classDiagram
    class ATM {
        -AtmState atmState
        -Card card
        -CashDispenser cashDispenser
        -BankService bankService
        -WithdrawalService withdrawalService
        -DepositService depositService
        +insertCard(card)
        +authenticate(pin)
        +withdraw(amount)
        +deposit(amount)
        +balanceEnquiry()
        +ejectCard()
    }
    class AtmState {
        <<interface>>
        +insertCard(atm, card)
        +authenticate(atm, pin)
        +withdraw(atm, amount)
        +deposit(atm, amount)
        +changePin(atm, pin)
        +balanceEnquiry(atm)
        +ejectCard(atm)
    }
    AtmState <|.. IdleState
    AtmState <|.. CardInsertedState
    AtmState <|.. AuthenticatedState
    ATM --> AtmState

    class CashDispenser {
        -Map~Denomination,Long~ inventory
        -DispenseStrategy strategy
        +canDispense(amount)
        +dispense(amount)
    }
    class DispenseStrategy {
        <<interface>>
        +calculate(amount, inventory) Map
    }
    DispenseStrategy <|.. GreedyDispenseStrategy
    CashDispenser --> DispenseStrategy
    ATM --> CashDispenser

    class BankService {
        -Map accounts
        -Map cards
        -Map transactions
        +authenticateCard(card, pin)
        +debit(amount, card)
        +credit(amount, card)
        +getAccountBalance(card)
        +saveTransaction(txn)
    }
    ATM --> BankService
    ATM --> WithdrawalService
    ATM --> DepositService
    WithdrawalService --> BankService
    WithdrawalService --> CashDispenser
    DepositService --> BankService

    class Card {
        cardNumber
        pin
        CardStatus status
        failedAttempts
    }
    class Account {
        accountNumber
        balance
        +debit() synchronized
        +credit() synchronized
    }
    Card --> Account
    ATM --> Card
    BankService --> Transaction
```

## 3. Withdraw flow (sequence)

```mermaid
sequenceDiagram
    actor U as User
    participant A as ATM
    participant S as AuthenticatedState
    participant W as WithdrawalService
    participant CD as CashDispenser
    participant G as GreedyDispenseStrategy
    participant B as BankService

    U->>A: withdraw(1500)
    A->>S: withdraw(atm, 1500)
    S->>W: withdraw(card, 1500)
    W->>W: isValidAmount? (>0 and multiple of 10)
    W->>CD: canDispense(1500)
    CD->>G: calculate(1500, inventory)
    G-->>CD: {500:3} or throw
    W->>B: getAccountBalance(card)
    alt balance >= amount
        W->>B: debit(1500, card)
        W->>CD: dispense(1500)
        Note over CD: synchronized<br/>calculate → deductInventory
        alt dispense fails
            W->>B: credit(1500, card)  (rollback)
        end
    end
```

**Greedy dispense idea:** go through denominations 500 → 200 → 100 → 50 → 20 → 10 and take `min(available, remaining / note)` of each note. If anything is left over at the end, throw.

## 4. Deposit flow

```mermaid
flowchart LR
    A[atm.deposit amt] --> B[AuthenticatedState.deposit]
    B --> C[DepositService.deposit]
    C --> D{amt > 0?}
    D -- no --> X[print error]
    D -- yes --> E[BankService.credit]
    E --> F[saveTransaction DEPOSIT / SUCCESS]
```

## 5. Revision points

- **Concurrency:** `Account.debit/credit` are `synchronized`, `CashDispenser.dispense` is `synchronized`, and `BankService` uses `ConcurrentHashMap` plus synchronized lists.
- **Compensation:** debit first, then dispense. If dispensing fails, credit the money back.
- **Card blocking:** after more than 3 failed PIN attempts, `blockCard` runs and the card is removed.

<details><summary>Gaps in the current code (worth fixing / mentioning in an interview)</summary>

- `authenticateCard` always returns `true`. It never compares the PIN.
- `Card.status` is never set to `ACTIVE`, so `validateCard` throws.
- `cardToAccountMap` is keyed by **account number** but looked up by **card number**.
- `GreedyDispenseStrategy` uses `remaining > value`. It should be `>=`.
- `deductInventory` has an inverted check: `available > needed` throws.
- `AuthenticatedState.changePin` is empty, and `balanceEnquiry` prints "not allowed".
- Withdrawals are never recorded as transactions (`recordTransaction` is never called).
</details>
