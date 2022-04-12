package ee.juhanr.exercise.banking.core.currency.service;

import ee.juhanr.exercise.banking.common.exception.ResourceNotFound;
import ee.juhanr.exercise.banking.core.currency.entity.CurrencyEntity;
import ee.juhanr.exercise.banking.core.currency.repository.CurrencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    private static final String CURRENCY_ISO_CODE = "EUR";

    @Mock private CurrencyRepository repository;
    private CurrencyService service;

    @BeforeEach
    void beforeEach() {
        service = new CurrencyService(repository);
    }


    @Test
    void getEntityByIsoCode_validIsoCode_returnsExpected() {
        var entity = mockEntity();
        when(repository.findByIsoCode(CURRENCY_ISO_CODE)).thenReturn(Optional.of(entity));

        var actual = service.getEntityByIsoCode(CURRENCY_ISO_CODE);

        assertThat(actual).isEqualTo(entity);
    }

    @Test
    void getEntityByIsoCode_validIsoCode_throwsNotFound() {
        when(repository.findByIsoCode(CURRENCY_ISO_CODE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEntityByIsoCode(CURRENCY_ISO_CODE))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("Currency not found: " + CURRENCY_ISO_CODE);
    }


    private CurrencyEntity mockEntity() {
        return CurrencyEntity.builder()
                .isoCode(CURRENCY_ISO_CODE)
                .build();
    }

}
