package com.aquarellian.genar.trying;

import com.aquarellian.genar.data.SonarReportBuilder;
import com.aquarellian.genar.data.entity.interfaces.SonarReport;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.SonarClient;
import org.sonar.wsclient.services.Property;
import org.sonar.wsclient.services.PropertyQuery;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class TestConnection {

    public static Sonar localSonar;

    private static SonarClient sonarClient;

    public static Property sonarStartTime;
    public static PropertyQuery findStartTime;
    public static Property testProperty;
    public static PropertyQuery findTestProperty;
    public static String testKey = "testKey";
    public static String testValue = "testValue";


    /**
     * @param args
     */
    public static void main(String[] args) {
        String json = readFile("/Users/aquarellian/Documents/work/genar/genar/src/main/resources/example/example.json");
        SonarReport rep = new SonarReportBuilder().fromJson(json);

        //localSonar = Sonar.create("http://localhost:9000");//pointed to my instance of Sonar

        //EDIT: using this line instead, but it still gives the same stack trace.
//        localSonar = Sonar.create("http://http://ecsc001006fa.epam.com:9000", "admin", "admin");//pointed to my instance of Sonar
        localSonar = Sonar.create("http://http://ecsc001006fa.epam.com:9000");//pointed to my instance of Sonar

        sonarClient = SonarClient.builder()
//                .readTimeoutMilliseconds(READ_TIMEOUT_IN_MILLISECONDS)
//                .connectTimeoutMilliseconds(CONNECT_TIMEOUT_IN_MILLISECONDS)
//                .url(host.getHost())
//                .login(host.getUsername())
//                .password(host.getPassword())
                .build();

        findStartTime = PropertyQuery.createForKey("sonar.core.startTime");//creating query for a key I know exists
        sonarStartTime = localSonar.find(findStartTime);//retrieve property object from my Sonar's database
        System.out.println(sonarStartTime);//print out this object
    }

    private static String readFile(String file){
        try {
            List<String> lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
            StringBuilder sb = new StringBuilder();
            for (String s: lines){
              sb.append(s);
            }
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }

//    public List<Issue> getAllIssuesFor(String resourceKey) {
////        final List<Issue> builder = Collections.unmodifiableList() ;
//        final List<Issue> list = new ArrayList<Issue>();
//        IssueQuery query = IssueQuery.create()
//                .componentRoots(resourceKey)
//                .resolved(false)
//                .pageSize(-1);
//        Issues issues = sonarClient.issueClient().find(query);
//        list.addAll(issues.list());
//        for (int pageIndex = 2; pageIndex <= issues.paging().pages(); pageIndex++) {
//            query = IssueQuery.create()
//                    .componentRoots(resourceKey)
//                    .resolved(false)
//                    .pageSize(-1)
//                    .pageIndex(pageIndex);
//            issues = sonarClient.issueClient().find(query);
//            list.addAll(issues.list());
//        }
//        return Collections.unmodifiableList(list);
//    }
}