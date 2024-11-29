package dmu.finder.chat.service;

import dmu.finder.chat.domain.entity.ChatMessage;
import dmu.finder.chat.domain.repository.ChatRepository;
import dmu.finder.chat.domain.repository.ChatRoomIdMapping;
import dmu.finder.chat.dto.ChatHistory;
import dmu.finder.user.domain.entity.User;
import dmu.finder.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public List<ChatHistory> getHistories(String userId) {
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

    public List<ChatMessage> getMessages(String roomId) {
        return chatRepository.findByRoomId(roomId);
    }

    public ChatMessage createChatRoom(String userId, String targetId) {
        String[] users = {userId, targetId};
        Arrays.sort(users);
        String roomId = String.join("_", users);

        return chatRepository.save(ChatMessage.builder()
                .roomId(roomId)
                .userId(Long.parseLong(targetId))
                .sender(Long.parseLong(userId))
                .message("")
                .messageType(ChatMessage.MessageType.ENTER)
                .messageDate(new Timestamp(System.currentTimeMillis()))
                .messageTime(new Time(System.currentTimeMillis()))
                .build());
    }
}
