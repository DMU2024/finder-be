package dmu.finder.lostgoods.controller;

import dmu.finder.lostgoods.domain.entity.LostGoods;
import dmu.finder.lostgoods.service.LostGoodsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/lostgoods")
@RequiredArgsConstructor
public class LostGoodsController {
    private final LostGoodsService lostGoodsService;

    @GetMapping
    public List<LostGoods> getLostGoodsByCoords(float lat_gte, float lng_gte, float lat_lte, float lng_lte) {
        return lostGoodsService.getLostGoodsByCoords(lat_gte, lng_gte, lat_lte, lng_lte);
    }

    @PostMapping
    public LostGoods postLostGoods(@RequestBody Map<String, String> request) {
        return lostGoodsService.postLostGoods(LostGoods.builder()
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

    @DeleteMapping("{id}")
    public LostGoods deleteLostGoodsById(@PathVariable String id) {
        return lostGoodsService.deleteLostGoodsById(id);
    }
}
