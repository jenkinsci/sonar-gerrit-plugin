package org.jenkinsci.plugins.sonargerrit.test_infrastructure.gerrit;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ChangeApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ReviewInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.client.ListChangesOption;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.ChangeInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.ChangeMessageInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.CommentInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.RestApiException;
import org.apache.commons.lang.StringUtils;

/** @author Réda Housni Alaoui */
public class GerritChange {

  private final ChangeApi changeApi;
  private final String changeNumericId;

  public GerritChange(ChangeApi changeApi) throws RestApiException {
    this.changeApi = changeApi;
    this.changeNumericId = String.valueOf(changeApi.info()._number);
  }

  public String refName(int patchSetNumber) {
    String numericIdSuffix = StringUtils.substring(changeNumericId, changeNumericId.length() - 2);
    if (numericIdSuffix.length() == 1) {
      numericIdSuffix = "0" + numericIdSuffix;
    }
    return "refs/changes/" + numericIdSuffix + "/" + changeNumericId + "/" + patchSetNumber;
  }

  public ChangeInfo getDetail() throws RestApiException {
    return changeApi.get(
        ListChangesOption.LABELS,
        ListChangesOption.DETAILED_LABELS,
        ListChangesOption.DETAILED_ACCOUNTS,
        ListChangesOption.REVIEWER_UPDATES,
        ListChangesOption.MESSAGES);
  }

  public GerritChange reviewAndSubmit() throws RestApiException {
    ReviewInput reviewInput =
        new ReviewInput().label("Verified", 1).label("Code-Quality", 1).label("Code-Review", 2);
    changeApi.current().review(reviewInput);
    changeApi.current().submit();

    return this;
  }

  public GerritChange revert() throws RestApiException {
    return new GerritChange(changeApi.revert());
  }

  public List<CommentInfo> listComments() throws RestApiException {
    return changeApi.comments().values().stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  public List<ChangeMessageInfo> listMessages() throws RestApiException {
    return changeApi.messages();
  }

  public boolean containsMessage(String message) throws RestApiException {
    return listMessages().stream()
        .map(messageInfo -> messageInfo.message)
        .anyMatch(msg -> msg.contains(message));
  }

  public int getNumber() throws RestApiException {
    return changeApi.info()._number;
  }

  public String changeNumericId() {
    return changeNumericId;
  }
}
