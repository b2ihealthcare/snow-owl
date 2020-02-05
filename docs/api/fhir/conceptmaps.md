## ConceptMap API

The endpoints `/ConceptMap` and `/ConceptMap/{conceptMapId}` and corresponding operations expose the following types of terminology resources:

* SNOMED CT Simple Map Reference Sets with Concepts as referenced components
* SNOMED CT Complex Map Reference Sets
* SNOMED CT Extended Map Reference Sets
* Snow Owl's generic Mapping Sets

## $translate

All concept map accessible via the `/ConceptMap` endpoints are considered when retrieving mappings (_translations_). The translate request's _source_ that designates the source value set cannot be interpreted hence not used.  With the exception of SNOMED CT where the standard URI is expected, our proprietary _short name_ or _component ids_ are used to designate the source/target code system.

SNOMED CT: 
* Simple Map Type Reference Set mappings are considered _equivalent_ in terms of their correlation
* The availability and format of target code systems are not guaranteed, there is an ongoing conversation at SNOMED CT International to rectify this.