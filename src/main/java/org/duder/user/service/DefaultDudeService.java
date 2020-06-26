package org.duder.user.service;

import org.duder.dto.user.Dude;
import org.duder.dto.user.FriendshipStatus;
import org.duder.user.model.User;
import org.duder.user.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
class DefaultDudeService extends LoggedDuderAwareBean implements DudeService {

    private final UserRepository userRepository;
    private final FriendshipService friendshipService;

    public DefaultDudeService(UserRepository userRepository, FriendshipService friendshipService) {
        this.userRepository = userRepository;
        this.friendshipService = friendshipService;
    }

    @Override
    public List<Dude> getDudes(int page, int size) {
        User user = getUser();

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("nickname"));
        List<User> allUsers = userRepository.findAllByIdNot(user.getId(), pageRequest).getContent();

        List<Dude> responseList = new ArrayList<>();
        allUsers.forEach(u -> {
            Dude dude = mapToDude(u);
            dude.setFriendshipStatus(getFriendshipStatus(user, u));
            responseList.add(dude);
        });
        return responseList;
    }

    private FriendshipStatus getFriendshipStatus(User user, User someDude) {
        return friendshipService.deduceFriendshipStatus(user, someDude.getId())
                .orElse(FriendshipStatus.NONE);
    }

    private Dude mapToDude(User user) {
        return Dude.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .build();
    }
}
