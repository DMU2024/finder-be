package dmu.demo.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Service 클래스
 * 외부 API와 통신하여 데이터를 가져오는 로직을 처리합니다.
 */
@Service
public class ApiService {

    /**
     * 외부 API에 GET 요청을 보내고 응답을 문자열로 반환합니다.
     *
     * @return 외부 API 응답 문자열
     * @throws IOException 입출력 예외 처리
     */
    public String exploreApi() throws IOException {
        // API 요청 URL을 생성합니다.
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1320000/LostGoodsInfoInqireService/getLostGoodsInfoAccToClAreaPd");
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=G0vWteBQzY7bvpVDG%2FnocLjJh0whBqYRdhAAglca9zVxvYMx0EV9%2BGgDAmRcnjT56cgh%2BgrxBbdSgW9hd7McMQ%3D%3D");
        urlBuilder.append("&" + URLEncoder.encode("START_YMD","UTF-8") + "=" + URLEncoder.encode("20170801", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("END_YMD","UTF-8") + "=" + URLEncoder.encode("20171130", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("PRDT_CL_CD_01","UTF-8") + "=" + URLEncoder.encode("PRA000", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("PRDT_CL_CD_02","UTF-8") + "=" + URLEncoder.encode("PRA300", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("LST_LCT_CD","UTF-8") + "=" + URLEncoder.encode("LCA000", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8"));
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8"));

        // URL 객체 생성
        URL url = new URL(urlBuilder.toString());
        // HTTP 연결 설정
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        // 응답 읽기
        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
        }

        // 응답을 StringBuilder에 저장
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();
        return sb.toString();
    }
}