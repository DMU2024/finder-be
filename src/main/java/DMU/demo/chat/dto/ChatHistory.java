package DMU.demo.chat.dto;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;

@Data
@Builder
public class ChatHistory {
    private long userId;
    private String username;
    private String profileImage;
    private String thumbnailImage;
    private String message;
    private Timestamp messageDate;
}
