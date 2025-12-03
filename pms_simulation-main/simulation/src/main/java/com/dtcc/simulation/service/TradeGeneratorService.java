package com.dtcc.simulation.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.dtcc.simulation.model.TradeEvent;

@Service
public class TradeGeneratorService {

    private Random random = new Random();

    private List<UUID> pList = List.of(
            UUID.fromString("a3f1c720-9b71-11ef-8023-9fb6bfe1c001"),
            UUID.fromString("a3f1c721-9b71-11ef-8023-9fb6bfe1c002"),
            UUID.fromString("a3f1c722-9b71-11ef-8023-9fb6bfe1c003"),
            UUID.fromString("a3f1c723-9b71-11ef-8023-9fb6bfe1c004"),
            UUID.fromString("a3f1c724-9b71-11ef-8023-9fb6bfe1c005")
    );

    private List<String> symbolList = List.of(
            "AAPL", "MSFT", "GOOGL", "AMZN", "META",
            "NVDA", "TSLA", "NFLX", "AMD", "INTC",
            "IBM", "ORCL", "BAC", "JPM", "WMT"
    );

    private boolean isNegativeScenario() {
        return random.nextDouble() < 0.05;
    }

    public TradeEvent generateTrade() {

        TradeEvent trade = new TradeEvent();
        trade.setPortfolioId(pList.get(random.nextInt(pList.size())));

        if (isNegativeScenario()) {
            trade.setTradeId(null);
            trade.setSymbol(null);
            trade.setSide(null);
            trade.setPricePerStock(null);
            trade.setQuantity(null);
            trade.setTimestamp(null);
            return trade;
        }

        trade.setTradeId(UUID.randomUUID());
        trade.setSymbol(symbolList.get(random.nextInt(symbolList.size())));
        trade.setSide(random.nextBoolean() ? "BUY" : "SELL");
        trade.setPricePerStock(100 + random.nextDouble() * 200);
        trade.setQuantity((long) (1 + random.nextInt(500)));

        long hoursBack = 1 + random.nextInt(72);
        trade.setTimestamp(LocalDateTime.now().minusHours(hoursBack));

        return trade;
    }
}
