package dmu.finder.bookmark.domain.repository;

import dmu.finder.bookmark.domain.entity.Bookmark;
import dmu.finder.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Integer> {
    BookmarkInfoMapping findBookmarkById(int id);

    List<BookmarkInfoMapping> findAllByUser(User user);

    List<Bookmark> findByUser(User user);
}
