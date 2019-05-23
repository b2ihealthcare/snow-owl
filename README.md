# <a href='https://docs.b2i.sg/snow-owl/'><img src='logo/logo-title.png' height='80' alt='Snow Owl Logo' /></a>

Snow Owl<sup>&reg;</sup> is a highly scalable, open source terminology server with revision-control capabilities and collaborative authoring platform features. It allows you to store, search and author high volumes of terminology artifacts quickly and efficiently.

[![build status](https://img.shields.io/travis/b2ihealthcare/snow-owl/7.x.svg?style=flat-square)](https://travis-ci.org/b2ihealthcare/snow-owl)
[![latest release](https://img.shields.io/github/tag/b2ihealthcare/snow-owl.svg?style=flat-square)](https://github.com/b2ihealthcare/snow-owl/releases/tag/v7.0.0)
[![downloads](https://img.shields.io/github/downloads/b2ihealthcare/snow-owl/total.svg?style=flat-square)](https://github.com/b2ihealthcare/snow-owl/releases/)
[![GitHub](https://img.shields.io/github/license/b2ihealthcare/snow-owl.svg?style=flat-square)](https://github.com/b2ihealthcare/snow-owl/blob/7.x/LICENSE)
[![grade: Java](https://img.shields.io/lgtm/grade/java/g/b2ihealthcare/snow-owl.svg?logo=lgtm&logoWidth=18&style=flat-square)](https://lgtm.com/projects/g/b2ihealthcare/snow-owl/context:java)
[![alerts](https://img.shields.io/lgtm/alerts/g/b2ihealthcare/snow-owl.svg?logo=lgtm&logoWidth=18&style=flat-square)](https://lgtm.com/projects/g/b2ihealthcare/snow-owl/alerts/)
[![codecov](https://codecov.io/gh/b2ihealthcare/snow-owl/branch/7.x/graph/badge.svg?style=flat-square)](https://codecov.io/gh/b2ihealthcare/snow-owl)

# Introduction

Features include:
* Revision-controlled authoring
    * Maintains multiple versions (including unpublished and published) for each terminology artifact and provides APIs to access them all
    * Independent work branches offer work-in-process isolation, external business workflow integration and team collaboration
* SNOMED CT and others
    * Full SNOMED CT terminology support (full RF2 support, ECL v1.3, Reference Sets, OWL 2 EL/DL support)
    * With its modular design, the server can maintain multiple terminologies (including local codes, mapping sets, value sets)
* Various set of APIs
    * HTTP RESTful API
    * FHIR API
    * Native Java API
* Highly extensible and configurable
    * Simple to use plug-in system makes it easy to develop and add new terminology tooling/API or any other functionality
* Built on top of [Elasticsearch](https://www.elastic.co/products/elasticsearch) (highly scalable, distributed, open source search engine)
    * Connect to your existing cluster or use the embedded instance
    * All the power of Elasticsearch is available (full-text search support, monitoring, analytics and many more)

# Download

* [WINDOWS](https://github.com/b2ihealthcare/snow-owl/releases/download/v7.0.0/snow-owl-oss-7.0.0.zip) - [sha](https://github.com/b2ihealthcare/snow-owl/releases/download/v7.0.0/snow-owl-oss-7.0.0.zip.sha512)
* [MACOS/LINUX](https://github.com/b2ihealthcare/snow-owl/releases/download/v7.0.0/snow-owl-oss-7.0.0.tar.gz) - [sha](https://github.com/b2ihealthcare/snow-owl/releases/download/v7.0.0/snow-owl-oss-7.0.0.tar.gz.sha512) 
* [RPM](https://github.com/b2ihealthcare/snow-owl/releases/download/v7.0.0/snow-owl-oss-7.0.0.rpm) - [sha](https://github.com/b2ihealthcare/snow-owl/releases/download/v7.0.0/snow-owl-oss-7.0.0.rpm.sha512)
* DEB (Coming soon!)

{% hint style="info" %}
This distribution only includes features licensed under the Apache 2.0 license. To get access to the full set of features, please contact [B2i Healthcare](mailto:info@b2i.sg).
{% endhint %}

View the detailed release notes [here](https://github.com/b2ihealthcare/snow-owl/releases/tag/v7.0.0).

Not the version you're looking for? View [past releases](https://github.com/b2ihealthcare/snow-owl/releases).

### Install and Run

NOTE: You need to have a recent version of Java installed (Java 8 update 171 or newer).

Once you have downloaded the appropriate package:

* Run `bin/startup` on unix, or `bin/startup.bat` on windows
* Run `curl http://localhost:8080/snowowl/admin/info`
* See [REST API docs](http://localhost:8080/snowowl/snomed-ct/v3), [FHIR API docs](http://localhost:8080/snowowl/fhir)

# Learn Snow Owl

* [Getting Started](docs/getting_started/index.md)
* [Set up Snow Owl](docs/setup/index.md)
* [Configuring Snow Owl](docs/setup/configure/index.md)
* [FHIR API](docs/api/fhir/index.md)
* [SNOMED CT API](docs/api/snomed/index.md)
* [Admin API](docs/api/admin/index.md)

# Building from source

Snow Owl uses Maven for its build system. In order to create a distribution, simply run the following command in the cloned directory. 

    mvn clean package

The distribution packages can be found in the `releng/com.b2international.snowowl.server.update/target` folder, when the build is complete.

To run the test cases, use the following command:

    mvn clean verify

# Development

These instructions will get Snow Owl up and running on your local machine for development and testing purposes.

## Prerequisites

Snow Owl is an Equinox-OSGi based server (using either Virgo or standalone OSGi). To develop plug-ins for Snow Owl you need to use Eclipse as IDE: 
* Use latest Eclipse IDE for Eclipse Committers package: http://www.eclipse.org/downloads/eclipse-packages/

Required Eclipse plug-ins (install the listed features via `Help` -> `Install New Software...`):

*Note: you may have to untick the `Show only the latest versions of the available software` checkbox to get older versions of a feature. Please use the exact version specified below, not the latest point release.*

* Xtext/Xtend (http://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/)
  * MWE 2 language SDK 2.9.0 (MWE)
  * Xtend IDE 2.11.0 (Xtext)
  * Xtext Complete SDK 2.11.0 (Xtext)
* Maven integration (http://download.eclipse.org/technology/m2e/releases) 
 
### Eclipse Preferences

Make sure you have the following preferences enabled/disabled.
* Plug-in development API baseline errors is set to Ignored (Preferences > Plug-in Development > API Baselines)
* The *Plugin execution not covered by lifecycle configuration: org.apache.maven.plugins:maven-clean-plugin:2.5:clean* type of errors can be ignored or changed to *Warnings* in *Preferences->Maven->Errors/Warnings*.
* Set the workspace encoding to *UTF-8* (Preferences->General->Workspace)
* Set the line endings to *Unix* style (Preferences->General->Workspace)

### Git configuration

* Make sure the Git line endings are set to *input* (Preferences->Team->Git->Configuration - add key if missing *core.autocrlf = input*)

## First steps

1. Import all projects into your Eclipse workspace and wait for the build to complete
2. Select all projects and hit `Alt` + `F5` and trigger an update to all Maven projects manually (to download dependencies from Maven)
3. Open the `target-platform/target-platform-local.target` file
4. Wait until Eclipse resolves the target platform (click on the `Resolve` button if it refuses to do so) and then click on `Set as Active Target platform`
5. Wait until the build is complete and you have no compile errors
6. Launch `snow-owl-oss` launch configuration in the Run Configurations menu
7. Navigate to `http://localhost:8080/snowowl/snomed-ct/v3`

# Contributing

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details.

# Versioning

Our [releases](https://github.com/b2ihealthcare/snow-owl/releases) use [semantic versioning](http://semver.org). You can find a chronologically ordered list of notable changes in [CHANGELOG.md](CHANGELOG.md).

# License

This project is licensed under the Apache 2.0 License. See [LICENSE](LICENSE) for details and refer to [NOTICE](NOTICE) for additional licensing notes and uses of third-party components.

# Acknowledgements

In March 2015, [SNOMED International](http://snomed.org) generously licensed the Snow Owl Terminology Server components supporting SNOMED CT. They subsequently made the licensed code available to their [members](http://www.snomed.org/members) and the global community under an open-source license.

In March 2017, [NHS Digital](https://digital.nhs.uk) licensed the Snow Owl Terminology Server to support the mandatory adoption of SNOMED CT throughout all care settings in the United Kingdom by April 2020. In addition to driving the UK’s clinical terminology efforts by providing a platform to author national clinical codes, Snow Owl will support the maintenance and improvement of the dm+d drug extension which alone is used in over 156 million electronic prescriptions per month. Improvements to the terminology server made under this agreement will be made available to the global community. 
