package org.duder.user.service;

import org.duder.common.DuderBean;
import org.duder.security.SessionHolder;
import org.duder.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class LoggedDuderAwareBean extends DuderBean {

    private SessionHolder sessionTokenHolder;

    @Autowired
    public void setSessionTokenHolder(SessionHolder sessionTokenHolder) {
        this.sessionTokenHolder = sessionTokenHolder;
    }

    protected User getUser() {
        return sessionTokenHolder.user;
    }
}
