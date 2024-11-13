package DMU.demo.lostfound.service;

import DMU.demo.lostfound.domain.entity.LostFound;
import DMU.demo.lostfound.domain.repository.LostFoundRepository;
import DMU.demo.lostfound.dto.LostFoundDetail;
import DMU.demo.place.domain.entity.Place;
import DMU.demo.place.domain.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

import java.util.List;

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
            Elements fdInfoElements = document.select(".find_info");
            String fdPrdtNm = fdInfoElements.select(".find_info_name").text().replace("습득물명 : ", "");

            Elements fdInfoList = fdInfoElements.select(".find02");

            String atcId = fdInfoList.get(0).text().split("-")[0];
            String fdSn = fdInfoList.get(0).text().split("-")[1];
            String fdYmd = fdInfoList.get(1).text().split(" ")[0];
            String fdHor = fdInfoList.get(1).text().split(" ")[1];
            String fdPlace = fdInfoList.get(2).text();
            String prdtClNm = fdInfoList.get(3).text();
            String depPlace = fdInfoList.get(5).text();
            String csteSteNm = fdInfoList.get(6).text();
            String tel = fdInfoList.get(7).text();

            String fdFilePathImg = "https://www.lost112.go.kr" + document.select(".lost_img")
                    .select("img")
                    .attr("src")
                    .replace("/thumbnail/", "/");

            String uniq = document.select(".find_info_txt").html()
                    .replace("<!-- 본문 텍스트 영역 -->", "")
                    .replace("내용", "")
                    .replace("<!--// 본문 텍스트 영역 --->", "")
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
