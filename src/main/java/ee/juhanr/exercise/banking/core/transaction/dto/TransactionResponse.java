package ee.juhanr.exercise.banking.core.transaction.dto;

import ee.juhanr.exercise.banking.core.transaction.enums.TransactionDirection;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TransactionResponse {
    private final Long id;
    private final Long accountId;
    private final Long amount;
    private final String currencyIsoCode;
    private final TransactionDirection direction;
    private final String description;
    private final Long accountBalanceAfter;
}
