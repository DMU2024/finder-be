package DMU.demo.chat.controller;

import DMU.demo.chat.domain.entity.ChatMessage;
import DMU.demo.chat.domain.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatRepository chatRepository;

    @GetMapping("/messages")
    public List<ChatMessage> getMessages(@RequestParam String roomId) {
        return chatRepository.findByRoomId(roomId);
    }
}

