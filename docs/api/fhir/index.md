# FHIR API

Fast Healthcare Interoperability Resources (FHIR) specifies resources, operations, coded data types and terminologies that are used for representing and communicating coded, structured data in the FHIR core specification within its Terminology Module. 

Snow Owl's pluggable and extensible architecture allows modular development of the FHIR API both in terms of the supported functionality as well as the exposed terminologies.  Additionally, Snow Owl's revision-based model allows the concurrent management of multiple versions. 

#Resources

The Snow Owl terminology server's FHIR API release includes support for the following resources:

* [CodeSystem API](./codesystems.md)
* [ValueSet API](./valuesets.md)

#Implementation

##Versions

Snow Owl's repository is a fully-fledged revision control system with branches, versions and revisions.  Snow Owl's terminology artefact _versions_ are exposed as FHIR versions for every supported code system with the exception of SNOMED CT where the standard SNOMED CT URI specification governs the format (short date) of the version.  If there is no version specified in a request, the last version is assumed.  If there is no version in the system, the last state (head of MAIN) is considered.

## Search

The supported search result filters:

*   _summary
*   _elements

The supported search parameters:
* _id

## Sorting and paging
Sorting and paging are not yet supported.

## URIs
Globally unique logical URIs that represent a terminology resource. For code systems these are:

| Code system               | URI                                  |
|---------------------------|--------------------------------------|
| ATC                       | http://www.whocc.no/atc              |
| SNOMED CT                 | http://snomed.info/sct               |
| ICD-10                    | http://hl7.org/fhir/sid/icd-10       |
| LOINC                     | http://loinc.org                     |   
| FHIR                      | Prefixed with http://hl7.org/fhir    |
| LCS                       | Prefixed with the organization link  |
| Value Set                 | Prefixed with the source URI         | 
| Mapping Set               | Prefixed with the source URI         | 
|                           |                                      |

### SNOMED CT

For SNOMED CT, Snow Owl's FHIR implementation follows the [SNOMED CT URI Standard](https://confluence.ihtsdotools.org/display/DOCURI).

### ICD-10

For ICD-10, Snow Owl's FHIR implementation follows the [HL7 FHIR Specification](https://www.hl7.org/fhir/icd.html).

### Local Code System

Snow Owl's Local Code Systems (LCS) identified by the URI that is based on the _Organization Link_ property stored within Snow Owl's Terminology Registry and the _Short Name_ of the LCS e.g.: https://b2i.sg/MyLocalCodeSystem. 


## IDs

The _id_ field of each terminology resource is assigned by our terminology server and is unique within Snow Owl.  Once is has been assigned, the _id_ never changes.  For this logical identifier, Snow Owl follows the pattern:

	repository:{branchPath}:{code}[|{member}]

For example to identify a particular LOINC code system with the version tag _20180131_:
	
	loincStore:MAIN/20180131

For example to address a particular SNOMED CT concept (_Blood bank procedure_):

	snomedStore:MAIN/201101031/DK/20140203:59524001

where
* 59524001 represents the concept id
* 20140203 represents the extension version
* DK represents the extension branch
* 20110131 represents the version of the International Edition the DK extension is based on

Our logical id has been extended to cover individual Reference Set members as well:
	
	snomedStore:MAIN/201101031/DK/20140203:98403008|84f56f72-9f8b-423d-98b8-25961811393c 

where
* 98403008 is the Reference Set ID
* 98484f56f72-9f8b-423d-98b8-25961811393c03008 is the reference set member

# Snow Owl's extension API

Snow Owl exposes a comprehensive REST API to support areas such as:
 * Syndication - content provisioning between servers or between the Snow Owl Authoring platform and servers 
 * Administration (repository and revision control management) 
 * Auditing
 * SNOMED CT specific browsing and authoring API 

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

