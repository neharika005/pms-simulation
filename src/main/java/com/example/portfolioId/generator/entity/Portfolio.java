package com.example.portfolioId.generator.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Portfolio {

    @Id
    private String id;

    private String name;
    private String phone;
    private String address;
}
