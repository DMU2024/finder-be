package DMU.demo.chat.domain.entity;

import lombok.*;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.sql.Time;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    public enum MessageType {
        ENTER, TALK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int messageId;

    @Column(nullable = false)
    private String roomId;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private long sender;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp messageDate;

    @Column(nullable = false)
    private Time messageTime;

    @PrePersist
    protected void onCreate() {
        messageDate = new Timestamp(System.currentTimeMillis());
        messageTime = new Time(System.currentTimeMillis());
    }
}