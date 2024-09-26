package DMU.demo.bookmark.domain.repository;

import DMU.demo.bookmark.domain.entity.Bookmark;
import DMU.demo.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Integer> {
    BookmarkInfoMapping findBookmarkById(int id);

    List<BookmarkInfoMapping> findAllByUser(User user);

    List<Bookmark> findByUser(User user);
}
