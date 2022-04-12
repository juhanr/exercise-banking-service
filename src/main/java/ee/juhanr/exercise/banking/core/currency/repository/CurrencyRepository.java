package ee.juhanr.exercise.banking.core.currency.repository;

import ee.juhanr.exercise.banking.core.currency.entity.CurrencyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Mapper
public interface CurrencyRepository {

    @Select("SELECT * FROM currency WHERE iso_code = #{isoCode}")
    Optional<CurrencyEntity> findByIsoCode(String isoCode);
}
