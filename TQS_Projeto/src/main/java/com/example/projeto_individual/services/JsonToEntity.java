package com.example.projeto_individual.services;

import com.example.projeto_individual.entities.AirComponent;
import com.example.projeto_individual.entities.CityAirQuality;
import com.example.projeto_individual.entities.DayValues;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

@Component
public class JsonToEntity {

    @Autowired
    SimpleCacheManager cacheManager;

    ObjectMapper objectMapper = new ObjectMapper();

    public CityAirQuality transform(String json,String query) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(json).get("data");

        // City,Link and AQI
        String city = jsonNode.get("city").get("name").asText();
        String link = jsonNode.get("city").get("url").asText();
        double aqi = jsonNode.get("aqi").asDouble();

        // Latitude & Longitude
        Iterator<JsonNode> coords = jsonNode.get("city").get("geo").elements();
        ArrayList<Double> coords_array = new ArrayList<>();
        while (coords.hasNext())
            coords_array.add(coords.next().asDouble());

        // CREATE CAQ OBJECT
        CityAirQuality caq = new CityAirQuality();
        caq.setAqi(aqi);
        caq.setCity(city);
        caq.setLink(link);
        caq.setQuery(query);
        caq.setLatitude(coords_array.get(0));
        caq.setLongitude(coords_array.get(1));
        caq.setLastAccess(System.currentTimeMillis());
        caq.setExpiration(System.currentTimeMillis());
        cacheManager.save(caq);

        // Components Qty For Day
        JsonNode compNode =jsonNode.get("forecast").get("daily");

        if(compNode!= null){
            Iterator<Map.Entry<String, JsonNode>> components = jsonNode.get("forecast").get("daily").fields();

            while (components.hasNext()){
                AirComponent ac = new AirComponent();
                Map.Entry<String, JsonNode> entry = components.next();
                ac.setName(entry.getKey());

                // Save AirComponent & Add to List
                cacheManager.save(ac);
                caq.getAirComponentList().add(ac);

                Iterator<JsonNode> daysList = entry.getValue().elements();
                while(daysList.hasNext()){
                    JsonNode node = daysList.next();
                    DayValues d = new DayValues();
                    d.setAvg(node.get("avg").asInt());
                    d.setMin(node.get("min").asInt());
                    d.setMax(node.get("max").asInt());

                    // Save Day & Add to List
                    cacheManager.save(d);
                    ac.getDayMap().put(node.get("day").asText(),d);
                }
                cacheManager.save(ac);
            }
            cacheManager.save(caq);
        }
        return caq;
    }

}
