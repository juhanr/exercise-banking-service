package ee.juhanr.exercise.banking.core.account.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
public class AccountCreateRequest {

    @NotNull
    private Long customerId;

    @NotEmpty
    @Length(min = 3, max = 3)
    private String countryIsoCode;

    @NotEmpty
    private List<String> currencies;
}
