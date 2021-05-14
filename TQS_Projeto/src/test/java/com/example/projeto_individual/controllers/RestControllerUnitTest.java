package com.example.projeto_individual.controllers;

import com.example.projeto_individual.entities.CityAirQuality;
import com.example.projeto_individual.services.ExternalAPIConnect;
import com.example.projeto_individual.services.JsonToEntity;
import com.example.projeto_individual.services.SimpleCacheManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.hasKey;

@WebMvcTest(RestControllers.class)
public class RestControllerUnitTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private SimpleCacheManager simpleCacheManager;

    @MockBean
    private ExternalAPIConnect externalAPI;

    @MockBean
    private JsonToEntity jsonToEntity;

    ResponseEntity<String> expected_NewYorkAnswer;
    ResponseEntity<String> expected_NonExistentAnswer;
    ResponseEntity<String> noResponse;
    CityAirQuality mockCAQ;

    @BeforeEach
    void setup(){
        expected_NewYorkAnswer = new ResponseEntity<>(
                "Info related to New York's air quality would come here..",
                HttpStatus.OK
        );
        expected_NonExistentAnswer = new ResponseEntity<>(
                "{\"status\":\"error\",\"data\":\"Unknown station\"}",
                HttpStatus.OK // Yeah.. the External API really uses 'HttpStatus.Ok' when the city is not found
        );
        noResponse = new ResponseEntity<>(
                "{\"status\":\"error\",\"data\":\"Unknown station\"}",
                HttpStatus.GATEWAY_TIMEOUT // Yeah.. the External API really uses 'HttpStatus.Ok' when the city is not found
        );
        mockCAQ= new CityAirQuality();
    }


    // getCityAQ Controller - 6 tests

    @Test
    public void whenNoCacheButCityExists_thenUseExternalAPIAndJsonToEntity__AndStatus200() throws Exception {

        // Expectations
        when(simpleCacheManager.findbyCityOrQuery(Mockito.anyString(),Mockito.anyString())).thenReturn(null);
        when(externalAPI.request(Mockito.anyString())).thenReturn(expected_NewYorkAnswer);
        when(jsonToEntity.transform(Mockito.anyString(),Mockito.anyString())).thenReturn(mockCAQ);

        // Request and Verify(s)
        requestAndExpects_forStatus200("/city?city_name=newyork");

        verify(simpleCacheManager, times(1)).findbyCityOrQuery(Mockito.any(),Mockito.any());
        verify(externalAPI, times(1)).request(Mockito.any());
        verify(jsonToEntity, times(1)).transform(Mockito.any(),Mockito.any());

    }

    @Test
    public void whenCacheAndCityExists_thenUseCache_AndStatus200() throws Exception {

        // Expectations
        when(simpleCacheManager.findbyCityOrQuery(Mockito.anyString(),Mockito.anyString())).thenReturn(mockCAQ);

        // Request and Verify(s)
        requestAndExpects_forStatus200("/city?city_name=newyork");

        verify(simpleCacheManager, times(1)).findbyCityOrQuery(Mockito.any(),Mockito.any());
        verify(externalAPI, times(0)).request(Mockito.any());
        verify(jsonToEntity, times(0)).transform(Mockito.any(),Mockito.any());
    }

    @Test
    public void whenNonExistentCity_thenStatus404() throws Exception {

        // Expectations
        when(simpleCacheManager.findbyCityOrQuery(Mockito.any(),Mockito.any())).thenReturn(null);
        when(externalAPI.request(Mockito.anyString())).thenReturn(expected_NonExistentAnswer);

        // Request and Verify(s)
        mvc.perform(MockMvcRequestBuilders.get("/city?city_name=newyork"))
                .andExpect(MockMvcResultMatchers.status().is(404))
                .andExpect(MockMvcResultMatchers.content().string("City not found."));

        verify(simpleCacheManager, times(1)).findbyCityOrQuery(Mockito.any(),Mockito.any());
        verify(externalAPI, times(1)).request(Mockito.any());
        verify(jsonToEntity, times(0)).transform(Mockito.any(),Mockito.any());
    }

    @Test
    public void whenValidDateFormatAndCityExists_thenStatus200() throws Exception {

        // Expectations
        when(simpleCacheManager.findbyCityOrQuery(Mockito.anyString(),Mockito.anyString())).thenReturn(mockCAQ);
        when(externalAPI.request(Mockito.anyString())).thenReturn(expected_NewYorkAnswer);

        // Request and Verify(s)
        requestAndExpects_forStatus200("/city?city_name=newyork&date=2021-05-21");

    }

    @Test
    void whenDateInvalidFormat_thenStatus400() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/city?city_name=newyork&date=21/13/1245"))
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().string("Invalid Date Format. Use: yyyy-mm-dd."));

    }

    @Test
    void whenNoResponseFromExternalAPI_thenStatus503() throws Exception {
        // Expectations
        when(simpleCacheManager.findbyCityOrQuery(Mockito.anyString(),Mockito.anyString())).thenReturn(null);
        when(externalAPI.request(Mockito.anyString())).thenReturn(noResponse);

        //Request and Verify(s)
        mvc.perform(MockMvcRequestBuilders.get("/city?city_name=newyork"))
                .andExpect(MockMvcResultMatchers.status().is(503))
                .andExpect(MockMvcResultMatchers.content().string("Failed to get response from External API."));

        verify(simpleCacheManager, times(1)).findbyCityOrQuery(Mockito.any(),Mockito.any());
        verify(externalAPI, times(1)).request(Mockito.any());
    }




    // getCityFromCoords Controller - 6 tests

    @Test
    public void whenNoCacheButCoords_thenUseExternalAPIAndJsonToEntity__AndStatus200() throws Exception {

        // Expectations
        when(simpleCacheManager.findbyCoords(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(null);
        when(externalAPI.request(Mockito.anyString())).thenReturn(expected_NewYorkAnswer);
        when(jsonToEntity.transform(Mockito.anyString(),Mockito.anyString())).thenReturn(mockCAQ);

        // Request and Verify(s)
        requestAndExpects_forStatus200("/coords?latitude=40&longitude=-74");

        verify(simpleCacheManager, times(1)).findbyCoords(Mockito.any(),Mockito.any(),Mockito.any());
        verify(externalAPI, times(1)).request(Mockito.any());
        verify(jsonToEntity, times(1)).transform(Mockito.any(),Mockito.any());

    }

    @Test
    public void whenCacheAndCoords_thenUseCache_AndStatus200() throws Exception {

        // Expectations
        when(simpleCacheManager.findbyCoords(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(mockCAQ);

        // Request and Verify(s)
        requestAndExpects_forStatus200("/coords?latitude=40&longitude=-74");


        verify(simpleCacheManager, times(1)).findbyCoords(Mockito.any(),Mockito.any(),Mockito.any());
        verify(externalAPI, times(0)).request(Mockito.any());
        verify(jsonToEntity, times(0)).transform(Mockito.any(),Mockito.any());
    }




    @Test
    public void whenValidDateFormatAndCoords_thenStatus200() throws Exception {

        // Expectations
        when(simpleCacheManager.findbyCoords(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(mockCAQ);
        when(externalAPI.request(Mockito.anyString())).thenReturn(expected_NewYorkAnswer);

        // Request and Verify(s)
        requestAndExpects_forStatus200("/coords?latitude=40&longitude=-74&date=2021-05-21");

    }

    @Test
    void whenCoordsInvalid_thenStatus400() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/coords?latitude=40_WRONG&longitude=-7abcdef"))
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().string("Coords must be numbers."));
    }

    @Test
    void whenDateInvalid2_thenStatus400() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get("/coords?latitude=40&longitude=-74&date=21/05/2000"))
                .andExpect(MockMvcResultMatchers.status().is(400))
                .andExpect(MockMvcResultMatchers.content().string("Invalid Date Format. Use: yyyy-mm-dd."));

    }

    @Test
    void whenNoResponseFromExternalAPI2_thenStatus503() throws Exception {
        // Expectations
        when(simpleCacheManager.findbyCoords(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(null);
        when(externalAPI.request(Mockito.anyString())).thenReturn(noResponse);

        //Request and Verify(s)
        mvc.perform(MockMvcRequestBuilders.get("/coords?latitude=40&longitude=-74"))
                .andExpect(MockMvcResultMatchers.status().is(503))
                .andExpect(MockMvcResultMatchers.content().string("Failed to get response from External API."));

    }

    void requestAndExpects_forStatus200(String url) throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(MockMvcResultMatchers.status().is(200))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(hasKey("city")))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(hasKey("components")))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(hasKey("latitude")))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(hasKey("longitude")))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(hasKey("aqi")))
                .andExpect(MockMvcResultMatchers.jsonPath("$").value(hasKey("date")));
    }
}