package ee.juhanr.exercise.banking.core.currency.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CurrencyEntity {
    private String isoCode;
    private String name;
}
