package ee.juhanr.exercise.banking.core.transaction.repository;

import ee.juhanr.exercise.banking.core.transaction.entity.TransactionEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface TransactionRepository {

    @Insert("INSERT INTO transaction (account_id, amount, currency_iso_code, direction, description, account_balance_after) "
            + "VALUES (#{accountId}, #{amount}, #{currencyIsoCode}, #{direction}, #{description}, #{accountBalanceAfter})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(TransactionEntity transactionEntity);

    @Select("SELECT * FROM transaction WHERE account_id = #{accountId} ORDER BY id")
    List<TransactionEntity> findByAccountId(long accountId);
}
