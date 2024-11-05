package DMU.demo.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    private long userId;

    @Column(nullable = false)
    private String username;
    private String profileImage;
    private String thumbnailImage;
    private String accessToken;
    private String refreshToken;
    private boolean notifyOnlyBookmarked;
}