package DMU.demo.lostfound.domain.repository;

import DMU.demo.lostfound.domain.entity.LostFound;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LostFoundRepository extends MongoRepository<LostFound, String> {
    @Query(sort = "{_id:-1}")
    List<LostFound> findBy(Pageable pageable);

    @Query(sort = "{_id:-1}")
    List<LostFound> findByDepPlace(String depPlace, Pageable pageable);

    @Query(sort = "{_id:-1}")
    List<LostFound> findByAtcIdGreaterThanEqual(String atcId, TextCriteria criteria);
}
