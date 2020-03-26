package org.duder.chat.dao.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.duder.chat.dto.MessageType;
import org.duder.user.dao.model.User;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private Timestamp timestamp;
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

}