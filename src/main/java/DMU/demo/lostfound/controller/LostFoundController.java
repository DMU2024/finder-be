package DMU.demo.lostfound.controller;

import DMU.demo.lostfound.domain.entity.LostFound;
import DMU.demo.lostfound.dto.LostFoundDetail;
import DMU.demo.lostfound.service.LostFoundService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/lostfound")
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
