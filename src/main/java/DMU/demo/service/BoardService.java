package DMU.demo.service;

import DMU.demo.DTO.BoardDto;
import DMU.demo.domain.entity.Board;
import DMU.demo.domain.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public void savePost(BoardDto boardDto) {
        Board board = Board.builder()
                .user_id(boardDto.getUser_id())
                .lost_name(boardDto.getLost_name())
                .category(boardDto.getCategory())
                .field(boardDto.getField())
                .lost_date(boardDto.getLost_date())
                .lost_location(boardDto.getLost_location())
                .lost_status(boardDto.getLost_status())
                .lost_img(boardDto.getLost_img())
                .build();

        boardRepository.save(board);
    }
}