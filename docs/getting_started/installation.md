# Installation

Snow Owl requires Java 11 or newer version. Specifically as of this writing, it is recommended that you use JDK (Oracle of OpenJDK is preferred) version 11.0.2. Java installation varies from platform to platform so we won’t go into those details here. Oracle’s recommended installation documentation can be found on Oracle’s website. Suffice to say, before you install Snow Owl, please check your Java version first by running (and then install/upgrade accordingly if needed):

```
java -version
echo $JAVA_HOME
```

Once we have Java set up, we can then download and run Snow Owl. The binaries are available at the [Releases](https://github.com/b2ihealthcare/snow-owl/releases) pages. For each release, you have a choice among a zip or tar archive, a DEB or RPM package.

## Installation example with zip

For simplicity, let's use a zip file.

Let's download the most recent Snow Owl release as follows:

```
curl -L -O https://github.com/b2ihealthcare/snow-owl/releases/download/<version>/snow-owl-oss-<version>.zip
```

Then extract it as follows:

```
unzip snow-owl-oss-<version>.zip
```

It will then create a bunch of files and folders in your current directory. We then go into the bin directory as follows:

```
cd snow-owl-oss-<version>/bin
```

And now we are ready to start the instance:

```
./startup
```

## Successfully running instance

If everything goes well with the installation, you should see a bunch of log messages that look like below:

```
TODO example output
```
