package truong2k4.identityService.entity;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Permission {
	@Id
	@Column(name = "permission_name")
	String name;
	@Column(name = "description")
	String description;
	@ManyToMany
	@JoinTable(name = "role_permission",
	        joinColumns = @JoinColumn(name = "permission_name"),
	        inverseJoinColumns = @JoinColumn(name = "role_name")
			)
	
	Set<Role> roles;

}
