package com.example.projeto_individual.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import javax.persistence.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class CityAirQuality {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String city;
    private String query;
    private String normalizedCity;
    private String link;
    private double latitude;
    private double longitude;
    private double aqi; //air quality index
    private long lastAccess;
    private long expiration;

    @OnDelete(action= OnDeleteAction.CASCADE)
    @OneToMany(targetEntity = AirComponent.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "city_air_quality_id")
    private List<AirComponent> airComponentList = new ArrayList<>();


    public CityAirQuality() { /* Constructor */ }

    // Getters & Setters
    public int getId() { return id; }
    public String getCity() { return city; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getAqi() { return aqi; }
    public long getLastAccess() { return lastAccess; }
    public String getQuery() { return query; }
    public long getExpiration() { return expiration; }
    public String getNormalizedCity() { return normalizedCity; }
    public List<AirComponent> getAirComponentList() { return airComponentList; }
    public String getLink() { return link; }

    public void setExpiration(long expiration) { this.expiration = expiration; }
    public void setQuery(String query) { this.query = query; }
    public void setLastAccess(long ttl) { this.lastAccess = ttl; }
    public void setId(int id) { this.id = id; }
    public void setCity(String city) { this.city = city; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setAqi(double aqi) { this.aqi = aqi; }
    public void setNormalizedCity(String normalizedCity) { this.normalizedCity = normalizedCity; }
    public void setAirComponentList(List<AirComponent> airComponentList) { this.airComponentList = airComponentList; }
    public void setLink(String link) { this.link = link; }

    public Map<String,Object> customizedResponse(String date){

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        String ftoday = dateFormat.format(today);
        if (date == null)
            date = ftoday;

        String finalDate = date;
        for (AirComponent ac : this.airComponentList){

            Map<String, DayValues> mapfiltered =
                    ac.getDayMap().entrySet().stream().filter(entry -> entry.getKey().equals(finalDate))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            ac.setDayMap(mapfiltered);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("city", this.city);
        map.put("link",this.link);
        map.put("aqi", ftoday.equals(date) ? this.aqi : "");
        map.put("date",finalDate);
        map.put("latitude", this.latitude);
        map.put("longitude", this.longitude);
        map.put("components",this.airComponentList);
        return map;
    }

    @Override
    public String toString() {
        return "CityAirQuality{" +
                "city='" + city + '\'' +
                ", query='" + query + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", aqi=" + aqi +
                ", lastAccess=" + lastAccess +
                ", expiration=" + expiration +
                ", airComponentList=" + airComponentList.toString() +
                '}';
    }
}
