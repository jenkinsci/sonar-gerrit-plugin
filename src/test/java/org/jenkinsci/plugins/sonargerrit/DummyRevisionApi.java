package org.jenkinsci.plugins.sonargerrit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.common.collect.ListMultimap;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ChangeApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.CherryPickInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.CommentApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.DraftApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.DraftInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.FileApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.RebaseInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.RelatedChangesInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ReviewInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.ReviewResult;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.RevisionApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.RevisionReviewerApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.RobotCommentApi;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.api.changes.SubmitInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.client.ArchiveFormat;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.client.SubmitType;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.ActionInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.ApprovalInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.ChangeInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.CommentInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.CommitInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.DiffInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.EditInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.FileInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.MergeableInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.RobotCommentInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.TestSubmitRuleInfo;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.common.TestSubmitRuleInput;
import me.redaalaoui.gerrit_rest_java_client.thirdparty.com.google.gerrit.extensions.restapi.BinaryResult;

/**
 * Project: Sonar-Gerrit Plugin Author: Tatiana Didik Created: 27.07.2017 13:05
 *
 * <p>$Id$
 */
public class DummyRevisionApi implements RevisionApi {
  private final Map<String, List<Integer>> path2changedValues;

  public DummyRevisionApi(Map<String, List<Integer>> path2changedValues) {
    this.path2changedValues = path2changedValues;
  }

  @Override
  public FileApi file(String path) {
    return getFileApi(path);
  }

  @Override
  public CommitInfo commit(boolean addLinks) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  private FileApi getFileApi(final String path) {
    return new FileApi() {
      @Override
      public BinaryResult content() {
        throw new UnsupportedOperationException("This is a dummy test class");
      }

      @Override
      public DiffInfo diff() {
        return generateDiffInfoByPath(path);
      }

      @Override
      public DiffInfo diff(String base) {
        throw new UnsupportedOperationException("This is a dummy test class");
      }

      @Override
      public DiffInfo diff(int parent) {
        throw new UnsupportedOperationException("This is a dummy test class");
      }

      @Override
      public DiffRequest diffRequest() {
        throw new UnsupportedOperationException("This is a dummy test class");
      }

      @Override
      public void setReviewed(boolean reviewed) {
        throw new UnsupportedOperationException("This is a dummy test class");
      }

      @Override
      public BlameRequest blameRequest() {
        throw new UnsupportedOperationException("This is a dummy test class");
      }
    };
  }

  protected DiffInfo generateDiffInfoByPath(String path) {
    DiffInfo info = new DiffInfo();
    info.content = new ArrayList<>();

    List<Integer> lines = path2changedValues.get(path);
    if (lines != null) { // if file had been affected by change
      for (int v : lines) {
        info.content.add(createContentEntry(lines.indexOf(v) % 2 != 0, v));
      }
    }
    return info;
  }

  protected DiffInfo.ContentEntry createContentEntry(boolean changed, int countOfStrings) {
    DiffInfo.ContentEntry entry = new DiffInfo.ContentEntry();
    for (int i = 0; i < countOfStrings; i++) {
      String v = ((Integer) i).toString();
      if (changed) {
        if (entry.a == null || entry.b == null) {
          entry.a = new ArrayList<>();
          entry.b = new ArrayList<>();
        }
        entry.a.add(v + v);
        entry.b.add(v + v + v);
      } else {
        if (entry.ab == null) {
          entry.ab = new ArrayList<>();
        }
        entry.ab.add(v);
      }
    }
    return entry;
  }

  @Override
  public void delete() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public String description() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public void description(String description) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public ReviewResult review(ReviewInput in) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public void submit() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public void submit(SubmitInput in) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public void publish() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public ChangeApi cherryPick(CherryPickInput in) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public ChangeInfo cherryPickAsInfo(CherryPickInput in) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public ChangeApi rebase() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public ChangeApi rebase(RebaseInput in) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public boolean canRebase() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public RevisionReviewerApi reviewer(String id) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public void setReviewed(String path, boolean reviewed) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Set<String> reviewed() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, FileInfo> files() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, FileInfo> files(String base) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public MergeableInfo mergeable() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public MergeableInfo mergeableOtherBranches() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, List<CommentInfo>> comments() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, List<CommentInfo>> drafts() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public DraftApi createDraft(DraftInput in) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public DraftApi draft(String id) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public CommentApi comment(String id) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, ActionInfo> actions() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public List<CommentInfo> commentsAsList() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public List<CommentInfo> draftsAsList() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, FileInfo> files(int parentNum) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public List<String> queryFiles(String query) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public MergeListRequest getMergeList() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public RelatedChangesInfo related() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public ListMultimap<String, ApprovalInfo> votes() {
    return null;
  }

  @Override
  public BinaryResult getArchive(ArchiveFormat format) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public BinaryResult patch() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public BinaryResult patch(String path) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public RobotCommentApi robotComment(String id) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public String etag() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, List<RobotCommentInfo>> robotComments() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public List<RobotCommentInfo> robotCommentsAsList() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public EditInfo applyFix(String fixId) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, DiffInfo> getFixPreview(String fixId) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public BinaryResult submitPreview() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public BinaryResult submitPreview(String format) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public SubmitType submitType() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public SubmitType testSubmitType(TestSubmitRuleInput in) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public TestSubmitRuleInfo testSubmitRule(TestSubmitRuleInput in) {
    throw new UnsupportedOperationException("This is a dummy test class");
  }
}
