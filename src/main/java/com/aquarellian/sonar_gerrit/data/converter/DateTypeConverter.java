package com.aquarellian.sonar_gerrit.data.converter;

import com.google.gson.*;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 11.06.2015 16:54
 * <p/>
 * $Id$
 */
//todo do something with magical date format
public class DateTypeConverter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    // @Override
    public JsonElement serialize(Date src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(DateUtil.formatDate(src));
    }

    //@Override
    public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        String str = json.getAsString();
        try {
            return DateUtil.parseDate(str);
        } catch (DateParseException e) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssz");
                return df.parse(str);
            } catch (ParseException e1) {
                e.printStackTrace();
                throw new JsonParseException(e);
            }
        }
    }
}