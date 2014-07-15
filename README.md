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

    cd plugin-eclipse
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

1. Clone the project with the Git client of your choice (integrated Eclipse eGit
or Git command line tool for instance),
2. Import cloned project in Eclipse as general project,
3. For a Luna target, open 4.4.target under *com.codenvy.eclipse.target*
(relative to project root),
4. Click on *‘Set as Target Platform’* in the upper right corner of the editor
to set this as the default target platform,
5. Give the project the Maven nature through *‘Configure → Convert to Maven
Project’*,
6. Open an Eclipse import wizard and chose *‘Existing Maven Projects…’*,
7. Wait for the project to be configured,
8. As root directory, set the location of the root project,
9. Check all sub-modules and click *‘Finish’*,
10. Wait for the projects to be configured.

Run the plugin
--------------

Once the project imported in Eclipse, right-click its sub-module
*com.codenvy.eclipse.ui* and chose *‘Run as → Eclipse Application’*.

License
-------

Copyright (c) 2012-2014 Codenvy, S.A.

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

Contributors:
	Codenvy, S.A. - initial API and implementation
 