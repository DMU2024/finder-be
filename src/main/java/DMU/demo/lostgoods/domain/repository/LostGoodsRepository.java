package DMU.demo.lostgoods.domain.repository;

import DMU.demo.lostgoods.domain.entity.LostGoods;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LostGoodsRepository extends MongoRepository<LostGoods, String> {
    @Query("{ 'lat' : { '$gte' : ?0, '$lte' : ?2 }, 'lng' : { '$gte' : ?1, '$lte' : ?3 }}")
    List<LostGoods> findLostGoodsBy(float lat_gte, float lng_gte, float lat_lte, float lng_lte, Sort sort);

    @Query(sort = "{_id:-1}")
    List<LostGoods> findLostGoodsByUserId(long userId);

    LostGoods deleteLostGoodsBy_id(String id);
}
