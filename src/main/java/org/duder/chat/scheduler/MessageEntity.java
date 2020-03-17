package org.duder.chat.scheduler;

import lombok.*;
import org.duder.chat.model.MessageType;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity(name = "Message")
@Table(name = "Message")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Timestamp timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type")
    private MessageType messageType;

    private String content;

    private String author;
}
