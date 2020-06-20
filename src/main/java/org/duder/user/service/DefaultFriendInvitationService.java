package org.duder.user.service;

import org.apache.commons.lang3.tuple.Pair;
import org.duder.chat.exception.DataNotFoundException;
import org.duder.dto.user.InvitationResponse;
import org.duder.user.model.FriendInvitation;
import org.duder.user.model.User;
import org.duder.user.repository.FriendInvitationRepository;
import org.duder.user.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
class DefaultFriendInvitationService extends LoggedDuderAwareBean implements FriendInvitationService {

    private final FriendInvitationRepository friendInvitationRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    public DefaultFriendInvitationService(FriendInvitationRepository friendInvitationRepository, UserService userService, UserRepository userRepository) {
        this.friendInvitationRepository = friendInvitationRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public InvitationResponse processInvitation(Long receiverId) {
        Pair<User, User> senderAndReceiver = getSenderAndReceiver(receiverId);
        User user = senderAndReceiver.getLeft();
        User receiver = senderAndReceiver.getRight();

        checkIfInvitationWasAlreadySent(user, receiver);

        boolean addedFriend = addFriendIfInvitationWasSentByReceiver(user, receiver);

        if (!addedFriend) {
            createInvitation(user, receiver);
            return InvitationResponse.INVITATION_SENT;
        }

        return InvitationResponse.FRIEND_ADDED;
    }

    private Pair<User, User> getSenderAndReceiver(Long receiverId) {
        User user = userService.getUserByToken(getSessionToken()).get();
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new DataNotFoundException("Missing user with id " + receiverId));
        return Pair.of(user, receiver);
    }

    private void createInvitation(User sender, User receiver) {
        FriendInvitation invitation =
                FriendInvitation.builder()
                        .sender(sender)
                        .receiver(receiver)
                        .build();
        friendInvitationRepository.saveAndFlush(invitation);
    }

    private void checkIfInvitationWasAlreadySent(User sender, User receiver) {
        friendInvitationRepository
                .findBySenderAndReceiver(sender, receiver)
                .ifPresent(friendInvitation -> {
                    throw new RuntimeException("Invitation already exist with id " + friendInvitation.getId());
                });
    }

    private boolean addFriendIfInvitationWasSentByReceiver(User sender, User receiver) {
        Optional<Boolean> isDeclined = friendInvitationRepository
                .findBySenderAndReceiver(receiver, sender)
                .map(FriendInvitation::getDeclined);

        if (isDeclined.isPresent() && !isDeclined.get()) {
            addUserFriend(sender, receiver);
            return true;
        }

        return false;
    }

    private void addUserFriend(User sender, User receiver) {
        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);
    }

    @Override
    @Transactional
    public void declineInvitation(Long senderId) {
        Pair<User, User> senderAndReceiver = getSenderAndReceiver(senderId);
        User user = senderAndReceiver.getLeft();
        User sender = senderAndReceiver.getRight();

        FriendInvitation invitation = friendInvitationRepository
                .findBySenderAndReceiver(sender, user)
                .orElseThrow(() -> new RuntimeException("Missing invitation to decline"));
        invitation.setDeclined(true);
    }
}
