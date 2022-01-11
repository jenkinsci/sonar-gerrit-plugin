package org.jenkinsci.plugins.sonargerrit.sonar;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

/**
 * Processes data like below to build file path of a Sonar component.
 *
 * <pre>
 *
 * {
 * "key": "com.megaproject.juice:juice-bootstrap",
 * "path": "juice-bootstrap"
 * },
 * {
 * "key": "com.megaproject.juice:juice-bootstrap:src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java",
 * "path": "src/main/java/com/turquoise/juice/bootstrap/plugins/ChildModule.java",
 * "moduleKey": "com.megaproject.juice:juice-bootstrap",
 * "status": "SAME"
 * }
 * </pre>
 */
@Restricted(NoExternalUse.class)
public class Components {

  private final Map<String, Node> nodes = Maps.newHashMap();

  public Components(final List<? extends Component> components) {
    checkNotNull(components);
    for (final Component c : components) {
      nodes.put(c.getKey(), new Node(c));
    }
  }

  public Optional<String> buildPrefixedPathForComponentWithKey(
      final String componentKey, final String prefix) {
    Node n = getNodeByComponentKey(componentKey);
    if (n != null) {
      connectAncestors(n);
      return Optional.of(n.buildPrefixedPath(Strings.nullToEmpty(prefix)));
    }
    return Optional.absent();
  }

  private void connectAncestors(final Node n) {
    // already connected
    if (n.isParentConnected()) {
      return;
    }
    final Optional<Node> parent = findParent(n);
    n.setParent(parent);
    if (parent.isPresent()) {
      connectAncestors(parent.get());
    }
  }

  private Optional<Node> findParent(final Node n) {
    if (n.getComponent().getModuleKey() != null) {
      return findParentOfSourceFile(n);
    } else {
      return findParentOfModule(n);
    }
  }

  private Optional<Node> findParentOfSourceFile(final Node n) {
    return Optional.fromNullable(getNodeByComponentKey(n.getComponent().getModuleKey()));
  }

  private Optional<Node> findParentOfModule(final Node n) {
    final String componentKey = n.getComponent().getKey();
    final int moduleSeparatorPos = componentKey.lastIndexOf(':');
    if (moduleSeparatorPos < 0) {
      return Optional.absent();
    }
    final String parentKeyTemplate = componentKey.substring(0, moduleSeparatorPos);

    Node parent = getNodeByComponentKey(parentKeyTemplate);
    if (parent != null) {
      return Optional.of(parent);
    }
    // find parent by replacing *one* '.' with ':' in back-to-front order
    // e.g. "base.juice:events", "base:juice.events"
    int dotPosition = parentKeyTemplate.length();
    while (parent == null) {
      dotPosition = parentKeyTemplate.lastIndexOf('.', dotPosition - 1);
      if (dotPosition < 0) {
        return Optional.absent();
      }
      final String parentKey =
          parentKeyTemplate.substring(0, dotPosition)
              + ":"
              + parentKeyTemplate.substring(dotPosition + 1);
      parent = getNodeByComponentKey(parentKey);
    }
    return Optional.of(parent);
  }

  private Node getNodeByComponentKey(final String componentKey) {
    return nodes.get(componentKey);
  }

  private static class Node {
    private static final String GERRIT_FILE_DELIMITER = "/";

    private final Component component;

    private Node parent;

    public Node(final Component c) {
      checkNotNull(c);
      this.component = c;
    }

    private static void appendFileDelimiterIfNecessary(final StringBuilder path) {
      if (path.length() > 0 && !pathEndsWith(path, GERRIT_FILE_DELIMITER)) {
        path.append(GERRIT_FILE_DELIMITER);
      }
    }

    private static boolean pathEndsWith(final StringBuilder path, final String suffix) {
      final int excess = path.length() - suffix.length();
      if (excess < 0) {
        return false;
      }
      for (int i = 0; i < suffix.length(); i++) {
        if (path.charAt(excess + i) != suffix.charAt(i)) {
          return false;
        }
      }
      return true;
    }

    public String buildPrefixedPath(final String prefix) {
      final StringBuilder path = new StringBuilder(prefix);
      buildPath(path);
      return path.toString();
    }

    private void buildPath(final StringBuilder path) {
      if (isParentFound()) {
        getParent().buildPath(path);
      }
      final String thisPath = component.getPath();
      if (!Strings.isNullOrEmpty(thisPath)) {
        appendFileDelimiterIfNecessary(path);
        path.append(thisPath);
      }
    }

    public Component getComponent() {
      return component;
    }

    public Node getParent() {
      return parent;
    }

    public void setParent(final Optional<Node> parent) {
      checkNotNull(parent);
      this.parent = parent.or(this);
    }

    /** Answers whether attempt to connect this node to parent has been attempted/completed. */
    public boolean isParentConnected() {
      return parent != null;
    }

    /**
     * Answers whether a parent of this node has been found.
     *
     * @return {@code true} if parent has been found; {@code false} if parent has not been found or
     *     attempt has not been performed to find parent of this node
     */
    public boolean isParentFound() {
      return parent != null && parent != this;
    }
  }
}
