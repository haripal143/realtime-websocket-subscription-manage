package com.example.websocketsubscription.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class SubscriptionService {

    private final SimpMessagingTemplate messagingTemplate;
    private final Map<String, String> userSubscriptions = new ConcurrentHashMap<>();

    public SubscriptionService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void handleSubscription(String sessionId, String destination) {
        log.info("New subscription: sessionId={}, destination={}", sessionId, destination);
        userSubscriptions.put(sessionId, destination);
    }

    public void handleUnsubscribe(String sessionId) {
        log.info("Unsubscribe: sessionId={}", sessionId);
        userSubscriptions.remove(sessionId);
    }

    public void handleError(String sessionId, Throwable error) {
        log.error("WebSocket error for session {}: {}", sessionId, error.getMessage());
        String destination = userSubscriptions.get(sessionId);
        if (destination != null) {
            sendErrorMessage(sessionId, destination, error.getMessage());
        }
    }

    private void sendErrorMessage(String sessionId, String destination, String errorMessage) {
        MessageHeaders headers = createHeaders(sessionId);
        messagingTemplate.convertAndSendToUser(
            sessionId,
            destination,
            Map.of("error", errorMessage),
            headers
        );
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();
    }
} 