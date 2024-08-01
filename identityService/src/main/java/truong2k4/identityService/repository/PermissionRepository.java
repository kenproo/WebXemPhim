package truong2k4.identityService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import truong2k4.identityService.entity.Permission;

public interface PermissionRepository  extends JpaRepository<Permission, String>{

}
