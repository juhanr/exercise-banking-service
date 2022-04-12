package ee.juhanr.exercise.banking.core.transaction.dto;

import ee.juhanr.exercise.banking.core.transaction.enums.TransactionDirection;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Getter
@Builder()
public class TransactionCreateRequest {

    @NotNull
    private Long accountId;

    @NotNull
    @Positive
    private Long amount;

    @NotNull
    private String currencyIsoCode;

    @NotNull
    private TransactionDirection direction;

    @NotNull
    private String description;
}
