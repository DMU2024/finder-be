package DMU.demo.bookmark.controller;

import DMU.demo.bookmark.domain.entity.Bookmark;
import DMU.demo.bookmark.domain.repository.BookmarkInfoMapping;
import DMU.demo.bookmark.domain.repository.BookmarkRepository;
import DMU.demo.bookmark.dto.BookmarkDto;
import DMU.demo.place.domain.entity.Place;
import DMU.demo.place.domain.repository.PlaceRepository;
import DMU.demo.user.domain.entity.User;
import DMU.demo.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    @PostMapping
    public BookmarkDto addLocation(@RequestBody Map<String, String> request) {
        long userId = Long.parseLong(request.get("userId"));
        String locationText = request.get("location");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Bookmark bookmark = bookmarkRepository.save(Bookmark.builder()
                .user(user)
                .location(locationText)
                .build());

        Place place = placeRepository.findByName(bookmark.getLocation());

        return BookmarkDto.builder()
                .id(bookmark.getId())
                .location(bookmark.getLocation())
                .address(place.getAddress())
                .lat(place.getLat())
                .lng(place.getLng())
                .build();
    }

    @GetMapping("/{userId}")
    public List<BookmarkDto> getLocationsByUser(@PathVariable long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<BookmarkInfoMapping> bookmarks = bookmarkRepository.findAllByUser(user);
        List<BookmarkDto> dto = new ArrayList<>();

        for (BookmarkInfoMapping bookmark : bookmarks) {
            Place place = placeRepository.findByName(bookmark.getLocation());

            dto.add(BookmarkDto.builder()
                    .id(bookmark.getId())
                    .location(bookmark.getLocation())
                    .address(place.getAddress())
                    .lat(place.getLat())
                    .lng(place.getLng())
                    .build());
        }

        return dto;
    }

    @DeleteMapping("/{locationId}")
    public int deleteLocation(@PathVariable int locationId) {
        bookmarkRepository.deleteById(locationId);
        return locationId;
    }
}