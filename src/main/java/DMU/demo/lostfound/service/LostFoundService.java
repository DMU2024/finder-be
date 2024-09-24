package DMU.demo.lostfound.service;

import DMU.demo.lostfound.domain.entity.LostFound;
import DMU.demo.lostfound.domain.repository.LostFoundRepository;
import DMU.demo.lostfound.dto.LostFoundDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class LostFoundService {
    private final LostFoundRepository lostFoundRepository;

    private final RestClient apiClient = RestClient.builder()
            .baseUrl("http://apis.data.go.kr/1320000")
            .build();

    @Value("${data.go.kr.api_key}")
    private String API_KEY;

    public List<LostFound> getLostFounds(int page) {
        return lostFoundRepository.findBy(PageRequest.of(page, 5));
    }

    public LostFoundDetail getLostFound(String id) {
        String[] parsedId = id.split("-");

        ResponseEntity<String> response = apiClient.get()
                .uri(uriBuilder -> uriBuilder.path("/LosfundInfoInqireService/getLosfundDetailInfo")
                        .queryParam("serviceKey", "{API_KEY}")
                        .queryParam("ATC_ID", parsedId[0])
                        .queryParam("FD_SN", parsedId[1])
                        .build(API_KEY))
                .retrieve()
                .toEntity(String.class);

        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(response.getBody())));
            Element root = document.getDocumentElement();

            if (root.getNodeName().equals("OpenAPI_ServiceResponse")) {
                NodeList nodeList = root.getChildNodes();
                StringBuilder cause = new StringBuilder();

                for (int i = 0; i < nodeList.getLength(); i++) {
                    cause.append(nodeList.item(i).getTextContent());
                }
                throw new RuntimeException(cause.toString().trim());
            } else {
                NodeList childNodes = root.getElementsByTagName("item").item(0).getChildNodes();
                Map<String, String> itemMap = new HashMap<>();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node childNode = childNodes.item(i);
                    itemMap.put(childNode.getNodeName(), childNode.getTextContent());
                }
                
                return LostFoundDetail.builder()
                        .atcId(itemMap.get("atcId"))
                        .csteSteNm(itemMap.get("csteSteNm"))
                        .depPlace(itemMap.get("depPlace"))
                        .fdFilePathImg(itemMap.get("fdFilePathImg"))
                        .fdHor(itemMap.get("fdHor"))
                        .fdPlace(itemMap.get("fdPlace"))
                        .fdPrdtNm(itemMap.get("fdPrdtNm"))
                        .fdSn(itemMap.get("fdSn"))
                        .fdYmd(itemMap.get("fdYmd"))
                        .fndKeepOrgnSeNm(itemMap.get("fndKeepOrgnSeNm"))
                        .orgId(itemMap.get("orgId"))
                        .orgNm(itemMap.get("orgNm"))
                        .prdtClNm(itemMap.get("prdtClNm"))
                        .tel(itemMap.get("tel"))
                        .uniq(itemMap.get("uniq"))
                        .build();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<LostFound> searchLostFounds(String keyword, int page) {
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matchingAny(keyword);
        return lostFoundRepository.findBy(criteria, PageRequest.of(page, 5));
    }

    public List<LostFound> getLostFoundsByPlace(String keyword, int page) {
        return lostFoundRepository.findByDepPlace(keyword, PageRequest.of(page, 5));
    }
}
