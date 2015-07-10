package com.aquarellian.genar.filter;

import com.aquarellian.genar.ObjectHelper;
import com.aquarellian.genar.data.entity.Issue;
import com.aquarellian.genar.data.entity.Report;
import com.aquarellian.genar.filter.model.Filter;
import com.aquarellian.genar.filter.model.IssueFilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 16.06.2015 11:19
 * <p/>
 * $Id$
 */
public class FilterEngine {
    private Filter filter;
    private List<Issue> issues;

    private List<Issue> filtered = new ArrayList<Issue>();

    public FilterEngine(Filter filter, List<Issue> issues) {
        this.filter = filter;
        this.issues = issues;
        applyFilter();
    }

    public FilterEngine(Filter filter, Report report) {
        this.filter = filter;
        this.issues = report.getIssues();
        applyFilter();
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        if (!filter.equals(this.filter)) { //todo improve
            this.filter = filter;
            reapplyFilter();
        }
    }

    private void reapplyFilter() {
        filtered = null;
        applyFilter();
    }

    public List<Issue> getIssues() {
        return issues;
    }

    public void setIssues(List<Issue> issues) {
        if (!issues.equals(this.issues)) {   //todo improve
            this.issues = issues;
            reapplyFilter();
        }
    }

    private void applyFilter() {
        for (IssueFilter f : filter.getFilters()) {
            applyIssueFilter(f);
        }
    }

    private void applyIssueFilter(IssueFilter f) {
        for (Issue i : issues) {
            if (isIssueMatchesCriteria(i, f)) {
                if (f.isInclude()) {
                    filtered.add(i);
                } else {
                    filtered.remove(i);
                }
            }
        }
    }

    private boolean isIssueMatchesCriteria(Issue i, IssueFilter f) {
        return (f.getAnew() == null || ObjectHelper.areEquals(f.getAnew(), i.isNew()))
                && ObjectHelper.dateInRange(i.getCreationDate(), f.getCreatedfrom(), f.getCreatedto())
                && isComponentMatchesCriteria(i.getComponent(), f.getComponents())
                && checkStringByListCriteria(i.getRule(), f.getRules())
                && isEnumValueStringInList(i.getSeverity(), f.getSeverities())
                && checkStringByListCriteria(i.getStatus(), f.getStatuses())
                && !isIssueMatchesExceptions(i, f);
    }

    private boolean isIssueMatchesExceptions(Issue i, IssueFilter f) {
        for (IssueFilter exc : f.getExceptions()) {
            if (isIssueMatchesCriteria(i, exc)) {
                return true;
            }
        }
        return false;
    }

    private boolean isComponentMatchesCriteria(String comp, List<String> comps) {
        return checkStringByListCriteria(comp, comps);
    }

    private boolean checkStringByListCriteria(String s, List<String> strs) {
        return !ObjectHelper.isEmpty(s) && (ObjectHelper.isEmpty(strs) || strs.contains(s));
    }

    private boolean isEnumValueStringInList(Enum s, List<String> strs) {
        return s != null && checkStringByListCriteria(s.name(), strs);
    }

    private boolean isNotEmptyEnumValueInList(Enum s, List<Enum> strs) {
        return strs != null && strs.contains(s);
    }

    public List<Issue> getFiltered() {
        return Collections.unmodifiableList(filtered);
    }
}
