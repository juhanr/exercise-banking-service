package ee.juhanr.exercise.banking.core.transaction.entity;

import ee.juhanr.exercise.banking.core.transaction.enums.TransactionDirection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity {
    private Long id;
    private Long accountId;
    private Long amount;
    private String currencyIsoCode;
    private TransactionDirection direction;
    private String description;
    private Long accountBalanceAfter;
}
