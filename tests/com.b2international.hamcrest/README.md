# Compatibility bundle for Hamcrest 2

## Why is this needed at all?

REST-assured depends on Hamcrest 2, but JUnit 4 has a direct dependency on version 1 of the `org.hamcrest.core` bundle. This is not going to go away any time soon, according to [this comment](https://github.com/junit-team/junit4/issues/1665#issuecomment-753528275):

> Our plan was to remove the dependency to Hamcrest completely (see [#1145](https://github.com/junit-team/junit4/issues/1145)). Unfortunately that is not easy. In the meantime JUnit Jupiter was released and we recommend developers to switch to JUnit Jupiter. Therefore the plan to remove Hamcrest is abandoned.
Changing the Hamcrest version in JUnit's dependency may cause problems and therefore it stays at 1.3. It is always possible to specify the version of Hamcrest in your pom.xml and override the version specified by JUnit.

...which is unfortunately only possible in Maven, where version 1 can be excluded entirely from the dependency tree. The JUnit 4 bundle shipped by Eclipse has a `Require-Bundle` version range attribute that restricts us from using version 2 of Hamcrest directly.

This small bundle wraps Hamcrest 2.0 and exposes its packages as version 2.2.0 (which REST-assured imports via `Import-Package` directives), however the bundle identifies itself as `org.hamcrest.core` version 1.99.0, which satisfies the requirements for JUnit 4.
