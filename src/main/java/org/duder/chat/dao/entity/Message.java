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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Column(columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp timestamp;
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

}
