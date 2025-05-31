package dmu.finder.place.controller;

import dmu.finder.place.domain.entity.Place;
import dmu.finder.place.domain.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/place")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceRepository placeRepository;

    @GetMapping
    public List<Place> getPlacesByCoords(float lat_gte, float lng_gte, float lat_lte, float lng_lte) {
        Sort sort = Sort.by(Sort.Order.desc("lat"), Sort.Order.asc("lng"));
        return placeRepository.findPlacesBy(lat_gte, lng_gte, lat_lte, lng_lte, sort);
    }
}
