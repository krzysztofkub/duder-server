package org.duder.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDto {
    private MessageTypeDto type;
    private String content;
    private String sender;
    private String to;
}
