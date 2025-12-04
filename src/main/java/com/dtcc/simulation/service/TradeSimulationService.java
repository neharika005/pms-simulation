package com.dtcc.simulation.service;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.dtcc.simulation.proto.TradeEventOuterClass;

import jakarta.annotation.PostConstruct;

@Service
public class TradeSimulationService {

    private  TradeGeneratorService generator;
    private  RabbitStreamProducer producer;
    private  Random random = new Random();

    private enum SpeedMode {
        SLOW(200), FAST(80), VERY_FAST(20), PAUSE(2000);
        final long delayMs;
        SpeedMode(long d) { this.delayMs = d; }
    }

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

            SpeedMode[] modes = SpeedMode.values();

            while (!Thread.currentThread().isInterrupted()) {

                var event = generator.generateTrade();

                TradeEventOuterClass.TradeEvent.Builder builder =
                        TradeEventOuterClass.TradeEvent.newBuilder()
                                .setPortfolioId(event.getPortfolioId().toString());

                if (event.getTradeId() != null)
                    builder.setTradeId(event.getTradeId().toString());

                if (event.getSymbol() != null)
                    builder.setSymbol(event.getSymbol());

                if (event.getSide() != null)
                    builder.setSide(event.getSide());

                if (event.getPricePerStock() != null)
                    builder.setPricePerStock(event.getPricePerStock());

                if (event.getQuantity() != null)
                    builder.setQuantity(event.getQuantity());

                if (event.getTimestamp() != null)
                    builder.setTimestamp(event.getTimestamp().toEpochSecond(java.time.ZoneOffset.UTC));

                TradeEventOuterClass.TradeEvent proto = builder.build();

                producer.publish(proto);

                SpeedMode mode = modes[random.nextInt(modes.length)];

                try {
                    Thread.sleep(mode.delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        simulationThread.setDaemon(true);
        simulationThread.start();
    }
}