package com.example.projeto_individual.repositories;

import com.example.projeto_individual.entities.DayValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DayValuesRepository extends JpaRepository<DayValues, Long> {

}
