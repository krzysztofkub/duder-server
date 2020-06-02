package org.duder.user.service;

import org.duder.dto.user.InvitationResponse;

public interface FriendInvitationService {
    InvitationResponse processInvitation(Long receiverId);

    void declineInvitation(Long senderId);
}
