package ee.juhanr.exercise.banking.core.balance.service;

import ee.juhanr.exercise.banking.common.exception.BadRequestException;
import ee.juhanr.exercise.banking.common.exception.ResourceNotFound;
import ee.juhanr.exercise.banking.core.balance.entity.BalanceEntity;
import ee.juhanr.exercise.banking.core.balance.publisher.BalancePublisher;
import ee.juhanr.exercise.banking.core.balance.repository.BalanceRepository;
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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    private static final long BALANCE_ID = 1L;
    private static final long ACCOUNT_ID = 10L;
    private static final String CURRENCY_ISO_CODE = "EUR";

    @Mock private BalanceRepository repository;
    @Mock private BalancePublisher publisher;
    private BalanceService service;

    @BeforeEach
    void beforeEach() {
        service = new BalanceService(repository, publisher);
    }


    @Test
    void createBalance_validData_returnsNewBalance() {
        doAnswer(invocation -> {
            BalanceEntity entity = invocation.getArgument(0);
            entity.setId(BALANCE_ID);
            return null;
        }).when(repository).insert(any(BalanceEntity.class));

        var actual = service.createBalance(ACCOUNT_ID, CURRENCY_ISO_CODE);

        assertThat(actual).isNotNull()
                .extracting("id", "accountId", "amount", "currencyIsoCode")
                .containsExactly(BALANCE_ID, ACCOUNT_ID, 0L, CURRENCY_ISO_CODE);
        verify(repository).insert(actual);
        verify(publisher).publish(actual);
    }


    @Test
    void increaseBalance_validData_returnsBalanceWithIncreasedAmount() {
        var entity = mockEntity();
        entity.setAmount(300L);
        when(repository.findByAccountIdAndCurrencyForUpdate(ACCOUNT_ID, CURRENCY_ISO_CODE))
                .thenReturn(Optional.of(entity));

        var actual = service.increaseBalance(ACCOUNT_ID, 100L, CURRENCY_ISO_CODE);

        assertThat(actual).isNotNull()
                .extracting("id", "accountId", "amount", "currencyIsoCode")
                .containsExactly(BALANCE_ID, ACCOUNT_ID, 400L, CURRENCY_ISO_CODE);
        verify(repository).update(actual);
        verify(publisher).publish(actual);
    }

    @Test
    void increaseBalance_invalidAccountId_throwsNotFound() {
        when(repository.findByAccountIdAndCurrencyForUpdate(ACCOUNT_ID, CURRENCY_ISO_CODE))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.increaseBalance(ACCOUNT_ID, 100L, CURRENCY_ISO_CODE))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage(String.format("Balance not found: account %d, currency %s", ACCOUNT_ID, CURRENCY_ISO_CODE));
    }


    @Test
    void decreaseBalance_validData_returnsBalanceWithDecreasedAmount() {
        var entity = mockEntity();
        entity.setAmount(300L);
        when(repository.findByAccountIdAndCurrencyForUpdate(ACCOUNT_ID, CURRENCY_ISO_CODE))
                .thenReturn(Optional.of(entity));

        var actual = service.decreaseBalance(ACCOUNT_ID, 100L, CURRENCY_ISO_CODE);

        assertThat(actual).isNotNull()
                .extracting("id", "accountId", "amount", "currencyIsoCode")
                .containsExactly(BALANCE_ID, ACCOUNT_ID, 200L, CURRENCY_ISO_CODE);
        verify(repository).update(actual);
        verify(publisher).publish(actual);
    }

    @Test
    void decreaseBalance_amountTooHigh_throwsBadRequest() {
        var entity = mockEntity();
        entity.setAmount(100L);
        when(repository.findByAccountIdAndCurrencyForUpdate(ACCOUNT_ID, CURRENCY_ISO_CODE))
                .thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> service.decreaseBalance(ACCOUNT_ID, 200L, CURRENCY_ISO_CODE))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Insufficient funds: requested transfer amount 200, existing balance amount 100");
    }


    @Test
    void findByAccountId_oneBalanceFound_returnsExpected() {
        var entity = mockEntity();
        when(repository.findByAccountId(ACCOUNT_ID)).thenReturn(List.of(entity));

        var actual = service.findByAccountId(ACCOUNT_ID);

        assertThat(actual).isEqualTo(List.of(entity));
    }

    @Test
    void findByAccountId_noBalancesFound_returnsEmpty() {
        when(repository.findByAccountId(ACCOUNT_ID)).thenReturn(List.of());

        var actual = service.findByAccountId(ACCOUNT_ID);

        assertThat(actual).isEmpty();
    }


    private BalanceEntity mockEntity() {
        return BalanceEntity.builder()
                .id(BALANCE_ID)
                .accountId(ACCOUNT_ID)
                .amount(0L)
                .currencyIsoCode(CURRENCY_ISO_CODE)
                .build();
    }

}
