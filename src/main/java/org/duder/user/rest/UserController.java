package org.duder.user.rest;

import org.duder.user.dto.UserDto;
import org.duder.user.exception.WrongUserCredentialsException;
import org.duder.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody UserDto userDto) {
        userService.register(userDto);
    }

    @GetMapping("/login")
    public UserDto login(@RequestParam("login") final String login, @RequestParam("password") final String password) {
        Optional<UserDto> user = userService.login(login, password);
        return user.orElseThrow(() -> new WrongUserCredentialsException("Wrong user credentials for user " + login));
    }
}
