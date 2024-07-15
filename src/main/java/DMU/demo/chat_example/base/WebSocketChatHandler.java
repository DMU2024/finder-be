package DMU.demo.chat_example.base;

import DMU.demo.chat_example.chat.dto.ChatMessage;
import DMU.demo.chat_example.chat.dto.User;
import DMU.demo.chat_example.chat.repository.ChatRepository;
import DMU.demo.chat_example.chat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private static Map<String, WebSocketSession> sessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        sessions.put(username, session);
        log.info("{} - 클라이언트 접속", username);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("payload : {}", payload);

        String sender = (String) session.getAttributes().get("username");
        String[] parts = payload.split(":", 2);
        String recipient = parts[0].trim();
        String msg = parts[1].trim();

        WebSocketSession recipientSession = sessions.get(recipient);
        if (recipientSession != null && recipientSession.isOpen()) {
            recipientSession.sendMessage(new TextMessage(sender + ": " + msg));
        }

        User senderUser = userRepository.findByUsername(sender);
        User recipientUser = userRepository.findByUsername(recipient);

        if (senderUser != null) {
            ChatMessage chatMessage = ChatMessage.builder()
                    .roomId("default")
                    .userId(senderUser.getUserId()) // int 타입으로 사용
                    .sender(senderUser.getUserId()) // int 타입으로 사용
                    .message(msg)
                    .messageType(ChatMessage.MessageType.TALK)
                    .messageDate(new Timestamp(System.currentTimeMillis()))
                    .messageTime(new Time(System.currentTimeMillis()))
                    .build();
            chatRepository.save(chatMessage);
        } else {
            log.warn("Sender user '{}' not found in the repository.", sender);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        log.info("{} - 클라이언트 접속 해제", username);
        sessions.remove(username);
    }
}