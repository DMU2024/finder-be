package DMU.demo.chat_example.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ChatController {

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

