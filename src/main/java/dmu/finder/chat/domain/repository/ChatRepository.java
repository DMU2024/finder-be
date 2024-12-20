package dmu.finder.chat.domain.repository;

import dmu.finder.chat.domain.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatRoomIdMapping> findDistinctByRoomIdContaining(String userId);

    ChatMessage findFirstByRoomIdOrderByMessageDateDesc(String roomId);

    List<ChatMessage> findByRoomId(String roomId);
}