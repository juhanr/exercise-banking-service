package ee.juhanr.exercise.banking.core.balance.repository;

import ee.juhanr.exercise.banking.core.balance.entity.BalanceEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Mapper
public interface BalanceRepository {

    @Insert("INSERT INTO balance (account_id, amount, currency_iso_code) "
            + "VALUES (#{accountId}, #{amount}, #{currencyIsoCode})")
    void insert(BalanceEntity entity);

    @Update("UPDATE balance SET amount = #{amount} WHERE id = #{id}")
    void update(BalanceEntity entity);

    @Select("SELECT * FROM balance WHERE account_id = #{accountId} AND currency_iso_code = #{currencyIsoCode} "
            + "FOR UPDATE")
    Optional<BalanceEntity> findByAccountIdAndCurrencyForUpdate(Long accountId, String currencyIsoCode);

    @Select("SELECT * FROM balance WHERE account_id = #{accountId} ORDER BY id")
    List<BalanceEntity> findByAccountId(Long accountId);
}
