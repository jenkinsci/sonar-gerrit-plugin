package com.aquarellian.genar.data.entity.interfaces;

import java.util.Date;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 10:53
 * <p/>
 * $Id$
 */
public interface SonarIssue {
    String getKey();

    String getSeverity();

    Integer getLine();

    String getMessage();

    String getRule();

    String getStatus();

    Date getCreationDate();

}
