## CodeSystem API

Code systems maintained within Snow Owl are exposed (read-only) via the endpoints `/CodeSystem` and `/CodeSystem/{codeSystemId}`.  Supported concept properties are handled and returned if requested. The currently exposed code systems are:

Snow Owl OSS:
*   SNOMED CT
*   Internal (FHIR) Code Systems (terminology subset)

Snow Owl Pro:
*   ATC
*   ICD-10
*   LOINC
*   OPCS

### SNOMED CT

All standard and default SNOMED CT properties are supported, including the relationship type properties. In addition to the FHIR SNOMED CT properties, Snow Owl can return the _effective time property_, with the URI `http://snomed.info/field/Concept.effectiveTime`.

### $lookup

Both _GET_ as well as _POST_ HTTP methods are supported. Concepts are queried based on `code`,  `version`, `system` or `Coding`.  Designations are included as part of the response as well as supported concept properties when requested. No `date` parameter is supported.

For SNOMED CT, all common and SNOMED CT properties are supported, including all active relationship types.

### $subsumes

Both _GET_ as well as _POST_ HTTP methods are supported. Subsumption testing is supported for ICD-10 and SNOMED CT terminologies.