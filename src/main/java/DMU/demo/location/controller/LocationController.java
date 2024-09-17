package DMU.demo.location.controller;

import DMU.demo.location.domain.entity.Location;
import DMU.demo.location.domain.repository.LocationInfoMapping;
import DMU.demo.location.domain.repository.LocationRepository;
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
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    @PostMapping
    public LocationInfoMapping addLocation(@RequestBody Map<String, String> request) {
        long userId = Long.parseLong(request.get("userId"));
        String locationText = request.get("location");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Location location = locationRepository.save(Location.builder()
                .user(user)
                .location(locationText)
                .build());

        return locationRepository.findLocationById(location.getId());
    }

    @GetMapping("/{userId}")
    public List<LocationInfoMapping> getLocationsByUser(@PathVariable long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return locationRepository.findAllByUser(user);
    }

    @DeleteMapping("/{locationId}")
    public int deleteLocation(@PathVariable int locationId) {
        locationRepository.deleteById(locationId);
        return locationId;
    }
}