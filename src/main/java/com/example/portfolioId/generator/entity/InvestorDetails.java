package com.example.portfolioId.generator.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "investor_details")
@Data
public class InvestorDetails {

    @Id
    private UUID portfolioId;

    private String name;
    private String phone;
    private String address;
}
