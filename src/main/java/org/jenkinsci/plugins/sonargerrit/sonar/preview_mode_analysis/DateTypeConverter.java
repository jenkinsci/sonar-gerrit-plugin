package org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 11.06.2015 16:54 */
// todo do something with magical date format
@Restricted(NoExternalUse.class)
class DateTypeConverter implements JsonSerializer<Date>, JsonDeserializer<Date> {

  /** Date format pattern used to parse HTTP date headers in RFC 1123 format. */
  private static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

  /** Date format pattern used to parse HTTP date headers in RFC 1036 format. */
  private static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";

  /**
   * Date format pattern used to parse HTTP date headers in ANSI C <code>asctime()</code> format.
   */
  private static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";

  /** Date format pattern used to parse HTTP date headers in ISO 8601 format. */
  private static final String PATTERN_ISO8601 = "yyyy-MM-dd'T'hh:mm:ssz";

  private static final Logger LOGGER = Logger.getLogger(DateTypeConverter.class.getName());

  // @Override
  @Override
  public JsonElement serialize(Date src, Type srcType, JsonSerializationContext context) {
    SimpleDateFormat formatter = new SimpleDateFormat(PATTERN_RFC1123);
    formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
    return new JsonPrimitive(formatter.format(src));
  }

  // @Override
  @Override
  public Date deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
    String str = json.getAsString();

    // trim single quotes around date if present
    if (str.length() > 1 && str.startsWith("'") && str.endsWith("'")) {
      str = str.substring(1, str.length() - 1);
    }

    SimpleDateFormat dateParser = null;
    List<ParseException> exceptions = new ArrayList<>();
    for (String format :
        Arrays.asList(PATTERN_ASCTIME, PATTERN_RFC1036, PATTERN_RFC1123, PATTERN_ISO8601)) {
      if (dateParser == null) {
        dateParser = new SimpleDateFormat(format);
        dateParser.setTimeZone(TimeZone.getTimeZone("GMT"));
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
        dateParser.set2DigitYearStart(calendar.getTime());
      } else {
        dateParser.applyPattern(format);
      }
      try {
        return dateParser.parse(str);
      } catch (ParseException e) {
        exceptions.add(e);
      }
    }

    JsonParseException e = new JsonParseException("Unable to parse the date " + str);
    for (ParseException exception : exceptions) {
      e.addSuppressed(exception);
    }
    LOGGER.log(Level.WARNING, "An exception occurred on DateTypeConverter", e);
    throw e;
  }
}
