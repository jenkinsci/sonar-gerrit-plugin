package com.aquarellian.genar;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

/**
 * Project: Santa-cruz
 * Author:  Tatiana Goretskaya
 * Created: 11.06.2015 10:36
 * <p/>
 * Copyright (c) 1999-2015 Magenta Corporation Ltd. All Rights Reserved.
 * Magenta Technology proprietary and confidential.
 * Use is subject to license terms.
 * <p/>
 * $Id$
 */
public class ObjectHelper {

    public static boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    public static boolean areEquals(Object o1, Object o2) {
        return o1 == null && o2 == null || o1 != null && o1.equals(o2);
    }

    public static boolean dateInRange(Date date, Date from, Date to) {
        if (date == null) {
            return from == null && to == null;
        }
        boolean fromMatched = from == null || date.getTime() - from.getTime() >= 0;
        boolean toMatched = to == null || to.getTime() - date.getTime() >= 0;
        return fromMatched && toMatched;
    }

    public static boolean dateInRange(Date date, Calendar from, Calendar to) {
        return dateInRange(date, toDate(from), toDate(to));
    }

    public static Date toDate(Calendar date) {
        return date == null ? null : date.getTime();
    }

}
