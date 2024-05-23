package dmu.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import dmu.demo.service.ApiService;

import java.io.IOException;

/**
 * Controller 클래스
 * HTTP 요청을 처리하고 응답을 반환합니다.
 */

@RestController
public class ApiController {

    @Autowired
    public ApiService apiService;

    /**
     * /explore-api 엔드포인트에 대한 GET 요청을 처리합니다.
     *
     * @return 외부 API 응답 문자열
     * @throws IOException 입출력 예외 처리
     */

    @GetMapping("/explore-api")
    public String exploreApi() throws IOException {
        return apiService.exploreApi();
    }
}
