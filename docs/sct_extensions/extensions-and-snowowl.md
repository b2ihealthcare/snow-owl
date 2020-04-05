# SNOMED CT Extension and Snow Owl

Snow Owl is a multi-purpose terminology server with main focus on SNOMED CT International Edition and its Extensions. Whether you are a producer of a SNOMED CT Extension or a consumer of one, Snow Owl has you covered. As always, feel free to ask your questions regarding any of the content you read here (raise a ticket on [GitHub Issues](https://github.com/b2ihealthcare/snow-owl/issues/new)).

## Snow Owl Concepts

Snow Owl uses the following basic concepts to provide authoring and maintenance support for SNOMED CT Extensions.

### Code Systems

From the [getting started](../getting_started/basic-concepts.md) page, we've learned what is a Repository and how Code Systems are defined as part of a Repository.

{% hint style="info" %}
Reminder: a Repository is a set of schemas and functionality to provide support for a dedicated set of Code Systems, eg. the SNOMED CT Repository stores all SNOMED CT related components under revision control and provides quick access). A Repository can contain one or more Code Systems and by default always comes with one predefined Code System, the root Code System (in case of SNOMED CT, this is often represents the International Edition).
{% endhint %}

A SNOMED CT Extensions in Snow Owl are basically Code Systems with their own set of properties and characteristics. With Snow Owl's Code System API, a Code System can be created for each SNOMED CT Extension to easily identify the Code System and its components with a single unique identifer, called the Code System short name.
The recommended naming approach when selecting the unique short name identifier is the following:
* SNOMED CT International Edition: `SNOMEDCT` - often mixed with other editions for distribution purposes
* National Release Center (single maintained extension) - `SNOMEDCT-US` - represents the SNOMED CT United States of America Extension
* National Release Center (multiple maintained extensions) - `SNOMEDCT-UK-CL`, `SNOMEDCT-UK-DR` - United Kingdom Clinical and Drug Extensions, respectively
* Care Provider with special extension based on a national extension - `SNOMEDCT-US-UNMC` - University of Nebraska Medical Center's extension builds on top of the `SNOMEDCT-US` extension

Primary namespace identifer, set of modules and languages can be set during the creation of the Code System, and can be updated later on if required. 
These properties can be used when users are accessing the terminology server for authoring purposes to provide a seamless authoring experience for the user without they needing to worry about selecting the proper namespace, modules, language tags, etc. (NOTE: this feature is not available yet in the OSS version of Snow Owl)

#### Extension Of

A Snow Owl Code System can be marked as an `extensionOf` another Code System, which ties them together, forming a dependency between the two Code Systems. A Code System can have multiple Extension Code Systems, but a Code System can only be `extensionOf` a single Code System.

### Branching

In Snow Owl, a Repository maintains a set of branches and Code Systems are always attached to a dedicated branch. For example, the default root Code Systems are always tied to the default branch, called `MAIN`.
When creating a new Code System, the "working" `branchPath` can be specified and doing so assigns the branch to the Code System. A Code System cannot be attached to multiple branches at the same time, and a branch can only be assigned to a single Code System in a Repository.
Snow Owl's branching infrastructure allows the use of isolated environments for both distribution and authoring workflows, therefore they play crucial role in SNOMED CT Extension managament as well. They also provide the support for seamless upgrade mechanism, which can be done whenever there is a new version available in one of your SNOMED CT Extension's dependant Code Systems.

### Versions

As in real life, a Code System can have zero or more versions (or with another name, releases). A version is a special branch, that is created during the versioning process and makes the currently available latest content accessible later in its current form. Since SNOMED CT Extensions can have releases as well, creating a Code System Version in Snow Owl is a must in order to produce the release packages.

// TODO image showing the MAIN branch, assigned to a Code System with versions

The next section describes the use case scenarios in the world of SNOMED CT and what are the recommended approach to deploy that scenario in Snow Owl.