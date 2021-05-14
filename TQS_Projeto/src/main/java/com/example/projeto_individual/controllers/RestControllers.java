package com.example.projeto_individual.controllers;

import com.example.projeto_individual.entities.CityAirQuality;
import com.example.projeto_individual.services.ExternalAPIConnect;
import com.example.projeto_individual.services.JsonToEntity;
import com.example.projeto_individual.services.SimpleCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
public class RestControllers {
    // Example Controller 1: http://localhost:8081/city?city_name=Paris&date=2021-05-09
    // Example Controller 2: http://localhost:8081/coords?latitude=40&longitude=35&date=2021-05-09
    // Example Controller 3: http://localhost:8081/cacheStatistics

    @Autowired
    ExternalAPIConnect AirQualityAPI;

    @Autowired
    SimpleCacheManager cacheManager;

    @Autowired
    JsonToEntity jsonToEntity;

    Logger logger = Logger.getLogger(RestControllers.class.getName());

    @GetMapping(value = "/city")
    public ResponseEntity<Object> getCityAQ
            (@RequestParam String city_name, @RequestParam(required = false) String date ) throws IOException {

        city_name = city_name.toLowerCase().replace(" ","");

        // Verify Date
        if(date != null && !validateDateFormat(date))
              return new ResponseEntity<>("Invalid Date Format. Use: yyyy-mm-dd.", HttpStatus.BAD_REQUEST);

        // Check Cache
        CityAirQuality caq= cacheManager.findbyCityOrQuery(city_name,city_name);
        if(caq != null){
            logger.log(Level.INFO,"Requested Coords: "+caq.getQuery()+"; Found in Cache: Yes ");
            return new ResponseEntity<>(caq.customizedResponse(date), HttpStatus.OK);
        }

        // No Cache => Use External API
        ResponseEntity<String> response = AirQualityAPI.request(city_name);

        // Found
        if(response.getStatusCode().equals(HttpStatus.OK)){
            String body =  response.getBody();
            if(body == null || body.contains("Unknown station"))
                return new ResponseEntity<>("City not found.", HttpStatus.NOT_FOUND);

            return genObjectAndReply(response,city_name,date);
        }

        // Not Found
        return new ResponseEntity<>("Failed to get response from External API.", HttpStatus.SERVICE_UNAVAILABLE);
    }


    @GetMapping(value = "/coords")
    public ResponseEntity<Object> getCityFromCoords
            (@RequestParam String latitude, @RequestParam String longitude,@RequestParam(required = false) String date) throws IOException {

        // Verify Coords
        if(!validateCoords(latitude.replace(",","."),longitude.replace(",",".")))
            return new ResponseEntity<>("Coords must be numbers.", HttpStatus.BAD_REQUEST);


        // Verify Date
        if(date != null && !validateDateFormat(date))
                return new ResponseEntity<>("Invalid Date Format. Use: yyyy-mm-dd.", HttpStatus.BAD_REQUEST);


        String query = latitude + ";" + longitude;
        double lat = Double.parseDouble(latitude.replace(",","."));
        double lon = Double.parseDouble(longitude.replace(",","."));

        // Check Cache
        CityAirQuality caq= cacheManager.findbyCoords(lat,lon,query);
        if(caq != null){
            logger.log(Level.INFO,"Request: "+caq.getQuery()+"; Found in Cache: Yes ");
            return new ResponseEntity<>(caq.customizedResponse(date), HttpStatus.OK);
        }

        // No Cache => Use External API
        ResponseEntity<String> response = AirQualityAPI.request("geo:" + lat + ";" + lon );

        // Found
        if(response.getStatusCode().equals(HttpStatus.OK))
            return genObjectAndReply(response,query,date);

        // Not Found
        return new ResponseEntity<>("Failed to get response from External API.", HttpStatus.SERVICE_UNAVAILABLE);
    }


    @GetMapping(value = "/cacheStatistics")
    public ResponseEntity<Object> getCacheStatistics(){
        int hits= cacheManager.getHits();
        int misses = cacheManager.getMisses();
        int total = hits + misses;

        Map<String,Integer> answer = new HashMap<>();
        answer.put("hits",hits);
        answer.put("misses",misses);
        answer.put("number_of_requests",total);
        logger.log(Level.INFO,"A Request for Cache's Statistics was done.");
        return new ResponseEntity<>(answer,HttpStatus.OK);

    }



    public ResponseEntity<Object> genObjectAndReply(ResponseEntity<String> response,String query,String date) throws IOException {
        CityAirQuality caq = jsonToEntity.transform(response.getBody(),query);
        logger.log(Level.INFO,"Request: "+caq.getQuery()+"; Found in Cache: No ");
        return new ResponseEntity<>(caq.customizedResponse(date), HttpStatus.OK);
    }

    public boolean validateDateFormat(String dateToValdate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        try {
            formatter.parse(dateToValdate);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public boolean validateCoords(String x, String y){
        try {
            Double.parseDouble(x);
            Double.parseDouble(y);
            return true;
        } catch (NumberFormatException  e) {
            return false;
        }
    }
}