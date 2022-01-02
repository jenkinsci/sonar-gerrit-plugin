# 2.4.5

## Bugs Fixed

[55](https://github.com/jenkinsci/sonar-gerrit-plugin/issues/55) - A password field is stored in plain text

# 2.3

2 Apr 2018

## Bugs Fixed

[JENKINS-49639](https://issues.jenkins.io/browse/JENKINS-49639) - Getting issue details... STATUS

# 2.2.1

5 Feb 2018

## Bugs Fixed

[JENKINS-49172](https://issues.jenkins.io/browse/JENKINS-49172) - Getting issue details... STATUS

# 2.1

6 Jan 2018

## Bugs Fixed

1. [JENKINS-43730](https://issues.jenkins-ci.org/browse/JENKINS-43730) - No issues reported if there are no Project Settings configurations

## New Features

1. [JENKINS-48808](https://issues.jenkins-ci.org/browse/JENKINS-48808) - Allow file name auto-match for projects with several modules
2. [JENKINS-48807](https://issues.jenkins-ci.org/browse/JENKINS-48807) - Implement separate filter for score settings

# 2.0

24 Apr 2017

## New Features

1. [JENKINS-43397](https://issues.jenkins-ci.org/browse/JENKINS-43397) - Support pipelines (initial)

# 1.0.8

6 Apr 2017

## Bugs Fixed

1. [JENKINS-43093](https://issues.jenkins-ci.org/browse/JENKINS-43093) - Replace NPE stacktrace with message in case when Gerrit Change and Patchset numbers are not set
2. [JENKINS-43047](https://issues.jenkins-ci.org/browse/JENKINS-43047) - Fix issue processing for nested modules
3. [JENKINS-42465](https://issues.jenkins-ci.org/browse/JENKINS-42465) - Fix LDAP lockout when using Gerrit HTTP password

## New Features

1. [JENKINS-40970](https://issues.jenkins-ci.org/browse/JENKINS-40970) - Add an option to override Gerrit HTTP credentials
2. [JENKINS-31240](https://issues.jenkins-ci.org/browse/JENKINS-31240) - UI: Hide "Score Settings" section if "Post scor" is not checked

# 1.0.7.6

10 Nov 2016

## New Features

1. [JENKINS-33892](https://issues.jenkins-ci.org/browse/JENKINS-33892) - Add details of RestAPIException to a log

# 1.0.6

3 Dec 2015

## New Features

1. [JENKINS-31892](https://issues.jenkins-ci.org/browse/JENKINS-31892) - Support multiple project locations for multi-jobs

# 1.0.5

18 Nov 2015

## Bugs Fixed

1. [JENKINS-31238](https://issues.jenkins-ci.org/browse/JENKINS-31238) - Replace "Sonar" with "SonarQube" in plugin ui

## New Features

1. [JENKINS-31639](https://issues.jenkins-ci.org/browse/JENKINS-31639) - Notify user about plugin changes causing incompatibility
2. [JENKINS-31003](https://issues.jenkins-ci.org/browse/JENKINS-31003) - Move plugin from post-build steps to post-build actions

# 1.0.4

24 Oct 2015

## Bugs Fixed

1. [JENKINS-31001](https://issues.jenkins-ci.org/browse/JENKINS-31001) - Unable to save changes for Filter settings

## New Features

1. [JENKINS-31006](https://issues.jenkins-ci.org/browse/JENKINS-31006) - Allow user to specify Gerrit category and post score under it.
2. [JENKINS-31005](https://issues.jenkins-ci.org/browse/JENKINS-31005) - Move some settings to Advanced section
3. [JENKINS-31004](https://issues.jenkins-ci.org/browse/JENKINS-31004) - Check if Gerrit RESTAPI is enabled

# 1.0.3

13 Oct 2015

## Bugs Fixed

1. [JENKINS-30932](https://issues.jenkins-ci.org/browse/JENKINS-30932) - Localisation is failed on error messages
2. [JENKINS-30933](https://issues.jenkins-ci.org/browse/JENKINS-30933) - NPE on attempt to run job with no SonarQube execution configured

# 1.0.2

13 Oct 2015

## New Features

1. [JENKINS-30915](https://issues.jenkins-ci.org/browse/JENKINS-30915) - Support plugin run in downstream jobs

# 1.0.1

9 Oct 2015

## New Features

1. [JENKINS-30853](https://issues.jenkins-ci.org/browse/JENKINS-30853) - Support projects located in subdirectories of repository root directory.

## Bugs Fixed

1. [JENKINS-30863](https://issues.jenkins-ci.org/browse/JENKINS-30863) - Unable to run plugin: NoSuchMethod Error: GerritTrigger.getTrigger

# 1.0

7 Oct 2015

Basic functionality
