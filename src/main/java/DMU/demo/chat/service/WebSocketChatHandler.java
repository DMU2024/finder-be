package DMU.demo.chat.service;

import DMU.demo.chat.dto.ChatMessage;
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
        String username = null;

        if (query != null && query.startsWith("username=")) {
            username = query.substring("username=".length());
        }

        if (username != null) {
            session.getAttributes().put("username", username);
            sessions.put(username, session);
            log.info("{} - 클라이언트 접속", username);
        } else {
            log.warn("Username is null in session attributes.");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received payload: {}", payload);

        String sender = (String) session.getAttributes().get("username");
        if (sender != null) {
            JsonNode jsonNode = objectMapper.readTree(payload);
            String recipient = jsonNode.get("recipient").asText();
            String msg = jsonNode.get("message").asText();

            // Create a consistent room ID for the chat room
            String[] users = {sender, recipient};
            Arrays.sort(users);
            String roomId = String.join("_", users);

            // Format message to include sender's username
            String formattedMessage = String.format("{\"sender\":\"%s\", \"message\":\"%s\"}", sender, msg);

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
            User senderUser = userRepository.findByUsername(sender);
            User recipientUser = userRepository.findByUsername(recipient);
            if (senderUser != null && recipientUser != null) {
                ChatMessage chatMessage = ChatMessage.builder()
                        .roomId(roomId)
                        .userId(recipientUser.getUserId())
                        .sender(senderUser.getUserId())
                        .message(msg)
                        .messageType(ChatMessage.MessageType.TALK)
                        .messageDate(new Timestamp(System.currentTimeMillis()))
                        .messageTime(new Time(System.currentTimeMillis()))
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
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            log.info("{} - 클라이언트 접속 해제", username);
            sessions.remove(username);
        } else {
            log.warn("Username is null in session attributes.");
        }
    }
}