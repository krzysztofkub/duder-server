package org.duder.user.service;

public interface FriendInvitationService {
    void processInvitation(Long receiverId);

    void declineInvitation(Long senderId);
}
