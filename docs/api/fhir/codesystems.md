## CodeSystem API

Code Systems maintained within Snow Owl are exposed (read-only) via the endpoints `/CodeSystem` and `/CodeSystem/{codeSystemId}`.  Supported concept properties are handled and returned if requested. The currently exposed code systems are:

Snow Owl OSS:
* SNOMED CT

Snow Owl:
* ATC
* ICD-10 (and extensions)
* LOINC
* OPCS
* Local Code Systems
* Any other terminology
### SNOMED CT

All standard and default SNOMED CT properties are supported, including the relationship type and concrete value properties. In addition to the FHIR SNOMED CT properties, Snow Owl can return the _effective time property_, with the URI `http://snomed.info/field/Concept.effectiveTime`.

### Operations
#### $lookup

Both _GET_ as well as _POST_ HTTP methods are supported. Concepts are queried based on `code`,  `version`, `system` or `Coding`. Designations are included as part of the response as well as supported concept properties when requested. No `date` parameter is supported.

Example for looking up properties (_inactive and method_) of the latest version of a SNOMED CT _procedure by method_ code:
```
 /CodeSystem/$lookup?system=http://snomed.info/sct&code=128927009&_format=json&property=inactive&property=http://snomed.info/id/260686004
```

For SNOMED CT, all common and SNOMED CT properties are supported, including all active relationship types.
#### $validate-code

Both _GET_ as well as _POST_ HTTP methods are supported for all exposed terminologies.
Example for validating a SNOMED CT code:
```
/CodeSystem/SNOMEDCT/2021-07-31/$validate-code?code=128927009
```      

#### $subsumes

Both _GET_ as well as _POST_ HTTP methods are supported. Subsumption testing is supported for all terminologies, including SNOMED CT.

Example for SNOMED CT (version 2021-07-31):
```
/CodeSystem/$subsumes?codeA=409822003&codeB=264395009&system=http://snomed.info/sct/900000000000207008/version/20210731
```
