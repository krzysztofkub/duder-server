package org.duder.user.rest;

import org.duder.user.dao.User;
import org.duder.user.dto.Code;
import org.duder.user.dto.Response;
import org.duder.user.dto.UserDto;
import org.duder.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Response register(@RequestBody UserDto userDto) {
        userService.register(userDto);
        return new Response(Code.OK);
    }
}
