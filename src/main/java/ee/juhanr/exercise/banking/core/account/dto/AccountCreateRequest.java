package ee.juhanr.exercise.banking.core.account.dto;

import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Builder
public class AccountCreateRequest {

    @NotNull
    private final Long customerId;

    @NotEmpty
    @Length(min = 3, max = 3)
    private final String countryIsoCode;

    @NotEmpty
    private final List<String> currencies;
}
