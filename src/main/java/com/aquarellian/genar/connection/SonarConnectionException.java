package com.aquarellian.genar.connection;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 10:47
 * <p/>
 * $Id$
 */
public class SonarConnectionException extends Exception {
    public SonarConnectionException(String message) {
        super(message);
    }

    public SonarConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SonarConnectionException(Throwable cause) {
        super(cause);
    }

    public SonarConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
