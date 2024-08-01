package truong2k4.identityService.service;

import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import truong2k4.identityService.dtos.response.UserResponse;
import truong2k4.identityService.repository.RoleRepository;
import truong2k4.identityService.repository.UserRepository;
import truong2k4.identityService.costant.PredefinedRole;
import truong2k4.identityService.dtos.request.UserCreationRequest;
import truong2k4.identityService.dtos.request.UserUpdateRequest;
import truong2k4.identityService.entity.Role;
import truong2k4.identityService.entity.User;
import truong2k4.identityService.exception.AppException;
import truong2k4.identityService.exception.ErrorCode;
import truong2k4.identityService.mapper.UserMapper;

@Log4j2
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
	UserMapper userMapper;
	UserRepository userRepository;
	PasswordEncoder passwordEncoder;
	RoleRepository roleRepository;

	public UserResponse createUser(UserCreationRequest request) {
		User user = userMapper.toUser(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));

		HashSet<Role> roles = new HashSet<>();
		roleRepository.findById(PredefinedRole.ROLE_USER).ifPresent(roles::add);

		user.setRoles(roles);

		try {
			user = userRepository.save(user);
		} catch (DataIntegrityViolationException exception) {
			throw new AppException(ErrorCode.USER_EXIST);
		}

		return userMapper.toUserResponse(user);
	}

	public UserResponse getMyInfo() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();

		User user = userRepository.findByUsername(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

		return userMapper.toUserResponse(user);
	}

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        log.info("In method get Users");
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST)));
    }
    
}