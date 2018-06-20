# Snow Owl

## Introduction
Snow Owl<sup>®</sup> is a terminology server and a collaborative terminology authoring platform.  The authoring platform maintains terminology artifacts developed by a team and supported by business workflows that are driven by external task management systems like Bugzilla and JIRA.  With its modular design, the server can maintain multiple terminologies where new terminologies can be plugged-in to the platform.  The functionality of Snow Owl is exposed via a REST API.

## Getting started

### Requirements

You need to have a recent version of Java installed (Java 8 update 102 or newer).

### Installation

* Download (Coming soon!) and unzip the official Snow Owl distribution (or use the official Docker image)
* Run `bin/startup` on unix, or `bin/startup.bat` on windows.
* Run `curl http://localhost:8080/snowowl/admin/info`
* See API [docs](http://localhost:8080/snowowl/snomed-ct/v3)

### Documentation

Please refer to the [installation guide](documentation/src/main/asciidoc/installation_guide.adoc). 
Prerequisites are detailed in the [software requirements](documentation/src/main/asciidoc/software_requirements.adoc) document. 
Then see the [configuration guide](documentation/src/main/asciidoc/configuration_guide.adoc) to configure for your particular installation.
The [SNOMED CT extension management guide](documentation/src/main/asciidoc/snomed_extension_management.adoc) provides details for managing multiple SNOMED extensions on a single terminology server.
See the [administration guide](documentation/src/main/asciidoc/administration_guide.adoc) to manage your installation. 
You may find the quick references for [console commands](documentation/src/main/asciidoc/administrative_console_reference.adoc) and the [administration REST API](documentation/src/main/asciidoc/administrative_rest_reference.adoc) helpful.

## Building from source

Snow Owl uses Maven for its build system. In order to create a distribution, simply run the following command in the cloned directory. 

    mvn clean package

The distribution package can be found in the `releng/distribution/target` folder, when the build is complete.

To run the test cases, use the following command:

    mvn clean verify

## Development

These instructions will get Snow Owl up and running on your local machine for development and testing purposes.

### Prerequisites

Snow Owl is an Equinox-OSGi based server (using either Virgo or standalone OSGi). To develop plug-ins for Snow Owl you need to use Eclipse as IDE: 
* Use latest Eclipse IDE for Eclipse Committers package: http://www.eclipse.org/downloads/eclipse-packages/

Required Eclipse plug-ins (install the listed features via `Help` -> `Install New Software...`):

*Note: you may have to untick the `Show only the latest versions of the available software` checkbox to get older versions of a feature. Please use the exact version specified below, not the latest point release.*

* Xtext/Xtend (http://download.eclipse.org/modeling/tmf/xtext/updates/composite/releases/)
  * MWE 2 language SDK 2.9.0 (MWE)
  * Xtend IDE 2.11.0 (Xtext)
  * Xtext Complete SDK 2.11.0 (Xtext)
* Optional: Maven integration (http://download.eclipse.org/technology/m2e/releases) 
 
#### Eclipse Preferences

Make sure you have the following preferences enabled/disabled.
* Plug-in development API baseline errors is set to Ignored (Preferences > Plug-in Development > API Baselines)
* The *Plugin execution not covered by lifecycle configuration: org.apache.maven.plugins:maven-clean-plugin:2.5:clean* type of errors can be ignored or changed to *Warnings* in *Preferences->Maven->Errors/Warnings*.
* Set the workspace encoding to *UTF-8* (Preferences->General->Workspace)
* Set the line endings to *Unix* style (Preferences->General->Workspace)

#### Git configuration

* Make sure the Git line endings are set to *input* (Preferences->Team->Git->Configuration - add key if missing *core.autocrlf = input*)

### First steps

1. Import all projects into your Eclipse workspace and wait for the build to complete
2. Open the `target-platform/target-platform-local.target` file
3. Wait until Eclipse Resolves the target platform and then click on `Set as Target platform`
4. Wait until the build is complete and you have no compile errors
5. Launch `snow-owl-community` launch configuration in the Run Configurations menu
6. Navigate to `http://localhost:8080/snowowl/snomed-ct/v3`

## Contributing

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## Versioning

Our [releases](https://github.com/b2ihealthcare/snow-owl/releases) use [semantic versioning](http://semver.org). You can find a chronologically ordered list of notable changes in [CHANGELOG.md](CHANGELOG.md).

## License

This project is licensed under the Apache 2.0 License. See [LICENSE](LICENSE) for details and refer to [NOTICE](NOTICE) for additional licencing notes and uses of third-party components.

## Acknowledgements

In March 2015, [SNOMED International](http://snomed.org) generously licensed the Snow Owl Terminology Server components supporting SNOMED CT. They subsequently made the licensed code available to their [members](http://www.snomed.org/members) and the global community under an open-source license.
