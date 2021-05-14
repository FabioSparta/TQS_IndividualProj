package com.example.projeto_individual.repositories;

import com.example.projeto_individual.entities.CityAirQuality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional
@Repository
public interface CityAirQualityRepository extends JpaRepository<CityAirQuality, Long> {
    List<CityAirQuality> findAll();
    CityAirQuality findByNormalizedCityOrQuery(String cityName,String query);

    // < 0.25 -> Error Margin / Tolerance
    @Query(value = "SELECT * FROM city_air_quality " +
            "WHERE abs(latitude - ?1) < 0.25 and  abs(longitude - ?2) < 0.25 OR query = ?3  ", nativeQuery = true)
    CityAirQuality findByLatitudeAndLongitude(double latitude, double longitude, String query);

    @Modifying
    @Query("delete from CityAirQuality caq where caq.lastAccess < ?1")
    void deleteLastAccessOlderThan(long time);


    @Modifying
    @Query("delete from CityAirQuality caq where caq.expiration < ?1")
    void deleteExpired(long time);
}
