package org.duder.chat.dao.model;

import lombok.*;
import org.duder.chat.dto.ChannelType;
import org.duder.user.dao.model.UserChannel;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Name should not be null but is not necessarily unique - not a natural id
    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private ChannelType channelType;

    @OneToMany(mappedBy = "channel")
    List<UserChannel> channelUsers = new ArrayList<>();

}