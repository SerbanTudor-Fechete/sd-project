package com.andrei.demo.repository;

import com.andrei.demo.model.Motorcycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MotorcycleRepository extends JpaRepository<Motorcycle, UUID> {
    boolean existsByLicensePlate(String licensePlate);
}
