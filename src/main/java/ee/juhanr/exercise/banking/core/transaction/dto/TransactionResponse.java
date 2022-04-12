package ee.juhanr.exercise.banking.core.transaction.dto;

import ee.juhanr.exercise.banking.core.transaction.enums.TransactionDirection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionResponse {
    private Long id;
    private Long accountId;
    private Long amount;
    private String currencyIsoCode;
    private TransactionDirection direction;
    private String description;
    private Long accountBalanceAfter;
}
