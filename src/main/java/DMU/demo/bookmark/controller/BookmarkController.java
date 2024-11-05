package DMU.demo.bookmark.controller;

import DMU.demo.bookmark.dto.BookmarkDto;
import DMU.demo.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping
    public BookmarkDto addLocation(@RequestBody Map<String, String> request) {
        long userId = Long.parseLong(request.get("userId"));
        String locationText = request.get("location");

        return bookmarkService.addLocation(userId, locationText);
    }

    @GetMapping("/{userId}")
    public List<BookmarkDto> getLocationsByUser(@PathVariable long userId) {
        return bookmarkService.getLocationsByUser((userId));
    }

    @DeleteMapping("/{locationId}")
    public int deleteLocation(@PathVariable int locationId) {
        return bookmarkService.deleteLocation(locationId);
    }
}