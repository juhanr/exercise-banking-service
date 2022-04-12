package ee.juhanr.exercise.banking.core.account.service;

import ee.juhanr.exercise.banking.common.exception.ResourceNotFound;
import ee.juhanr.exercise.banking.core.account.dto.AccountCreateRequest;
import ee.juhanr.exercise.banking.core.account.dto.AccountResponse;
import ee.juhanr.exercise.banking.core.account.entity.AccountEntity;
import ee.juhanr.exercise.banking.core.account.mapper.AccountMapper;
import ee.juhanr.exercise.banking.core.account.publisher.AccountPublisher;
import ee.juhanr.exercise.banking.core.account.repository.AccountRepository;
import ee.juhanr.exercise.banking.core.balance.dto.BalanceResponse;
import ee.juhanr.exercise.banking.core.balance.entity.BalanceEntity;
import ee.juhanr.exercise.banking.core.balance.service.BalanceService;
import ee.juhanr.exercise.banking.core.country.service.CountryService;
import ee.juhanr.exercise.banking.core.currency.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    private static final long ACCOUNT_ID = 1L;
    private static final long CUSTOMER_ID = 10L;
    private static final String COUNTRY = "XXX";
    private static final String CURRENCY = "EUR";
    private static final List<String> CURRENCIES = List.of("EUR");

    @Mock private AccountRepository repository;
    @Mock private AccountPublisher publisher;
    @Mock private AccountMapper mapper;
    @Mock private CountryService countryService;
    @Mock private CurrencyService currencyService;
    @Mock private BalanceService balanceService;

    private AccountService service;

    @BeforeEach
    void beforeEach() {
        service = new AccountService(repository, publisher, mapper, countryService, currencyService, balanceService);
    }


    @Test
    void createAccount_validData_returnsExpected() {
        var request = mockCreateRequest();
        var entity = mockEntity();
        var response = mockResponse();
        when(mapper.toEntity(any())).thenReturn(entity);
        when(mapper.toResponse(any(), anyList())).thenReturn(response);
        var balanceEntity = BalanceEntity.builder().build();
        when(balanceService.createBalance(any(), any())).thenReturn(balanceEntity);

        var actual = service.createAccount(request);

        assertThat(actual).isEqualTo(response);
        verify(mapper).toEntity(request);
        verify(mapper).toResponse(entity, List.of(balanceEntity));
        verifyNoMoreInteractions(mapper);
        verify(countryService).getEntityByIsoCode(COUNTRY);
        verify(currencyService).getEntityByIsoCode(CURRENCY);
        verify(balanceService).createBalance(ACCOUNT_ID, CURRENCY);
        verify(repository).insert(entity);
        verify(publisher).publish(entity);
    }

    @Test
    void createAccount_invalidCountry_throwsNotFound() {
        var request = mockCreateRequest();
        when(countryService.getEntityByIsoCode(COUNTRY))
                .thenThrow(new ResourceNotFound("Country not found"));

        assertThatThrownBy(() -> service.createAccount(request))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("Country not found");
    }

    @Test
    void createAccount_invalidCurrency_throwsNotFound() {
        var request = mockCreateRequest();
        when(currencyService.getEntityByIsoCode(CURRENCY))
                .thenThrow(new ResourceNotFound("Currency not found"));

        assertThatThrownBy(() -> service.createAccount(request))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("Currency not found");
    }


    @Test
    void getEntityById_validAccountId_returnsExpected() {
        var entity = mockEntity();
        when(repository.findById(ACCOUNT_ID)).thenReturn(Optional.of(entity));

        var actual = service.getEntityById(ACCOUNT_ID);

        assertThat(actual).isEqualTo(entity);
    }

    @Test
    void getEntityById_invalidAccountId_throwsNotFound() {
        when(repository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getEntityById(ACCOUNT_ID))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("Account not found: " + ACCOUNT_ID);
    }


    @Test
    void getResponseById_validAccountId_returnsExpected() {
        var entity = mockEntity();
        var response = mockResponse();
        when(repository.findById(ACCOUNT_ID)).thenReturn(Optional.of(entity));
        var balances = List.of(BalanceEntity.builder().build());
        when(balanceService.findByAccountId(ACCOUNT_ID)).thenReturn(balances);
        when(mapper.toResponse(entity, balances)).thenReturn(response);

        var actual = service.getResponseById(ACCOUNT_ID);

        assertThat(actual).isEqualTo(response);
    }

    @Test
    void getResponseById_invalidAccountId_throwsNotFound() {
        when(repository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getResponseById(ACCOUNT_ID))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("Account not found: " + ACCOUNT_ID);
    }


    private AccountEntity mockEntity() {
        return AccountEntity.builder()
                .id(ACCOUNT_ID)
                .customerId(CUSTOMER_ID)
                .countryIsoCode(COUNTRY)
                .build();
    }

    private AccountCreateRequest mockCreateRequest() {
        return AccountCreateRequest.builder()
                .customerId(CUSTOMER_ID)
                .countryIsoCode(COUNTRY)
                .currencies(CURRENCIES)
                .build();
    }

    private AccountResponse mockResponse() {
        return AccountResponse.builder()
                .id(ACCOUNT_ID)
                .customerId(CUSTOMER_ID)
                .balances(List.of(BalanceResponse.builder()
                        .amount(0L)
                        .currencyIsoCode(CURRENCY)
                        .build()))
                .build();
    }
}
