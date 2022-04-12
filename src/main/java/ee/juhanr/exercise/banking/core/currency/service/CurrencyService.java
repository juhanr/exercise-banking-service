package ee.juhanr.exercise.banking.core.currency.service;

import ee.juhanr.exercise.banking.common.exception.ResourceNotFound;
import ee.juhanr.exercise.banking.core.currency.entity.CurrencyEntity;
import ee.juhanr.exercise.banking.core.currency.repository.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyRepository repository;

    public CurrencyEntity getEntityByIsoCode(String isoCode) {
        return repository.findByIsoCode(isoCode)
                .orElseThrow(() -> new ResourceNotFound("Currency not found: " + isoCode));
    }
}
