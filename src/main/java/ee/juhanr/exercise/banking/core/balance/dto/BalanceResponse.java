package ee.juhanr.exercise.banking.core.balance.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BalanceResponse {
    private Long amount;
    private String currencyIsoCode;
}
