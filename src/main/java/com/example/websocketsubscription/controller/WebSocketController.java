package com.example.websocketsubscription.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send")
    public void handleMessage(@Payload Map<String, Object> payload, SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Received message from session {}: {}", sessionId, payload);

        try {
            // Process the message
            String response = "Processed: " + payload.get("message");
            
            // Send response to the specific user
            messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/messages",
                Map.of("response", response)
            );
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                Map.of("error", "Failed to process message: " + e.getMessage())
            );
        }
    }
} 