# SNOMED CT Extension Management

## Introduction

Snow Owl Terminology Server is capable of managing multiple SNOMED CT extensions for both distribution and authoring purposes in a single deployment. 
This guide describes the typical scenarios, like creating, managing, releasing and upgrading SNOMED CT Extensions in great detail with images. 
If you are unfamiliar with SNOMED CT Extensions, the next section walks you through their logical model and basic characteristics, while the following pages describe distribution and authoring scenarios as well as how to use the Snow Owl Terminology Server for SNOMED CT Extensions.

[Extensions and Snow Owl](./extensions-and-snowowl.md)

## What is a SNOMED CT Extension?

{% hint style="info" %}
The official SNOMED CT Extension Practical Guide has been used to help produce the content available on this page: https://confluence.ihtsdotools.org/display/DOCEXTPG
{% endhint %}

### Common Structure

> SNOMED CT is a multilingual clinical terminology that covers a broad scope. However, some users may need additional concepts, relationships, descriptions or reference sets to support national, local or organizational needs.

> The extension mechanism allows SNOMED CT to be customized to address the terminology needs of a country or organization that are not met by the International Edition. 

> A SNOMED CT Extension may contain components and/or derivatives (e.g. reference sets used to represent subsets, maps or language preferences). Since the international edition and all extensions share a common structure, the same application software can be used to enter, store and process information from different extensions. Similarly, reference sets can be constructed to refer to content from both the international release and extensions. The common structure also makes it easier for content developed by an extension producer to be submitted for possible inclusion in a National Edition or the International Edition.

Therefore, a SNOMED CT Extension uses the same Release Format version 2 as the International Edition, they share a common structure and schema (see [Release Format 2 specification](http://snomed.org/rfs)).

### Namespace

> Extensions are managed by SNOMED International, and Members or Affiliate Licensees who have been issued a namespace identifier by SNOMED International. A namespace identifier is used to create globally unique SNOMED CT identifiers for each component (i.e. concept, description and relationship) within a Member or Affiliate extension. This ensures that references to extension concepts contained in health record data are unambiguous and can be clearly attributed to a specific issuing organization.

> A national or local extension uses a namespace identifier issued by SNOMED International to ensure that all extension components can be uniquely identified (across all extensions).

Therefore, a SNOMED CT Extension uses a single namespace identifier to identify all core components in a SNOMED CT Extension (see [Namespace identifier](https://confluence.ihtsdotools.org/display/DOCEXTPG/4.1+Namespaces)).

### Modules

> Every SNOMED CT Extension includes one or more modules, and each module contains either SNOMED CT components or reference sets (or both). Modules may be dependent on other modules. A SNOMED CT Edition includes the contents of a focus module together with the contents of all the modules on which it depends. This includes the modules in the International Edition and possibly other modules from a national and/or local extension.

> An edition is defined based on a single focus module. This focus module must be the most dependent module, in that the focus module is dependent on all the other modules in the edition.

Therefore, a SNOMED CT Extension uses one or more modules to categorize the components into meaningful groups (see [Modules](https://confluence.ihtsdotools.org/display/DOCEXTPG/4.2+Modules)).

### Language

> SNOMED CT extensions can support a variety of use cases, including:
> 
> Translating SNOMED CT, for example 
> * Adding terms used in a local language or dialect
> * Adding terms used by a specific user group, such as patient-friendly terms

> Representing language, dialect or specialty-specific term preferences is possible using a SNOMED CT extension. The logical design of SNOMED CT enables a single clinical idea to be associated with a range of terms or phrases from various languages, as depicted in Figure 3.1-1 below. In an extension, terms relevant for a particular country, speciality, hospital (or other organization) may be created, and different options for term preferences may be specified. Even within the same country, different regional dialects or specialty-specific languages exist may influence which synonyms are preferred. SNOMED CT supports this level of granularity for language preferences at the national or local level.

Therefore, an Extension can have its own language to support patient-friendly terms, local user groups, etc. (see [Purpose](https://confluence.ihtsdotools.org/display/DOCEXTPG/3+Purpose)).

### Dependency

> A SNOMED CT extension is a set of components and reference set members that add to the SNOMED CT International Edition. An extension is created, structured, maintained and distributed in accordance with SNOMED CT specifications and guidelines. Unlike, the International Edition an extension is not a standalone terminology. The content in an extension depends on the SNOMED CT International Edition, and must be used together with the International Edition and any other extension module on which it depends.

Therefore, a SNOMED CT Extension depends on the SNOMED CT International Edition directly or indirectly through another SNOMED CT Extension (see [Extensions](https://confluence.ihtsdotools.org/display/DOCEXTPG/4.3+Extensions)).

### Versions

> A specific version of an extension can be referred to using the date on which the extension was published.

> There are many use cases that require a date specific version of an edition, including specifying the substrate of a SNOMED CT query, and specifying the version of SNOMED CT used to code a specific data element in a health record. A versioned edition includes the contents of the specified version of the focus module, plus the contents of all versioned modules on which the versioned focus module depends (as specified in the |Module dependency reference set|). The version of an edition is based on the date on which the edition was released. Many extension providers release their extensions as a versioned edition, using regular and predictable release cycles.

Therefore, a SNOMED CT Extension can be versioned and have different release cycle than the SNOMED CT International Edition (see [Versions](https://confluence.ihtsdotools.org/display/DOCEXTPG/4.4+Editions)).

## Characteristics

To summarize, a SNOMED CT Extension has the following characteristics:
* Uses the same RF2 structure as SNOMED CT International Edition
* Uses a single namespace identifer to globally identify its content
* Uses one or more modules to categorize the content into groups
* Uses one or more languages to support specific user groups and patient-friendly terms
* Depends on the SNOMED CT International Edition
* Uses versions (effective times) to identity its content across multiple releases

Now that we have a clear understanding of what a SNOMED CT Extension is, let's take a look at how can we use them in Snow Owl.