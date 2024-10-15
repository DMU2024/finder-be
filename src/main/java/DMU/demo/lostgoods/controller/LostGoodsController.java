package DMU.demo.lostgoods.controller;

import DMU.demo.lostgoods.domain.entity.LostGoods;
import DMU.demo.lostgoods.domain.repository.LostGoodsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/lostgoods")
@RequiredArgsConstructor
public class LostGoodsController {
    private final LostGoodsRepository lostGoodsRepository;

    @GetMapping
    public List<LostGoods> getPlacesByCoords(float lat_gte, float lng_gte, float lat_lte, float lng_lte) {
        Sort sort = Sort.by(Sort.Order.desc("lat"), Sort.Order.asc("lng"));
        return lostGoodsRepository.findLostGoodsBy(lat_gte, lng_gte, lat_lte, lng_lte, sort);
    }

    @PostMapping
    public LostGoods postLostGoods(@RequestBody Map<String, String> request) {
        return lostGoodsRepository.save(LostGoods.builder()
                .name(request.get("name"))
                .date(request.get("date"))
                .address(request.get("address"))
                .category(request.get("category"))
                .info(request.get("info"))
                .lat(Float.parseFloat(request.get("lat")))
                .lng(Float.parseFloat(request.get("lng")))
                .userId(Long.parseLong(request.get("userId")))
                .build());
    }
}
