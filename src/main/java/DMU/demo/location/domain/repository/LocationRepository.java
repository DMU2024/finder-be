package DMU.demo.location.domain.repository;

import DMU.demo.location.domain.entity.Location;
import DMU.demo.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    LocationInfoMapping findLocationById(int id);

    List<LocationInfoMapping> findAllByUser(User user);

    List<Location> findByUser(User user);
}
