package org.duder.user.service;

import org.duder.common.DuderBean;
import org.duder.security.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class LoggedDuderAwareBean extends DuderBean {

    private SessionHolder sessionTokenHolder;

    @Autowired
    public void setSessionTokenHolder(SessionHolder sessionTokenHolder) {
        this.sessionTokenHolder = sessionTokenHolder;
    }

    protected String getSessionToken() {
        return sessionTokenHolder.user.getSessionToken();
    }
}
