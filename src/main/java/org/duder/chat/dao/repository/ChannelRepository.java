package org.duder.chat.dao.repository;

import org.duder.chat.dao.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
}
