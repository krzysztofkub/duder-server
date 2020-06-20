package org.duder.user.rest;

import org.duder.dto.user.Dude;
import org.duder.dto.user.FriendshipStatus;
import org.duder.user.service.DudeService;
import org.duder.user.service.FriendshipService;
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

    private final DudeService dudeService;
    private final FriendshipService friendInvitationService;

    public DudeController(DudeService dudeService, FriendshipService friendInvitationService) {
        this.dudeService = dudeService;
        this.friendInvitationService = friendInvitationService;
    }

    @GetMapping
    public List<Dude> getDudes(@RequestParam int page, @RequestParam int size) {
        return dudeService.getDudes(page, size);
    }

    @PostMapping("/invite/{receiverId}")
    public FriendshipStatus invite(@PathVariable Long receiverId) {
        return friendInvitationService.processInvitation(receiverId);
    }

    @PostMapping("/decline/{senderId}")
    public void declineInvitation(@PathVariable Long senderId) {
        friendInvitationService.declineInvitation(senderId);
    }
}
