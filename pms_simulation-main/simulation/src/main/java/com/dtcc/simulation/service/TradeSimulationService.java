package com.dtcc.simulation.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.dtcc.simulation.model.TradeEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Service
public class TradeSimulationService {

    private TradeGeneratorService generator;
    private ObjectMapper mapper;

    private static Logger log = LoggerFactory.getLogger(TradeSimulationService.class);

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    private Map<String, ScheduledFuture<?>> tradeTasks = new ConcurrentHashMap<>();
    private Map<String, ScheduledFuture<?>> paceTasks = new ConcurrentHashMap<>();
    private Map<String, Integer> speedIndex = new ConcurrentHashMap<>();

    private long[] speeds = {1000, 500, 150, 50, -1};

    public TradeSimulationService(TradeGeneratorService generator) {
        this.generator = generator;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public void startSession(WebSocketSession session) {
        String id = session.getId();
        speedIndex.put(id, 0);

        startTradeTask(session, speeds[0]);
        startSpeedTask(session);
    }

    public void stopSession(WebSocketSession session) {
        String id = session.getId();

        ScheduledFuture<?> trade = tradeTasks.remove(id);
        if (trade != null) {
            trade.cancel(false);
        }

        ScheduledFuture<?> pace = paceTasks.remove(id);
        if (pace != null) {
            pace.cancel(false);
        }

        speedIndex.remove(id);
    }

    private void startTradeTask(WebSocketSession session, long interval) {

        String id = session.getId();

        ScheduledFuture<?> old = tradeTasks.remove(id);
        if (old != null) {
            old.cancel(false);
        }

        if (interval == -1) {
            return;
        }

        ScheduledFuture<?> task = executor.scheduleAtFixedRate(
                () -> pushTrade(session),
                0,
                interval,
                TimeUnit.MILLISECONDS
        );

        tradeTasks.put(id, task);
    }

    private void startSpeedTask(WebSocketSession session) {

        String id = session.getId();

        ScheduledFuture<?> old = paceTasks.remove(id);
        if (old != null) {
            old.cancel(false);
        }

        ScheduledFuture<?> pace = executor.scheduleAtFixedRate(() -> {

            int index = speedIndex.getOrDefault(id, 0);
            index = (index + 1) % speeds.length;
            speedIndex.put(id, index);

            startTradeTask(session, speeds[index]);

        }, 10, 10, TimeUnit.SECONDS);

        paceTasks.put(id, pace);
    }

    private void pushTrade(WebSocketSession session) {
        try {
            if (!session.isOpen()) {
                return;
            }

            TradeEvent event = generator.generateTrade();
            String json = mapper.writeValueAsString(event);

            session.sendMessage(new TextMessage(json));

        } catch (Exception e) {
            log.error("Error while pushing trade event for session {}: {}" ,
                session.getId(), e.getMessage());
        }
    }
}


