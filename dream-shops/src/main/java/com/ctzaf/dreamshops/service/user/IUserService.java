package com.ctzaf.dreamshops.service.user;

import com.ctzaf.dreamshops.dto.UserDto;
import com.ctzaf.dreamshops.model.User;
import com.ctzaf.dreamshops.request.CreateUserRequest;
import com.ctzaf.dreamshops.request.UserUpdateRequest;

public interface IUserService {

    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UserUpdateRequest request, Long userId);
    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);
}
