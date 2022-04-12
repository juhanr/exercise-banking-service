package ee.juhanr.exercise.banking.core.account.repository;

import ee.juhanr.exercise.banking.core.account.entity.AccountEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Mapper
public interface AccountRepository {

    @Insert("INSERT INTO account (customer_id, country_iso_code) "
            + "VALUES (#{customerId}, #{countryIsoCode})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void insert(AccountEntity accountEntity);

    @Select("SELECT * FROM account WHERE id = #{id}")
    Optional<AccountEntity> findById(long id);
}
