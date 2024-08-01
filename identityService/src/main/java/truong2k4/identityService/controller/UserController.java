package truong2k4.identityService.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import truong2k4.identityService.dtos.request.UserCreationRequest;
import truong2k4.identityService.dtos.response.ApiResponse;
import truong2k4.identityService.dtos.response.UserResponse;
import truong2k4.identityService.service.UserService;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
	UserService userService;

	@PostMapping()
	ApiResponse<UserResponse> createUser(@RequestBody UserCreationRequest userCreationRequest) {
		return ApiResponse.<UserResponse>builder().result(userService.createUser(userCreationRequest)).build();
	}

}
