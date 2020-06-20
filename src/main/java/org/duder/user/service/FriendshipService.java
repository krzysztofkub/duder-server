package org.duder.user.service;

import org.duder.dto.user.FriendshipStatus;
import org.duder.user.model.User;

import java.util.Optional;

public interface FriendshipService {
    FriendshipStatus processInvitation(Long receiverId);

    void declineInvitation(Long senderId);

    Optional<FriendshipStatus> deduceFriendshipStatus(User user, Long userId);
}
