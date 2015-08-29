package com.aquarellian.genar.jenkins;

import com.aquarellian.genar.data.SonarReportBuilder;
import com.aquarellian.genar.data.entity.Issue;
import com.aquarellian.genar.data.entity.Report;
import com.aquarellian.genar.filter.FilterEngine;
import com.aquarellian.genar.filter.FilterParser;
import com.aquarellian.genar.filter.model.Filter;
import hudson.EnvVars;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.google.common.base.MoreObjects.*;

/**
 * Sample {@link Builder}.
 * <p/>
 * <p/>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked
 * and a new {@link SonarToGerritBuilder} is created. The created
 * instance is persisted to the project configuration XML by using
 * XStream, so this allows you to use instance fields (like {@link #path})
 * to remember the configuration.
 * <p/>
 * <p/>
 * When a build is performed, the {@link #perform(AbstractBuild, Launcher, BuildListener)}
 * method will be invoked.
 *
 * @author Kohsuke Kawaguchi
 */
@SuppressWarnings("unused")
public class SonarToGerritBuilder extends Builder {

    private static final String DEFAULT_PATH = "target/sonar/sonar-report.json";

    private final String path;

    // Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
    @DataBoundConstructor
    public SonarToGerritBuilder(String path) {
        this.path = firstNonNull(path, DEFAULT_PATH);
    }

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getPath() {
        return path;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        println(listener, getPath());
        println(listener, "Load issue filter");

        Filter f = null;
        try {
            f = new FilterParser().parseFilter("filter.xml");    //todo
            println(listener, "Issue filter loaded");
        } catch (JAXBException e) {
            println(listener, "Unable to load filter");
        }


        println(listener, "Load Sonar report for path = " + path);

        EnvVars envVars;
        envVars = build.getEnvironment(listener);
        String workspace = envVars.get("WORKSPACE");
        println(listener, "WORKSPACE = " + workspace);
        String fullPath = workspace + File.separator + path;
        println(listener, "fullpath = " + fullPath);
        listener.getLogger().println("GERRIT_HOST" + " ---> " + envVars.get("GERRIT_HOST"));
        listener.getLogger().println("GERRIT_PORT" + " ---> " + envVars.get("GERRIT_PORT"));
        try {
            String json = readFile(fullPath);
            Report rep = new SonarReportBuilder().fromJson(json);
            FilterEngine engine = new FilterEngine(f, rep.getIssues());
            for (Issue i : engine.getFiltered()) {
                println(listener, "Found issue: " + i.getKey());
            }
        } catch (IOException ex) {
            println(listener, String.format("Unable to open file '%s'", path));
        }



//        envVars = build.getEnvironment(listener);
//        for (String s : envVars.keySet()) {
//            String v = envVars.get(s);
//            listener.getLogger().println(s + "--->" + v);
//        }
//        listener.getLogger().println("sonar.surefire.reportsPath" + " ---> " + envVars.get("sonar.surefire.reportsPath"));
        listener.getLogger().println("GERRIT_HOST" + " ---> " + envVars.get("GERRIT_HOST"));
        listener.getLogger().println("GERRIT_PORT" + " ---> " + envVars.get("GERRIT_PORT"));      //"target/sonar/sonar-report.json"
        listener.getLogger().println("");
        return true;
    }

    private void println(BuildListener listener, String format) {
        listener.getLogger().println(format);
    }


    private String readFile(String file) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(file), Charset.defaultCharset());
        StringBuilder sb = new StringBuilder();
        for (String s : lines) {
            sb.append(s);
        }
        return sb.toString();

    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    /**
     * Descriptor for {@link SonarToGerritBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     * <p/>
     * <p/>
     * See <tt>src/main/resources/hudson/plugins/hello_world/SonarToGerritBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension // This indicates to Jenkins that this is an implementation of an extension point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * To persist global configuration information,
         * simply store it in a field and call save().
         * <p/>
         * <p/>
         * If you don't want fields to be persisted, use <tt>transient</tt>.
         */
        private boolean useFrench;

        /**
         * In order to load the persisted global configuration, you have to
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'path'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p/>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        public FormValidation doCheckPath(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please set a path");
            if (value.length() < 4)
                return FormValidation.warning("Isn't the path too short?");
            return FormValidation.ok();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types
            return true;
        }

        /**
         * This human readable path is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Send Sonar results to Gerrit";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            // To persist global configuration information,
            // set that to properties and call save().
            useFrench = formData.getBoolean("useFrench");
            // ^Can also use req.bindJSON(this, formData);
            //  (easier when there are many fields; need set* methods for this, like setUseFrench)
            save();
            return super.configure(req, formData);
        }

        /**
         * This method returns true if the global configuration says we should speak French.
         * <p/>
         * The method name is bit awkward because global.jelly calls this method to determine
         * the initial state of the checkbox by the naming convention.
         */
        public boolean getUseFrench() {
            return useFrench;
        }
    }
}

