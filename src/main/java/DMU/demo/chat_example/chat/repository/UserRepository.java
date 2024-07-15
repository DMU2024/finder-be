package DMU.demo.chat_example.chat.repository;

import DMU.demo.chat_example.chat.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
}