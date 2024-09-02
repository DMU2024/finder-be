package DMU.demo.location.controller;

import DMU.demo.chat.domain.entity.User;
import DMU.demo.chat.domain.repository.UserRepository;
import DMU.demo.location.domain.entity.Location;
import DMU.demo.location.domain.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    @PostMapping
    public Location addLocation(@RequestBody Map<String, String> request) {
        int userId = Integer.parseInt(request.get("userId"));
        String locationText = request.get("location");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Location location = Location.builder()
                .user(user)
                .location(locationText)
                .build();

        return locationRepository.save(location);
    }

    @GetMapping("/{userId}")
    public List<Location> getLocationsByUser(@PathVariable int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return locationRepository.findByUser(user);
    }

    @DeleteMapping("/{locationId}")
    public void deleteLocation(@PathVariable int locationId) {
        locationRepository.deleteById(locationId);
    }

    @GetMapping("/search/{userId}")
    public List<Location> searchLocations(@PathVariable int userId, @RequestParam String query) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return locationRepository.findByUserAndLocationContaining(user, query);
    }
}