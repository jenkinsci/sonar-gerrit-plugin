package org.jenkinsci.plugins.sonargerrit;

import com.google.gerrit.extensions.common.DiffInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Map;
import org.jenkinsci.plugins.sonargerrit.filter.util.DummyRevision;
import org.jenkinsci.plugins.sonargerrit.inspection.converter.DateTypeConverter;
import org.jenkinsci.plugins.sonargerrit.inspection.entity.Report;
import org.jenkinsci.plugins.sonargerrit.inspection.sonarqube.SonarReportBuilder;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 14.11.2017 17:31
 *
 * <p>$Id$
 */
public class ReportBasedTest {
  protected Report readreport(String file)
      throws IOException, InterruptedException, URISyntaxException {
    URL url = getClass().getClassLoader().getResource(file);

    File path = new File(url.toURI());
    FilePath filePath = new FilePath(path);
    String json = filePath.readToString();
    return new SonarReportBuilder().fromJson(json);
  }

  protected Map<String, DiffInfo> readChange(String file)
      throws IOException, InterruptedException, URISyntaxException {
    URL url = getClass().getClassLoader().getResource(file);

    File path = new File(url.toURI());
    FilePath filePath = new FilePath(path);
    String json = filePath.readToString();

    Gson GSON = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeConverter()).create();
    DummyRevision rev = GSON.fromJson(json, DummyRevision.class);
    return rev.toMap();
  }
}
