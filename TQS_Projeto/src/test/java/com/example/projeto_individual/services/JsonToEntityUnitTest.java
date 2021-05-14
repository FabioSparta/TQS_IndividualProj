package com.example.projeto_individual.services;

import com.example.projeto_individual.entities.CityAirQuality;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class JsonToEntityUnitTest {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    JsonToEntity jsonToEntity;

    @Test
    void whenTransformIsCalled_thenCAQCreatedAsExpected() throws JsonProcessingException {
        String responseJson ="{\"status\":\"ok\",\"data\":{\"aqi\":1,\"idx\":3309,\"attributions\":[{\"url\":\"http://www.dec.ny.gov/\",\"name\":\"New York State Department of Environmental Conservation (NYSDEC)\",\"logo\":\"US-NYDEC.png\"},{\"url\":\"http://www.airnow.gov/\",\"name\":\"Air Now - US EPA\"},{\"url\":\"https://waqi.info/\",\"name\":\"World Air Quality Index Project\"}],\"city\":{\"geo\":[40.7127837,-74.0059413],\"name\":\"New York\",\"url\":\"https://aqicn.org/city/newyork\"},\"dominentpol\":\"pm25\",\"iaqi\":{\"h\":{\"v\":54.6},\"p\":{\"v\":1021.8},\"pm25\":{\"v\":1},\"t\":{\"v\":12.3},\"w\":{\"v\":9.6}},\"time\":{\"s\":\"2021-05-12 09:00:00\",\"tz\":\"-04:00\",\"v\":1620810000,\"iso\":\"2021-05-12T09:00:00-04:00\"},\"forecast\":{\"daily\":{\"o3\":[{\"avg\":19,\"day\":\"2021-05-10\",\"max\":34,\"min\":3},{\"avg\":13,\"day\":\"2021-05-11\",\"max\":37,\"min\":2},{\"avg\":14,\"day\":\"2021-05-12\",\"max\":36,\"min\":1},{\"avg\":21,\"day\":\"2021-05-13\",\"max\":42,\"min\":2},{\"avg\":5,\"day\":\"2021-05-14\",\"max\":34,\"min\":1},{\"avg\":13,\"day\":\"2021-05-15\",\"max\":40,\"min\":1}],\"pm10\":[{\"avg\":7,\"day\":\"2021-05-10\",\"max\":12,\"min\":4},{\"avg\":12,\"day\":\"2021-05-11\",\"max\":16,\"min\":5},{\"avg\":8,\"day\":\"2021-05-12\",\"max\":15,\"min\":3},{\"avg\":15,\"day\":\"2021-05-13\",\"max\":33,\"min\":5},{\"avg\":52,\"day\":\"2021-05-14\",\"max\":62,\"min\":21},{\"avg\":50,\"day\":\"2021-05-15\",\"max\":63,\"min\":16}],\"pm25\":[{\"avg\":20,\"day\":\"2021-05-10\",\"max\":32,\"min\":9},{\"avg\":34,\"day\":\"2021-05-11\",\"max\":44,\"min\":12},{\"avg\":18,\"day\":\"2021-05-12\",\"max\":34,\"min\":4},{\"avg\":40,\"day\":\"2021-05-13\",\"max\":81,\"min\":11},{\"avg\":115,\"day\":\"2021-05-14\",\"max\":149,\"min\":62},{\"avg\":122,\"day\":\"2021-05-15\",\"max\":158,\"min\":54}],\"uvi\":[{\"avg\":0,\"day\":\"2021-05-11\",\"max\":0,\"min\":0},{\"avg\":1,\"day\":\"2021-05-12\",\"max\":5,\"min\":0},{\"avg\":1,\"day\":\"2021-05-13\",\"max\":6,\"min\":0},{\"avg\":1,\"day\":\"2021-05-14\",\"max\":6,\"min\":0},{\"avg\":1,\"day\":\"2021-05-15\",\"max\":6,\"min\":0},{\"avg\":1,\"day\":\"2021-05-16\",\"max\":6,\"min\":0}]}},\"debug\":{\"sync\":\"2021-05-12T23:42:32+09:00\"}}}";
        // Expectations
        String expectedCityName = "New York";
        double expectedAqi = 1.0;
        double expectedLatitude = 40.7127837;
        double  expectedLongitude = -74.0059413;
        String expectedComponentList = "[AirComponent{name='o3', dayMap={2021-05-10=DayValues{max=34, min=3, avg=19}, 2021-05-11=DayValues{max=37, min=2, avg=13}, 2021-05-12=DayValues{max=36, min=1, avg=14}, 2021-05-13=DayValues{max=42, min=2, avg=21}, 2021-05-14=DayValues{max=34, min=1, avg=5}, 2021-05-15=DayValues{max=40, min=1, avg=13}}}, AirComponent{name='pm10', dayMap={2021-05-10=DayValues{max=12, min=4, avg=7}, 2021-05-11=DayValues{max=16, min=5, avg=12}, 2021-05-12=DayValues{max=15, min=3, avg=8}, 2021-05-13=DayValues{max=33, min=5, avg=15}, 2021-05-14=DayValues{max=62, min=21, avg=52}, 2021-05-15=DayValues{max=63, min=16, avg=50}}}, AirComponent{name='pm25', dayMap={2021-05-10=DayValues{max=32, min=9, avg=20}, 2021-05-11=DayValues{max=44, min=12, avg=34}, 2021-05-12=DayValues{max=34, min=4, avg=18}, 2021-05-13=DayValues{max=81, min=11, avg=40}, 2021-05-14=DayValues{max=149, min=62, avg=115}, 2021-05-15=DayValues{max=158, min=54, avg=122}}}, AirComponent{name='uvi', dayMap={2021-05-11=DayValues{max=0, min=0, avg=0}, 2021-05-12=DayValues{max=5, min=0, avg=1}, 2021-05-13=DayValues{max=6, min=0, avg=1}, 2021-05-14=DayValues{max=6, min=0, avg=1}, 2021-05-15=DayValues{max=6, min=0, avg=1}, 2021-05-16=DayValues{max=6, min=0, avg=1}}}]";

        CityAirQuality caq = jsonToEntity.transform(responseJson,"newyork");

        // Asserts
        assertThat(caq.getCity(),is(expectedCityName));
        assertThat(caq.getAqi(),is(expectedAqi));
        assertThat(caq.getLatitude(),is(expectedLatitude));
        assertThat(caq.getLongitude(),is(expectedLongitude));
        assertThat(caq.getAirComponentList().toString(),is(expectedComponentList));

    }
}