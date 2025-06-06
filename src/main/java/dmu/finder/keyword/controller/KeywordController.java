package dmu.finder.keyword.controller;

import dmu.finder.keyword.domain.repository.KeywordInfoMapping;
import dmu.finder.keyword.service.KeywordService;
import dmu.finder.user.domain.entity.User;
import dmu.finder.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/keywords")
@RequiredArgsConstructor
public class KeywordController {
    private final UserRepository userRepository;
    private final KeywordService keywordService;

    @PostMapping
    public KeywordInfoMapping addKeyword(@RequestBody Map<String, String> request) {
        long userId = Long.parseLong(request.get("userId"));
        String keywordText = request.get("keyword");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return keywordService.addKeyword(user, keywordText);
    }

    @GetMapping("/{userId}")
    public List<KeywordInfoMapping> getKeywordsByUser(@PathVariable long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return keywordService.getKeywordsByUser(user);
    }

    @DeleteMapping("/{keywordId}")
    public int deleteKeyword(@PathVariable int keywordId) {
        return keywordService.deleteKeyword(keywordId);
    }
}