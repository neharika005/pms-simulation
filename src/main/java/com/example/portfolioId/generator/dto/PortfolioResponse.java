package com.example.portfolioId.generator.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PortfolioResponse {
    private UUID portfolioId;
}
