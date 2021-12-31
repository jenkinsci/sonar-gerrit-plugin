package org.jenkinsci.plugins.sonargerrit.inspection.converter;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.util.DateParseException;
import org.apache.commons.httpclient.util.DateUtil;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 11.06.2015 16:54 */
// todo do something with magical date format
public class DateTypeConverter implements JsonSerializer<Date>, JsonDeserializer<Date> {
  private static final Logger LOGGER = Logger.getLogger(DateTypeConverter.class.getName());
  // @Override
  @Override
  public JsonElement serialize(Date src, Type srcType, JsonSerializationContext context) {
    return new JsonPrimitive(DateUtil.formatDate(src));
  }

  // @Override
  @Override
  public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
    String str = json.getAsString();
    try {
      return DateUtil.parseDate(str);
    } catch (DateParseException e) {
      try {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssz");
        return df.parse(str);
      } catch (ParseException e1) {
        LOGGER.log(
            Level.WARNING, "An exception occurred on DateTypeConverter: {0}", e.getStackTrace());
        throw new JsonParseException(e);
      }
    }
  }
}
