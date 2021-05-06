package com.example.projeto_individual.services;

import com.example.projeto_individual.entities.AirComponent;
import com.example.projeto_individual.entities.CityAirQuality;
import com.example.projeto_individual.entities.DayValues;
import com.example.projeto_individual.repositories.AirComponentRepository;
import com.example.projeto_individual.repositories.CityAirQualityRepository;
import com.example.projeto_individual.repositories.DayValuesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

@Service
public class JsonToEntity {
    @Autowired
    CityAirQualityRepository CAQRepository;
    @Autowired
    AirComponentRepository ACRepository;
    @Autowired
    DayValuesRepository dayValuesRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    public CityAirQuality transform(String json,String query) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(json);

        // City
        String city = jsonNode.get("data").get("city").get("name").asText();

        // AQI
        double aqi = jsonNode.get("data").get("aqi").asDouble();

        // Latitude & Longitude
        Iterator<JsonNode> coords = jsonNode.get("data").get("city").get("geo").elements();
        ArrayList<Double> coords_array = new ArrayList<>();

        while (coords.hasNext())
            coords_array.add(coords.next().asDouble());

        // CREATE CAQ OBJECT
        CityAirQuality caq = new CityAirQuality();
        caq.setLastAccess(System.currentTimeMillis());
        caq.setExpiration(System.currentTimeMillis());
        caq.setAqi(aqi);
        caq.setCity(city);
        caq.setQuery(query);
        if(coords_array.size()==2){
            caq.setLatitude(coords_array.get(0));
            caq.setLongitude(coords_array.get(1));
        }
        CAQRepository.save(caq);

        // Components Qty For Day
        Iterator<Map.Entry<String, JsonNode>> components = jsonNode.get("data").get("forecast").get("daily").fields();

        while (components.hasNext()){
            AirComponent ac = new AirComponent();
            Map.Entry<String, JsonNode> entry = components.next();
            ac.setName(entry.getKey());

            // Save AirComponent & Add to List
            ACRepository.save(ac);
            caq.getAirComponentList().add(ac);

            Iterator<JsonNode> daysList = entry.getValue().elements();
            while(daysList.hasNext()){
                JsonNode node = daysList.next();
                DayValues d = new DayValues();
                // d.setDate(node.get("day").asText());
                d.setAvg(node.get("avg").asInt());
                d.setMin(node.get("min").asInt());
                d.setMax(node.get("max").asInt());

                // Save Day & Add to List
                dayValuesRepository.save(d);
                ac.getDayMap().put(node.get("day").asText(),d);
            }
            ACRepository.save(ac);
        }
        CAQRepository.save(caq);
        return caq;
    }

}
