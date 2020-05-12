package org.duder.user.rest;

import org.duder.dto.user.Dude;
import org.duder.dto.user.LoginResponse;
import org.duder.dto.user.RegisterAccount;
import org.duder.user.exception.WrongUserCredentialsException;
import org.duder.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
class SigningController {

    private final UserService userService;

    public SigningController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterAccount account) {
        userService.register(account);
    }

    @GetMapping("/login")
    public LoginResponse login(@RequestParam("login") final String login, @RequestParam("password") final String password) {
        return userService.login(login, password).orElseThrow(() -> new WrongUserCredentialsException("Wrong user credentials for user " + login));
    }

    @GetMapping("/fb-login")
    public LoginResponse fbLogin(@RequestParam("accessToken") final String accessToken) {
        return userService.fbLogin(accessToken).get();
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam("sessionToken") String sessionToken) {
        return userService.getUserByToken(sessionToken).isPresent();
    }
}
