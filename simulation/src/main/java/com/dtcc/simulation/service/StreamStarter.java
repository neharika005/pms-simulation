package com.dtcc.simulation.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.dtcc.simulation.client.StreamingApiClient;

@Component
public class StreamStarter implements CommandLineRunner {

    private final StreamingApiClient client;

    public StreamStarter(StreamingApiClient client) {
        this.client = client;
    }

    @Override
    public void run(String... args) {
        client.start();
    }
}
