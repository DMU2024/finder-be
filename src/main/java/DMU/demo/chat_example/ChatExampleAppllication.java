package DMU.demo.chat_example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "DMU.demo.chat_example.chat.dto")
@EnableJpaRepositories(basePackages = "DMU.demo.chat_example.chat.repository")
public class ChatExampleAppllication {
    public static void main(String[] args) {
        SpringApplication.run(ChatExampleAppllication.class, args);
    }
}