package DMU.demo.location.domain.repository;

import DMU.demo.user.domain.entity.User;
import DMU.demo.location.domain.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByUser(User user);

    List<Location> findByUserAndLocationContaining(User user, String location);
}
