package ee.juhanr.exercise.banking.core.country.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CountryEntity {
    private String isoCode;
    private String name;
}
