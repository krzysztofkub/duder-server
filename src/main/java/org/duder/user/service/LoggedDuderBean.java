package org.duder.user.service;

import org.duder.common.DuderBean;
import org.duder.security.SessionHolder;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class LoggedDuderBean extends DuderBean {

    private SessionHolder sessionTokenHolder;

    protected String getSessionToken() {
        return sessionTokenHolder.sessionToken;
    }

    @Autowired
    public final void setSessionTokenHolder(SessionHolder sessionTokenHolder) {
        this.sessionTokenHolder = sessionTokenHolder;
    }

}
