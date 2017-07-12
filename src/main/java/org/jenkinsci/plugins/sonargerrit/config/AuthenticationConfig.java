package org.jenkinsci.plugins.sonargerrit.config;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormValidation;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 12.11.2017 21:43
 * $Id$
 */
public abstract class AuthenticationConfig extends AbstractDescribableImpl<AuthenticationConfig> {

    /*
    * Gerrit http username if overridden (the original one is in Gerrit Trigger settings)
    * todo needs to be replaced by Credentials plugin config
    * */
    @Nonnull
    private String username;

    /*
    * Gerrit http password if overridden (the original one is in Gerrit Trigger settings)
    * todo needs to be replaced by Credentials plugin config
    * */
    @Nonnull
    private String password;

    public AuthenticationConfig(@Nonnull String username, @Nonnull String password) {
        this.username = username;
        this.password = password;
    }

    @SuppressFBWarnings(value="NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
    public AuthenticationConfig() {
    }

    @Nonnull
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nonnull String username) {
        this.username = username;
    }

    @Nonnull
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nonnull String password) {
        this.password = password;
    }

    @Override
    public abstract DescriptorImpl getDescriptor();

    public static abstract class DescriptorImpl extends Descriptor<AuthenticationConfig> {
        /**
         * Performs on-the-fly validation of the form field 'username'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        public FormValidation doCheckUsername(@QueryParameter String value) {
            return FormValidation.validateRequired(value);
        }

        /**
         * Performs on-the-fly validation of the form field 'password'.
         *
         * @param value This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the browser.
         * <p>
         * Note that returning {@link FormValidation#error(String)} does not
         * prevent the form from being saved. It just means that a message
         * will be displayed to the user.
         */
        public FormValidation doCheckPassword(@QueryParameter String value) {
            return FormValidation.validateRequired(value);
        }

        public abstract FormValidation doTestConnection(@QueryParameter("username") final String username,
                                                        @QueryParameter("password") final String password,
                                                        @QueryParameter("serverName") final String serverName)
                throws IOException, ServletException;


        public abstract List<String> getServerNames();

        public String getDisplayName() {
            return "AuthenticationConfig";
        }
    }
}
