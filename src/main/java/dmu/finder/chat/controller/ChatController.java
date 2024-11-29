package dmu.finder.chat.controller;

import dmu.finder.chat.domain.entity.ChatMessage;
import dmu.finder.chat.dto.ChatHistory;
import dmu.finder.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ChatMessage createChatRoom(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String targetId = request.get("targetId");

        return chatService.createChatRoom(userId, targetId);
    }
}

