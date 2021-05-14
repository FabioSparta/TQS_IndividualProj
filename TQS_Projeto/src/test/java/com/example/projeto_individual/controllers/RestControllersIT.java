package com.example.projeto_individual.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import io.restassured.RestAssured;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasKey;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestControllersIT {

    @LocalServerPort
    int randomServerPort;


    // getCityAQ Controller - 4 tests

    @Test
        void whenCityNameExists_thenReturnCityTodayForecast() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String expected_date = LocalDate.now().format(formatter);
        String expected_city = "New York";

      RestAssured.given().port(randomServerPort).get("/city?city_name=newyork")
                .then()
                .statusCode(200)
                .and().body("city",equalTo(expected_city))
                .and().body("$",hasKey("aqi"))
                .and().body("$",hasKey("components"))
                .and().body("$",hasKey("latitude"))
                .and().body("$",hasKey("longitude"))
                .and().body("date",equalTo(expected_date));

    }

    @Test
    void whenNonExistentCity_thenReturnNotFound(){
        RestAssured.given().port(randomServerPort).get("/city?city_name=NonExistentCity")
                .then()
                .statusCode(404)
                .and().body(containsString("City not found."));
    }

    @Test
    void whenCityNameExistsAndDateExists_thenReturnCityDateForecast(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String forecast_date= LocalDate.now().plusDays(1).format(formatter);
        String expected_city = "New York";

        RestAssured.given().port(randomServerPort).get("/city?city_name=newyork&date=" +forecast_date)
                .then()
                .statusCode(200)
                .and().body("city",equalTo(expected_city))
                .and().body("$",hasKey("aqi"))
                .and().body("$",hasKey("components"))
                .and().body("$",hasKey("latitude"))
                .and().body("$",hasKey("longitude"))
                .and().body("date",equalTo(forecast_date));
    }

    @Test
    void whenDateInvalidFormat_thenReturnBadRequest(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String expected_date = LocalDate.now().format(formatter);

        RestAssured.given().port(randomServerPort).get("/city?city_name=newyork&date=" + expected_date)
                .then()
                .statusCode(400)
                .and().body(containsString("Invalid Date Format. Use: yyyy-mm-dd."));
    }




    // getCityFromCoords Controller - 4 tests

    @Test
    void whenCityLatitudeLongitude_thenReturnClosestCityTodayForecast(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String expected_date = LocalDate.now().format(formatter);
        String expected_city = "New York";


        RestAssured.given().port(randomServerPort).get("/coords?latitude=40&longitude=-74")
                .then()
                .statusCode(200)
                .and().body("city",containsString(expected_city))
                .and().body("$",hasKey("aqi"))
                .and().body("$",hasKey("components"))
                .and().body("$",hasKey("latitude"))
                .and().body("$",hasKey("longitude"))
                .and().body("date",equalTo(expected_date));

    }

    @Test
    void whenCoordsInvalid_thenReturnBadRequest(){
        RestAssured.given().port(randomServerPort).get("/coords?latitude=40_WRONG&longitude=-7abcdef" )
                .then()
                .statusCode(400)
                .and().body(containsString("Coords must be numbers."));
    }

    @Test
    void whenCoordsAndDateExists_thenReturnCityDateForecast(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String forecast_date= LocalDate.now().plusDays(1).format(formatter);
        String expected_city = "New York";

        RestAssured.given().port(randomServerPort).get("/coords?latitude=40&longitude=-74&date=" + forecast_date)
                .then()
                .statusCode(200)
                .and().body("city",containsString(expected_city))
                .and().body("$",hasKey("aqi"))
                .and().body("$",hasKey("components"))
                .and().body("$",hasKey("latitude"))
                .and().body("$",hasKey("longitude"))
                .and().body("date",equalTo(forecast_date));
    }

    @Test
    void whenDateInvalid2_thenStatus400(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String expected_date = LocalDate.now().format(formatter);

        RestAssured.given().port(randomServerPort).get("/coords?latitude=40&longitude=-74&date=" + expected_date)
                .then()
                .statusCode(400)
                .and().body(containsString("Invalid Date Format. Use: yyyy-mm-dd."));
    }


}