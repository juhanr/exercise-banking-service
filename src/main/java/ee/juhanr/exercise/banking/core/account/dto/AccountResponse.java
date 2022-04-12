package ee.juhanr.exercise.banking.core.account.dto;

import ee.juhanr.exercise.banking.core.balance.dto.BalanceResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class AccountResponse {
    private Long id;
    private Long customerId;
    private List<BalanceResponse> balances;
}
