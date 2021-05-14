package com.example.projeto_individual.services;

import com.example.projeto_individual.entities.CityAirQuality;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureTestDatabase
class SimpleCacheManagerTest {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    SimpleCacheManager simpleCacheManager;

    @BeforeEach
    public void setup(){
        CityAirQuality caq1 = new CityAirQuality();
        CityAirQuality caq2 = new CityAirQuality();

        caq1.setQuery("Lisbon");
        caq1.setCity("Entrecampos, Lisboa, Portugal"); // city_name that comes from the API when entry is 'Lisbon'
        caq1.setNormalizedCity("entrecampos,lisboa,portugal");

        caq2.setQuery("NewYoRk");
        caq2.setCity("New York");
        caq2.setNormalizedCity("newyork");
        caq2.setLatitude(40.71);
        caq2.setLongitude(-74.01);
        caq2.setLastAccess(System.currentTimeMillis());
        caq2.setExpiration(System.currentTimeMillis());

        simpleCacheManager.save(caq1);
        simpleCacheManager.save(caq2);
    }

    @AfterEach
    void clean(){
        simpleCacheManager.CAQRepository.deleteAll();
    }


    @Test
    void whenSavedCityQuery_thenCityQueryShouldExist(){
        CityAirQuality found = simpleCacheManager.findbyCityOrQuery("----","Lisbon");
        assertThat(found.getQuery(),is("Lisbon"));
    }

    @Test
    void whenSavedCityName_thenCityNameShouldExist(){
        CityAirQuality found = simpleCacheManager.findbyCityOrQuery("newyork","----");
        assertThat(found.getNormalizedCity(),is("newyork"));
    }

    @Test
    void whenSaved2Cities_then2CitiesShouldExist(){
        assertTrue(simpleCacheManager.CAQRepository.findAll().size()==2);
    }


    @Test
    void whenSavedCityLatitudeLongitude_thenCityShouldExist_With_0_25ErrorTolerance(){
        CityAirQuality found = simpleCacheManager.findbyCoords(40.69,-73.99,"---");
        assertThat(found.getLatitude(),is(40.71));
        assertThat(found.getLongitude(),is(-74.01));
        assertThat(found.getNormalizedCity(),is("newyork"));
    }

    @Test
    void whenCitiesExist_thenCacheHitsIncremented(){
        // Set to Zero cus the other tests may have changed this values in unknown quantities
        simpleCacheManager.setHits(0);
        simpleCacheManager.setMisses(0);

        CityAirQuality hit1 = simpleCacheManager.findbyCityOrQuery("----","Lisbon");
        CityAirQuality hit2 = simpleCacheManager.findbyCityOrQuery("----","Lisbon");
        assertThat(simpleCacheManager.getHits(),is(2));
        assertThat(simpleCacheManager.getMisses(),is(0));

    }

    @Test
    void whenCitiesDontExist_thenCacheMissesIncremented(){
        // Set to Zero cus the other tests may have changed this values in unknown quantities
        simpleCacheManager.setHits(0);
        simpleCacheManager.setMisses(0);

        CityAirQuality miss1 = simpleCacheManager.findbyCityOrQuery("----","----");
        CityAirQuality miss2 = simpleCacheManager.findbyCityOrQuery("----","----");
        CityAirQuality miss3 = simpleCacheManager.findbyCityOrQuery("----","----");
        assertThat(simpleCacheManager.getHits(),is(0));
        assertThat(simpleCacheManager.getMisses(),is(3));
    }

    @Test
    void whenLastAccessTooOld_thenCityShouldHaveBeenDeleted() throws InterruptedException {
        CityAirQuality found;
        simpleCacheManager.setLAST_ACCESS(3);
        simpleCacheManager.setEXPIRATION(7);

        found = simpleCacheManager.findbyCityOrQuery("newyork","----");
        assertNotNull(found);

        Thread.sleep(4000); // After 3 seconds not being accessed, it should have been deleted
        found = simpleCacheManager.findbyCityOrQuery("newyork","----");

        assertTrue(simpleCacheManager.CAQRepository.findAll().size()==0);
        assertNull(found);
    }

    @Test
    void whenLastAccessNew_thenCityShouldExistUntilExpiration() throws InterruptedException {
        CityAirQuality found;
        simpleCacheManager.setLAST_ACCESS(3);
        simpleCacheManager.setEXPIRATION(7);

        found = simpleCacheManager.findbyCityOrQuery("newyork","----");

        for(int i =0;i<3;i++) {
            assertNotNull(found);
            Thread.sleep(2000); // After 2/4/6 secs -> should exist
            found = simpleCacheManager.findbyCityOrQuery("newyork", "----");
        }

        Thread.sleep(2000); // Atm, around 8  secs passed  > 7 (Expiration)
        found = simpleCacheManager.findbyCityOrQuery("newyork", "----");
        assertNull(found);

    }
}