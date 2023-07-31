package org.jenkinsci.plugins.sonargerrit;

import static org.jenkinsci.plugins.sonargerrit.util.Localization.getLocalized;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Multimap;
import com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.AbortException;
import hudson.EnvVars;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import jenkins.tasks.SimpleBuildStep;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.NotifyHandling;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ReviewInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.jenkinsci.Symbol;
import org.jenkinsci.plugins.sonargerrit.gerrit.GerritAuthenticationConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.GerritConnectionInfo;
import org.jenkinsci.plugins.sonargerrit.gerrit.GerritConnector;
import org.jenkinsci.plugins.sonargerrit.gerrit.GerritReviewBuilder;
import org.jenkinsci.plugins.sonargerrit.gerrit.GerritRevision;
import org.jenkinsci.plugins.sonargerrit.gerrit.NotificationConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ReviewConfig;
import org.jenkinsci.plugins.sonargerrit.gerrit.ScoreConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.Inspection;
import org.jenkinsci.plugins.sonargerrit.sonar.InspectionReport;
import org.jenkinsci.plugins.sonargerrit.sonar.Issue;
import org.jenkinsci.plugins.sonargerrit.sonar.IssueFilter;
import org.jenkinsci.plugins.sonargerrit.sonar.IssueFilterConfig;
import org.jenkinsci.plugins.sonargerrit.sonar.Severity;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.PreviewModeAnalysisStrategy;
import org.jenkinsci.plugins.sonargerrit.sonar.preview_mode_analysis.SubJobConfig;
import org.jenkinsci.plugins.sonargerrit.util.BackCompatibilityHelper;
import org.jenkinsci.plugins.sonargerrit.util.Localization;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

/** Project: Sonar-Gerrit Plugin Author: Tatiana Didik */
public class SonarToGerritPublisher extends Notifier implements SimpleBuildStep {

  private static final Logger LOGGER = Logger.getLogger(SonarToGerritPublisher.class.getName());

  // ------------------ configuration settings
  /*
   * The URL of SonarQube server to be used for comments
   * */
  @Nonnull private Inspection inspectionConfig = new Inspection();

  @Nonnull private NotificationConfig notificationConfig = new NotificationConfig();

  @Nonnull private ReviewConfig reviewConfig = new ReviewConfig();

  private ScoreConfig scoreConfig = null;

  private GerritAuthenticationConfig authConfig = null;

  private final BackCompatibilityHelper backCompatibilityHelper = new BackCompatibilityHelper(this);

  @DataBoundConstructor
  public SonarToGerritPublisher() {}

  @Override
  public void perform(
      @Nonnull Run<?, ?> run,
      @Nonnull FilePath filePath,
      @NonNull EnvVars env,
      @Nonnull Launcher launcher,
      @Nonnull TaskListener listener)
      throws InterruptedException, IOException {

    GerritTrigger trigger = GerritTrigger.getTrigger(run.getParent());
    GerritConnectionInfo connectionInfo =
        new GerritConnectionInfo(env, trigger, authConfig, run.getParent());
    try {
      GerritRevision revision = GerritConnector.connect(connectionInfo).fetchRevision();

      // load inspection report
      InspectionReport report = inspectionConfig.analyse(run, listener, revision, filePath);

      Map<String, Set<Integer>> fileToChangedLines = revision.getFileToChangedLines();

      // generate review output
      // get issues to be commented
      Multimap<String, Issue> file2issuesToComment =
          getFilteredFileToIssueMultimap(
              reviewConfig.getIssueFilterConfig(), report, fileToChangedLines);
      TaskListenerLogger.logMessage(
          listener,
          LOGGER,
          Level.INFO,
          "jenkins.plugin.issues.to.comment",
          file2issuesToComment.entries().size());

      // get issues to be scored
      Multimap<String, Issue> file2issuesToScore = null;
      boolean postScore = scoreConfig != null;
      if (postScore) {
        file2issuesToScore =
            getFilteredFileToIssueMultimap(
                scoreConfig.getIssueFilterConfig(), report, fileToChangedLines);
        TaskListenerLogger.logMessage(
            listener,
            LOGGER,
            Level.INFO,
            "jenkins.plugin.issues.to.score",
            file2issuesToScore.entries().size());
      }

      // send review
      ReviewInput reviewInput =
          new GerritReviewBuilder(
                  file2issuesToComment,
                  file2issuesToScore,
                  reviewConfig,
                  scoreConfig,
                  notificationConfig)
              .buildReview();
      revision.sendReview(reviewInput);

      TaskListenerLogger.logMessage(listener, LOGGER, Level.INFO, "jenkins.plugin.review.sent");
    } catch (RestApiException e) {
      LOGGER.log(Level.SEVERE, e, () -> "Unable to post review: " + e.getMessage());
      throw new AbortException("Unable to post review: " + e.getMessage());
    } catch (NullPointerException | IllegalArgumentException | IllegalStateException e) {
      throw new AbortException(e.getMessage());
    }
  }

