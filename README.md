# Snow Owl

## Legal

Public domain:
Base64.java

IBM EPL in B2i plugins:
MacOSXDebugVMRunner.java
MacOSXVMInstall.java
MacOSXVMInstallType.java
MacOSXVMRunner.java

EPL:
SerializableMultiStatus.java

GNU LGPL:
StatementMap.java

## Build

Snow Owl uses Maven for its build system.

In order to create a distribution, simply run the `mvn clean package -Pdependencies -Psite -Pdist` command in the cloned directory.

To run the test cases, simply run:

    mvn clean verify -Pdependencies -Psite -Pdist

The distribution package can be found in the `releng/distribution/target` folder, when the build completes.
