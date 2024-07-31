package DMU.demo.chat.domain.repository;

import DMU.demo.chat.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByRoomId(String roomId);
}