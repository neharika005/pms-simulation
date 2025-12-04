package com.dtcc.simulation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dtcc.simulation.model.TradeEvent;

@Service
public class TradeGeneratorService {

    private  Random random = new Random();

    private  List<UUID> portfolios = List.of(
            UUID.fromString("a3f1c720-9b71-11ef-8023-9fb6bfe1c001"),
            UUID.fromString("a3f1c721-9b71-11ef-8023-9fb6bfe1c002"),
            UUID.fromString("a3f1c722-9b71-11ef-8023-9fb6bfe1c003"),
            UUID.fromString("a3f1c723-9b71-11ef-8023-9fb6bfe1c004"),
            UUID.fromString("a3f1c724-9b71-11ef-8023-9fb6bfe1c005")
    );

    private  List<String> symbols = List.of(
            "AAPL", "MSFT", "GOOGL", "AMZN", "META",
            "NVDA", "TSLA", "NFLX", "AMD", "INTC",
            "IBM", "ORCL", "BAC", "JPM", "WMT"
    );

    public TradeEvent generateTrade() {

        TradeEvent t = new TradeEvent();

        t.setPortfolioId(portfolios.get(random.nextInt(portfolios.size())));

        boolean missingFields = random.nextDouble() < 0.20;

        
        boolean invalidTrade = random.nextDouble() < 0.10;

        if (missingFields) {
            
            return t;
        }
        if (invalidTrade) {
            
            t.setTradeId(UUID.randomUUID());
            
            t.setPricePerStock(-1 * (10 + random.nextDouble(50)));
            t.setQuantity(-1L * (1 + random.nextInt(20)));
            
            t.setTimestamp(LocalDateTime.now().plusHours(1 + random.nextInt(24)));
            
            t.setSymbol(symbols.get(random.nextInt(symbols.size())));
            t.setSide(random.nextBoolean() ? "BUY" : "SELL");

            return t;
        }

        t.setTradeId(UUID.randomUUID());
        t.setSymbol(symbols.get(random.nextInt(symbols.size())));
        t.setSide(random.nextBoolean() ? "BUY" : "SELL");

        t.setPricePerStock(100 + random.nextDouble(101));
        
        t.setQuantity(1 + random.nextLong(100));
        
        t.setTimestamp(LocalDateTime.now().minusHours(random.nextInt(24) + 1));

        return t;
    }
}