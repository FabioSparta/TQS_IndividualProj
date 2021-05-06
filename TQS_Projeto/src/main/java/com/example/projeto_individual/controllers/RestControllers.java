package com.example.projeto_individual.controllers;

import com.example.projeto_individual.entities.CityAirQuality;
import com.example.projeto_individual.repositories.CityAirQualityRepository;
import com.example.projeto_individual.services.JsonToEntity;
import com.example.projeto_individual.services.SimpleCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


@RestController
public class RestControllers {
    // EXTERNAL AIR QUALITY API WEBSITE: https://aqicn.org/api/
    // My Token: 6767c09a8ffb49fd6ec3be10d592e6b634812f24
    // Example 1: http://localhost:8081/city?city_name=London?date=2021-05-09
    // Example 2: http://localhost:8081/coords?latitude=40&longitude=35date=2021-05-09

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    SimpleCacheManager cacheManager;

    @Autowired
    JsonToEntity jsonToEntity;

    @Autowired
    CityAirQualityRepository CAQRepository;


    @GetMapping(value = "/city")
    public ResponseEntity<Object> getCityAQ
            (@RequestParam String city_name, @RequestParam(required = false) String date ) throws IOException {
        city_name = city_name.toLowerCase().replace(" ","");
        // Verify Date
        if(date != null)
          if(!validateDateFormat(date))
              return new ResponseEntity<>("Invalid Date Format. Use: yyyy-mm-dd.", HttpStatus.BAD_REQUEST);

        // Check Cache
        CityAirQuality caq= CAQRepository.findByNormalizedCityOrQuery(city_name,city_name);
        if(caq != null)
            return updateCacheAndReply(caq,date);


        // No Cache => Use External API
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "https://api.waqi.info/feed/";
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + city_name +"/?token=" + apiKey , String.class);

        // Found
        if(response.getStatusCode().equals(HttpStatus.OK)){
            return genObjectAndReply(response,city_name,date);
        }

        // Not Found
        return new ResponseEntity<>("City not found.", HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/coords")
    public ResponseEntity<Object> getCityFromCoords
            (@RequestParam String latitude, @RequestParam String longitude,@RequestParam(required = false) String date) throws IOException {

        // Verify Coords
        if(!validateCoords(latitude.replace(",","."),longitude.replace(",",".")))
            return new ResponseEntity<>("Coords must be numbers.", HttpStatus.BAD_REQUEST);


        // Verify Date
        if(date != null)
            if(!validateDateFormat(date))
                return new ResponseEntity<>("Invalid Date Format. Use: yyyy-mm-dd.", HttpStatus.NOT_FOUND);


        String query = latitude + ";" + longitude;
        double lat = Double.parseDouble(latitude.replace(",","."));
        double lon = Double.parseDouble(longitude.replace(",","."));

        // Check Cache
        CityAirQuality caq= CAQRepository.findByLatitudeAndLongitude(lat,lon,query);
        if(caq != null)
           return updateCacheAndReply(caq,date);


        // No Cache => Use External API
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "https://api.waqi.info/feed/geo:";
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + lat + ";" + lon +"/?token=" + apiKey , String.class);

        // Found
        if(response.getStatusCode().equals(HttpStatus.OK))
            return genObjectAndReply(response,query,date);

        // Not Found
        return new ResponseEntity<>("City not found.", HttpStatus.NOT_FOUND);
    }




    public ResponseEntity<Object> genObjectAndReply(ResponseEntity<String> response,String query,String date) throws IOException {
        System.out.println("found it via External API!");
        CityAirQuality caq = jsonToEntity.transform(response.getBody(),query);
        cacheManager.setHits(cacheManager.getMisses()+1);
        return new ResponseEntity<>(caq.customizedResponse(date), HttpStatus.OK);
    }

    public ResponseEntity<Object> updateCacheAndReply( CityAirQuality caq, String date)  {
        System.out.println("Found it via cache!");
        caq.setLastAccess(System.currentTimeMillis());
        CAQRepository.save(caq);
        cacheManager.setMisses(cacheManager.getMisses()+1);
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