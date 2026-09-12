package advance.lld.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private final String transactionId;
    private final TransactionType transactionType;
    private final TransactionStatus status;
    private final BigDecimal amount;
    @Getter
    private final String accountNumber;
    private final LocalDateTime timestamp;


    public Transaction(
            String transactionId,
            String accountNumber,
            TransactionType type,
            BigDecimal amount,
            TransactionStatus status,
            LocalDateTime timestamp
    ) {

        this.transactionId = transactionId;
        this.accountNumber = accountNumber;
        this.transactionType = type;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }


    @Override
    public String toString() {

        return "Transaction{" +
                "id='" + transactionId + '\'' +
                ", type=" + transactionType +
                ", amount=" + amount +
                ", status=" + status +
                ", timestamp=" + timestamp +
                '}';
    }
}
