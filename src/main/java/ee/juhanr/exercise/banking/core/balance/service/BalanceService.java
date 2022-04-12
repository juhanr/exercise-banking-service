package ee.juhanr.exercise.banking.core.balance.service;

import ee.juhanr.exercise.banking.common.exception.BadRequestException;
import ee.juhanr.exercise.banking.common.exception.ResourceNotFound;
import ee.juhanr.exercise.banking.core.balance.entity.BalanceEntity;
import ee.juhanr.exercise.banking.core.balance.publisher.BalancePublisher;
import ee.juhanr.exercise.banking.core.balance.repository.BalanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository repository;
    private final BalancePublisher publisher;

    @Transactional
    public BalanceEntity createBalance(Long accountId, String currencyIsoCode) {
        BalanceEntity entity = BalanceEntity.builder()
                .accountId(accountId)
                .amount(0L)
                .currencyIsoCode(currencyIsoCode)
                .build();
        repository.insert(entity);
        log.info("Created balance: account {}, currency {}", accountId, currencyIsoCode);
        publisher.publish(entity);
        return entity;
    }

    public BalanceEntity increaseBalance(Long accountId, Long amount, String currencyIsoCode) {
        BalanceEntity balanceEntity = getByAccountIdAndCurrencyForUpdate(accountId, currencyIsoCode);

        return updateBalance(balanceEntity, balanceEntity.getAmount() + amount);
    }

    public BalanceEntity decreaseBalance(Long accountId, Long amount, String currencyIsoCode) {
        BalanceEntity balanceEntity = getByAccountIdAndCurrencyForUpdate(accountId, currencyIsoCode);

        Long existingAmount = balanceEntity.getAmount();
        if (amount > existingAmount) {
            throw new BadRequestException(
                    String.format("Insufficient funds: requested transfer amount %d, existing balance amount %s",
                            amount, existingAmount));
        }

        return updateBalance(balanceEntity, existingAmount - amount);
    }

    private BalanceEntity updateBalance(BalanceEntity balanceEntity, Long newAmount) {
        balanceEntity.setAmount(newAmount);

        // Save the balance record
        repository.update(balanceEntity);
        log.info("Updated balance: id {}, new amount {}", balanceEntity.getId(), newAmount);
        publisher.publish(balanceEntity);

        return balanceEntity;
    }

    public List<BalanceEntity> findByAccountId(Long accountId) {
        return repository.findByAccountId(accountId);
    }

    /**
     * Returns the balance entity of the provided account and currency.
     * Uses database row locking (FOR UPDATE) to avoid race condition.
     */
    private BalanceEntity getByAccountIdAndCurrencyForUpdate(Long accountId, String currencyIsoCode) {
        return repository.findByAccountIdAndCurrencyForUpdate(accountId, currencyIsoCode)
                .orElseThrow(() -> new ResourceNotFound(
                        String.format("Balance not found: account %d, currency %s", accountId, currencyIsoCode)));
    }

}
