package org.duder.user.repository;

import org.duder.user.model.FriendInvitation;
import org.duder.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FriendInvitationRepository extends JpaRepository<FriendInvitation, Long> {
    Optional<FriendInvitation> findBySenderAndReceiver(User sender, User receiver);
}
