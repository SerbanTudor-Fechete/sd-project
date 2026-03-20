package com.andrei.demo.model;

import com.andrei.demo.config.Status;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "serviceappointment")
public class ServiceAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "scheduledate", nullable = false)
    private String scheduleDate;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "totalcost", nullable = false)
    private Double totalCost;

    @Column(name = "status", nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorcycle_id")
    @JsonIgnore
    private Motorcycle motorcycle;

    @ManyToMany
    @JoinTable(name = "appointment_parts", joinColumns = @JoinColumn(name = "appointment_id"), inverseJoinColumns = @JoinColumn(name = "part_id"))
    private List<Part> partsUsed = new ArrayList<>();
}
