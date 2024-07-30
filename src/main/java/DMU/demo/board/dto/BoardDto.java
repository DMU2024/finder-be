package DMU.demo.board.dto;

import DMU.demo.board.domain.entity.Board;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardDto {
    private Integer lost_id;
    private String user_id;
    private String lost_name;
    private String large_category;
    private String category;
    private String field;
    private LocalDateTime write_date;
    private LocalDateTime lost_date;
    private String lost_location;
    private String lost_status;
    private byte[] lost_img;
    private String imageBase64;

    public Board toEntity() {
        return Board.builder()
                .lost_id(lost_id)
                .user_id(user_id)
                .lost_name(lost_name)
                .large_category(large_category)
                .category(category)
                .field(field)
                .write_date(write_date)
                .lost_date(lost_date)
                .lost_location(lost_location)
                .lost_status(lost_status)
                .lost_img(lost_img)
                .build();
    }

    @Builder
    public BoardDto(Integer lost_id, String user_id, String lost_name, String large_category, String category, String field, LocalDateTime write_date, LocalDateTime lost_date, String lost_location, String lost_status, byte[] lost_img) {
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