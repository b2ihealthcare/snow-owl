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

* SNOMED CT Simple Type Reference Sets with Concepts are referenced components.
* SNOMED CT Query Type Reference Sets with ECL expressions (each member is a Value Set)
* Snow Owl's generic Value Sets
 
`Delete` and `create` operations are not yet implemented.

### $expand

All value sets accessible via the `/ValueSet` endpoints can be _expanded_.
The in-parameters are not yet supported.


## ConceptMap

The _ConceptMap_ resource is not yet implemented.

#Implementation

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

