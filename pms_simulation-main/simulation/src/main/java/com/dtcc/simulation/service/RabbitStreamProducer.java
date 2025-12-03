package com.dtcc.simulation.service;

import org.springframework.stereotype.Service;

import com.dtcc.simulation.proto.TradeEventOuterClass;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.Message;
import com.rabbitmq.stream.Producer;

import jakarta.annotation.PostConstruct;

@Service
public class RabbitStreamProducer {

    private Environment env;
    private Producer producer;

    @PostConstruct
    public void init() {

        String host = System.getenv("RABBITMQ_HOST");
        if (host == null || host.isBlank()) {
            host = "localhost";  // IDE case
        }

        System.out.println("🔗 Connecting to RabbitMQ Stream at: " + host + ":5552");

        // Retry until RabbitMQ Stream is ready
        int attempts = 0;
        while (attempts < 10) {
            try {
                env = Environment.builder()
                        .host(host)
                        .port(5552)
                        .username("guest")
                        .password("guest")
                        .build();
                break; // success
            } catch (Exception e) {
                attempts++;
                System.out.println("⏳ RabbitMQ not ready, retrying " + attempts + "/10 ...");
                try { Thread.sleep(3000); } catch (InterruptedException ignored) {}
            }
        }

        if (env == null) {
            throw new RuntimeException("❌ Failed to connect to RabbitMQ Stream");
        }

        // Create stream
        try {
            env.streamCreator()
                    .stream("trade-stream")
                    .create();
            System.out.println("✔ Stream 'trade-stream' created.");
        } catch (Exception ex) {
            System.out.println("ℹ Stream already exists — continuing...");
        }

        // Build producer
        producer = env.producerBuilder()
                .stream("trade-stream")
                .build();

        System.out.println("✔ RabbitMQ Stream Producer is READY.");
    }

    public void publish(TradeEventOuterClass.TradeEvent event) {
        byte[] body = event.toByteArray();

        Message msg = producer.messageBuilder()
                .addData(body)
                .build();

        producer.send(msg, confirmation -> {
            if (!confirmation.isConfirmed()) {
                System.err.println("❌ Failed to send message to stream");
            }
        });
    }
}
