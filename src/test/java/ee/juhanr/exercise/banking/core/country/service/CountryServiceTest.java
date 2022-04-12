package ee.juhanr.exercise.banking.core.country.service;

import ee.juhanr.exercise.banking.common.exception.ResourceNotFound;
import ee.juhanr.exercise.banking.core.country.entity.CountryEntity;
import ee.juhanr.exercise.banking.core.country.repository.CountryRepository;
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
class CountryServiceTest {

    private static final String COUNTRY_ISO_CODE = "EST";
    private static final String COUNTRY_NAME = "Estonia";

    @Mock private CountryRepository repository;
    private CountryService service;

    @BeforeEach
    void beforeEach() {
        service = new CountryService(repository);
    }


    @Test
    void getEntityByIsoCode_validIsoCode_returnsExpected() {
        var entity = mockEntity();
        when(repository.findByIsoCode(COUNTRY_ISO_CODE)).thenReturn(Optional.of(entity));

        var actual = service.getEntityByIsoCode(COUNTRY_ISO_CODE);

        assertThat(actual).isEqualTo(entity)
                .extracting("isoCode", "name")
                .containsExactly(COUNTRY_ISO_CODE, COUNTRY_NAME);
    }

    @Test
    void getEntityByIsoCode_invalidIsoCode_throwsNotFound() {
        when(repository.findByIsoCode(COUNTRY_ISO_CODE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEntityByIsoCode(COUNTRY_ISO_CODE))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("Country not found: " + COUNTRY_ISO_CODE);
    }


    private CountryEntity mockEntity() {
        return CountryEntity.builder()
                .isoCode(COUNTRY_ISO_CODE)
                .name(COUNTRY_NAME)
                .build();
    }

}
