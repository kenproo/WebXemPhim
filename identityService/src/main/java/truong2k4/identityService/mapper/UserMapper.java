package truong2k4.identityService.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import truong2k4.identityService.dtos.request.UserUpdateRequest;

import truong2k4.identityService.entity.User;
import truong2k4.identityService.dtos.request.UserCreationRequest;
import truong2k4.identityService.dtos.response.UserResponse;
@Mapper(componentModel = "spring")
public interface UserMapper  {
    UserResponse toUserResponse(User user);
    User toUser(UserCreationRequest userCreationRequest);
    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
