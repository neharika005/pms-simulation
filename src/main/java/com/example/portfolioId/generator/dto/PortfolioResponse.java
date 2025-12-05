package com.example.portfolioId.generator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PortfolioResponse {

    private String portfolioId;
    private String message;
}
