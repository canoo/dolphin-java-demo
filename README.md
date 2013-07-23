Dolphin Java Demo
===================

Pure Java Demos for the use of Open Dolphin.
Open Dolphin is used as an external dependency.
For documentation on Open Dolphin see http://open-dolphin.org

Prerequisites
-------------
If you use Java 7u6 or later, you are all set.
Otherwise, JAVAFX_HOME must be set to a 2.1.0 version. (Version 2.2 also works fine.)

Run an initial Demo (LazyLoadingDemo)
> gradlew LayLoadingDemo

To see a choice of demos use:
> gradlew listDemos


How to build
------------
> gradlew clean build

To see a choice of all available gradle tasks use:
> gradlew tasks


Purpose
-------
Showing how to use Open Dolphin with pure Java
- We choose Java FX as UI Technology for our demos but there might be other clients in the future


Project layout
--------------
The multi-project build consists of these subprojects

- shared (code that is needed on both client and server)
- client (visualization)
- server (domain model and control logic)
- combined (for testing the client-server combination)
