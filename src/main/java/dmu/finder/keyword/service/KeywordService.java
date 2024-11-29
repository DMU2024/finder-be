package dmu.finder.keyword.service;

import dmu.finder.bookmark.dto.BookmarkDto;
import dmu.finder.bookmark.service.BookmarkService;
import dmu.finder.kakao.service.KakaoService;
import dmu.finder.keyword.domain.entity.Keyword;
import dmu.finder.keyword.domain.repository.KeywordInfoMapping;
import dmu.finder.keyword.domain.repository.KeywordRepository;
import dmu.finder.lostfound.domain.entity.LostFound;
import dmu.finder.lostfound.service.LostFoundService;
import dmu.finder.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;
    private final LostFoundService lostFoundService;
    private final KakaoService kakaoService;
    private final BookmarkService bookmarkService;


    public KeywordInfoMapping addKeyword(User user, String keywordText) {
        Keyword keyword = keywordRepository.save(Keyword.builder()
                .user(user)
                .keyword(keywordText)
                .build());

        return keywordRepository.findKeywordById(keyword.getId());
    }

    public List<KeywordInfoMapping> getKeywordsByUser(User user) {
        return keywordRepository.findAllByUser(user);
    }

    public int deleteKeyword(int keywordId) {
        keywordRepository.deleteById(keywordId);
        return keywordId;
    }

    @Scheduled(cron = "0 0 12 * * ?")
    public void KeywordJob() {
        String atcId = "F" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "00000001";
        List<Keyword> keywords = keywordRepository.findAll();
        Map<String, List<User>> keywordGroup = keywords.stream()
                .collect(Collectors.groupingBy(Keyword::getKeyword, Collectors.mapping(Keyword::getUser, Collectors.toList())));

        for (Map.Entry<String, List<User>> entry : keywordGroup.entrySet()) {
            String keyword = entry.getKey();
            List<LostFound> lostFounds = lostFoundService.getLostFoundsByAtcId(atcId, keyword);

            if (!lostFounds.isEmpty()) {
                for (LostFound lostfound : lostFounds) {
                    String message = String.format("[%s 키워드 알림] %s - %s", keyword, lostfound.getFdSbjt(), lostfound.getDepPlace());
                    String image = lostfound.getFdFilePathImg();
                    String id = lostfound.get_id();

                    for (User user : entry.getValue()) {
                        if (user.getAccessToken() == null || user.getRefreshToken() == null)
                            continue;

                        if (user.isNotifyOnlyBookmarked()) {
                            List<BookmarkDto> bookmarks = bookmarkService.getLocationsByUser(user.getUserId());
                            List<String> locations = bookmarks.stream().map(BookmarkDto::getLocation).toList();

                            if (!locations.contains(lostfound.getDepPlace())) {
                                continue;
                            }
                        }

                        kakaoService.postSendMessage(user, message, image, id);
                    }
                }
            }
        }
    }
}
