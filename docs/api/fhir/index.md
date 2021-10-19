# FHIR API

Fast Healthcare Interoperability Resources (FHIR) specifies resources, operations, coded data types and terminologies that are used for representing and communicating coded, structured data in the FHIR core specification within its Terminology Module. 

Snow Owl's pluggable and extensible architecture allows modular development of the FHIR API both in terms of the supported functionality as well as the exposed terminologies.  Additionally, Snow Owl's revision-based model allows the concurrent management of multiple versions. 

# Resources

The Snow Owl terminology server's FHIR API release includes support for the following resources:

* [CodeSystem API](./codesystems.md)
* [ValueSet API](./valuesets.md)
* [ConceptMap API](./conceptmaps.md)
* [Bundle API](./bundles.md)
* [CapabilityStatement API](./metadata.md)

## Versions

Versions in Snow Owl are represented as individual FHIR Resources when accessed via the FHIR API endpoints. If there are no versions present for a given resource, only the latest development version is returned as available FHIR Resource. When accessing a terminology resource via the FHIR API, but without specifying an exact version tag, then the system will always assume and return the latest development version, including not yet published changes. It is recommended to always query a specific version of any terminology content to get consistent results, especially when the same terminology server instance is being used for both authoring and distribution.

## Search

Resource representations can be filtered by the following supported official FHIR payload filters:

*   _summary - to return a predefined set of properties and their values
*   _elements - to return only the mandatory and the specified list of properties and nothing else

The supported search parameters:
* _id - to filter FHIR resources by their logical identifier
* name - to filter FHIR resources by their name (which in Snow Owl equals to the logical identifier)
* title - to filter FHIR resources by their title property lexically (Snow Owl by default uses exact, phrase and prefix matching during its lexical search activities)
* url - to filter FHIR resources by their assigned `url` value
* system - to filter FHIR resources by their assigned `system` value (which in Snow Owl always matches the `url` value)
* version - to filter FHIR resource by their `version` property value
* _lastUpdated - exposed but not supported yet

## Sorting and paging
Sorting supported via standard FHIR sort parameters, while paging is supported with a new `after` parameter (using `count` as page size). `Offset` + `count` based traditional paging is not supported.

## URIs
Globally unique logical URIs that represent a terminology resource. For code systems these are:

| Code system               | URI                                   |
|---------------------------|---------------------------------------|
| SNOMED CT                 | http://snomed.info/sct                |
| LCS                       | Defined when the resource was created |
| Value Set                 | Defined when the resource was created | 
| Concept Map               | Defined when the resource was created | 
| ATC                       | http://www.whocc.no/atc               |
| ICD-10                    | http://hl7.org/fhir/sid/icd-10        |
| LOINC                     | http://loinc.org                      |   
|                           |                                       |

### SNOMED CT

For SNOMED CT, Snow Owl's FHIR implementation follows the [SNOMED CT URI Standard](https://confluence.ihtsdotools.org/display/DOCURI).

### ICD-10

For ICD-10, Snow Owl's FHIR implementation follows the [HL7 FHIR Specification](https://www.hl7.org/fhir/icd.html).

### Local Code System

Snow Owl's Local Code Systems (LCS) identified by the URI that is based on the _Organization Link_ property stored within Snow Owl's Terminology Registry and the _Short Name_ of the LCS e.g.: https://b2i.sg/MyLocalCodeSystem. 


## IDs

The logical _id_ field of each resource is assigned by Snow Owl and is unique within it. Once it has been assigned, the _id_ never changes. For this logical identifier, Snow Owl follows the pattern:

	resourceId[/version]

For example to identify a particular SNOMED CT Edition with its version _2021-03-01_:

   SNOMEDCT-US/2021-03-01

For example to identify a particular LOINC code system with the version tag _v2.64_:

	 LOINC/v2.64

## REST API

Currently only JSON format is supported with UTF-8 encoding and content type of `Content-Type = application/fhir+json;charset=utf-8`. In case of any errors during the processing the API responds with an `OperationOutCome` within the response body using one of the HTTP status codes:

| HTTP Status   | Reason         |
| ------------- | -------------- |
| 200           | OK             |
| 400           | Bad Request    |
| 401           | Unauthorized   |
| 403           | Forbidden      |
| 404           | Not Found      |
| 500           | Internal Error |

# Snow Owl's extension API

Snow Owl exposes a comprehensive REST API to support areas such as:
 * Syndication - content provisioning between servers or between the Snow Owl Authoring platform and servers 
 * Administration (repository and revision control management) 
 * Auditing
 * SNOMED CT specific browsing and authoring API 