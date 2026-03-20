package com.andrei.demo.repository;

import com.andrei.demo.model.Motorcycle;
import com.andrei.demo.model.ServiceAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServiceAppointmentRepository extends JpaRepository<ServiceAppointment, UUID> {

}
