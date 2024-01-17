# <a href='https://docs.b2ihealthcare.com/snow-owl/'><img src='logo/logo-title.png' height='80' alt='Snow Owl Logo' /></a>

Snow Owl<sup>&reg;</sup> is a highly scalable, open source terminology server with revision-control capabilities and collaborative authoring platform features. It allows you to store, search and author high volumes of terminology artifacts quickly and efficiently.
If you’d like to see Snow Owl in action, the [Snowray Terminology Service™](https://snowray.app) provides a managed terminology server and high-quality terminology content management from your web browser.

[![build status](https://img.shields.io/github/actions/workflow/status/b2ihealthcare/snow-owl/maven.yml?branch=9.x&style=flat-square)](https://github.com/b2ihealthcare/snow-owl/actions)
[![latest release](https://img.shields.io/github/v/release/b2ihealthcare/snow-owl?style=flat-square)](https://github.com/b2ihealthcare/snow-owl/releases/tag/v9.0.0)
[![downloads](https://img.shields.io/github/downloads/b2ihealthcare/snow-owl/total.svg?style=flat-square)](https://github.com/b2ihealthcare/snow-owl/releases/)
[![Docker](https://img.shields.io/docker/pulls/b2ihealthcare/snow-owl-oss?style=flat-square)](https://hub.docker.com/r/b2ihealthcare/snow-owl-oss)
[![GitHub](https://img.shields.io/github/license/b2ihealthcare/snow-owl.svg?style=flat-square)](https://github.com/b2ihealthcare/snow-owl/blob/9.x/LICENSE)
[![codecov](https://codecov.io/gh/b2ihealthcare/snow-owl/branch/9.x/graph/badge.svg?style=flat-square)](https://codecov.io/gh/b2ihealthcare/snow-owl)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Fb2ihealthcare%2Fsnow-owl.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Fb2ihealthcare%2Fsnow-owl?ref=badge_shield)

# Introduction

Features include:
* Revision-controlled authoring
    * Maintains multiple versions (including unpublished and published) for each terminology artifact and provides APIs to access them all
    * Independent work branches offer work-in-process isolation, external business workflow integration and team collaboration
* SNOMED CT and others
    * SNOMED CT terminology support
        * RF2 Release File Specification as of 2023-12-01
        * Support for Relationships with concrete values
        * Official and Custom Reference Sets
        * Expression Constraint Language v2.1.0 [spec](https://confluence.ihtsdotools.org/download/attachments/33493263/doc_ExpressionConstraintLanguage_v2.1-en-US_INT_20220824.pdf?api=v2), [implementation](https://github.com/b2ihealthcare/snomed-ecl)
        * Compositional Grammar 2.3.1 [spec](https://confluence.ihtsdotools.org/download/attachments/33496020/doc_CompositionalGrammar_v2.3.1-en-US_INT_20161118.pdf?api=v2), [implementation](https://github.com/b2ihealthcare/snomed-scg)
        * Expression Template Language 1.0.0 [spec](https://confluence.ihtsdotools.org/download/attachments/45529301/doc_TemplateSyntax_v1.0-en-US_INT_20170726.pdf?api=v2), [implementation](https://github.com/b2ihealthcare/snomed-etl)
    * With its modular design, the server can maintain multiple terminologies (including local codes, mapping sets, value sets)
* Various set of APIs
    * SNOMED CT API (RESTful and native Java API)
    * FHIR API R5 [spec](https://hl7.org/fhir/R5/index.html) (R4B and R4 are also support for certain resource types)
    * CIS API 1.0 [see reference implementation](https://github.com/IHTSDO/component-identifier-service)
* Highly extensible and configurable
    * Simple to use plug-in system makes it easy to develop and add new terminology tooling/API or any other functionality
* Built on top of [Elasticsearch](https://www.elastic.co/elasticsearch) (highly scalable, distributed, open source search engine)
    * Connect to your existing cluster or use the embedded instance (supports up to Elasticsearch 8.x)
    * All the power of Elasticsearch is available (full-text search support, monitoring, analytics and many more)

# Download

* [WINDOWS](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/9.0.0/com.b2international.snowowl.server.update-9.0.0-oss.zip) - [sha](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/9.0.0/com.b2international.snowowl.server.update-9.0.0-oss.zip.sha1)
* [LINUX](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/9.0.0/com.b2international.snowowl.server.update-9.0.0-oss.tar.gz) - [sha](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/9.0.0/com.b2international.snowowl.server.update-9.0.0-oss.tar.gz.sha1) 
* [RPM](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/9.0.0/com.b2international.snowowl.server.update-9.0.0-rpm.rpm) - [sha](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/9.0.0/com.b2international.snowowl.server.update-9.0.0-rpm.rpm.sha1)
* [DEB](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/9.0.0/com.b2international.snowowl.server.update-9.0.0-deb.deb) - [sha](https://nexus.b2i.sg/repository/maven-releases/com/b2international/snowowl/com.b2international.snowowl.server.update/9.0.0/com.b2international.snowowl.server.update-9.0.0-deb.deb.sha1)

{% hint style="info" %}
This distribution only includes features licensed under the Apache 2.0 license. To get access to the full set of features, please contact [B2i Healthcare](mailto:info@b2ihealthcare.com).
{% endhint %}

View the detailed release notes [here](https://github.com/b2ihealthcare/snow-owl/releases/tag/v9.0.0).

Not the version you're looking for? View [past releases](https://github.com/b2ihealthcare/snow-owl/releases).

### Install and Run

NOTE: You need to have at least version 17 of the JDK installed for local builds and running the development environment. Official releases include the runtime.

Once you have downloaded the appropriate package:

* Run `bin/snowowl.sh` on unix, or `bin/snowowl.bat` on windows
* Run `curl http://localhost:8080/snowowl/info` to access server health status information
* Run `curl http://localhost:8080/snowowl/fhir/metadata` to access FHIR terminology capabilities
* Navigate to `http://localhost:8080/snowowl` to access the REST API documentation page
* Find more information about Snow Owl here in the [official docs](https://docs.b2ihealthcare.com/snow-owl)

# Building from source

Snow Owl uses Maven for its build system. In order to create a distribution, simply run the following command in the cloned directory. 

    ./mvnw clean package

The distribution packages can be found in the `releng/com.b2international.snowowl.server.update/target` folder, when the build is complete.

To run the test cases, use the following command:

    ./mvnw clean verify

# Development

These instructions will get Snow Owl up and running on your local machine for development and testing purposes.

## Prerequisites

Snow Owl is an Equinox-OSGi based server. To develop plug-ins for Snow Owl you need to use Eclipse as IDE: 
* Download Eclipse IDE for Eclipse Committers 2023-09 package from here: https://www.eclipse.org/downloads/packages/release/2023-09/r/eclipse-ide-eclipse-committers

Required Eclipse plug-ins in order (install the listed features via `Help` &rarr; `Install New Software...`):

*Note: you may have to untick the `Show only the latest versions of the available software` checkbox to get older versions of a feature. Please use the exact version specified below, not the latest point release.*

* Groovy Development Tools (https://groovy.jfrog.io/artifactory/plugins-release/e4.29)
  * Eclipse Groovy Development Tools - 5.1.0 (in category "Main Package")
* MWE2 (https://download.eclipse.org/modeling/emft/mwe/updates/releases/2.12.1/)
  * MWE SDK 1.6.1 (MWE)
* Xtext/Xtend (https://download.eclipse.org/modeling/tmf/xtext/updates/releases/2.25.0/)
  * Xtend IDE 2.25.0 (Xtext)
  * Xtext Complete SDK 2.25.0 (Xtext)
 
### Eclipse Preferences

Make sure you have the following preferences enabled/disabled.
* Plug-in development API baseline errors is set to Ignored (Preferences > Plug-in Development > API Baselines)
* The *Plugin execution not covered by lifecycle configuration: org.apache.maven.plugins:maven-clean-plugin:2.5:clean* type of errors can be ignored or changed to *Warnings* in *Preferences&rarr;Maven&rarr;Errors/Warnings*.
* Set the workspace encoding to *UTF-8* (Preferences&rarr;General&rarr;Workspace)
* Set the line endings to *Unix* style (Preferences&rarr;General&rarr;Workspace)
* Set the number of imports and static imports wildcard limit to *5* (Preferences&rarr;Java&rarr;Code Style&rarr;Organize Imports)

### Git configuration

* Make sure the Git line endings are set to *input* (Preferences&rarr;Team&rarr;Git&rarr;Configuration - add key if missing *core.autocrlf = input*)

### Maven Settings

* Make sure the `settings.xml` in your ~/.m2/settings.xml location is updated with the content from the `settings.xml` from this repository's root folder.

## First steps

1. Import all projects into your Eclipse workspace and wait for the build to complete
2. Select all projects and hit `Alt` + `F5` and trigger an update to all Maven projects manually (to download dependencies from Maven)
3. Open the `target-platform/target-platform.target` file
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
