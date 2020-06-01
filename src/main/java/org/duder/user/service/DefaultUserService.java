package org.duder.user.service;

import org.duder.dto.user.Dude;
import org.duder.user.model.FriendInvitation;
import org.duder.user.model.User;
import org.duder.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
class DefaultUserService extends LoggedDuderBean implements UserService {
    private final UserRepository userRepository;

    public DefaultUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> getUserByToken(String token) {
        return userRepository.findBySessionToken(token);
    }

    @Override
    public Set<User> getUserFriendsByToken(String token) {
        return userRepository.findBySessionToken(token)
                .map(User::getFriends)
                .orElse(new HashSet<>());
    }

    @Override
    public List<Dude> getDudes(int page, int size) {
        User user = userRepository.findBySessionToken(getSessionToken()).get();
        Set<User> friends = user.getFriends();
        Set<User> invitatedUsers = user.getSentInvitations().stream()
                .map(FriendInvitation::getReceiver)
                .collect(Collectors.toSet());
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("nickname"));
        List<User> allUsers = userRepository.findAllByIdNot(user.getId(), pageRequest).getContent();

        List<Dude> responseList = new ArrayList<>();
        allUsers.forEach(u -> {
            Dude dude = mapToDude(u);
            dude.setIsFriend(friends.contains(u));
            dude.setIsInvitationSent(invitatedUsers.contains(u));
            responseList.add(dude);
        });
        return responseList;
    }

    private Dude mapToDude(User user) {
        return Dude.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .build();
    }
}
