Fast Healthcare Interoperability Resources (FHIR) specifies resources, operations, coded data types and terminologies that are used for representing and communicating coded, structured data in the FHIR core specification within its Terminology Module. The Snow Owl terminology server's first FHIR API release includes support for the following resources:

## CodeSystem

Code systems maintained within Snow Owl are exposed via the endpoints _/CodeSystem_ and _/CodeSystem/{codeSystemId}_.  Supported concept properties are handled and returned if requested. No Delete or Create operations are currently exposed.

### $lookup

Both _GET_ as well as _POST_ HTTP methods are supported for the lookup operation.  Concepts are queried based on _code,  version, system_ or _Coding_.  Designations are included as part of the response as well as supported concept properties when requested. No _date_ parameter is supported.

For SNOMED CT, all common and SNOMED CT properties are supported, including all active relationship types.

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

Snow Owl's pluggable and extensible architecture allows modular development of the FHIR API both in terms of the supported functionality as well as the exposed terminologies.  Additionally, Snow Owl's revision-based model allows the support for multiple terminology versions queried concurrently.