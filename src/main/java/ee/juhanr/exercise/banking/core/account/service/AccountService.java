package ee.juhanr.exercise.banking.core.account.service;

import ee.juhanr.exercise.banking.common.exception.ResourceNotFound;
import ee.juhanr.exercise.banking.core.account.dto.AccountCreateRequest;
import ee.juhanr.exercise.banking.core.account.dto.AccountResponse;
import ee.juhanr.exercise.banking.core.account.entity.AccountEntity;
import ee.juhanr.exercise.banking.core.account.mapper.AccountMapper;
import ee.juhanr.exercise.banking.core.account.publisher.AccountPublisher;
import ee.juhanr.exercise.banking.core.account.repository.AccountRepository;
import ee.juhanr.exercise.banking.core.balance.entity.BalanceEntity;
import ee.juhanr.exercise.banking.core.balance.service.BalanceService;
import ee.juhanr.exercise.banking.core.country.service.CountryService;
import ee.juhanr.exercise.banking.core.currency.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository repository;
    private final AccountPublisher publisher;
    private final AccountMapper mapper;
    private final CountryService countryService;
    private final CurrencyService currencyService;
    private final BalanceService balanceService;

    @Transactional
    public AccountResponse createAccount(AccountCreateRequest request) {
        // Check if the country exists
        countryService.getEntityByIsoCode(request.getCountryIsoCode());
        // Check if the currencies exist
        request.getCurrencies().forEach(currencyService::getEntityByIsoCode);

        AccountEntity accountEntity = mapper.toEntity(request);
        repository.insert(accountEntity);
        publisher.publish(accountEntity);
        Long accountId = accountEntity.getId();
        log.info("Created account: id {}", accountId);

        List<BalanceEntity> balances = request.getCurrencies()
                .stream()
                .map(currency -> balanceService.createBalance(accountId, currency))
                .toList();

        return mapper.toResponse(accountEntity, balances);
    }

    public AccountEntity getEntityById(Long accountId) {
        return repository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFound("Account not found: " + accountId));
    }

    public AccountResponse getResponseById(Long accountId) {
        var accountEntity = getEntityById(accountId);
        var balances = balanceService.findByAccountId(accountId);
        return mapper.toResponse(accountEntity, balances);
    }
}
