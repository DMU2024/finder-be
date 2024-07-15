package DMU.demo.chat_example.chat.repository;

import DMU.demo.chat_example.chat.dto.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ChatRepository extends JpaRepository<ChatMessage, Integer> {
}