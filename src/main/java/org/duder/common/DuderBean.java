package org.duder.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DuderBean {
    private transient volatile Logger logger;


    protected Logger getLogger() {
        if (logger == null) {
            logger = LoggerFactory.getLogger(getClass());
        }
        return logger;
    }

    protected void info(String message) {
        getLogger().info(message);
    }

    protected void info(String format, Object... args) {
        if (getLogger().isInfoEnabled()) {
            getLogger().info(String.format(format, args));
        }
    }

    protected void debug(String message) {
        getLogger().debug(message);
    }

    protected void debug(String format, Object... args) {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(String.format(format, args));
        }
    }

    protected void trace(String message) {
        getLogger().trace(message);
    }

    protected void trace(String format, Object... args) {
        getLogger().trace(String.format(format, args));
    }

    protected void warn(String message) {
        getLogger().warn(message);
    }

    protected void warn(String format, Object... args) {
        getLogger().warn(String.format(format, args));
    }

    protected void error(String format, Object... args) {
        getLogger().error(String.format(format, args));
    }

    protected void error(String message) {
        getLogger().error(message);
    }

    protected void error(String message, Throwable e) {
        getLogger().error(message, e);
    }
}
