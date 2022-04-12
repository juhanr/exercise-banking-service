package ee.juhanr.exercise.banking.core.account.mapper;

import ee.juhanr.exercise.banking.core.account.dto.AccountCreateRequest;
import ee.juhanr.exercise.banking.core.account.dto.AccountResponse;
import ee.juhanr.exercise.banking.core.account.entity.AccountEntity;
import ee.juhanr.exercise.banking.core.balance.entity.BalanceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    AccountEntity toEntity(AccountCreateRequest request);

    AccountResponse toResponse(AccountEntity accountEntity, List<BalanceEntity> balances);
}
