package DMU.demo.chat.service;

import DMU.demo.chat.domain.entity.ChatMessage;
import DMU.demo.chat.domain.repository.ChatRepository;
import DMU.demo.chat.domain.repository.ChatRoomIdMapping;
import DMU.demo.chat.dto.ChatHistory;
import DMU.demo.user.domain.entity.User;
import DMU.demo.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public List<ChatHistory> getHistories(@RequestParam String userId) {
        List<ChatRoomIdMapping> rooms = chatRepository.findDistinctByRoomIdContaining(userId);

        return rooms.stream()
                .map(room -> {
                    ChatMessage latestMsg = chatRepository.findFirstByRoomIdOrderByMessageDateDesc(room.getRoomId());
                    long otherId = Long.parseLong(userId) == latestMsg.getSender() ? latestMsg.getUserId() : latestMsg.getSender();
                    User otherUser = userRepository.findById(otherId).orElse(User.builder().userId(otherId).build());

                    return ChatHistory.builder()
                            .userId(otherId)
                            .username(otherUser.getUsername())
                            .profileImage(otherUser.getProfileImage())
                            .thumbnailImage(otherUser.getThumbnailImage())
                            .message(latestMsg.getMessage())
                            .messageDate(latestMsg.getMessageDate())
                            .build();
                })
                .toList();
    }

    public List<ChatMessage> getMessages(@RequestParam String roomId) {
        return chatRepository.findByRoomId(roomId);
    }
}
