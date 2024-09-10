package DMU.demo.keyword.controller;

import DMU.demo.keyword.domain.entity.Keyword;
import DMU.demo.user.domain.entity.User;
import DMU.demo.user.domain.repository.UserRepository;
import DMU.demo.keyword.domain.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/keywords")
@RequiredArgsConstructor
public class KeywordController {
    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository;

    @PostMapping
    public Keyword addKeyword(@RequestBody Map<String, String> request) {
        long userId = Long.parseLong(request.get("userId"));
        String keywordText = request.get("keyword");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Keyword keyword = Keyword.builder()
                .user(user)
                .keyword(keywordText)
                .build();

        return keywordRepository.save(keyword);
    }

    @GetMapping("/{userId}")
    public List<Keyword> getKeywordsByUser(@PathVariable long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return keywordRepository.findByUser(user);
    }

    @DeleteMapping("/{keywordId}")
    public void deleteKeyword(@PathVariable int keywordId) {
        keywordRepository.deleteById(keywordId);
    }

    @GetMapping("/search/{userId}")
    public List<Keyword> searchKeywords(@PathVariable long userId, @RequestParam String query) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return keywordRepository.findByUserAndKeywordContaining(user, query);
    }

}