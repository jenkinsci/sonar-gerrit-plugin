package org.jenkinsci.plugins.sonargerrit.gerrit;

/** @author Réda Housni Alaoui */
public enum ReviewCommentType {
  STANDARD("Standard"),
  ROBOT("Robot (since Gerrit 2.14)");

  private final String displayName;

  ReviewCommentType(String displayName) {
    this.displayName = displayName;
  }

  public String displayName() {
    return displayName;
  }
}
