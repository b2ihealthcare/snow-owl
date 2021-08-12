# <a href='https://docs.b2i.sg/snow-owl/'><img src='logo/logo-title.png' height='80' alt='Snow Owl Logo' /></a>

Snow Owl<sup>&reg;</sup> is a highly scalable, open source terminology server with revision-control capabilities and collaborative authoring platform features. It allows you to store, search and author high volumes of terminology artifacts quickly and efficiently.

[![build status](https://img.shields.io/github/workflow/status/b2ihealthcare/snow-owl/Java%20CI/8.x?style=flat-square)](https://github.com/b2ihealthcare/snow-owl/actions)
[![latest release](https://img.shields.io/github/tag/b2ihealthcare/snow-owl.svg?style=flat-square)](https://github.com/b2ihealthcare/snow-owl/releases/tag/v7.17.3)
[![downloads](https://img.shields.io/github/downloads/b2ihealthcare/snow-owl/total.svg?style=flat-square)](https://github.com/b2ihealthcare/snow-owl/releases/)
[![Docker](https://img.shields.io/docker/pulls/b2ihealthcare/snow-owl-oss?style=flat-square)](https://hub.docker.com/r/b2ihealthcare/snow-owl-oss)
[![GitHub](https://img.shields.io/github/license/b2ihealthcare/snow-owl.svg?style=flat-square)](https://github.com/b2ihealthcare/snow-owl/blob/8.x/LICENSE)
[![grade: Java](https://img.shields.io/lgtm/grade/java/g/b2ihealthcare/snow-owl.svg?logo=lgtm&logoWidth=18&style=flat-square)](https://lgtm.com/projects/g/b2ihealthcare/snow-owl/context:java)
[![alerts](https://img.shields.io/lgtm/alerts/g/b2ihealthcare/snow-owl.svg?logo=lgtm&logoWidth=18&style=flat-square)](https://lgtm.com/projects/g/b2ihealthcare/snow-owl/alerts/)
[![codecov](https://codecov.io/gh/b2ihealthcare/snow-owl/branch/8.x/graph/badge.svg?style=flat-square)](https://codecov.io/gh/b2ihealthcare/snow-owl)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fb2ihealthcare%2Fsnow-owl.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fb2ihealthcare%2Fsnow-owl?ref=badge_shield)

# Introduction

Features include:
* Revision-controlled authoring
    * Maintains multiple versions (including unpublished and published) for each terminology artifact and provides APIs to access them all
    * Independent work branches offer work-in-process isolation, external business workflow integration and team collaboration
* SNOMED CT and others
    * SNOMED CT terminology support
        * RF2 Release File Specification as of 2021-07-31
        * :new: Support for Relationships with concrete values
        * Official and Custom Reference Sets
        * Expression Constraint Language v1.4.0 [spec](https://confluence.ihtsdotools.org/download/attachments/33493263/doc_ExpressionConstraintLanguage_v1.4-en-US_INT_20200617.pdf%09?api=v2), [implementation](https://github.com/b2ihealthcare/snomed-ecl)
        * Compositional Grammar 2.3.1 [spec](https://confluence.ihtsdotools.org/download/attachments/33496020/doc_CompositionalGrammar_v2.3.1-en-US_INT_20161118.pdf?api=v2), [implementation](https://github.com/b2ihealthcare/snomed-scg)
        * Expression Template Language 1.0.0 [spec](https://confluence.ihtsdotools.org/download/attachments/45529301/doc_TemplateSyntax_v1.0-en-US_INT_20170726.pdf?api=v2), [implementation](https://github.com/b2ihealthcare/snomed-etl)
        * Query Language Draft 0.1.0 [draft](https://confluence.ihtsdotools.org/download/attachments/52180605/Draft%20SNOMED%20Query%20Language%20-%20Examples%20for%20Discussion.pptx?api=v2), [implementation](https://github.com/b2ihealthcare/snomed-ql)
    * With its modular design, the server can maintain multiple terminologies (including local codes, mapping sets, value sets)
* Various set of APIs
    * SNOMED CT API (RESTful and native Java API)
    * FHIR API R4 v4.0.1 [spec](https://www.hl7.org/fhir/history.html#v4.0.1)
    * CIS API 1.0 [see reference implementation](https://github.com/IHTSDO/component-identifier-service)
* Highly extensible and configurable
    * Simple to use plug-in system makes it easy to develop and add new terminology tooling/API or any other functionality
* Built on top of [Elasticsearch](https://www.elastic.co/products/elasticsearch) (highly scalable, distributed, open source search engine)
    * Connect to your existing cluster or use the embedded instance
    * All the power of Elasticsearch is available (full-text search support, monitoring, analytics and many more)

# Download

* [WINDOWS](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/7.17.3/com.b2international.snowowl.server.update-7.17.3-oss.zip) - [sha](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/7.17.3/com.b2international.snowowl.server.update-7.17.3-oss.zip.sha1)
* [MACOS/LINUX](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/7.17.3/com.b2international.snowowl.server.update-7.17.3-oss.tar.gz) - [sha](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/7.17.3/com.b2international.snowowl.server.update-7.17.3-oss.tar.gz.sha1) 
* [RPM](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/7.17.3/com.b2international.snowowl.server.update-7.17.3-rpm.rpm) - [sha](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/7.17.3/com.b2international.snowowl.server.update-7.17.3-rpm.rpm.sha1)
* [DEB](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/7.17.3/com.b2international.snowowl.server.update-7.17.3-deb.deb) - [sha](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/7.17.3/com.b2international.snowowl.server.update-7.17.3-deb.deb.sha1)

{% hint style="info" %}
This distribution only includes features licensed under the Apache 2.0 license. To get access to the full set of features, please contact [B2i Healthcare](mailto:info@b2i.sg).
{% endhint %}

View the detailed release notes [here](https://github.com/b2ihealthcare/snow-owl/releases/tag/v7.17.3).

Not the version you're looking for? View [past releases](https://github.com/b2ihealthcare/snow-owl/releases).

### Install and Run

NOTE: You need to have a recent version of Java installed (Java 11+, https://adoptopenjdk.net/).

Once you have downloaded the appropriate package:

* Run `bin/snowowl.sh` on unix, or `bin/snowowl.bat` on windows
* Run `curl http://localhost:8080/snowowl/admin/info`
* Navigate to `http://localhost:8080/snowowl`
* See [SNOMED CT API docs](https://docs.b2i.sg/snow-owl/api/snomed), [FHIR API docs](https://docs.b2i.sg/snow-owl/api/fhir)

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

Snow Owl is an Equinox-OSGi based server. To develop plug-ins for Snow Owl you need to use Eclipse as IDE: 
* Download Eclipse IDE for Eclipse Committers 2020-09 package from here: https://www.eclipse.org/downloads/packages/release/2020-09/r/eclipse-ide-eclipse-committers

Required Eclipse plug-ins (install the listed features via `Help` -> `Install New Software...`):

*Note: you may have to untick the `Show only the latest versions of the available software` checkbox to get older versions of a feature. Please use the exact version specified below, not the latest point release.*

* Xtext/Xtend (https://download.eclipse.org/modeling/tmf/xtext/updates/releases/2.23.0/)
  * Xtend IDE 2.23.0 (Xtext)
  * Xtext Complete SDK 2.23.0 (Xtext)
* MWE2 (https://download.eclipse.org/modeling/emft/mwe/updates/releases/2.11.3/)
  * MWE SDK	1.5.3 (MWE)
  * MWE 2 language SDK 2.11.3 (MWE)
* Groovy Development Tools (https://dist.springsource.org/release/GRECLIPSE/3.9.0/e4.17)
  * Eclipse Groovy Development Tools 3.9.0 (Main Package)
* M2Eclipse (https://download.eclipse.org/technology/m2e/releases/latest/)
  * m2e 1.17.2
  * m2e PDE 1.17.2
 
### Eclipse Preferences

Make sure you have the following preferences enabled/disabled.
* Plug-in development API baseline errors is set to Ignored (Preferences > Plug-in Development > API Baselines)
* The *Plugin execution not covered by lifecycle configuration: org.apache.maven.plugins:maven-clean-plugin:2.5:clean* type of errors can be ignored or changed to *Warnings* in *Preferences->Maven->Errors/Warnings*.
* Set the workspace encoding to *UTF-8* (Preferences->General->Workspace)
* Set the line endings to *Unix* style (Preferences->General->Workspace)

### Git configuration

* Make sure the Git line endings are set to *input* (Preferences->Team->Git->Configuration - add key if missing *core.autocrlf = input*)

### Maven Settings

* Make sure the `settings.xml` in your ~/.m2/settings.xml location is updated with the content from the `settings.xml` from this repository's root folder.

## First steps

1. Import all projects into your Eclipse workspace and wait for the build to complete
2. Select all projects and hit `Alt` + `F5` and trigger an update to all Maven projects manually (to download dependencies from Maven)
3. Open the `target-platform/target-platform-local.target` file
4. Wait until Eclipse resolves the target platform (click on the `Resolve` button if it refuses to do so) and then click on `Set as Active Target platform`
5. Wait until the build is complete and you have no compile errors
6. Launch `snow-owl-oss` launch configuration in the Run Configurations menu
7. Navigate to `http://localhost:8080/snowowl`

# Contributing

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details.

# Versioning

Our [releases](https://github.com/b2ihealthcare/snow-owl/releases) use [semantic versioning](http://semver.org). You can find a chronologically ordered list of notable changes in [CHANGELOG.md](CHANGELOG.md).

# License

This project is licensed under the Apache 2.0 License. See [LICENSE](LICENSE) for details and refer to [NOTICE](NOTICE) for additional licensing notes and uses of third-party components.


[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fb2ihealthcare%2Fsnow-owl.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Fb2ihealthcare%2Fsnow-owl?ref=badge_large)

# Acknowledgements

In March 2015, [SNOMED International](http://snomed.org) generously licensed the Snow Owl Terminology Server components supporting SNOMED CT. They subsequently made the licensed code available to their [members](https://www.snomed.org/our-stakeholders/members) and the global community under an open-source license.

In March 2017, [NHS Digital](https://digital.nhs.uk) licensed the Snow Owl Terminology Server to support the mandatory adoption of SNOMED CT throughout all care settings in the United Kingdom by April 2020. In addition to driving the UK’s clinical terminology efforts by providing a platform to author national clinical codes, Snow Owl will support the maintenance and improvement of the dm+d drug extension which alone is used in over 156 million electronic prescriptions per month. Improvements to the terminology server made under this agreement will be made available to the global community. 

Many other organizations have directly and indirectly contributed to Snow Owl, including: Singapore Ministry of Health; American Dental Association; University of Nebraska Medical Center (USA); Federal Public Service of Public Health (Belgium); Danish Health Data Authority; Health and Welfare Information Systems Centre (Estonia); Department of Health (Ireland); New Zealand Ministry of Health; Norwegian Directorate of eHealth; Integrated Health Information Systems (Singapore); National Board of Health and Welfare (Sweden); eHealth Suisse (Switzerland); and the National Library of Medicine (USA).