  private Multimap<String, Issue> getFilteredFileToIssueMultimap(
      IssueFilterConfig filterConfig,
      InspectionReport report,
      Map<String, Set<Integer>> fileToChangedLines) {
    IssueFilter commentFilter =
        new IssueFilter(filterConfig, report.getIssues(), fileToChangedLines);
    Iterable<Issue> issuesToComment = commentFilter.filter();
    return Issue.asMultimap(issuesToComment);
  }

  // Overridden for better type safety.
  // If your plugin doesn't really define any property on Descriptor,
  // you don't have to do this.
  @Override
  public DescriptorImpl getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }

  @Override
  public BuildStepMonitor getRequiredMonitorService() {
    return BuildStepMonitor.NONE;
  }

  /**
   * Descriptor for {@link SonarToGerritPublisher}. Used as a singleton. The class is marked as
   * public so that it can be accessed from views.
   *
   * <p>See <tt>src/main/resources/hudson/plugins/hello_world/SonarToGerritBuilder/*.jelly</tt> for
   * the actual HTML fragment for the configuration screen.
   */
  @Symbol("sonarToGerrit")
  @Extension // This indicates to Jenkins that this is an implementation of an extension point.
  public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

    public static final NotifyHandling NOTIFICATION_RECIPIENT_NO_ISSUES = NotifyHandling.NONE;
    public static final NotifyHandling NOTIFICATION_RECIPIENT_SOME_ISSUES = NotifyHandling.OWNER;
    public static final NotifyHandling NOTIFICATION_RECIPIENT_NEGATIVE_SCORE = NotifyHandling.OWNER;

    public static final String PROJECT_PATH = "";
    public static final String SONAR_REPORT_PATH = "target/sonar/sonar-report.json";
    public static final String SONAR_URL = "http://localhost:9000";
    public static final String DEFAULT_INSPECTION_CONFIG_TYPE =
        PreviewModeAnalysisStrategy.DescriptorImpl.BASE_TYPE;
    public static final boolean AUTO_MATCH_INSPECTION_AND_REVISION_PATHS = false;

    public static final String NO_ISSUES_TEXT =
        Localization.getLocalized("jenkins.plugin.default.review.title.no.issues");
    public static final String SOME_ISSUES_TEXT =
        Localization.getLocalized("jenkins.plugin.default.review.title.issues");
    public static final String ISSUE_COMMENT_TEXT =
        Localization.getLocalized("jenkins.plugin.default.review.body");

    public static final boolean ISSUE_OMIT_DUPLICATE_COMMENTS = false;

    public static final String CATEGORY = "Code-Review";
    public static final Integer NO_ISSUES_SCORE = 1;
    public static final Integer SOME_ISSUES_SCORE = -1;

    public static final String SEVERITY = Severity.INFO.name();
    public static final boolean NEW_ISSUES_ONLY = false;
    public static final boolean CHANGED_LINES_ONLY = false;

    /**
     * In order to load the persisted global configuration, you have to call load() in the
     * constructor.
     */
    @SuppressWarnings("unused")
    public DescriptorImpl() {
      load();
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
      // Indicates that this builder can be used with all kinds of project types
      return true;
    }

    /** This human readable name is used in the configuration screen. */
    @Override
    public String getDisplayName() {
      return getLocalized("jenkins.plugin.build.step.name");
    }
  }

  @Nonnull
  public Inspection getInspectionConfig() {
    return inspectionConfig;
  }

  @Nonnull
  public NotificationConfig getNotificationConfig() {
    return notificationConfig;
  }

  @Nonnull
  public ReviewConfig getReviewConfig() {
    return reviewConfig;
  }

  public ScoreConfig getScoreConfig() {
    return scoreConfig;
  }

  public GerritAuthenticationConfig getAuthConfig() {
    return authConfig;
  }

  @DataBoundSetter
  public void setInspectionConfig(Inspection inspectionConfig) {
    this.inspectionConfig = MoreObjects.firstNonNull(inspectionConfig, new Inspection());
  }

  @DataBoundSetter
  public void setNotificationConfig(NotificationConfig notificationConfig) {
    this.notificationConfig =
        MoreObjects.firstNonNull(notificationConfig, new NotificationConfig());
  }

  @DataBoundSetter
  public void setReviewConfig(ReviewConfig reviewConfig) {
    this.reviewConfig = MoreObjects.firstNonNull(reviewConfig, new ReviewConfig());
  }

  @DataBoundSetter
  public void setScoreConfig(ScoreConfig scoreConfig) {
    this.scoreConfig = scoreConfig;
  }

  @DataBoundSetter
  public void setAuthConfig(GerritAuthenticationConfig authConfig) {
    this.authConfig = authConfig;
  }

  // --------------deprecated methods to support back compatibility

  @Deprecated
  @DataBoundSetter
  public void setSonarURL(String sonarURL) {
    backCompatibilityHelper.setSonarURL(sonarURL);
  }

  @Deprecated
  @DataBoundSetter
  public void setSubJobConfigs(List<SubJobConfig> subJobConfigs) {
    backCompatibilityHelper.setSubJobConfigs(subJobConfigs);
  }

  @Deprecated
  @DataBoundSetter
  public void setSeverity(String severity) {
    backCompatibilityHelper.setSeverity(severity);
  }

  @Deprecated
  @DataBoundSetter
  public void setNewIssuesOnly(boolean newIssuesOnly) {
    backCompatibilityHelper.setNewIssuesOnly(newIssuesOnly);
  }

  @Deprecated
  @DataBoundSetter
  public void setChangedLinesOnly(boolean changedLinesOnly) {
    backCompatibilityHelper.setChangedLinesOnly(changedLinesOnly);
  }

  @Deprecated
  @DataBoundSetter
  public void setNoIssuesToPostText(String noIssuesToPost) {
    backCompatibilityHelper.setNoIssuesToPostText(noIssuesToPost);
  }

  @Deprecated
  @DataBoundSetter
  public void setSomeIssuesToPostText(String someIssuesToPost) {
    backCompatibilityHelper.setSomeIssuesToPostText(someIssuesToPost);
  }

  @Deprecated
  @DataBoundSetter
  public void setIssueComment(String issueComment) {
    backCompatibilityHelper.setIssueComment(issueComment);
  }

  @Deprecated
  @DataBoundSetter
  public void setOverrideCredentials(boolean overrideCredentials) {
    backCompatibilityHelper.setOverrideCredentials(overrideCredentials);
  }

  @Deprecated
  @DataBoundSetter
  public void setPostScore(boolean postScore) {
    backCompatibilityHelper.setPostScore(postScore);
  }

  @Deprecated
  @DataBoundSetter
  public void setCategory(String category) {
    backCompatibilityHelper.setCategory(category);
  }

  @Deprecated
  @DataBoundSetter
  public void setNoIssuesScore(String score) {
    backCompatibilityHelper.setNoIssuesScore(score);
  }

  @Deprecated
  @DataBoundSetter
  public void setIssuesScore(String score) {
    backCompatibilityHelper.setIssuesScore(score);
  }

  @Deprecated
  @DataBoundSetter
  public void setNoIssuesNotification(String notification) {
    backCompatibilityHelper.setNoIssuesNotification(notification);
  }

  @Deprecated
  @DataBoundSetter
  public void setIssuesNotification(String notification) {
    backCompatibilityHelper.setIssuesNotification(notification);
  }

  @Deprecated
  @DataBoundSetter
  public void setProjectPath(String path) {
    backCompatibilityHelper.setProjectPath(path);
  }

  @Deprecated
  @DataBoundSetter
  public void setPath(String path) {
    backCompatibilityHelper.setPath(path);
  }


  @Deprecated
  public String getSonarURL() {
    return backCompatibilityHelper.getSonarURL();
  }

  @Deprecated
  public Collection<SubJobConfig> getSubJobConfigs() {
    return backCompatibilityHelper.getSubJobConfigs();
  }

  @Deprecated
  public String getSeverity() {
    return backCompatibilityHelper.getSeverity();
  }

  @Deprecated
  public boolean isNewIssuesOnly() {
    return backCompatibilityHelper.isNewIssuesOnly();
  }

  @Deprecated
  public boolean isChangedLinesOnly() {
    return backCompatibilityHelper.isChangedLinesOnly();
  }

  @Deprecated
  public String getNoIssuesToPostText() {
    return backCompatibilityHelper.getNoIssuesToPostText();
  }

  @Deprecated
  public String getSomeIssuesToPostText() {
    return backCompatibilityHelper.getSomeIssuesToPostText();
  }

  @Deprecated
  public String getIssueComment() {
    return backCompatibilityHelper.getIssueComment();
  }

  @Deprecated
  public boolean isOverrideCredentials() {
    return backCompatibilityHelper.isOverrideCredentials();
  }

  @Deprecated
  @DataBoundSetter
  public void setHttpUsername(String httpUsername) {
    backCompatibilityHelper.setHttpUsername(httpUsername);
  }

  @Deprecated
  public String getHttpUsername() {
    return backCompatibilityHelper.getHttpUsername();
  }

  @Deprecated
  @DataBoundSetter
  public void setHttpPassword(String httpPassword) {
    backCompatibilityHelper.setHttpPassword(httpPassword);
  }

  @Deprecated
  public String getHttpPassword() {
    return backCompatibilityHelper.getHttpPassword();
  }

  @Deprecated
  public boolean isPostScore() {
    return backCompatibilityHelper.isPostScore();
  }

  @Deprecated
  public String getCategory() {
    return backCompatibilityHelper.getCategory();
  }

  @Deprecated
  public String getNoIssuesScore() {
    return backCompatibilityHelper.getNoIssuesScore();
  }

  @Deprecated
  public String getIssuesScore() {
    return backCompatibilityHelper.getIssuesScore();
  }

  @Deprecated
  public String getNoIssuesNotification() {
    return backCompatibilityHelper.getNoIssuesNotification();
  }

  @Deprecated
  public String getIssuesNotification() {
    return backCompatibilityHelper.getIssuesNotification();
  }

  @Deprecated
  public String getProjectPath() {
    return backCompatibilityHelper.getProjectPath();
  }

  @Deprecated
  public String getPath() {
    return backCompatibilityHelper.getPath();
  }
}
