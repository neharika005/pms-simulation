package com.dtcc.simulation.handler;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.dtcc.simulation.model.TradeEvent;
import com.dtcc.simulation.service.TradeGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Component
public class TradeWebSocketHandler extends TextWebSocketHandler {

    private final TradeGeneratorService generator;
    private final ObjectMapper mapper;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Map<String, ScheduledFuture<?>> sessionTasks = new ConcurrentHashMap<>();

    public TradeWebSocketHandler(TradeGeneratorService generator) {
        this.generator = generator;
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("WebSocket connected: {}", session.getId());

        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> pushTrade(session), 0, 500, TimeUnit.MILLISECONDS);

        sessionTasks.put(session.getId(), task);
    }

    private void pushTrade(WebSocketSession session) {
        try {
            if (!session.isOpen()) return;

            TradeEvent event = generator.generateTrade();
            String json = mapper.writeValueAsString(event);

            session.sendMessage(new TextMessage(json));

        } catch (Exception e) {
            log.error("Error sending message for session {}: {}", session.getId(), e.getMessage());
            closeSession(session);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.warn("Transport error on session {}: {}", session.getId(), exception.getMessage());
        closeSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("WebSocket session closed: {}", session.getId());
        closeSession(session);
    }

    private void closeSession(WebSocketSession session) {
        String id = session.getId();

        ScheduledFuture<?> task = sessionTasks.remove(id);
        if (task != null) task.cancel(true);

        try {
            if (session.isOpen()) session.close();
        } catch (Exception ignored) {}
    }
}
