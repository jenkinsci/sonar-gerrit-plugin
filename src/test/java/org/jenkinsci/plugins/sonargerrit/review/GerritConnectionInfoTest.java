package org.jenkinsci.plugins.sonargerrit.review;

import com.sonyericsson.hudson.plugins.gerrit.trigger.hudsontrigger.GerritTrigger;
import junit.framework.Assert;
import org.jenkinsci.plugins.sonargerrit.config.GerritAuthenticationConfig;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 28.11.2017 23:06
 * <p>
 * $Id$
 */
public class GerritConnectionInfoTest {
    @Test(expected = NullPointerException.class)
    public void testNullServerName() {
        Map<String, String> envVars = createEnvVarsMap(null, "1", "1");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, null, null);
    }

    @Test
    public void testTriggerServerName() {
        Map<String, String> envVars = createEnvVarsMap(null, "1", "1");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, createDummyTrigger("test"), null);
        Assert.assertEquals("test", info.getServerName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyServerName() {
        Map<String, String> envVars = createEnvVarsMap("", "1", "1");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, null, null);
    }

    @Test
    public void testServerName() {
        Map<String, String> envVars = createEnvVarsMap("testo", "1", "1");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, createDummyTrigger("test"), null);
        Assert.assertEquals("testo", info.getServerName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullChangeNum() {
        Map<String, String> envVars = createEnvVarsMap("Test", null, "1");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyChangeNum() {
        Map<String, String> envVars = createEnvVarsMap("Test", "", "1");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongChangeNum() {
        Map<String, String> envVars = createEnvVarsMap("Test", "test", "1");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, null, null);
    }

    @Test
    public void testChangeNum() {
        Map<String, String> envVars = createEnvVarsMap("Test", "2", "1");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, null, null);
        Assert.assertEquals("2", info.getChangeNumber());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPatchsetNum() {
        Map<String, String> envVars = createEnvVarsMap("Test", "1", null);
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPatchsetNum() {
        Map<String, String> envVars = createEnvVarsMap("Test", "1", "");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongPatchsetNum() {
        Map<String, String> envVars = createEnvVarsMap("Test", "1", "test");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, null, null);
    }

    @Test
    public void testPatchsetNum() {
        Map<String, String> envVars = createEnvVarsMap("Test", "1", "3");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, null, null);
        Assert.assertEquals("3", info.getPatchsetNumber());
    }

    @Test
    public void testNullAuthConfig() {
        Map<String, String> envVars = createEnvVarsMap("Test", "1", "1");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, null, null);
        Assert.assertNull(info.getUsername());
        Assert.assertNull(info.getPassword());
    }

    @Test
    public void testAuthConfig() {
        Map<String, String> envVars = createEnvVarsMap("Test", "1", "1");
        GerritAuthenticationConfig authenticationConfig = new GerritAuthenticationConfig("tusername", "tpassword");
        GerritConnectionInfo info = new GerritConnectionInfo(envVars, null, authenticationConfig);
        Assert.assertEquals("tusername", info.getUsername());
        Assert.assertEquals("tpassword", info.getPassword());
    }

    private Map<String, String> createEnvVarsMap(String server, String change, String patchset) {
        Map<String, String> map = new HashMap<>();
        map.put(GerritConnectionInfo.GERRIT_NAME_ENV_VAR_NAME, server);
        map.put(GerritConnectionInfo.GERRIT_CHANGE_NUMBER_ENV_VAR_NAME, change);
        map.put(GerritConnectionInfo.GERRIT_PATCHSET_NUMBER_ENV_VAR_NAME, patchset);
        return map;
    }

    private GerritTrigger createDummyTrigger(final String name) {
        return new GerritTrigger(
                null, null, null, null, null, null, null, null, null, null,
                null, null, false, false, false, false, false, null, null, null,
                null, null, null, null, null, null, null, null, false, null, null) {
            @Override
            public String getServerName() {
                return name;
            }
        };
    }
}
