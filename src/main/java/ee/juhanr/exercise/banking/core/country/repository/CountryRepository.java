package ee.juhanr.exercise.banking.core.country.repository;

import ee.juhanr.exercise.banking.core.country.entity.CountryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Mapper
public interface CountryRepository {

    @Select("SELECT * FROM country WHERE iso_code = #{isoCode}")
    Optional<CountryEntity> findByIsoCode(String isoCode);
}
