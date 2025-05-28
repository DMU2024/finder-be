package dmu.finder.lostfound.service;

import dmu.finder.lostfound.domain.entity.LostFound;
import dmu.finder.lostfound.domain.repository.LostFoundRepository;
import dmu.finder.lostfound.dto.LostFoundDetail;
import dmu.finder.place.domain.entity.Place;
import dmu.finder.place.domain.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LostFoundService {
    private final LostFoundRepository lostFoundRepository;
    private final PlaceRepository placeRepository;
    private final MongoTemplate mongoTemplate;

    private final RestClient apiClient = RestClient.builder()
            .baseUrl("https://www.lost112.go.kr")
            .build();

    public List<LostFound> getLostFounds(int page) {
        return lostFoundRepository.findBy(PageRequest.of(page, 5));
    }

    public LostFoundDetail getLostFound(String id) {
        String[] parsedId = id.split("-");

        ResponseEntity<String> response = apiClient.get()
                .uri(uriBuilder -> uriBuilder.path("/find/findDetail.do")
                        .queryParam("ATC_ID", parsedId[0])
                        .queryParam("FD_SN", parsedId[1])
                        .build())
                .retrieve()
                .toEntity(String.class);

        try {
            Document document = Jsoup.parse(response.getBody());
            Elements divFdInfo = document.select(".find_info");
            String fdPrdtNm = divFdInfo.select(".find_info_name").text().replace("습득물명 : ", "");

            Map<String, String> data = new HashMap<>();

            Elements items = divFdInfo.select("ul > li");
            for (Element li : items) {
                Element keyElem = li.selectFirst("p.find01");
                Element valElem = li.selectFirst("p.find02");

                if (keyElem != null && valElem != null) {
                    String key = keyElem.text().trim();
                    String val = valElem.text().trim();
                    data.put(key, val);
                }
            }

            String atcId = data.get("관리번호").split("-")[0];
            String fdSn = data.get("관리번호").split("-")[1];
            String fdYmd = data.get("습득일").split(" ")[0];
            String fdHor = data.get("습득일").split("-")[1];
            String fdPlace = data.get("습득장소");
            String prdtClNm = data.get("물품분류");
            String depPlace = data.get("보관장소");
            String csteSteNm = data.get("유실물상태");
            String tel = data.get("보관장소연락처");

            String fdFilePathImg = "https://www.lost112.go.kr" + document.select(".lost_img")
                    .select("img")
                    .attr("src")
                    .replace("/thumbnail/", "/");

            String uniq = Jsoup.clean(document.select(".find_info_txt").html(), Safelist.basic())
                    .replace("내용", "")
                    .replaceAll("<br>", "")
                    .strip();

            Place place = placeRepository.findByName(depPlace);
            float lat = 0;
            float lng = 0;

            if (place != null) {
                lat = place.getLat();
                lng = place.getLng();
            }

            return LostFoundDetail.builder()
                    .fdPrdtNm(fdPrdtNm)
                    .atcId(atcId)
                    .fdSn(fdSn)
                    .fdYmd(fdYmd)
                    .fdHor(fdHor)
                    .fdPlace(fdPlace)
                    .prdtClNm(prdtClNm)
                    .depPlace(depPlace)
                    .csteSteNm(csteSteNm)
                    .tel(tel)
                    .fdFilePathImg(fdFilePathImg)
                    .uniq(uniq)
                    .lat(lat)
                    .lng(lng)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<LostFound> searchLostFounds(String keyword, String startYmd, String endYmd, String category, String color, int page) {
        Query query = new Query()
                .with(PageRequest.of(page, 5))
                .with(Sort.by(Sort.Direction.DESC, "_id"));

        if (StringUtils.hasText(keyword)) {
            query.addCriteria(TextCriteria.forDefaultLanguage().matchingAny(keyword));
        }

        // 날짜 검색의 경우, 습득물이 등록된 날짜 기준으로 검색
        if (StringUtils.hasText(startYmd)) {
            startYmd = "F" + String.join("", startYmd.split("-")) + "00000001";
        }
        if (StringUtils.hasText(endYmd)) {
            endYmd = "F" + String.join("", endYmd.split("-")) + "99999999";
        }

        if (StringUtils.hasText(startYmd) && !StringUtils.hasText(endYmd)) {
            query.addCriteria(Criteria.where("atcId").gte(startYmd));
        } else if (!StringUtils.hasText(startYmd) && StringUtils.hasText(endYmd)) {
            query.addCriteria(Criteria.where("atcId").lte(endYmd));
        } else if (StringUtils.hasText(startYmd) && StringUtils.hasText(endYmd)) {
            query.addCriteria(Criteria.where("atcId").gte(startYmd).lte(endYmd));
        }

        if (StringUtils.hasText(category)) {
            if (category.contains(">")) {
                query.addCriteria(Criteria.where("prdtClNm").is(category));
            } else {
                query.addCriteria(Criteria.where("prdtClNm").regex("^" + category, "i"));
            }
        }

        if (StringUtils.hasText(color)) {
            query.addCriteria(Criteria.where("clrNm").is(color));
        }

        return mongoTemplate.find(query, LostFound.class);
    }

    public List<LostFound> getLostFoundsByPlace(String keyword, int page) {
        return lostFoundRepository.findByDepPlace(keyword, PageRequest.of(page, 5));
    }

    public List<LostFound> getLostFoundsByAtcId(String atcId, String keyword) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(keyword);
        return lostFoundRepository.findByAtcIdGreaterThanEqual(atcId, criteria);
    }
}
