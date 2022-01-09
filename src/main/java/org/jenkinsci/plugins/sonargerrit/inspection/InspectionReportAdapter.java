package org.jenkinsci.plugins.sonargerrit.inspection;

import java.util.List;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.IssueAdapter;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 19.12.2017 21:55 */
@Restricted(NoExternalUse.class)
public interface InspectionReportAdapter {

  List<IssueAdapter> getIssues();
}
