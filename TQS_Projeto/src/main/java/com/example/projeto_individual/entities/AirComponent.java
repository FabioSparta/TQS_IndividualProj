package com.example.projeto_individual.entities;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
public class AirComponent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;

    @OnDelete(action= OnDeleteAction.CASCADE)
    @OneToMany(targetEntity = DayValues.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "air_component_id")
    private Map<String, DayValues> dayMap = new HashMap<>();

    public AirComponent() { }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map<String, DayValues> getDayMap() { return dayMap; }
    public void setDayMap(Map<String, DayValues> dayList) { this.dayMap = dayList; }
}
