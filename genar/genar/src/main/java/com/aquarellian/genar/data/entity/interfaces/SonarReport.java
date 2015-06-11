package com.aquarellian.genar.data.entity.interfaces;


import com.aquarellian.genar.data.entity.SonarComponentImpl;
import com.aquarellian.genar.data.entity.SonarIssueImpl;
import com.aquarellian.genar.data.entity.SonarRuleImpl;
import com.aquarellian.genar.data.entity.SonarUserImpl;

import java.util.List;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 11.06.2015 16:12
 * <p/>
 * $Id$
 */
public interface SonarReport {
    List<SonarIssueImpl> getIssues();

    List<SonarComponentImpl> getComponents();

    List<SonarRuleImpl> getRules();

    List<SonarUserImpl> getUsers();
}
