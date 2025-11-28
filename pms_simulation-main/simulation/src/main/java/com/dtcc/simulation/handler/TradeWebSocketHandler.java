package com.dtcc.simulation.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.dtcc.simulation.service.TradeSimulationService;

@Component
public class TradeWebSocketHandler extends TextWebSocketHandler {

    private final TradeSimulationService simulationService;

    public TradeWebSocketHandler(TradeSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        simulationService.startSession(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        simulationService.stopSession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        simulationService.stopSession(session);
    }
}
