Fast Healthcare Interoperability Resources (FHIR) specifies resources, operations, coded data types and terminologies that are used for representing and communicating coded, structured data in the FHIR core specification within its Terminology Module. The Snow Owl terminology server's first FHIR API release includes support for the following resources:

## CodeSystem

Code systems maintained within Snow Owl are exposed (read-only) via the endpoints `/CodeSystem` and `/CodeSystem/{codeSystemId}`.  Supported concept properties are handled and returned if requested.

### $lookup

Both _GET_ as well as _POST_ HTTP methods are supported. Concepts are queried based on `code`,  `version`, `system` or `Coding`.  Designations are included as part of the response as well as supported concept properties when requested. No `date` parameter is supported.

For SNOMED CT, all common and SNOMED CT properties are supported, including all active relationship types.

### $subsumes

Both _GET_ as well as _POST_ HTTP methods are supported. Subsumption testing is supported for ICD-10 and SNOMED CT terminologies. In case of error (eg. _codeA/codeB/system/version_ not found) the API responds with an error `OperationOutCome`. 

##ValueSet
 
Simple type reference sets maintained within Snow Owl are exposed (read-only) via the endpoints `/ValueSet` and `/ValueSet/{valueSetId}`. `Delete` and `create` operations are not yet implemented. 

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

Currently only JSON format is supported with UTF-8 encoding and the `Content-Type = application/fhir+json;charset=utf-8`. 

The current HTTP status codes are:

| HTTP Status   | Reason         |
| ------------- | -------------- |
| 200           | OK             |
| 400           | Bad Request    |
| 401           | Unauthorized   |
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
