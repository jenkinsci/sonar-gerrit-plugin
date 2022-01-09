package org.jenkinsci.plugins.sonargerrit.sonar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.DiffInfo;
import org.jenkinsci.plugins.sonargerrit.gerrit.DummyRevision;

/** @author Réda Housni Alaoui */
public class JsonReports {

  public static ReportRepresentation readReport(String file)
      throws IOException, InterruptedException, URISyntaxException {
    URL url = JsonReports.class.getClassLoader().getResource(file);

    File path = new File(Objects.requireNonNull(url).toURI());
    FilePath filePath = new FilePath(path);
    String json = filePath.readToString();
    return new SonarReportBuilder().fromJson(json);
  }

  public static Map<String, DiffInfo> readChange(String file)
      throws IOException, InterruptedException, URISyntaxException {
    URL url = JsonReports.class.getClassLoader().getResource(file);

    File path = new File(Objects.requireNonNull(url).toURI());
    FilePath filePath = new FilePath(path);
    String json = filePath.readToString();

    Gson GSON = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeConverter()).create();
    DummyRevision rev = GSON.fromJson(json, DummyRevision.class);
    return rev.toMap();
  }
}
