package org.duder.user.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void whenDifferentLogin_thenUsersNotTheSame() {
        // given
        final String login = "dude";

        // when
        final User u1 = User.builder().login(login).build();
        final User u2 = User.builder().login(login).build();
        final User u3 = User.builder().login(login + "asdasd").build();

        // then
        assertEquals(u1, u2);
        assertNotEquals(u1, u3);
    }
}