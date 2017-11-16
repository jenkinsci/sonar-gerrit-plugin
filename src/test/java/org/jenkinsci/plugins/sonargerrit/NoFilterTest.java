package org.jenkinsci.plugins.sonargerrit;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import org.jenkinsci.plugins.sonargerrit.config.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.data.entity.Issue;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 14.11.2017 17:33
 * $Id$
 */
public class NoFilterTest extends BaseFilterTest {

    @Test
    public void testFilter() {

        // filter by severity predicate
        Iterable<Issue> issues = publisher.filterIssuesByPredicates(report.getIssues(), publisher.getReviewConfig().getIssueFilterConfig());
        Assert.assertEquals(19, Sets.newHashSet(issues).size());

        Multimap<String, Issue> multimap = getMultimap();
        Assert.assertEquals(8, multimap.keySet().size());
        Collection<Issue> childModuleIssues = multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/ChildModule.java");
        Assert.assertEquals(1, childModuleIssues.size());
        Assert.assertEquals(2, multimap.get("guice-jpa/src/main/java/com/magenta/guice/jpa/DBInterceptor.java").size());
        Assert.assertEquals(8, multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/plugins/PluginsManager.java").size());
        Assert.assertEquals(4, multimap.get("guice-bootstrap/src/main/java/com/magenta/guice/bootstrap/xml/XmlModule.java").size());
        Assert.assertEquals(1, multimap.get("guice-events/src/main/java/com/magenta/guice/events/ClassgenHandlerInvocator.java").size());
        Assert.assertEquals(1, multimap.get("guice-events/src/main/java/com/magenta/guice/events/EnumMatcher.java").size());
        Assert.assertEquals(1, multimap.get("guice-events/src/main/java/com/magenta/guice/events/EventDispatcher.java").size());
        Assert.assertEquals(1, multimap.get("src/main/java/com/aquarellian/sonar-gerrit/ObjectHelper.java").size());
    }

    @Override
    protected IssueFilterConfig getFilterConfig() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doCheckFilteredOutByCriteria(Object o) {
        throw new UnsupportedOperationException();
    }
}
