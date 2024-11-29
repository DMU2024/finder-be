package dmu.finder.keyword.domain.repository;

import dmu.finder.keyword.domain.entity.Keyword;
import dmu.finder.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Integer> {
    KeywordInfoMapping findKeywordById(int id);

    List<KeywordInfoMapping> findAllByUser(User user);

    List<Keyword> findByUser(User user);
}
