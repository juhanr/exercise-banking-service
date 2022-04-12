package ee.juhanr.exercise.banking.core.balance.entity;

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
public class BalanceEntity {
    private Long id;
    private Long accountId;
    private Long amount;
    private String currencyIsoCode;
}
