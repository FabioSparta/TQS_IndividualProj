package com.example.projeto_individual.services;

import com.example.projeto_individual.entities.CityAirQuality;
import com.example.projeto_individual.repositories.CityAirQualityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class SimpleCacheManager {

    @Autowired
    CityAirQualityRepository CAQRepository;
    private int hits = 0;
    private int misses = 0;
    final static int INTERVAL = 15; // seconds
    final static int TTL = 30; // seconds
    final static int EXPIRATION = 120; //seconds

    public SimpleCacheManager(){
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(INTERVAL*1000);
                } catch (InterruptedException ex) {
                }
                System.out.println("Manager Passing through..!");
                CleanUpOld();
            }
        });

        t.setDaemon(true);
        t.start();
    }

    private void CleanUpOld() {
        CAQRepository.deleteLastAccessOlderThan(System.currentTimeMillis() -  TTL *1000);
        CAQRepository.deleteExpired(System.currentTimeMillis() - EXPIRATION * 1000);
    }

    public int getMisses() { return misses; }
    public void setMisses(int misses) { misses = misses; }
    public int getHits() { return hits; }
    public void setHits(int hits) { hits = hits; }
}
