package DMU.demo.lostgoods.service;

import DMU.demo.lostgoods.domain.entity.LostGoods;
import DMU.demo.lostgoods.domain.repository.LostGoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LostGoodsService {
    private final LostGoodsRepository lostGoodsRepository;

    public List<LostGoods> getLostGoodsByCoords(float lat_gte, float lng_gte, float lat_lte, float lng_lte) {
        Sort sort = Sort.by(Sort.Order.desc("lat"), Sort.Order.asc("lng"));
        return lostGoodsRepository.findLostGoodsBy(lat_gte, lng_gte, lat_lte, lng_lte, sort);
    }

    public List<LostGoods> getLostGoodsByUserId(long userId) {
        return lostGoodsRepository.findLostGoodsByUserId(userId);
    }

    public LostGoods postLostGoods(LostGoods lostGoods) {
        return lostGoodsRepository.save(lostGoods);
    }

    public LostGoods deleteLostGoodsById(String id) {
        return lostGoodsRepository.deleteLostGoodsBy_id(id);
    }
}
