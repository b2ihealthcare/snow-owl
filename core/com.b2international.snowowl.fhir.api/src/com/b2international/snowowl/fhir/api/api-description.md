Fast Healthcare Interoperability Resources (FHIR) specifies resources, operations, coded data types and terminologies that are used for representing and communicating coded, structured data in the FHIR core specification within its Terminology Module. The Snow Owl terminology server's first FHIR API release includes support for the following resources:

## CodeSystem

Code systems maintained within Snow Owl are exposed (read-only) via the endpoints _/CodeSystem_ and _/CodeSystem/{codeSystemId}_.  Supported concept properties are handled and returned if requested.

### $lookup

Both _GET_ as well as _POST_ HTTP methods are supported for the lookup operation.  Concepts are queried based on _code,  version, system_ or _Coding_.  Designations are included as part of the response as well as supported concept properties when requested. No _date_ parameter is supported.

For SNOMED CT, all common and SNOMED CT properties are supported, including all active relationship types.

##ValueSet
 
 Simple type reference sets maintained within Snow Owl are exposed (read-only) via the endpoints _/ValueSet_ and _/ValueSet/{valueSetId}_. _Delete_ and _create_ operations are not yet implemented. 

### Search

The supported search result parameters:

*   _summary
*   _elements

## Code Systems

The currently exposed code systems are:

*   SNOMED CT
*   ICD-10
*   Internal (FHIR) Code Systems

## REST API

Currently only JSON format is supported with UTF-8 encoding and the *Content-Type = application/fhir+json;charset=utf-8*. 

The current HTTP status codes are:

| HTTP Status   | Reason         |
| ------------- | -------------- |
| 200           | OK             |
| 400           | Bad Request    |
| 401           | Unauthorised   |
| 403           | Forbidden      |
| 404           | Not Found      |
| 500           | Internal Error |

## Roadmap

Snow Owl's pluggable and extensible architecture allows modular development of the FHIR API both in terms of the supported functionality as well as the exposed terminologies.  Additionally, Snow Owl's revision-based model allows the concurrent management of multiple versions.

**Milestone 2**

1.  ValueSet/$validate-code
2.  ValueSet/$expand
3.  Local Code systems read
4.  Generic Value Sets read

**Milestone 3**
1.  ConceptMap read
2.  ValueSet create/update/delete
3.  Generic Mapping Sets
4.  Additional search parameters

**Milestone 4**
1.  LOINC
2.  ICD-10-UK/AM/CM
3.  OPCS
4.  ATC
5.  ConceptMap create/update/delete
