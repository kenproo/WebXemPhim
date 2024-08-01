package truong2k4.identityService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import truong2k4.identityService.entity.InvalidatedToken;

public interface InvalidatedTokenRepository  extends JpaRepository<InvalidatedToken, String>{

}
