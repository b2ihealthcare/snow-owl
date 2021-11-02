## ValueSet API
 
The endpoints `/ValueSet` and `/ValueSet/{valueSetId}` and corresponding operations expose all Value Set resources stored (or implict Value Sets if the corresponding Value Set plug-in supports it) in the server. CUD operations are not supported.

## $expand

All value sets accessible via the `/ValueSet` endpoints can be _expanded_.

For SNOMED CT URIs, implicit value sets are supported:
 
 * ?fhir_vs - all Concept IDs in the edition/version. If the base URI is http://snomed.info/sct, this means all possible SNOMED CT concepts
 *	?fhir_vs=isa/[sctid] - all concept IDs that are subsumed by the specified Concept.
 *	?fhir_vs=refset - all concept ids that correspond to real references sets defined in the specified SNOMED CT edition
 *	?fhir_vs=refset/[sctid] - all concept IDs in the specified reference set

The following in-parameters are supported:
* activeOnly - to return only active codes in the response
* filter - to filter the results lexically
* displayLanguage - to select the language for the returned display values
* includeDesignations - whether to include all designations or not in the returned response
* count - to select the number of codes to be returned in the expansion
* after - to select codes to be returned after this last page value (cursor)

## $validate-code

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