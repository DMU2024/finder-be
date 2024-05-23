package dmu.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 애플리케이션 시작 클래스
 */

@SpringBootApplication
public class ApiExplorer {

    /**
     * 애플리케이션의 시작 지점입니다.
     *
     * @param args 커맨드라인 인자
     */

    public static void main(String[] args) {
        SpringApplication.run(ApiExplorer.class, args);
    }
    /**
    *  http://localhost:8080/explore-api 입력
    * */
}