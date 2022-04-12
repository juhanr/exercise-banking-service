package ee.juhanr.exercise.banking.core.transaction.mapper;

import ee.juhanr.exercise.banking.core.transaction.dto.TransactionCreateRequest;
import ee.juhanr.exercise.banking.core.transaction.dto.TransactionResponse;
import ee.juhanr.exercise.banking.core.transaction.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "id", ignore = true)
    TransactionEntity toEntity(TransactionCreateRequest request, Long accountBalanceAfter);

    TransactionResponse toResponse(TransactionEntity transaction);
}
