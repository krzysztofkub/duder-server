package org.duder.chat.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.duder.chat.model.MessageType;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Timestamp timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType;
}
