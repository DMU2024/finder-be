package dmu.finder.lostfound.controller;

import dmu.finder.lostfound.domain.entity.LostFound;
import dmu.finder.lostfound.dto.LostFoundDetail;
import dmu.finder.lostfound.service.LostFoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/lostfound")
@RequiredArgsConstructor
public class LostFoundController {
    private final LostFoundService lostFoundService;

    @GetMapping
    public List<LostFound> getLostFounds(int page) {
        return lostFoundService.getLostFounds(page);
    }

    @GetMapping("/{id}")
    public LostFoundDetail getLostFound(@PathVariable String id) {
        return lostFoundService.getLostFound(id);
    }

    @GetMapping("/search")
    public List<LostFound> searchLostFounds(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startYmd,
            @RequestParam(required = false) String endYmd,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String color,
            int page) {
        return lostFoundService.searchLostFounds(keyword, startYmd, endYmd, category, color, page);
    }

    @GetMapping("/place")
    public List<LostFound> getLostFoundsByPlace(String keyword, int page) {
        return lostFoundService.getLostFoundsByPlace(keyword, page);
    }
}
