package DMU.demo.chat.controller;

import DMU.demo.chat.domain.entity.ChatMessage;
import DMU.demo.chat.dto.ChatHistory;
import DMU.demo.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/histories")
    public List<ChatHistory> getHistories(@RequestParam String userId) {
        return chatService.getHistories(userId);
    }

    @GetMapping("/messages")
    public List<ChatMessage> getMessages(@RequestParam String roomId) {
        return chatService.getMessages(roomId);
    }
}

