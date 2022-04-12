package ee.juhanr.exercise.banking.core.balance.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BalanceResponse {
    private final Long amount;
    private final String currencyIsoCode;
}
