package com.dtcc.simulation.service;

import org.springframework.stereotype.Service;

import com.dtcc.simulation.proto.TradeEventOuterClass;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.Producer;

import jakarta.annotation.PostConstruct;

@Service
public class RabbitStreamProducer {

    private static  int PORT = 5552;
    private static  int MAX_RETRIES = 10;
    private static  int RETRY_DELAY_MS = 3000;
    private static  String STREAM_NAME = "trade-stream";

    private Environment env;
    private Producer producer;

    @PostConstruct
    public void init() {
        String host = System.getenv("RABBITMQ_HOST");
        if (host == null || host.isBlank()) {
            host = "localhost";
        }

        env = createEnvironmentWithRetry(host);

        createStreamIfNotExists(env, STREAM_NAME);

        producer = env.producerBuilder()
                .stream(STREAM_NAME)
                .build();
    }

    private Environment createEnvironmentWithRetry(String host) {
        int attempts = 0;
        Exception lastException = null;

        while (attempts < MAX_RETRIES) {
            try {
                return Environment.builder()
                        .host(host)
                        .port(PORT)
                        .username("guest")
                        .password("guest")
                        .build();
            } catch (Exception e) {
                lastException = e;
                attempts++;
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Thread interrupted during retry", ex);
                }
            }
        }

        throw new IllegalStateException("Failed to connect to RabbitMQ Stream after retries", lastException);
    }

    private void createStreamIfNotExists(Environment env, String streamName) {
        try {
            env.streamCreator().stream(streamName).create();
        } catch (Exception e) {
            if (!e.getMessage().contains("STREAM_ALREADY_EXISTS")) {
                throw new IllegalStateException("Failed to create RabbitMQ Stream: " + streamName, e);
            }
        }
    }

    public void publish(TradeEventOuterClass.TradeEvent event) {
        if (producer == null) {
            throw new IllegalStateException("Producer not initialized");
        }

        if (event == null) {
            throw new IllegalArgumentException("TradeEvent cannot be null");
        }

        Message msg = producer.messageBuilder()
                .addData(event.toByteArray())
                .build();

        producer.send(msg, confirmation -> {
            if (!confirmation.isConfirmed()) {
                throw new RuntimeException("RabbitMQ Stream failed to confirm message");
            }
        });
    }
}
