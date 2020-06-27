package org.duder.user.service;

import org.duder.common.DuderBean;
import org.duder.security.SessionHolder;
import org.duder.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class LoggedDuderAwareBean extends DuderBean {

    @PersistenceContext
    private EntityManager entityManager;
    private SessionHolder sessionTokenHolder;

    @Autowired
    public void setSessionTokenHolder(SessionHolder sessionTokenHolder) {
        this.sessionTokenHolder = sessionTokenHolder;
    }

    protected User getUser() {
        entityManager.merge(sessionTokenHolder.user);
        return sessionTokenHolder.user;
    }
}
