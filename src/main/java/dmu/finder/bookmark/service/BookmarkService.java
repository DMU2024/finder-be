package dmu.finder.bookmark.service;

import dmu.finder.bookmark.domain.entity.Bookmark;
import dmu.finder.bookmark.domain.repository.BookmarkInfoMapping;
import dmu.finder.bookmark.domain.repository.BookmarkRepository;
import dmu.finder.bookmark.dto.BookmarkDto;
import dmu.finder.place.domain.entity.Place;
import dmu.finder.place.domain.repository.PlaceRepository;
import dmu.finder.user.domain.entity.User;
import dmu.finder.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    public BookmarkDto addLocation(long userId, String locationText) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

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

    public List<BookmarkDto> getLocationsByUser(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

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

    public int deleteLocation(int locationId) {
        bookmarkRepository.deleteById(locationId);
        return locationId;
    }
}
