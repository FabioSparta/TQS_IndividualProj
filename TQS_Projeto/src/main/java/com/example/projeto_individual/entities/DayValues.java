package com.example.projeto_individual.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class DayValues {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int max;
    private int min;
    private int avg;

    public DayValues() { }

    // GETTERS AND SETTERS
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMax() { return max; }
    public void setMax(int max) { this.max = max; }
    public int getMin() { return min; }
    public void setMin(int min) { this.min = min; }
    public int getAvg() { return avg; }
    public void setAvg(int avg) { this.avg = avg; }
}
