package DMU.demo.bookmark.controller;

import DMU.demo.bookmark.domain.entity.Bookmark;
import DMU.demo.bookmark.domain.repository.BookmarkInfoMapping;
import DMU.demo.bookmark.domain.repository.BookmarkRepository;
import DMU.demo.user.domain.entity.User;
import DMU.demo.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;

    @PostMapping
    public BookmarkInfoMapping addLocation(@RequestBody Map<String, String> request) {
        long userId = Long.parseLong(request.get("userId"));
        String locationText = request.get("location");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Bookmark location = bookmarkRepository.save(Bookmark.builder()
                .user(user)
                .location(locationText)
                .build());

        return bookmarkRepository.findBookmarkById(location.getId());
    }

    @GetMapping("/{userId}")
    public List<BookmarkInfoMapping> getLocationsByUser(@PathVariable long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return bookmarkRepository.findAllByUser(user);
    }

    @DeleteMapping("/{locationId}")
    public int deleteLocation(@PathVariable int locationId) {
        bookmarkRepository.deleteById(locationId);
        return locationId;
    }
}