package com.andrei.demo.repository;

import com.andrei.demo.model.Motorcycle;
import com.andrei.demo.model.ServiceAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ServiceAppointmentRepository extends JpaRepository<ServiceAppointment, UUID> {

        @Query("SELECT sa FROM ServiceAppointment sa JOIN FETCH sa.motorcycle m JOIN FETCH m.owner o WHERE o.email = :email")
        List<ServiceAppointment> findByMotorcycle_Owner_Email(String email);
}
