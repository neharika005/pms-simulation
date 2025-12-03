package com.dtcc.simulation.service;

import org.springframework.stereotype.Service;

import com.dtcc.simulation.proto.TradeEventOuterClass;

import jakarta.annotation.PostConstruct;

@Service
public class TradeSimulationService {

    private final TradeGeneratorService generator;
    private final RabbitStreamProducer producer;

    public TradeSimulationService(
            TradeGeneratorService generator,
            RabbitStreamProducer producer
    ) {
        this.generator = generator;
        this.producer = producer;
    }

    @PostConstruct
    public void start() {

        Thread simulationThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {

                var event = generator.generateTrade();

                TradeEventOuterClass.TradeEvent proto =
                        TradeEventOuterClass.TradeEvent.newBuilder()
                                .setPortfolioId(event.getPortfolioId().toString())
                                .setTradeId(event.getTradeId() == null ? "" : event.getTradeId().toString())
                                .setSymbol(event.getSymbol() == null ? "" : event.getSymbol())
                                .setSide(event.getSide() == null ? "" : event.getSide())
                                .setPricePerStock(event.getPricePerStock() == null ? 0 : event.getPricePerStock())
                                .setQuantity(event.getQuantity() == null ? 0 : event.getQuantity())
                                .setTimestamp(event.getTimestamp() == null ? 0 :
                                        event.getTimestamp().toEpochSecond(java.time.ZoneOffset.UTC))
                                .build();

                producer.publish(proto);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        simulationThread.setDaemon(true);
        simulationThread.start();
    }
}
