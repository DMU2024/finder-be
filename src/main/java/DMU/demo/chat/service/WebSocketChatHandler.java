package DMU.demo.chat.service;

import DMU.demo.chat.domain.entity.ChatMessage;
import DMU.demo.chat.dto.ChatDto;
import DMU.demo.chat.dto.User;
import DMU.demo.chat.domain.repository.ChatRepository;
import DMU.demo.chat.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private static final Map<String, WebSocketSession> sessions = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        String userId = null;

        if (query != null && query.startsWith("userId=")) {
            userId = query.substring("userId=".length());
        }

        if (userId != null) {
            session.getAttributes().put("userId", userId);
            sessions.put(userId, session);
            log.info("{} - 클라이언트 접속", userId);
        } else {
            log.warn("Username is null in session attributes.");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received payload: {}", payload);

        String sender = (String) session.getAttributes().get("userId");
        if (sender != null) {
            JsonNode jsonNode = objectMapper.readTree(payload);
            String recipient = jsonNode.get("recipient").asText();
            String msg = jsonNode.get("message").asText();

            // Create a consistent room ID for the chat room
            String[] users = {sender, recipient};
            Arrays.sort(users);
            String roomId = String.join("_", users);

            // Format message to include sender's username
            ObjectMapper mapper = new ObjectMapper();
            ChatDto chatDto = ChatDto.builder()
                    .sender(Integer.parseInt(sender))
                    .message(msg)
                    .messageDate(new Timestamp(System.currentTimeMillis()))
                    .messageTime(new Time(System.currentTimeMillis()))
                    .build();

            String formattedMessage = mapper.writeValueAsString(chatDto);

            // Send the message to the recipient
            WebSocketSession recipientSession = sessions.get(recipient);
            if (recipientSession != null && recipientSession.isOpen()) {
                recipientSession.sendMessage(new TextMessage(formattedMessage));
            } else {
                log.warn("Recipient session is not available or not open.");
            }

            // Also send the message to the sender
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(formattedMessage));
            }

            // Save the message to the chat repository
            if (recipient != null) {
                ChatMessage chatMessage = ChatMessage.builder()
                        .roomId(roomId)
                        .userId(Integer.parseInt(recipient))
                        .sender(chatDto.getSender())
                        .message(chatDto.getMessage())
                        .messageType(ChatMessage.MessageType.TALK)
                        .messageDate(chatDto.getMessageDate())
                        .messageTime(chatDto.getMessageTime())
                        .build();
                chatRepository.save(chatMessage);
            } else {
                log.warn("Sender or recipient user not found in the repository.");
            }
        } else {
            log.warn("Sender is null in session attributes.");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            log.info("{} - 클라이언트 접속 해제", userId);
            sessions.remove(userId);
        } else {
            log.warn("Username is null in session attributes.");
        }
    }
}