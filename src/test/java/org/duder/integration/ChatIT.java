package org.duder.integration;

import org.duder.dto.chat.ChatMessage;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

public class ChatIT extends AbstractIT {

    private static final String GET_CHAT_STATE_ENDPOINT = "/api/chat/getChatState";

    @Test
    public void getChatState() {
        //given
        HttpHeaders headers = getActiveSessionToken();

        //when
        ResponseEntity<ChatMessage[]> exchange = testRestTemplate.exchange(url + GET_CHAT_STATE_ENDPOINT, HttpMethod.GET, new HttpEntity<>(headers), ChatMessage[].class);
        ChatMessage[] messages = exchange.getBody();

        assertEquals(1, messages.length);
    }
}
