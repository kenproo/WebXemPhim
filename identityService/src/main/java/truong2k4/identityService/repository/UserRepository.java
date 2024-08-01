package truong2k4.identityService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import truong2k4.identityService.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	 Optional<User> findByUsername(String username);
}
