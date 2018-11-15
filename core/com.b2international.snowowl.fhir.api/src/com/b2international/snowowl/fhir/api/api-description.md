Fast Healthcare Interoperability Resources (FHIR) specifies resources, operations, coded data types and terminologies that are used for representing and communicating coded, structured data in the FHIR core specification within its Terminology Module. 

Snow Owl's pluggable and extensible architecture allows modular development of the FHIR API both in terms of the supported functionality as well as the exposed terminologies.  Additionally, Snow Owl's revision-based model allows the concurrent management of multiple versions. 

#Resources

The Snow Owl terminology server's FHIR API release includes support for the following resources:

## CodeSystem

Code systems maintained within Snow Owl are exposed (read-only) via the endpoints `/CodeSystem` and `/CodeSystem/{codeSystemId}`.  Supported concept properties are handled and returned if requested. The currently exposed code systems are:

*   SNOMED CT
*   ICD-10
*   Internal (FHIR) Code Systems

### SNOMED CT

All standard and default SNOMED CT properties are supported, including the relationship type properties. In addition to the FHIR SNOMED CT properties, Snow Owl can return the _effective time property_, with the URI `http://snomed.info//field/Concept.effectiveTime`.

### $lookup

Both _GET_ as well as _POST_ HTTP methods are supported. Concepts are queried based on `code`,  `version`, `system` or `Coding`.  Designations are included as part of the response as well as supported concept properties when requested. No `date` parameter is supported.

For SNOMED CT, all common and SNOMED CT properties are supported, including all active relationship types.

### $subsumes

Both _GET_ as well as _POST_ HTTP methods are supported. Subsumption testing is supported for ICD-10 and SNOMED CT terminologies.

## ValueSet
 
The endpoints `/ValueSet` and `/ValueSet/{valueSetId}` and corresponding operations expose the following types of terminology resources:

* SNOMED CT Simple Type Reference Sets with Concepts as referenced components.
* SNOMED CT Query Type Reference Sets with ECL expressions (each member is a Value Set)
* Snow Owl's generic Value Sets
 
`Delete` and `create` operations are not implemented.

### $expand

All value sets accessible via the `/ValueSet` endpoints can be _expanded_.

For SNOMED CT URIs, implicit value sets are supported:
 
 * ?fhir_vs - all Concept IDs in the edition/version. If the base URI is http://snomed.info/sct, this means all possible SNOMED CT concepts
 *	?fhir_vs=isa/[sctid] - all concept IDs that are subsumed by the specified Concept.
 *	?fhir_vs=refset - all concept ids that correspond to real references sets defined in the specified SNOMED CT edition
 *	?fhir_vs=refset/[sctid] - all concept IDs in the specified reference set

The in-parameters are not yet supported.

### $validate-code

Codes can be validated against a given Value Set specified by the value set's logical id or canonical URL.  In terms of Snow Owl terminology components, codes are validated against:

* SNOMED CT Simple Type Reference Sets with Concepts as referenced components.
* SNOMED CT Query Type Reference Sets with ECL expressions (each member is a Value Set)
* Snow Owl's generic Value Sets

Validation performs the following checks: 
 * The existence of the given Value Set (__error__ if not found)
 * The existence of the reference in the existing Value Set to the given code (__error__ if not found)
 * The existence of the given code in the system (__error__ if not found)
 * Potential version mismatch (__error_ if the reference points to a version that is different to the code's version) 
 * The status of the given code and reference (__warning__ if code is inactive while reference is active)
 
For SNOMED CT URIs, implicit value sets are supported:
 
 * ?fhir_vs - all Concept IDs in the edition/version. If the base URI is http://snomed.info/sct, this means all possible SNOMED CT concepts
 * ?fhir_vs=isa/[sctid] - all concept IDs that are subsumed by the specified Concept.
 * ?fhir_vs=refset - all concept ids that correspond to real references sets defined in the specified SNOMED CT edition
 * ?fhir_vs=refset/[sctid] - all concept IDs in the specified reference set

## ConceptMap

The endpoints `/ConceptMap` and `/ConceptMap/{conceptMapId}` and corresponding operations expose the following types of terminology resources:

* SNOMED CT Simple Map Reference Sets with Concepts as referenced components
* SNOMED CT Complex Map Reference Sets
* SNOMED CT Extended Map Reference Sets
* Snow Owl's generic Mapping Sets

### $translate

All concept map accessible via the `/ConceptMap` endpoints are considered when retrieving mappings (_translations_). The translate request's _source_ that designates the source value set cannot be interpreted hence not used.  With the exception of SNOMED CT where the standard URI is expected, our proprietary _short name_ or _component ids_ are used to designate the source/target code system.

SNOMED CT: 
* Simple Map Type Reference Set mappings are considered _equivalent_ in terms of their correlation
* The availability and format of target code systems are not guaranteed, there is an ongoing conversation at SNOMED CT International to rectify this.

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

