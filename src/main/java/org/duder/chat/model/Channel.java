package org.duder.chat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.duder.dto.chat.ChannelType;
import org.duder.user.model.UserChannel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Channel {

    @OneToMany(mappedBy = "channel")
    List<UserChannel> channelUsers = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Name should not be null but is not necessarily unique - not a natural id
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    private ChannelType channelType;

}
