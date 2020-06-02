package org.duder.user.rest;

import org.duder.dto.user.Dude;
import org.duder.dto.user.InvitationResponse;
import org.duder.user.service.FriendInvitationService;
import org.duder.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dudes")
class DudeController {

    private final UserService userService;
    private final FriendInvitationService friendInvitationService;

    public DudeController(UserService userService, FriendInvitationService friendInvitationService) {
        this.userService = userService;
        this.friendInvitationService = friendInvitationService;
    }

    @GetMapping
    public List<Dude> getDudes(@RequestParam int page, @RequestParam int size) {
        return userService.getDudes(page, size);
    }

    @PostMapping("/invite/{receiverId}")
    public InvitationResponse invite(@PathVariable Long receiverId) {
        return friendInvitationService.processInvitation(receiverId);
    }

    @PostMapping("/decline/{senderId}")
    public void declineInvitation(@PathVariable Long senderId) {
        friendInvitationService.declineInvitation(senderId);
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam("sessionToken") String sessionToken) {
        return userService.getUserByToken(sessionToken).isPresent();
    }
}
