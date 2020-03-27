package org.duder.user.service;

import org.duder.user.dao.model.User;
import org.duder.user.dto.UserDto;
import org.duder.user.exception.UserAlreadyExistsException;

public interface UserService {
    User register(UserDto userDto) throws UserAlreadyExistsException;
}
