package org.jenkinsci.plugins.sonargerrit.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

/** @author Réda Housni Alaoui */
public class UrlBuilder {

  private final List<String> segments = new ArrayList<>();
  private final Map<String, List<String>> queryParameters = new HashMap<>();

  public UrlBuilder addSegment(String segment) {
    segments.add(StringUtils.removeEnd(StringUtils.removeStart(segment, "/"), "/"));
    return this;
  }

  public UrlBuilder addQueryParameter(String key, String value) {
    queryParameters.computeIfAbsent(key, s -> new ArrayList<>()).add(value);
    return this;
  }

  public String build() {
    String baseUrl = String.join("/", segments);
    if (queryParameters.isEmpty()) {
      return baseUrl;
    }
    String queryParams =
        queryParameters.entrySet().stream()
            .flatMap(this::buildQueryParam)
            .collect(Collectors.joining("&"));
    return baseUrl + "?" + queryParams;
  }

  private Stream<String> buildQueryParam(Map.Entry<String, List<String>> entry) {
    return entry.getValue().stream().map(value -> entry.getKey() + "=" + urlEncode(value));
  }

  private String urlEncode(String value) {
      return URLEncoder.encode(value, StandardCharsets.UTF_8);
  }
}
