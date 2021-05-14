package com.example.projeto_individual.services;

import com.example.projeto_individual.entities.AirComponent;
import com.example.projeto_individual.entities.CityAirQuality;
import com.example.projeto_individual.entities.DayValues;
import com.example.projeto_individual.repositories.AirComponentRepository;
import com.example.projeto_individual.repositories.CityAirQualityRepository;
import com.example.projeto_individual.repositories.DayValuesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class SimpleCacheManager {

    @Autowired
    CityAirQualityRepository CAQRepository;
    @Autowired
    AirComponentRepository ACRepository;
    @Autowired
    DayValuesRepository DVRepository;

    private int hits = 0;
    private int misses = 0;
    static final int INTERVAL = 1; // seconds
    int LAST_ACCESS = 20; // seconds
    int EXPIRATION = 50; //seconds

    public SimpleCacheManager(){
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(INTERVAL*1000L);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                //System.out.println("Manager Passing through..!");
                CleanUpOld();
            }
        });

        t.setDaemon(true);
        t.start();

    }

    public int getMisses() { return misses; }
    public void setMisses(int misses) { this.misses = misses; }
    public int getHits() { return hits; }
    public void setHits(int hits) { this.hits = hits; }

    public int getLAST_ACCESS() { return LAST_ACCESS; }
    public void setLAST_ACCESS(int LAST_ACCESS) { this.LAST_ACCESS = LAST_ACCESS; }
    public int getEXPIRATION() { return EXPIRATION; }
    public void setEXPIRATION(int EXPIRATION) { this.EXPIRATION = EXPIRATION; }

    public void save(CityAirQuality caq){ CAQRepository.save(caq); }
    public void save(AirComponent ac){ ACRepository.save(ac); }
    public void save(DayValues dv){ DVRepository.save(dv); }

    public CityAirQuality findbyCityOrQuery(String city_name,String query){
        CityAirQuality found = CAQRepository.findByNormalizedCityOrQuery(city_name,query);
        updateStatus(found);
        return found;
    }

    public CityAirQuality findbyCoords(Double lat,Double lon,String query) {
        CityAirQuality found = CAQRepository.findByLatitudeAndLongitude(lat,lon,query);
        updateStatus(found);
        return found;
    }
    private void updateStatus(CityAirQuality found) {
        if (found != null) {
            this.hits++;
            found.setLastAccess(System.currentTimeMillis());
            CAQRepository.save(found);
        }
        else
            this.misses++;
    }

    private void CleanUpOld() {
        CAQRepository.deleteLastAccessOlderThan(System.currentTimeMillis() -  LAST_ACCESS *1000);
        CAQRepository.deleteExpired(System.currentTimeMillis() - EXPIRATION * 1000);
    }


}
