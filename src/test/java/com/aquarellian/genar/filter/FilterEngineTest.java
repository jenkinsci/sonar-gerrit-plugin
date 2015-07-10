package com.aquarellian.genar.filter;

import com.aquarellian.genar.FileReader;
import com.aquarellian.genar.data.SonarReportBuilder;
import com.aquarellian.genar.data.entity.Issue;
import com.aquarellian.genar.data.entity.Report;
import com.aquarellian.genar.filter.model.Filter;
import junit.framework.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 10.07.2015 13:00
 * <p/>
 * $Id$
 */
public class FilterEngineTest {

    @Test
    public void testFilter() throws Exception {
        URL url = getClass().getClassLoader().getResource("filter/filter_expected_results.xml");
        Properties p = FileReader.loadProperties(url);

        test("filter/filters/simple_single_filter.xml", "filter/data/simple_single_data.json", p);
        test("filter/filters/simple_filter.xml", "filter/data/simple_data.json", p);
        test("filter/filters/single_criteria_filter.xml", "filter/data/single_criteria_data.json", p);
        test("filter/filters/simple_exception_filter.xml", "filter/data/simple_exception_data.json", p);
        test("filter/filters/complicated_exception_filter.xml", "filter/data/complicated_exception_data.json", p);
    }

    private void test(String filterPath, String reportPath, Properties p) throws JAXBException {
        Filter f = new FilterParser().parseFilter(filterPath);

        URL url = getClass().getClassLoader().getResource(reportPath);
        String json = FileReader.readFile(url);
        Report rep = new SonarReportBuilder().fromJson(json);

        FilterEngine engine = new FilterEngine(f, rep.getIssues());
        for (Issue i : rep.getIssues()) {
            String errorMsg = String.format("Assertion failed on IssueID=%s, FilterPath=%s, ReportPath=%s", i.getKey(), filterPath, reportPath);
            boolean expected = Boolean.valueOf(p.getProperty(i.getKey())).booleanValue();
            boolean actual = engine.getFiltered().contains(i);
            Assert.assertEquals(errorMsg, expected, actual);
            getLogger().log(Level.INFO, String.format("Test passed on IssueID=%s, FilterPath=%s, ReportPath=%s", i.getKey(), filterPath, reportPath));

        }
    }

    private Logger getLogger() {
        return Logger.getLogger(this.getClass().getName());
    }

}
