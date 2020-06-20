package org.duder.user.rest;

import org.duder.dto.user.LoginResponse;
import org.duder.dto.user.RegisterAccount;
import org.duder.user.exception.WrongUserCredentialsException;
import org.duder.user.service.SessionService;
import org.duder.user.service.SigningService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
class SigningController {

    private final SigningService signingService;
    private final SessionService sessionService;

    SigningController(SigningService signingService, SessionService sessionService) {
        this.signingService = signingService;
        this.sessionService = sessionService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterAccount account) {
        signingService.register(account);
    }

    @GetMapping("/login")
    public LoginResponse login(@RequestParam("login") final String login, @RequestParam("password") final String password) {
        return signingService.login(login, password).orElseThrow(() -> new WrongUserCredentialsException("Wrong user credentials for user " + login));
    }

    @GetMapping("/fb-login")
    public LoginResponse fbLogin(@RequestParam("accessToken") final String accessToken) {
        return signingService.fbLogin(accessToken).get();
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam("sessionToken") String sessionToken) {
        return sessionService.getUserByToken(sessionToken).isPresent();
    }
}
