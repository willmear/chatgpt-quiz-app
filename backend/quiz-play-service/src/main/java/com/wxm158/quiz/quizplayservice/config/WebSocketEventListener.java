package com.wxm158.quiz.quizplayservice.config;

import com.wxm158.quiz.quizplayservice.model.entity.Participant;
import com.wxm158.quiz.quizplayservice.model.request.ParticipantMessage;
import com.wxm158.quiz.quizplayservice.model.enums.MessageType;
import com.wxm158.quiz.quizplayservice.model.enums.Status;
import com.wxm158.quiz.quizplayservice.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ParticipantRepository participantRepository;
    private final SimpMessageSendingOperations messageTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        Long sessionId = (Long) headerAccessor.getSessionAttributes().get("sessionId");
        if (username != null) {
            log.info("User disconnected: {}", username);
            var participantMessage = ParticipantMessage.builder()
                    .type(MessageType.LEAVE)
                    .participant(username)
                    .build();
            Participant participant = participantRepository.findByAliasAndQuizSessionId(username, sessionId);
            participant.setStatus(Status.OFFLINE);
            participantRepository.save(participant);
            messageTemplate.convertAndSend("/topic/public", participantMessage);
        }
    }
}
