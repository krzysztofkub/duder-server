package org.duder.user.rest;

import org.duder.dto.user.Dude;
import org.duder.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dudes")
class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<Dude> getDudes(@RequestParam int page, @RequestParam int size, @RequestHeader("Authorization") String sessionToken) {
        return userService.getDudes(page, size, sessionToken);
    }

    @PostMapping("/profile-pic")
    public void profilePicture(@RequestBody String url,
                               @RequestHeader("Authorization") String sessionToken) {
        userService.updateProfileImage(url, sessionToken);
    }
}
