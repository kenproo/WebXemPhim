package truong2k4.identityService.entity;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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

public class Role {
	@Id
	@Column(name = "role_name")
	String nameRole;
	@Column(name = "description")
	String description;
	@ManyToMany(mappedBy =  "roles")
	Set<Permission> permissions;
    @ManyToMany
    @JoinTable(
    		name = "student_role",
    		joinColumns = { @JoinColumn(name = "id_user")},
    		inverseJoinColumns = {@JoinColumn(name = "role_name")}
    		)  
    Set<User> users;
}
