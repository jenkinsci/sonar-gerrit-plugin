package org.jenkinsci.plugins.sonargerrit;

import com.google.common.collect.ListMultimap;
import com.google.gerrit.extensions.api.changes.*;
import com.google.gerrit.extensions.client.ArchiveFormat;
import com.google.gerrit.extensions.client.SubmitType;
import com.google.gerrit.extensions.common.*;
import com.google.gerrit.extensions.restapi.BinaryResult;
import com.google.gerrit.extensions.restapi.RestApiException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
  public CommitInfo commit(boolean addLinks) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  private FileApi getFileApi(final String path) {
    return new FileApi() {
      @Override
      public BinaryResult content() throws RestApiException {
        throw new UnsupportedOperationException("This is a dummy test class");
      }

      @Override
      public DiffInfo diff() throws RestApiException {
        return generateDiffInfoByPath(path);
      }

      @Override
      public DiffInfo diff(String base) throws RestApiException {
        throw new UnsupportedOperationException("This is a dummy test class");
      }

      @Override
      public DiffInfo diff(int parent) throws RestApiException {
        throw new UnsupportedOperationException("This is a dummy test class");
      }

      @Override
      public DiffRequest diffRequest() throws RestApiException {
        throw new UnsupportedOperationException("This is a dummy test class");
      }

      @Override
      public void setReviewed(boolean reviewed) throws RestApiException {
        throw new UnsupportedOperationException("This is a dummy test class");
      }

      @Override
      public BlameRequest blameRequest() throws RestApiException {
        throw new UnsupportedOperationException("This is a dummy test class");
      }
    };
  }

  protected DiffInfo generateDiffInfoByPath(String path) {
    DiffInfo info = new DiffInfo();
    info.content = new ArrayList<DiffInfo.ContentEntry>();

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
          entry.a = new ArrayList<String>();
          entry.b = new ArrayList<String>();
        }
        entry.a.add(v + v);
        entry.b.add(v + v + v);
      } else {
        if (entry.ab == null) {
          entry.ab = new ArrayList<String>();
        }
        entry.ab.add(v);
      }
    }
    return entry;
  }

  @Override
  public void delete() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public String description() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public void description(String description) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public ReviewResult review(ReviewInput in) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public void submit() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public void submit(SubmitInput in) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public void publish() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public ChangeApi cherryPick(CherryPickInput in) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public ChangeInfo cherryPickAsInfo(CherryPickInput in) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public ChangeApi rebase() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public ChangeApi rebase(RebaseInput in) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public boolean canRebase() {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public RevisionReviewerApi reviewer(String id) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public void setReviewed(String path, boolean reviewed) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Set<String> reviewed() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, FileInfo> files() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, FileInfo> files(String base) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public MergeableInfo mergeable() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public MergeableInfo mergeableOtherBranches() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, List<CommentInfo>> comments() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, List<CommentInfo>> drafts() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public DraftApi createDraft(DraftInput in) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public DraftApi draft(String id) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public CommentApi comment(String id) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, ActionInfo> actions() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public List<CommentInfo> commentsAsList() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public List<CommentInfo> draftsAsList() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, FileInfo> files(int parentNum) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public List<String> queryFiles(String query) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public MergeListRequest getMergeList() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public RelatedChangesInfo related() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public ListMultimap<String, ApprovalInfo> votes() throws RestApiException {
    return null;
  }

  @Override
  public BinaryResult getArchive(ArchiveFormat format) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public BinaryResult patch() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public BinaryResult patch(String path) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public RobotCommentApi robotComment(String id) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public String etag() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, List<RobotCommentInfo>> robotComments() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public List<RobotCommentInfo> robotCommentsAsList() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public EditInfo applyFix(String fixId) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public Map<String, DiffInfo> getFixPreview(String fixId) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public BinaryResult submitPreview() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public BinaryResult submitPreview(String format) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public SubmitType submitType() throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public SubmitType testSubmitType(TestSubmitRuleInput in) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }

  @Override
  public TestSubmitRuleInfo testSubmitRule(TestSubmitRuleInput in) throws RestApiException {
    throw new UnsupportedOperationException("This is a dummy test class");
  }
}
