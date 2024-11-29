package dmu.finder.chat.dto;

import lombok.*;

import java.sql.Time;
import java.sql.Timestamp;

@Data
@Builder
public class ChatDto {
    private long sender;
    private String message;
    private Timestamp messageDate;
    private Time messageTime;
}
