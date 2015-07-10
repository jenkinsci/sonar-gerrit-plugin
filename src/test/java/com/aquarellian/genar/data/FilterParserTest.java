package com.aquarellian.genar.data;

import com.aquarellian.genar.filter.FilterParser;
import com.aquarellian.genar.filter.model.Filter;
import com.aquarellian.genar.filter.model.IssueFilter;
import junit.framework.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Project: genar
 * Author:  Tatiana Didik
 * Created: 12.06.2015 17:06
 * <p/>
 * $Id$
 */
public class FilterParserTest {

    @Test
    public void testFilterReading() throws JAXBException {

        Filter f = new FilterParser().parseFilter("filter.xml");

        Assert.assertNotNull(f);
        IssueFilter filter = f.getFilters().get(0);
        Assert.assertEquals(2, filter.getComponents().size());
        Assert.assertEquals("str1234", filter.getComponents().get(0));
        Assert.assertEquals("str5678", filter.getComponents().get(1));
        Assert.assertEquals(1, filter.getRules().size());
        Assert.assertEquals("str1234", filter.getRules().get(0));
        Assert.assertEquals("MAJOR", filter.getSeverities().get(0));
        Assert.assertEquals("OPEN", filter.getStatuses().get(0));
        Assert.assertTrue(filter.isSetAnew());
        Assert.assertTrue(filter.isInclude());
//        Assert.assertEquals(createDate(2012, 12, 13, 0, 0, 0), filter.getCreatedfrom());
//        Assert.assertEquals(createDate(2013, 12, 13, 0, 0, 0), filter.getCreatedto());
        List<IssueFilter> exceptions = filter.getExceptions();
        Assert.assertNotNull(exceptions);
        Assert.assertEquals(1, exceptions.size());

        IssueFilter exclude = exceptions.get(0);
        Assert.assertNotNull(exclude);
        Assert.assertEquals(1, exclude.getComponents().size());
        Assert.assertEquals("3579", exclude.getComponents().get(0));
        Assert.assertEquals(1, exclude.getRules().size());
        Assert.assertEquals("str3579", exclude.getRules().get(0));
        Assert.assertEquals("MINOR", exclude.getSeverities().get(0));
        Assert.assertEquals("OPEN", exclude.getStatuses().get(0));
        Assert.assertFalse(exclude.isSetAnew());
//        Assert.assertEquals(createDate(2012, 12, 13, 0, 0, 0), include.getCreatedfrom());
//        Assert.assertEquals(createDate(2013, 12, 13, 0, 0, 0), include.getCreatedto());

    }

    private Date createDate(int year, int month, int day, int hour, int min, int sec) {
        Calendar c = Calendar.getInstance();
//        c.setTimeZone(TimeZone.getTimeZone("GMT+3:00"));
        c.set(year, month - 1, day, hour, min, sec);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
}
