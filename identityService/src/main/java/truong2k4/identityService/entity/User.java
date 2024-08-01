package truong2k4.identityService.entity;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id_user")
   String idUser;
	@Column(name = "username")
   String username;
	@Column(name = "password")
   String password;
	@Column(name = "first_name")
   String firstName;
	@Column(name = "last_name")
   String lastName;
	@Column(name = "date")
   Date dob;
   @ManyToMany(mappedBy = "users")
   Set<Role> roles; 
}
