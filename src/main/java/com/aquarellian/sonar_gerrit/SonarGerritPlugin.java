package com.aquarellian.sonar_gerrit;

import hudson.Plugin;

import java.util.logging.Logger;

/**
 * Project: Sonar-Gerrit Plugin
 * Author:  Tatiana Didik
 * Created: 16.09.2015 21:27
 * <p/>
 * $Id$
 */
public class SonarGerritPlugin extends Plugin {
    private final static Logger LOG = Logger.getLogger(SonarGerritPlugin.class.getName());

    @Override
    public void start() throws Exception {
        LOG.info("SonarGerrit: sending SonarQube results to Gerrit");
    }
}
