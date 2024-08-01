package truong2k4.identityService.dtos.response;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import truong2k4.identityService.entity.User;

@Getter
@Setter
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
	String id;
	String userName;
	String password;
	String firstName;
	String lastName;

}
