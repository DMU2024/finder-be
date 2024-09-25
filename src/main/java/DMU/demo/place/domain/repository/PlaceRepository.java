package DMU.demo.place.domain.repository;

import DMU.demo.place.domain.entity.Place;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PlaceRepository extends MongoRepository<Place, String> {
    @Query("{ 'lat' : { '$gte' : ?0, '$lte' : ?2 }, 'lng' : { '$gte' : ?1, '$lte' : ?3 }}")
    List<Place> findPlacesBy(float lat_gte, float lng_gte, float lat_lte, float lng_lte, Sort sort);
}
