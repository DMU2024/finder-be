package DMU.demo.Controller;

import DMU.demo.DTO.BoardDto;
import DMU.demo.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;


@Controller
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

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
        // 게시글 서비스를 통해 게시글 저장
        boardService.savePost(boardDto);
        return "redirect:/board/post"; // 저장 후 다시 글 작성 페이지로 리다이렉트
    }
}
