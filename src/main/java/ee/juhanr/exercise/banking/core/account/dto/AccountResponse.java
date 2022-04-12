package ee.juhanr.exercise.banking.core.account.dto;

import ee.juhanr.exercise.banking.core.balance.dto.BalanceResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AccountResponse {
    private final Long id;
    private final Long customerId;
    private final List<BalanceResponse> balances;
}
