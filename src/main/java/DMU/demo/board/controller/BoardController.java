package DMU.demo.board.controller;

import DMU.demo.board.dto.BoardDto;
import DMU.demo.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Base64;

@Controller
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;
    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    // 게시글 작성 페이지를 보여주는 메서드
    @GetMapping("/post")
    public String showPostForm(Model model) {
        // 새로운 BoardDto 객체를 모델에 추가하여 게시글 작성 폼에 사용
        model.addAttribute("boardDto", new BoardDto());
        return "board/post";
    }

    // 게시글 작성 폼에서 제출된 데이터를 처리하는 메서드
    @PostMapping("/post")
    public String submitPostForm(@ModelAttribute("boardDto") BoardDto boardDto) {
        logger.info(boardDto.toString());
        // 게시글 서비스를 통해 게시글 저장
        boardService.savePost(boardDto);
        return "redirect:/board/post"; // 저장 후 다시 글 작성 페이지로 리다이렉트
    }

    //게시글 리스트 페이지
    @GetMapping("/list")
    public String list(Model model) {
        logger.info("Accessing /board/list");
        List<BoardDto> boardList = boardService.getBoardList();

        for (BoardDto board : boardList) {
            if (board.getLost_img() != null) {
                String base64Image = Base64.getEncoder().encodeToString(board.getLost_img());
                board.setImageBase64("data:image/jpeg;base64," + base64Image);  // MIME 타입 추가
                logger.info("Base64 Image: " + board.getImageBase64());  // 로그 출력 추가
            }
        }

        model.addAttribute("boardList", boardList);
        return "board/list";
    }

}
