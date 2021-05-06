package com.example.projeto_individual.repositories;

import com.example.projeto_individual.entities.AirComponent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirComponentRepository extends JpaRepository<AirComponent, Long> {

}