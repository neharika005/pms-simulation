package com.dtcc.simulation.model;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class TradeEvent {

    private UUID portfolioId;          
    private UUID tradeId;            
    private String symbol;        
    private String side;            
    private double pricePerStock;
    private long quantity;
    private LocalDateTime timestamp;
}
