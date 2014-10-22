Eclipse Plugin for Codenvy
==========================

Description
-----------

This project aims to provide an Eclipse plugin to give the ability to developers
to work either on Codenvy or in Eclipse, sharing the same code base without the
need of a Git repo.

Build the project with Maven
----------------------------

Prerequisites:

* a Maven installation, minimum version 3.2,
* a JDK, minimum version 7.

Steps:

1. Clone the project:

        git clone https://github.com/codenvy/eclipse-plugin.git

2. Execute the following commands:

        cd eclipse-plugin
        mvn clean install

**Warning:** It will launch UI integration tests with SWTBot, be careful not to
preempt focus when those tests are running to avoid a test failure.

Get this project working under Eclipse
--------------------------------------

Prerequisites:

* same as those of the Maven build,
* plus an Eclipse installation with m2e and PDE plug-ins installed,
* having a M2_REPO variable configured in Eclipse:
    1. In Eclipse preferences, go to *‘Run/Debug → String Substitution’*.
    2. Create a *‘New String Substitution Variable’* with `M2_REPO` as name and
    the path to your Maven local repository as value.

Steps:

1. Import the Git project as a Maven project in Eclipse (be careful to import
all its submodules too),
2. Wait for the projects to be configured (some errors should be reported on
projects, it's normal as target is not yet configured),
3. For a Luna target, open 4.4.target under *com.codenvy.eclipse.target*
(relative to project root),
4. Click on *‘Set as Target Platform’* in the upper right corner of the editor
to set this as the default target platform,
5. Update all the projects (the root one and its submodules) through *‘Maven →
Update Project…’*.

Run the plugin
--------------

Once the project imported in Eclipse, right-click its sub-module
*com.codenvy.eclipse.ui* and chose *‘Run as → Eclipse Application’*.

Release the plugin
------------------

1. Clone the project:

        git clone https://github.com/codenvy/eclipse-plugin.git
        cd eclipse-plugin

2. Prepare release:

        mvn tycho-versions:set-version -DnewVersion=${releaseVersion}
        mvn clean install
        git commit -am "Prepare release ${releaseVersion}"
        git push origin master

3. Tag released version:

        git tag -a ${releaseVersion} -m "Tag ${releaseVersion}"
        git push origin ${releaseVersion}

4. Prepare for next development iteration:

        mvn tycho-versions:set-version -DnewVersion=${nextDevelopmentVersion}
        mvn clean install
        git commit -am "Prepare for next development iteration"
        git push origin master

License
-------

Copyright (c) 2012-2014 Codenvy, S.A.

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

Contributors:
	Codenvy, S.A. - initial API and implementation

