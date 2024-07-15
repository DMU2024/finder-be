package DMU.demo.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "LostItems")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer lost_id;

    @Column(nullable = false)
    private String user_id;

    @Column(nullable = false)
    private String lost_name;

    @Column(nullable = false)
    private String large_category;

    @Column(nullable = false)
    private String category;

    private String field;

    @Column(nullable = false)
    private LocalDateTime write_date;

    @Column(nullable = false)
    private LocalDateTime lost_date;

    @Column(nullable = false)
    private String lost_location;

    @Column(nullable = false)
    private String lost_status;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] lost_img;

    @Builder
    public Board(Integer lost_id, String user_id, String lost_name, String large_category, String category, String field, LocalDateTime write_date, LocalDateTime lost_date, String lost_location, String lost_status, byte[] lost_img) {
        this.lost_id = lost_id;
        this.user_id = user_id;
        this.lost_name = lost_name;
        this.large_category = large_category;
        this.category = category;
        this.field = field;
        this.write_date = write_date;
        this.lost_date = lost_date;
        this.lost_location = lost_location;
        this.lost_status = lost_status;
        this.lost_img = lost_img;
    }
}
