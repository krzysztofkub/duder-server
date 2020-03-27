package org.duder.utils;

import org.duder.user.dao.model.User;

/*
This class is simply holder for values from /db/mysql/data.sql
 */
public class DataSQLValues {
    public static User getUser() {
        return User
                .builder()
                .login("login")
                .nickname("nickname")
                .id(1L)
                .build();
    }
}
