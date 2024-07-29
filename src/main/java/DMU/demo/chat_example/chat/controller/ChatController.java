package DMU.demo.chat_example.chat.controller;

import DMU.demo.chat_example.chat.dto.ChatMessage;
import DMU.demo.chat_example.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatRepository chatRepository;

    @GetMapping("/chat/messages")
    public List<ChatMessage> getMessages(@RequestParam String roomId) {
        return chatRepository.findByRoomId(roomId);
    }

    @GetMapping("/chat/room")
    public String chatRoom(@RequestParam String userId, Model model) {
        model.addAttribute("userId", userId);
        return "chat_room";
    }

    @GetMapping("/chat")
    public String userList() {
        return "user_list";
    }
}

