package ee.juhanr.exercise.banking.core.country.service;

import ee.juhanr.exercise.banking.common.exception.ResourceNotFound;
import ee.juhanr.exercise.banking.core.country.entity.CountryEntity;
import ee.juhanr.exercise.banking.core.country.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository repository;

    public CountryEntity getEntityByIsoCode(String isoCode) {
        return repository.findByIsoCode(isoCode)
                .orElseThrow(() -> new ResourceNotFound("Country not found: " + isoCode));
    }
}
