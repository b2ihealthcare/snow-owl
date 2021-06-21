# Change Log
All notable changes to this project will be documented in this file.

## 7.17.2

### Bugs/Improvements
- [build] improvements to b2i-ci's `Jenkinsfile` (skip deploy, downstream build configuration) (27de738, eba50fe)

## 7.17.1

### Bugs/Improvements
- [index] use real-time scrolling instead of snapshot scroll when creating compare between two branches (11de246)
- [snomed] validate incorrect partition and component identifiers when validating any SNOMED CT IDs during RF2 import (4bc59b1)
- [snomed] improve concept preferred description change processing performance (9cb727c)
- [api] apply alternate type rule for ComponentIdentifier type (9f50aeb)
- [api] return with proper error response when labeling up ECL expressions via `POST /label-expressions` API (#830)

## 7.17.0

### Core
- Code System API
  * Move to `POST /upgrade` endpoint (#808)
  * Support selecting Code System versions as sources when creating upgrades to let authors continue working on unpublished content without the need of fixing data at two places (#808, #822)
  * Create versions on non-working branches is now disabled (#799, #810)

### SNOMED CT
- Support for SNOMED CT Concrete Value Specification (RF2 2021-07-31) (#811)
  * Add complete support for SNOMED International Concrete Value Specification, including import and export of the new `der2_RelationshipConcreteValues` RF2 file, searching concrete values using ECL expressions and editing then classifying concrete values.
- Add new `ignoreMissingReferencesIn` configuration option to RF2 file imports (#803)
  * This option can be used to define SNOMED CT Reference Set IDs when importing an RF2 archive where the selected Reference Sets might reference non-existent components. (eg. SNOMED CT UK Clinical Extension, UK Edition RF2 content often released with such reference set memberships)
- New `POST /:path/label-expressions` endpoint to label up one or more ECL expressions using the selected description type (`FSN` or `PT`) and locales (#814, #817)
- New validation rules (#801, #804)
  * Active terms should not contain double spaces
  * Active terms should be capitalized or have `Entire term case sensitive` case significance
  * Spacing around hyphens should be consistent in active terms
  * In active terms, spaces should not precede commas, colons, semicolons or full stops
  * Descriptions which are not Fully Specified Names should not contain semantic tags
  * The fully specified names of active concept should contain a bracketed suffix (semantic tag)
  * Terms should only contain ASCII characters
  * Fully specified names should not contain <, >, &, %, $, @, # characters
  * Synonyms should not contain $, @ characters
  * Brackets in active terms should balance and be sequentially valid (in correct order)
  * FSN terms should not duplicate other FSN terms, regardless of case
  * Synonyms should not duplicate other synonyms, regardless of case
  * Reference Sets should not contain retired concepts
  * Reference Sets should not contain retired descriptions

### FHIR API
- Add initial support for FHIR Search Parameters, like `_id`, `_name` (#806)
- Add initial support for FHIR `$validate-code` operation endpoints (#816)

### Bugs/Improvements
- [index] use segment diff to compute accurate base/head timestamps when merging empty branches (#807)
- [index] support indexing fields with type `double` (b275329)
- [index] don't create "null" strings when mapping results to String[] (c78a710)
- [index] support exlucing non-property type changes when comparing branches (#815, #818, #825)
- [index] cache rootField and fieldPaths to prevent recomputing them when processing larger commits (1cb9e6a)
- [index] compute property diffs only for FSNs during change processing to reduce memory pressure (fe6ed78)
- [core] improve concept map compare comment merge phase to avoid exponential complexity and never ending compares (67d20d6)
- [snomed] fixed an issue where importing released version of a component did not set the `released` flag to `true` after RF2 import (#802)
- [classification] commit classification relationship/concrete value changes in batches (#809)
- [quality] fix major and minor code smells (#819, #824) Thank you for the contribution @eranhash!

### Dependencies
- Upgrade [snomed-ecl](https://github.com/b2ihealthcare/snomed-ecl) to 1.4.3 (#826)

## 7.16.2

### Bugs/Improvements
- [core] support automatic merging of conflict-free nested objects and arrays (#790, #791)
- [snomed] register reactivated concepts for taxonomy updates (#792)
- [snomed] use "broken bone" icon for morphologic abnormalities (cfe08be)
- [api] disable versioning of upgrade codesystems (#789)
- [cli] improved formatting of 'snowowl codesystems' command (#761)

### Dependencies
- Bump Eclipse Platform to 2020-09 (#779)

## 7.16.1

### Bugs/Improvements
- [index] support fields alias migration on index level (#783)
- [snomed] exclude inactive OWL Axioms when calculating concept's definition status (#785)
- [validation] consider `moduleId` when calculating reference set member duplication in SIMPLE, LANGUAGE, ASSOCIATION and ATTRIBUTE reference sets (#782)

## 7.16.0

### Core
- Introduce `commitWatermarkLow` and `commitWatermarkHigh` configuration settings to raise warning log entries for large (low mark) and very large (high mark) commits (#771)
- Introduce `CappedTransactionContext` to cap commits to a certain threshold and commit in batches to reduce commit sizes (#771)
  * Versioning terminology content uses this new feature to reduce the amount of changes that go into a single commit
- Introduce new boolean prefix query type (#773)

### SNOMED CT API
- SNOMED CT Description search now uses the new boolean prefix query type to offer better results and scores when only the last word represents a partial/prefix match (#773)  

### Bugs/Improvements
- [snomed] fixed an issue where an unpublished description inactivation would keep the descriptions acceptabilities set incorrectly (#776) 
- [validation] avoid creating duplicate issues in MRCM constraint type rule (#772)

### Dependencies
- Bump jackson-databind 2.10.5.1 to resolve a security vulnerability issue (#766)
- Bump netty to 4.1.59.Final to resolve a security vulnerability issue (#758)

## 7.15.3

### Bugs/Improvements
- [core] extract common score normalization logic and enable it for all documents (#770)

## 7.15.2

### Bugs/Improvements
- [core] prevent unnecessary branch delete attempt in case of regular version create requests (8a076e6)
- [core] add comments to Concept Map Mapping (#767)
- [core] fix lowerCase method in StringUtils (#768)
- [core] return total added/changed/removed branch compare counters based on number of components instead of changes (#768) 

## 7.15.1

### Bugs/Improvements
- [core] compare only active mapping members in Concept Map Compare (#759)
- [core] make branch name validation messages more brief (#760)
- [snomed] decrease color depth of icons to 32 BPP (895ef2b)
- [validation] update description of rule 671 (#763)
- [cli] add `ldap` commands to verify LDAP configuration (#764)

## 7.15.0

### Core
- Code System Upgrade (beta) (#755, #757)
  * New API to work with specific Code System branches and environments, especially dependency version upgrades (eg. SNOMED International version upgrades)
  * Support full lifecycle of Code System upgrades (start -> complete)
  * Support expanding `extensionOfBranchInfo()` and `upgradeOfBranchInfo()` in Code System upgrades
- Support `force` flag in version create requests (#746)
  * Allow recreating the latest published Code System version (optionally reusing the same effective date) with `force` flag

### Bugs/Improvements
- [api] ignore component not found exceptions during delete requests
- [core] change `Date` types to LocalDate in EffectiveTime arguments, APIs (#749, #754)
- [core] potential NPE when searching for concept maps (#748)
- [snomed] add moduleId to classification statement fragments for extension specific filtering (#751)
- [snomed] change module of inactivated content to the current module in request scope (#752)
- [build] bump Tycho to 2.2.0 (#745)

## 7.14.3

### Bugs/Improvements
- [index] fix content visibility issues when creating branch from parent with non-squash merge sources (b442723, 0cf45c6)

## 7.14.2

### Bugs/Improvements
- [index] improve commit performance by reducing number of terms in ID queries and increase batch size during bulk updates (6700c4f, b02ae99, eee60a7, 8cd6723)

## 7.14.1

### Bugs/Improvements
- [core] fix NPE in shutdown hook (#741)
- [snomed] fix a potential exception in component effective time restore (4cd52c7)
- [api] add elapsedTime to Job API representation (e8069b1)
- [api] add missing async request timeout handler to exception mapping, HTTP 504 (3ebf813, dfa9a72)
- [api] increase default request timeout to `300s` (be258f9)
- [api] support custom `codeSystemId` when upgrading (52a521a)
- [api] add initial support for content donation during upgrades, migrated from 6.x (#739)

## 7.14.0

### Core
- Code System Upgrade (experimental preview version) (#735)
  * Upgrades to newer Code System dependencies now can be started from the new `POST /codesystems/:id/upgrades` endpoint
  * The full upgrade flow is not available yet, it is scheduled for release 7.15.0
  
### Bugs/Improvements
- [core] validate LDAP settings during boot (#731)
- [core] support truly random Base64 IDs (not just time-based random Base64 IDs) (#733)
- [core] support filtering commits by branch prefix (Java API only) (#734)
- [snomed] properly restore effective time on Code System upgrade branches using both the dependency version and the previous extension version (#735)
- [snomed] disable validation of map target field during RF2 import (075a575)
- [api] fix concept search issue when searching by `semanticTag` without a non-empty `term` filter (1f77112)

## 7.13.0

### Core
- Support filtering CodeSystem codes by their kind (aka CodeType) in the Generic Concept Search API (#727)
- Support simple DSV export for Concept Map Compare (not exposed over REST yet) (#725)

### SNOMED CT
- New `response` property in `GET /:path/import/:id` RF2 Import API return value (#720, #699)
  * `response.success` - `true` if the import successfully completed, `false` otherwise  
  * `response.error` - has a non-empty String value if the import can not be performed due validation errors or due to other errors 
  * `response.defects` - lists all defects encountered during the RF2 Import Validation phase
  * `response.visitedComponents` - lists all components that have been updated during the RF2 import
- Support `dryRun` in RF2 import API, `POST /:path/import` (8d3c9ac, #699)
  * If set to `true` the importer validates the incoming RF2 archive against the current data and reports any defects. Default value is `false`. 

### Bugs/Improvements
- [index] fix minor performance issue when collecting search hits to field maps (b4728f7) 
- [core] include changed containers in changedComponents as well in branch compare (a7ea7df)
- [core] filter out ROOT object IDs from compare result (15249f5)
- [validation] improve unpublished performance of rule 663 (368542d)
- [validation] improve unpublished performance of rule 664 (e149a4d)
- [validation] improve unpublished performance of rule 668 (245c1c7)
- [validation] improve unpublished performance of rule 671 (79c3e9f)
- [snomed] validate effective time slices in reverse chronological order to improve import performance (6384209)
- [api] fix possible NPE when searching jobs via REST (cc704a8)

## 7.12.0

### Core
- CodeSystems
  * Support filtering Code Systems by their HL7 OID (#710)
  * Make OID optional in Code System Create API (#710)
- Validation
  * Support `resourceURI` instead of just `branchPath` on validation issues (#707, #708)  
- Jobs
  * Support filtering jobs by multiple user values (8c307db)
  * Support filtering jobs by their state value in `GET /jobs` (8db3fa6)
  * Add `DELETE /jobs/:id` endpoint to delete jobs (853299b)
- Commits
  * Add support for searching for commits by timestamps (#722)

### Bugs/Improvements
- [index] clean up and remove unused logic from index module (493cd47, 6d53e5b, 57232cb, 2aa99f5, 07d09ed, fdb64ad)
- [index] support array property tracking properly (#709)
- [index] omit copy and move operations to properly migrate index schema (2595716)
- [index] exclude items from the "from" change set early to prevent invalid conflicts (#718)
- [index] fix potential resource leak when performing multiple searches with Integer.MAX_VALUE (aka `all()`) limit without explicit pagination (860be3a)
- [index] replace `_id` based sorting with `_doc` when sorting documents by default (44d4218, 2249b3c, bbd2497, 115153c)
- [index] use default `node.roles` instead of deprecated `node.master=true` in embedded EsNode (28ca645)
- [index] dynamic string values will be mapped to `keyword` by default (fe51c28)
- [core] fetch only one CodeSystem in CodeSystemService (5299092)
- [core] support SETs as primary components in certain toolings (58536b5)
- [core] make sure we send out commit notifications about merge/rebase commits (f429ac7)
- [core] Add property to ConceptMapMapping for indicating if a match is approximate (#724)
- [validation] apply unpublished effective time filter in snomed-query validation rules only if the `isUnpublishedOnly` rule parameter is set to `true` (12ce787)
- [validation] increase minimum number of validation threads to `4` (405010f)
- [validation] remove aggregation from rule669 to improve its performance (1675105) 
- [api] add configuration to return all matches at once in Concept Map Search API (0cbccfc)
- [api] support filter by description type in generic concept search API (de3777d, #714)
- [api] support scores in generic concept search API (ee3f55c, #714)
- [performance] speed up classpath scanning by restricting packages to `com.b2international` (#717)
- [log] truncate collection request parameters to the first 10 items and remaining count (df069b0)

### Dependencies
* Bump Elasticsearch to 7.10.0 (#719, #721)
* Bump classgraph to 4.8.90 (f14336d)
* Bump owl-toolkit to 2.9.1 (c0f2c23)

## 7.11.1

### Bugs/Improvements
- Fix rare branch rebase conflict due to multiple changes on the same property (#706) 

## 7.11.0

### Core
- New, configurable term search API (#701)
- Added `stop-word` search to Generic Concept Search API (65cc672, #701)
- Improved Concept Map Compare API and algorithm (#702, #703, #704)
  * Support equivalence configuration, supported values are, `codeSystem`, `code`, `term`

### SNOMED CT
- Support `effectiveTime` force updates on concept/description/relationship REST API (2dfbe7c)
- Added `stop-word` search to SNOMED CT Description Search API (65cc672, #701)

### Bugs/Improvements
- [index] use Elasticsearch auto-generated IDs for revision documents to increase indexing perf (bab9cc0)
- [core] support file names when executing groovy scripts (f56cfc8)
- [snomed] fix potential StackOverflowError when evaluating large OR'd ID ECL/QL expressions (6c666a5)
- [classification] cache computed direct/indirect destination edges to speed up classification process by ~80% (#700)

## 7.10.1

### Core
- Add `minTermMatch` and `fuzzy` search support to Generic Concept Search API (#684)
- Expose `squash` property for advanced branch merge scenarios (#686)
- New configuration keys to configure index bulk commit sizes (#690, 38bc1bd):
  * `repository.index.bulkActionSize` - the number of bulk action requests to send in a single batch to the configured Elasticsearch cluster, defaults to `10.000`.
  * `repository.index.bulkActionSizeInMb` - the maximum bulk request size (in megabytes) to send to the configured Elasticsearch cluster, defaults to `9MB`.

### Bugs/Improvements
- [index] reduce the number of edge_ngram prefixes generated to `2-12` (#684)
- [index] fix potential branch rebase issue when there were no actual tracked changes on both branches (#696)
- [index] remove deprecated `_hash` field (#691)
- [core] always render SNOMED CT Metadata concepts with their preferred synonym (PT) (#685) 
- [snomed] fix RF2 FULL export issue when exporting SNOMED CT Extensions in a deep branching scenario (#689, 57f1fdf)
- [releng] improve test code coverage numbers, clean up unused functionality, types, libraries (#694)

### Dependencies
- Bump Elasticsearch to 7.9.2 (#682)
- Bump Hibernate validator to 5.3.6 (#644)

## 7.10.0

### Core
- Code Systems
  * Support analyzed (tokenized, exact, prefix) `name` property searches in Code System Java API (#667)
- Concept Maps
  * Support fetching concept map mappings by reference source and target IDs (#660)
  * Improve Concept Map model to include the following attributes: `containerIconId`, `containerTerm`, `targetIconId` (#679)
  * Limit Concept Map compare results to `5000` per change category (#654)

### Validation
- Make some SNOMED CT validation rules available in the OSS version (#666, #675)
- Allow users to customize display labels in FSN via Validation Java API (#674) 

### Bugs/Improvements
- [index] add the ability to change the search time analyzer to something else than the default specified on the index field mapping (#669)
- [index] serialize nested objects as JSON during commits (75d2139)
- [index] apply recognized property diff conflict resolutions as JSON patch directly on the source document (#680, 3b7fddc)
- [core] support ID prefix and ID regexp queries (21eacab)
- [classification] log unexpected errors during classification (#673)
- [classification] fix registration of GCI, reflexive, transitive axioms (#673)
- [snomed] fix `owlExpressions()` expansion issue (857db34) 
- [cis] port some CIS ID registration leftover fixes from 6.x (5f3f9f0, 0341b5d, 1e7e965)

## 7.9.2

### Bugs/Improvements
- Fix incorrect reuse and auto-generation of inactivation indicator members (#664)

## 7.9.1

### Bugs/Improvements
- Support multi-valued (List, Set, Array) String properties on JSON documents in history and automatic conflict resolution (f39e115)
- Fix branch rebase issue when there are no semantic changes between the two branches (#652, 0d9f55a)

### Dependencies 
- Upgrade Elasticsearch to 7.9.1 (#653)

## 7.9.0

### SNOMED CT
- Enforce order of preferred descriptions: US preferred FSN, GB preferred FSN, all other Synonyms grouped by type (#645)

### Validation
- Improve performance of rule 671

### Bugs/Improvements
- Improve ConceptMap Compare performance (#637, #639)
- Improve ConceptMap Mapping Java API (#640)
- Add new convenience method to Dates Java API (#642)
- Fix SNOMED CT icon calculation (#645)
- Add support for CHANGE vs. REMOVE case during branch compare (#646)
- Avoid creating duplicate description inactivation indicators (#648)
- Make return value of commit timestamps more flexible (#649)

### Dependencies
- Upgrade Elasticsearch to 7.9.0 (PR: #641, Fixes GH issue: #626)

## 7.8.4

### Bugs/Improvements
- Further improvements and fixes related to rebasing/merging changes between child-parent branches
- Make sure we only register actual changes in commits and not everything when merging from another branches 

## 7.8.3

### Bugs/Improvements
- Fix another revision duplication issue when rebasing a child branch on top of the parent branch (#638)
- Fix missing context when using certain commands in Snow Owl CLI (#635)
- Improve performance of conflict processing, reaching super fast execution times (#634)  

## 7.8.2

### Bugs/Improvements
- Fixed issue when merging deletions to a branch with related components

## 7.8.1

### Bugs/Improvements
- Fix rule 110 issues (#627)
- Fix revision duplication after rebasing a branch (#629)
- Fix incorrect inactivation of concept components (#629)
- Migrate automated validation script to 7.x (#628)

## 7.8.0

### Core
- Branching/Commits (#621)
  * New `squashMerge` field has been added to Commit documents to detect squash vs. fast-forward merges (this index schema change is backward compatible in most cases, feel free to report [here](https://github.com/b2ihealthcare/snow-owl/issues/new/choose) if not in your case)
  * Properly calculate the state of a branch compared to another branch even if the branch has been synchronized with the other branch a couple of times. Take actual commits into account before deciding whether a branch is in forward or behind state.
  * Fixes #594
- Add generic ConceptMap Mapping Java API search functionality (#610) 
- Add generic ConceptMap Compare Java API functionality (supports SNOMED CT map type reference sets) (#610)

### SNOMED CT
- Support for setting target code system for Map type refsets (#609)

### Validation
- Add OWL Axiom support to the validation rules (#611) 

### Bugs/Improvements
- Fix Boolean compare in SNOMED CT component status check (#622)
- Fix issue with CodeSystemURI calculation on CodeSystem type (#624)
- Ensure rule 668 returns only OWL Axioms members (#620)

### Dependencies
- Upgrade Elasticsearch to 7.8.0 (#615)
- Upgrade Netty to 4.1.50.Final (#606) 

## 7.7.0

### Core
- Branching
  * Support more URL safe characters in branch names (#590)
- Generic Concept Suggestion API (#569, #571, #574, #588)
  * Based on CodeSystem specific similarity algorithms the system is now able to provide similar concepts for a set of input concepts
- Generic Query Optimization API (#581)
  * Based on CodeSystem specific query optimization algorithm the system is now able to optimize queries in a generic fashion
  * SNOMED CT concept enumerations can now be reduced to a few clauses if they have a common ancestor
- Generic Member Request API (#558)

### Validation
- New Validation Rule (#580): 
  * Active descriptions on inactive concepts should have a single inactivation indicator member

### SNOMED CT
- IconId calculation improvements (#560):
  * SNOMED CT Concept Icon computation logic have been improved to take semanticTags of the concept into account.
  * SemanticTag takes a precendence over the hierarchical placement
  * `iconId` property will be set to a semantic tag if there is an icon for it

### Bugs/Improvements
- [core] add new, file-based upload/download methods to Attachment API (6f3a946)
- [core] new, generic ExportRequest Java API (a021783)
- [core] consider deleted branches when creating CodeSystems (#565)
- [api] allow `CodeSystem.locales` update to empty array if it's `null` (00fafec)
- [api] fix issue with special search syntax `@` parsing (#566)
- [api] allow creating SNOMED CT Concepts without any relationship/axiom (c9e0aeb)
- [snomed] fix potential NPE in during locale to refset id list conversion (afffbef)
- [snomed] add shortcuts to ECL evaluation (#568)
- [snomed] fix issue without duplicate concept non-current members appearing on descriptions (#584)
- [snomed] fix RF2 import job key issue (#577)
- [snomed] properly update OWL Axiom Expression in member update API (7c02b1e, 0a4e09e, 3c0ad0e)
- [snomed] fix RF2 import content vs release type validation, allow importing on child branches if MAIN is empty (2fa6ea8)  
- [runtime] decrease embedded Elasticsearch disk threshold monitor values (#570)
- [runtime] add more curator configuration for backup and restore (#595)
- [tests] update mini RF2 from 2018-01-31 to 2020-01-31

### Dependencies
- Upgrade Elasticsearch to 7.7.0 
- Upgrade Lucene to 8.5.1
- Upgrade Equinox to 3.20.
- Upgrade Eclipse Platform to 4.15.0.
- Upgrade Groovy Eclipse to 3.7.0 (e4.15.0).
- Upgrade Net4j to 4.9.0.
- Upgrade Orbit update site to `R20200224183213`.
- Upgrade Xtext/Xtend to 2.21.0.
- Upgrade EMF to 2.21.0.
- Upgrade MWE2 to 2.11.2.
- Upgrade Guava to 27.1.0.
- Upgrade classgraph to 4.8.78
- Upgrade Jackson Databind to 2.9.10.4
- Upgrade assertj to 3.16.1
- Upgrade fastutil to 8.3.1.
- Upgrade commons-codec to 1.13.
- Upgrade Eclipse Tycho to 1.7.0
- Remove unused dom4j.

## 7.6.0

### Core
- CodeSystem API improvements (#546)
  * Support `additionalProperties` to associated custom values with your Code System
  * Support `locales` - which in case of SNOMED CT auto-populates itself during RF2 import
  * Support `availableUpgrades()` expansion on Code Systems to show when an upgrade to a newer dependency is available
  * Support Code System URIs in `extensionOf` property to explicitly depend on a versioned release of another Code System
  * Support `extensionOf` property updates via Code System Update endpoint

### Configuration
- Support configuration of max allowed upload file sizes and limits (#544)
  * New configuration keys are `api.http.maxFileSize`, `api.http.maxRequestSize`, `api.http.maxInMemorySize`
  * Increase the default allowed upload file size to `1gb`
- Support advanced configuration of the LDAP identity provider (efdb7e4)
  * Support different `roleBaseDn` than `baseDn`. Defaults to `baseDn` value.
  * Support customization of `userFilter` and `roleFilter` search queries for advanced users
  * Replace `rootDn` and `rootDnPassword` with `bindDn` and `bindDnPassword` configuration keys (the old settings are deprecated and will be removed in Snow Owl 8.0).

### SNOMED CT
- New ECL/QL labeler Java API to label up IDs in an expression (#541)
- Support setting start/end effective times in automated RF2 export script (#549)

### Packaging
- Reduce the size of the OSS Docker image (#542)

### Docs
- Improve LDAP documentation (#548)
- Add backup and restore documentation (a2faa3b)

### Bugs/Improvements
- [index] skip transient fields when creating index mapping (9f9f948)
- [snomed] fix incorrect state when importing RF2 file without the the Job API (5b0e4af)
- [snomed] fix occasional error in RF2 export when exporting deep branches, extensions (#550)
- [mrcm] fix authorization issue with MRCM import/export (#554)
- [api] prevent duplicate language refset IDs when converting a list of extended locales to list of language refset ids (6eebee1)
- [api] fix incorrect swagger representation of some String values (#552)

## 7.5.1

### Core
- Bump Elasticsearch to the latest 7.6.2 release [`#530`](https://github.com/b2ihealthcare/snow-owl/pull/530)

### SNOMED CT
- New special search expression syntax in selected filters (d04df18)
  * `GET /:path/concepts` endpoint's term filter now accepts special filter expressions `@field(value)`
  * Examples:
    * `@ecl(<!138875005)`
    * `@module(900000000000207008)`
    * `@namespace(1000000)`
    * Any other filter expression can be used as special `field` in the syntax
- Add degree-of-interest scoring to the Concept REST API search endpoints (ef52ee7)
- Restore effectiveTime during RF2 Delta imports [`#532`](https://github.com/b2ihealthcare/snow-owl/pull/532)

### Docs
- Initial version of SNOMED CT Extension Guide, see [docs](https://docs.b2i.sg/snow-owl)

### Bugs/Improvements
- [api] path expressions not working [`#534`](https://github.com/b2ihealthcare/snow-owl/pull/534)
- [core] fix registration of additional TerminologyComponent types in registry [`#533`](https://github.com/b2ihealthcare/snow-owl/pull/533)
- [scripts] update SNOMED CT auto RF2 export script [`#536`](https://github.com/b2ihealthcare/snow-owl/pull/536)

## 7.5.0

### Core
- New `/:path` variable expression `<CODESYSTEM>[/<PATH>]` is available on all endpoints (#511)
  * `<CODESYSTEM>` is the shortName ID of a Code System
  * `<PATH>` is one of the following values:
    * `LATEST` - special value that represents the latest released version of the codesystem. This is the default value if PATH is omitted. Examples: `SNOMEDCT` (implicit latest) or `SNOMEDCT/LATEST` (explicit latest).
    * `HEAD` - special value that represents the latest development version of the codesystem. Examples: `SNOMEDCT/HEAD`
    * `<versionId>` - an explicit `versionId` that matches one existing version of the Code System. Examples: `SNOMEDCT/2019-01-31` or `SNOMEDCT-UK/2019-10-31`
    * `<branch_path>` - any other path value will be treated as relative path to the CodeSystem's current working branch (`CodeSystem.branchPath`). Examples: `SNOMEDCT/a/b`.
- Commit API
  * Commits now include timestamp value in a String typed property, `timestampString` (37a4f09, 0392e54)
  * Expose `timestampFrom` and `timestampTo` filters (76c8f51)
- Allow full customization of LDAP Identity Provider (#513)
  * Support customization of user and role object classes (defaults are `inetOrgPerson` and `groupOfUniqueNames`)
  * Support customization of permission and member properties (defaults are `description` and `uniqueMember`)
  * NOTE: The selected default values are available in both OpenLDAP and AD 

### SNOMED CT
- New RF2 Import/Export APIs (#517)
  * NOTE: The old APIs are still available and functional (a few unused properties were removed)
  * `POST /:path/import?createVersions=true&type=full`
    * Initiates an import with the specified config values and a multipart/form-data file
    * Returns the Location header of the import task with the import ID
  * `GET /:path/import/:id` - Retrieve the status of the import task using its ID
  * `DELETE /:path/import/:id` - Delete an import task using its ID (NOTE: executing on a running import will mark the task as CANCEL_REQUESTED, but cancelletation is not supported)
  * `GET /:path/export?type=full` - single RF2 export endpoint 
  * The new APIs do not use any in-memory objects and can be safely used in scenarios when there are multiple Snow Owl instances working together on the same dataset
- New RF2 Export REST API (#517)
- Remove deprecated `acceptability` filter option from SNOMED CT Description API (d70f9e7)
  * Use `acceptableIn`, `preferredIn` or `languageRefSet` alternatives instead

### Validation
- Add new duplicate reference set member rule (#516, 996630a)

### Bugs/Improvements
- [api] new exception handlers added to ControllerExceptionMapper (#517)
- [api] fix malformed Location header values (36974a9, #500)
- [api] fix potential NPE when using sortBy with null value (16af94a)
- [api] fix potential NPE when using ID filter with null value (92dfa22)
- [core] Removed FeatureToggle system (superseded by jobs and locks) (#517)
- [core] do NOT perform empty commit when staging area is not dirty (caeaef3)
- [core] add all objects as changed to the commit notification (beede99)
- [core] allow deleted version branches to be reused for versioning (1d908f1)
- [index] add the current revision to the commit if marked as revised even if the diff is empty (4cf64df)
- [index] fix performance issue when checking Elasticsearch cluster and index health (7ba2aff)
- [snomed] fix possible NPE when not supplying any acceptability values (d168d18)
- [snomed] Do not unset effective time in SnomedConceptUpdateRequest for subclass definition status changes (32a189a)
- [export] allow exporting members with inactive referenced components to be exported to DSV (21fcfc6)
- [deps] Bump jackson-databind from 2.9.10.1 to 2.9.10.3 (#509, 7fa55b5)

## 7.4.0

### Changes from 6.x stream since 7.3.0 release

All changes from the `6.x` stream (the 6.25.0 release) have been merged into the `7.4.0` release. See changelog entry `6.25.0` for details.

### Elasticsearch 7.x upgrade
The supported Elasticsearch version has been upgraded to `7.5.2` (see #478, e19ed34). 
Elasticsearch 7.5.2 is backwards compatible with Elasticsearch 6.x, so any dataset that has been created with an earlier Snow Owl 7.x release should be compatible with this release.

### New Swagger Documentation
From Snow Owl 7.4.0, the API documentation can be found at the URL `http://localhost:8080/snowowl`. (see 418feef)
This new documentation will gather and render all available groups from all available modules.   

### Core
- Add `id` filter to `/commits` API (121d15a, #473)
- Support `affectedComponentId` in `GET /commits` `details()` expand parameter (af8da80, #473)
- Implement RepositoryTransactionContext#clearContents (a540784)
- Change default REST API timeout to 2 minutes in most of the endpoints (ee51b29)

### Configuration
- Introduce `repository.maxThreads` configuration key (90ddade)
  * Replaces `numberOfWorkers` configuration key
  * Default value is set to `200`
  * The underlying worker pool will start with less threads and grow/shrink based on the number of incoming requests
  * Excess threads will terminate after 1 minute (4577b2a)
- Remove unnecessary `systemUser` configuration key, mostly used by tests (24d3785)  

### SNOMED CT
- Add support for "complex map with map block" type reference (b0ce5ce, 824c3b8)
- Allow filtering reference set members by complex `mapBlock` property (4a03c51)
- Support for `module()` expand in all component API endpoints (cd3e5b3)
- Support expansion of `definitionStatus()` in `GET /concept` API (f4ba3e6, 97f1ff9)
- Support `acceptabilities()` expansion in `GET /descriptions` APIs (ecba5d6)
- Support `caseSignificance()` expansion in `GET /descriptions` APIs (81659ce)
- Support `characteristicType()` expansion in `GET /relationships` APIs (a0cd98f)
- Support `modifier()` expansion in `GET /relationships` APIs (a0cd98f)
- Redesign `inactivationProperties` in component endpoints (c0abbdc)
- Support unpublished effectiveTime filter in REST API via `-1` and `Unpublished` values (#485, ebd5521)
- Allow descriptions to be created without any language reference set members (e2fa3c5)
- Change type of `SnomedConcept.definitionStatus` to `SnomedConcept` (f4ba3e6, 2435c7c)
- Migrate validation rule from `6.x` stream to `7.x` (#471)
- Make sure we time out from ECL evaluations after 3 minutes (09293f4)

### FHIR
- Change ValueSet `compose` property cardinality to `0..1` (e4c4407)

### Bugs/Improvements
- [core] increase embedded Elasticsearch max_buckets to `1.500.000` temporarily (e164183)
- [core] fix unauthorized issue when executing console commands (14fe6b2)
- [core] prevent unnecessary mapping updates during startup (9f14138)
- [core] fix calculation of RevisionCompare added/changed/removed numbers (9384003)
- [core] fix incorrect paramType value for nested query parameters (5222a5f, #441)
- [core] fix RevisionSegment.withEnd(newEnd) logic, fixes `branch@timestamp` queries in certain deep branching scenarios (4651965)
- [core] respond with HTTP Method Not Allowed properly in REST API (5a16c02)
- [core] improve performance of deletion of huge amounts of validation issues (b5a9f16)
- [snomed] run effective time restore only in non-import scenarios (2eb05b2)
- [snomed] fix OWL expression conversion issue (c49ed26)
- [snomed] fix incorrect conversion of `null` values to empty filter values in refset API (refSetType, referencedComponentType and mapTargetComponentType filters) (f737c14, #482)
- [snomed] unset effective time of inactivated members properly during save (inactivated by another component inactivation) (307879a, b60acf1, eb988de)
- [lgtm] fix errors/warnings reported by LGTM (eacca94, 61f79f5, 4659b04, 0d00a34, c63c1a2, c4d3995, 37b4685, 8f331c2, 5497d95, e018278, a438fe1, e3fdf56, c6368dc, 72faec7, 5456f5a, 204632c, cb17a33, b808eed, 7d8fb27, 325ce93, e02857b, 64623ef, a7226e1, fc2e4b1, 0b769ac)

## 7.3.0

### Changes from 6.x stream since 7.2.0 release

All changes from the `6.x` stream (the 6.24.0 release) have been merged into the `7.3.0` release. See changelog entry `6.24.0` for details.

### Core
- Support unprotected API routes/requests (d07e6c2, 2e5166a)
  * `GET /admin/info` and `POST /admin/login` are unprotected routes
- Add back `responseTime` property (unit is milliseconds) to request log (57b913d)

### SNOMED CT
- SNOMED CT computable languages are now supported (#470)
  * [ETL 1.0](http://snomed.org/etl)
  * [SCG 2.3.1](http://snomed.org/scg)

### FHIR
- Exclude mapping members by default from ConceptMap responses (0ed6b1c, 14a1ad5, 67d1e03, 376391a, )

### Bugs/Improvements
- [core] simplify raw index read requests in Java API (5214922)
- [jobs] reduce memory requirements of job clean up (b43d658)
- [log] improve error logging from failed API requests, omit Broken pipe errors (edbc7f9)

## 7.2.0

### Changes from 6.x stream since 7.1.0 release

All changes from the `6.x` stream (since 6.19.0) have been merged into the `7.2.0` release. See changelog entries from `6.19.0` to `6.23.0` for details.

### Docker build

Snow Owl 7.2.0 now supports Docker based deployments via the official image. See [here](https://hub.docker.com/r/b2ihealthcare/snow-owl-oss). 

### Known issues
- Query parameters are not supplied to `Try it out` requests from the Swagger UI (see GH issue: #441)

### Core changes

#### API
- New Swagger API design and navigation menu (#419)
- Generate proper commit notifications after successful commit (32ce42d)
- Improve revision merging by introducing Git-like behaviour when merging a branch into another (e3d2da5)

#### Authentication & Authorization
- New `POST /login` endpoint to authenticate a user and get back a JWT token for further API calls (#403)
- Support `X-Author` header in most transaction endpoints (#403)
- New bearer token based authentication support (#403)
- New permission system to allow fine grained `operation:resource` based authorization (#403)
- API endpoints that do not require permissions: `GET /admin/info`, `GET /admin/jobs`, `GET /admin/repositories` (#413)

#### Rate limiting
- New rate-limiting configuration options are available (#409)

### SNOMED CT changes

#### API
- Add support for refset member filtering based on the following properties: `acceptabilityId`, `valueId`, `correlationId`, `descriptionFormat`, `characteristicTypeId`, `typeId`, `mapCategoryId`, `domainId`, `contentTypeId`, `ruleStrengthId`, `mrcmRuleRefSetId`, `relationshipGroup`, `mapTarget`, `grouped` (beede5f)
- Support ECL expressions in the following refset member property filters: `characteristicTypeId`, `correlationId`, `descriptionFormat`, `mapCategoryId`, `targetComponentId`, `targetComponent`, `valueId`, `mrcmDomainId`, `mrcmContentTypeId`, `mrmcRuleStrengthId`, `mrcmRuleRefSetId`, `owlExpressionConceptId`, `owlExpressionDestinationId`, `owlExpressionTypeId` (7b3d21c)
- Allow creation of SNOMED CT Concepts without relationships (fc81d59)

#### RF2
- Validate `referencedComponentId` values when importing RF2 RefSet files (93167d2)
- Register IDs in CIS after successfully importing RF2 content (908d053)

#### Classification
- Add ELK as default classifier (ee68a94)

### Scalability and reliability
- Health check now includes ES cluster and indices health statuses (2a6628b)
- Use retry policy to detect ES cluster outages and retry a few times before failing the request (97fc936, d5d8418) 

### Plug-in development
- New easily extensible REST API modules (#403)

### Bugs/Improvements
- Add shutdown hook to gracefully shut down Snow Owl (00faaae)   
- Fix NPE in list branches OSGi command (80bc0c5)
- Fix potential NPE in case of communication failure with ES cluster (d00e56e)
- Fix `modifiers` field access issue on Java 12 (93cb94b)
- Fix branch counter initialization issue causing issues after restart (277bf10)
- Do NOT index revisions when there are no changes (66c3f0e) 
- Fix `namespace` field indexing issue (a09c44c)
- Fix MRCM constraints deletion issue (88de20b)
- Fixed reported GH issues: #422, #424, #436, #442, #444, #450

### Dependencies
- Bump Jackson to 2.9.10
- Bump SnakeYAML to 1.25
- Bump Spring to 5.1.9
- Bump Springfox to 2.9.2
- Bump Swagger to 1.5.23
- Bump Swagger UI to 3.24.3
- Bump micrometer to 1.3.2 
- Bump Tycho to 1.5.0

## 7.1.0

### Breaking changes

This section discusses the changes that you need to be aware of when migrating your application to Snow Owl 7.1.0 (from any previous 6.x or 7.x version).

#### Upgrade to Java 11

Starting from `7.1.0`, Snow Owl requires Java 11 both compile and runtime as minimum Java version (see https://jdk.java.net/archive/ for OpenJDK downloads).

#### Changes from 6.x stream since 7.0.0 release

All changes happened on the 6.x stream (between `6.10.0` and `6.19.0`) has been merged into the `7.1.0` release.
Major breaking changes that require a full RF2 re-import of any existing 7.0.x data:
- Concrete Domain Reference Set Member changes from #288 (https://github.com/b2ihealthcare/snow-owl/releases/tag/v6.11.0)
- SNOMED CT Query Language Support (https://github.com/b2ihealthcare/snow-owl/releases/tag/v6.12.0)
- OWL Axiom support (https://github.com/b2ihealthcare/snow-owl/releases/tag/v6.14.0)

#### Packaging and platform changes

Due to the move to Java 11, Snow Owl can no longer depend on `Eclipse Virgo with Tomcat` server as OSGi container to provide its services and functionality.
Instead, it builds upon a bare Equinox OSGi runtime (Eclipse 4.12, Equinox 3.18.0) running a Jetty (9.4.18) web server.
This resulted in a much faster startup time and development of third party terminology plug-ins.

#### Configuration changes

Important configuration changes that require attention when migrating to Snow Owl 7.1.0:
- Configuration key `snomed.ids` has been replaced with the new `cis` root configuration node (#379)
- `snomed.language` configuration key has been removed. APIs, commands now accept a list of `locales` in preference order to compute display names/labels/etc. (2ef2a4a) 

### CIS package and API
- Snow Owl 7.1.0 introduces partial support for the [official CIS API](https://github.com/IHTSDO/component-identifier-service)
 * See full release notes [here](https://github.com/b2ihealthcare/snow-owl/releases/tag/v6.18.0)

### FHIR
- Supported FHIR API version has been upgraded to [FHIR 4.0.0](https://www.hl7.org/fhir/history.html)
 * See additional documentation at the [FHIR API docs](http://docs.b2i.sg/snow-owl/api/fhir)

### Debian
- Snow Owl 7.1.0 adds support for Debian systems with a dedicated `.deb` package.

### Dependencies
- Bump Eclipse Platform to 4.12
- Bump Jetty to 9.4.18
- Bump Elasticsearch to 6.8.2
- Bump Classgraph (formerly `fast-classpath-scanner`) to 4.8.43
- Remove Virgo packaging and dependencies

## 7.0.0

### Breaking changes

This section discusses the changes that you need to be aware of when migrating your application to Snow Owl 7.0.0.

#### Datasets created before 7.0.0
Snow Owl v7.0.0 does not support indexes created by Snow Owl 6.x stream anymore. 
Migration from Snow Owl 6.x to 7.x is still work in progress, in the meantime if you would like to try Snow Owl 7.0.0 out, we recommend starting from scratch from an RF2 export from your Snow Owl 6.x instance or by importing an official RF2 distribution. 

#### Database
MySQL RDBMS software requirement has been removed and Snow Owl no longer requires it for its data source. 
Instead, Snow Owl requires only a single Elasticsearch cluster to operate on.

#### Documentation
The new improved and shiny Snow Owl 7.x documentation is available at `https://docs.b2i.sg/snow-owl/`

### Added
- FHIR v3.0.1 API support (https://www.hl7.org/fhir/http.html)
  * New Swagger API endpoint collection is available at `/snowowl/fhir`
  * It supports read-only capabilities of `/CodeSystem`, `/ValueSet` and `/ConceptMap` concepts
- APIs
  * New `/stats` endpoint to scrape [micrometer](http://micrometer.io/) based statistics for [prometheus](https://prometheus.io/)
  * New `/commits` endpoint to query commits in a repository and for a given component
  * New `/compare` endpoint to efficiently compare two branches
  * New `<branch>@<timestamp>` branch path expression support to query a branch at any arbitrary point in time
  * `UTF-8` encoding to all endpoints
- SNOMED CT
  * New `JSON` based MRCM export and import format
  * Refactored classification services, using Elasticsearch indexes instead of custom Lucene store
- Configuration
  * `SO_PATH_CONF` environment variable to configure Snow Owl configuration folder
  * Environment variable substitution is now supported in `snowowl.yml` configuration file via `${...}` expressions
  * Added `monitoring.tags` support for tagging metrics with custom tags
- Packaging
  * Travis-CI build integration (https://travis-ci.org/b2ihealthcare/snow-owl/)
  * `tar.gz` packaging for Unix/Linux systems
  * RPM packaging for RPM based systems (like CentOS, RedHat, etc.)
- Modules
  * New plug-in mechanism with the help of classpath scanning to simplify development of third-party modules
  * `com.b2international.snowowl.fhir.core`
  * `com.b2international.snowowl.fhir.api`
  * `com.b2international.snowowl.snomed.fhir`
- Dependencies
  * Added zjsonpatch `0.4.4`
  * Added micrometer `1.0.6`
  * Added picocli `3.5.1`  
  * Added fast-classpath-scanner `3.1.6`
  * Bumped Spring to `4.3.10`
  * Bumped Protege to `5.0.0-beta21`
  * Bumped SLF4J to `1.7.13`
  * Bumped Logback to `1.1.3`

### Changed
- Revision control features have been rewritten from the ground up to support scaling to billions of revision documents (using IPv6 based addressing)
- SNOMED CT RF2 importer APIs now use the new RF2 importer implementation
- Console
  * Completely rewritten using the awesome `picocli` library with full version, help support, POSIX-style grouped short options and more
- Configuration
  * Renamed `snowowl_config.yml` configuration file to `snowowl.yml` and move it inside the `configuration` folder
  * Renamed `metrics` node to `monitoring`

### Removed
- SNOMED CT
  * XMI based MRCM import/export functionality has been removed (remaining options are `CSV` and `JSON`) 
- Modules
  * `org.eclipse.emf.cdo.*`
  * `org.eclipse.net4j.db.mysql`
  * `com.b2international.snowowl.snomed.model`
  * `com.b2international.snowowl.snomed.refset.model`
  * `com.b2international.snowowl.snomed.mrcm.model`
  * `com.b2international.snowowl.server.console`
  * `com.b2international.snowowl.datastore.server`
  * `com.b2international.snowowl.snomed.datastore.server`
  * `com.b2international.snowowl.snomed.exporter.server`
  * `com.b2international.snowowl.snomed.reasoner.server`
  * `com.b2international.snowowl.snomed.importer`
  * `com.b2international.snowowl.snomed.importer.rf2`
  * `system.bundle.package.exporter`
- Configuration
  * `yaml` and `json` file extension support for `snowowl.yml` configuration file
  * `resources/defaults` XML configuration folder and support 
  * Removed `database` configuration options from `repository` node
  * Removed `revisionCache` configuration option from `repository` node

## 6.25.0

### API
- Support `definitionStatusId` in `POST /concepts` endpoint (8552b99)
- Set definitionStatus to primitive automatically when inactivating a concept (8552b99) 

### Bugs/Improvements
- [cis] allow ID registration of already published IDs (ccc225d)
- [cis] do not report unauthorized and forbidden errors in the log (28ac77a)
- [cis] add CIS SCTID status update bash script (96776cc, f78ea65)

## 6.24.0

### API
- Support filtering refset members by `mapPriority` (a0bff3d)

### Bugs/Improvements
- [index] Log the number of pending cluster tasks (959a088)
- [index] fix query boosting issue (2de4841)
- [core] Allow customization of component adjustment during publication (a0e6820)
- [history] Ignore many-valued features altogether in HistoryInfoProvider (526d3e7)
- [snomed] Fix incorrectly categorized query refset evaluation changes (#467)
- [snomed] Fix incorrect parentage values when reactivating concept's relationships first then the concept in two different commits (5298252)
- [cis] accept all SNOMED CT ID statuses when publishing them (c914c05)
- [mrcm] Handle ECL expressions in attributes' concept set definitions in MRCM validation rules (0cc7b61)
- [export] Create general ExportResult class (filename, UUID pair) (8265617)

## 6.23.0

### API
- Support OWL Axiom (`owlRelationships()`) expansion on OWL Reference Set Members

### Validation
- Support OWL Axiom Members in certain SNOMED CT Validation Rules (#455) 

### Bugs/Improvements
- [reasoner] multiple changes to SNOMED to OWL conversion (28f6615)
- [reasoner] fix group numbering issue in classification (8678901)
- [validation] handle no result when evaluating query based validation rules (1cedb3b)

## 6.22.0

### Bugs/Improvements
- [api] add filter by `mapGroup` member property (76560db)
- [api] Add proper throws declarations for attachments api methods (adc2ed7)
- [ecl] support BooleanValueEquals and NotEquals data comparisons in concrete domain member refinements (3399d33)
- [validation] duplicate reference set member validation rule (#420)
- [classification] cancel classification task properly after cancelling the corresponding remote job (05bf99b)
- [jobs] Fix NPE for canceled but scheduled jobs (173fcb6)

## 6.21.0

### API
- Support filtering refset members by OWL expression (#407)

### MRCM
- Add new concept set definition type (#408)

### Jobs
- Improve job filtering API (#411)
- Add auto cleanup option to job schedule (#406)

### Validation
- Rename common validation rules (#410)

### Bugs/improvements
- Fix refset member duplication issue (#412)
- Allow adding inactive query refset members (#414)

## 6.20.0

### API
- Expose special member property based filtering over REST API (5fde911)

### CIS
- Add data synchronization script and migration guide to CIS product (d645ce1, 5f01750)

### Scripts
- New auto RF2 import script to automatically import RF2 files via REST API (#398)

### Validation
- Supporting deletion of validation issues by tooling and branch (db9b492)
- Add published/unpublished filtering to validation issues (#391) 
- Properly filter unpublished members in rule `snomed-common-4` (#394)
- Add new description character length validation rule (#400)
- Handle reference sets properly in validation issue detail expansion (#402)

### Bugs/Improvements
- Revert fix `[api] inactivate all language members when inactivating a SNOMED CT Description`
- Allow duplicate relationships in DSV export (#393)
- Handle descriptions during equivalent concept merging during classification (#392)

## 6.19.0

### Bugs/Improvements
- [api] allow description reactivation with RETIRED (aka empty) inactivation indicator
- [api] inactivate all language members when inactivating a SNOMED CT Description
- [api] allow creating inactive concepts and descriptions
- [export] avoid issues due to duplicate data during DSV export
- [export] compare source and target effective dates of latest module dependencies when calculating effective time for RF2 export 
- [validation] update snomed common rule 4's description
- [classification] track redundant relationships as well in hasInferredChanges flag

## 6.18.0

### CIS
- New CIS product and module is now available (#379)
  * It partially supports the [official CIS API](https://github.com/IHTSDO/component-identifier-service)
  * Specifically the single and bulk ID operations, retrieval and authentication endpoints
  * See the new `/snowowl/cis` Swagger API documentation page
  * Configure it under the new `cis` root configuration node

### Validation
- Support filtering of validation white list entries by `created_at` and `affectedComponentLabel` (#376, #381)
- New common validation rule to check duplicate preferred FSNs and Synonyms in any language reference set (#382)

### Configuration
- Disable TCP transport when setting `repository.port` to `0` (740c904) 
- Configuration key `snomed.ids` has been replaced with the new `cis` root configuration node (#379)

### SNOMED CT
- Create SNOMED CT Extension aware module and namespace assignment algorithm (#380)

### Bugs
- [reindex] Recreate index document of branches that were created after the very last commit (#375)
- Fix SNOMED CT OWL Toolkit and Axiom conversion issue (c381c07)
- Exclude inactive query members from query refset evaluation (#383)

## 6.17.0

### API
- Allow querying owl expressions by either `type` or `destination` ids (9ab1048, 958ee81)
- Allow setting pending move inactivation indicator on SNOMED CT Descriptions of new SNOMED CT Concepts (63f4b1f)
- Remove logic that disallowed reactivating a concept while adding an inactivation indicator and/or association target(s) (86ed3c7)
- Support `defaultModuleId` for all enum based member updates (#368)
- Remove minimum number of required SNOMED CT Relationships validation from SNOMED CT Concept create request (9ac5c99)

### Console
- New `user adduser` command has been added to the available OSGi commands (#374)

### Configuration
- Support id exclusion list (#372)
  * Read and use all ID exclusion files under <SNOWOWL_HOME>/configuration/reservations folder
  * Format: SNOMED CT Identifiers separated by new line character(s)  

### Validation
- Support whitelist entry labels (#369)

### Changes
- Let the axiom conversion service create 'SubObjectPropertyOf' and 'SubDataPropertyOf' axioms (e9b9c34)

### Bugs
- [api] Fix refset member search request parameter (a76c0d7)
- [api] lookup moduleId when creating refset member (41adfc5)
- [api] fix potential race condition when building bulk request from multiple threads (9d1b64e)
- [console] fix `rf2_refset` import OSGi command (054da61)
- [import] Fix error when importing SNAPSHOT without any valid data (69b9bda)
- [reasoner] Add missing concept IDs to the relationship module and namespace collector in the equivalent concept merging phase (a042274)
- [reasoner] Skip axiom relationships where either the source or the destination is inactive (96c4494)
- [reasoner] Fix initial state of SCHEDULED classification task (733e4b8)
- [validation] Remove stated MRCM rule (2657abd)

### Dependencies
- Bump SNOMED OWL Toolkit to 2.6.0 

## 6.16.0

### REST API changes
- New `HTTP POST` `/search` endpoints have been added for `/:path/concepts`, `/:path/descriptions`, `/:path/relationships`, `/:path/members`  
- Add sort query parameter to all search endpoints that have internal support for field-based sorting (`/concepts`, `/descriptions`, `/relationships`, `/refsets`, `/members`, `/classifications`, `/branches`)
- Add term based sorting to concepts endpoint (`sort=term[:asc|desc]`)
- Add `parent`, `ancestor`, `statedParent`, `statedAncestor` based filtering directly to `/:path/concepts` endpoints
- Add `id` filter to all component search endpoints
- Expose `refsetIds` filter to the `RF2` Export REST API and make `branch`, `modules` and `refsets` configurable in the auto RF2 export script
- Expose `/reasoners` endpoint to get available reasoner from the REST API
- Add `owlExpression.conceptId` and `owlExpression.gci` filters to `/:path/members` endpoints

### Configuration
- Make classification `excludedModuleIds` property configurable via `snowowl_config.yml`

### Validation
- Support term highlighting in Validation Framework

### Changes
- [search] increase `query` column length to 8192 characters in MySQL Database schema

### Bugs
- [api] fix missing JSON/CSV Validation issues endpoints in Swagger UI
- [api] allow encoded slash in GET request URLs
- [docs] fix styling of client and server errors section
- [search] fix nested SNOMED CT Query evaluations
- [search] fix ascending flag bug for script-based sorts
- [reasoner] fix missing `ontology list` OSGi command
- [reasoner] fix classification issues after applying OWL changes from `20190731` alpha release
- [export] replace tab/newline chars in `owlExpression` to spaces

## 6.15.0

### Added
- New `com.b2international.snowowl.snomed.icons` module with default SNOMED CT Concept images (0d444fa, ac0123e)
- New MRCM based type validation rule and stated version for all MRCM rule (#344)
- SNOMED CT API improvements:
  * New `semanticTag` filter on `GET /:path/concepts` endpoint (0f304ec)
  * New `semanticTag` filter on `GET /:path/descriptions` endpoint (0f304ec)
  * New `caseSignificance` filter on `GET /:path/descriptions` endpoint (0f304ec)
  * `characteristicTypeId` and `modifierId` have been added to `SnomedRelationship` JSON representation (66bf620, 763e1ee)
  * Changed type of array values of `parentIds`, `ancestorIds`, `statedParentIds`, `statedAncestorIds` to `String` from `long` (81f7628)

### Changed
- Updated OWLAPI to support more recent reasoner versions (#342)

### Removed
- `defaults` directory support (6468aa1)
- `.xtend` files from all modules except `com.b2international.snowowl.snomed.ecl.tests` (#345)

### Bugs
- Fixed definition status calculation issue (1dbb3c5)
- Excluded whitelisted response items in `GET /validations/:id/issues` endpoint (#347)
- Fixed incorrect Location URL returned in `POST /validations` endpoint (#347)
- Fixed incorrect (non-null) effective time value after changing `targetComponentId`/`valueId` on active reference set members (5959e88)
- Fixed missing ECL cardinality conversion service in SNOMED CT Query Language infrastructure (706ba1c)
- Fixed incorrect wrapping of `IdRequest` in SNOMED CT transactions (#349) 

### Dependencies
- Bump OWLAPI to 4.5.10 (#342)
- Bump Protege libraries to 5.0.3-b2i (see repository https://github.com/b2ihealthcare/protege) (#342)
- Bump ELK reasoner to 0.4.3 (7bdb231)

## 6.14.2

### Changed
- Change `moduleId` of updated relationship or concrete domain member using the currently set module assigner algorithm (#341)
- Redirect Elasticsearch log messages to SLF4J logger instead of logging to stdout (86fdc02)  

### Bugs
- Fix incorrect parent/ancestor array values on inactive SNOMED CT Concepts (#343)
- Fix random code system allocation bug in e2e SNOMED CT test cases (03b6001)

## 6.14.1

### Bugs
- Fix issue with SNOMED CT RF2 Snapshot file imports (c2a5bcd)

## 6.14.0

### Added
- OWL Axiom support 
  * Compute stated tree form based on OWL Axiom `SubClassOf` definitions
  * Query OWL Axioms when evaluating ECL expressions on stated form
  * Hybrid mode on definition status updates (either updates an existing OWL Expression member or the Concept's `definitionStatusId` property)
- Effective time restore functionality for SNOMED CT Reference Set Members
- Support for relationship group updates in classification
- Evaluation of ECL expressions on stated form
- New generic validation rule to report relationships with incorrect characteristic types

### Changed
- Report only active relationships with inactive reference as conflicts upon merge/rebase
- Severity change in MRCM range validation rule

### Bugs
- Fix repeated header in Validation DSV Export API
- Fix various issues with Simple/Map type DSV Exports
- Fix incorrect update of concrete domain MRCM predicates
- Fix `session disconnect` command bug
- Properly disconnect user after failed login attempt to prevent `Already logged in` exceptions

### Performance
- Improve performance of SNOMED CT Bulk updates with lots of Reference Set Member updates
- Improve performance of restore effective time functionality

### Dependencies
- Add SNOMED CT OWL Toolkit 2.3.2
- Upgrade Spring to 4.3.22
- Replace Swaggermvc 0.9.3 with Springfox 2.8.0
- Upgrade rest-assured library to 3.2.0
- Upgrade mapdb library to 3.0.7
- Upgrade SLF4J to 1.7.25 
- Upgrade Logback to 1.2.3
- Upgrade Fastutil to 8.2.2 
- Upgrade Tycho to 1.2.0

## 6.13.1

### Added
- New MRCM attribute range validation rule (#319)

### Changes
- Numerous improvements to classification time equivalent concept merging functionality (#318)

### Removed
- Acceptability merge conflict rule (#321)

### Bugs
- Fix incorrect update of members when more than 50 members were present for a concept (b224370)
- Fix occasional startup failure due to incorrect initialization phase used in the reasoner module (#325)
- Fix DSV export group occurence bug (c494229)

### Performance
- Normal form generation performance improvements (#318)

## 6.13.0

### Added
- SNOMED CT Validation API (#307)
  * Validate the content of SNOMED CT with custom queries and scripts
- Support sorting of validation issues by label (8b59181)
- SNOMED CT Query Language improvements (#306)
  * Support `active`, `moduleId` filters on `Concept` and `Description` components
  * Support `languageCode`, `typeId`, `caseSignificanceId`, `preferredIn`, `acceptableIn` and `languageRefSetId` filters on SNOMED CT Descriptions
  * Support regular expressions and exact term matching in `term` filter
  * Add `{{...}}` syntax to match the official SNOMED CT Query Language draft syntax

### Performance
- Ignore property chain hierarchies collection if no type IDs make use of this functionality, to speed up normal form computation (a31ce0c)
  
### Bugs
- Replace line break and tab characters with empty spaces when exporting `query` and `term` fields in RF2 (#304, 920a0e2)
- Fix issues with Simple type Reference Set DSV export (#309)
- Fix invalid validation errors when trying to import SNAPSHOT RF2 with Unpublished effective times (#308)
- Fix errors when trying to classify relationships with inactive source/destination concepts (fa540a8)
- Fix errors when trying to expand inactive `ancestors` or `descendants` in SNOMED CT Concept API (86f0aa0)
- Fix UUID validation in SNOMED CT RF2 import validator (#315)

## 6.12.1

### Changed
- Make classification requests more customizable/configurable (#305)

### Bugs
- Fix RF2 import lock timeout issue (#303)

## 6.12.0

### Added
- SNOMED CT Query Language feature (#298)
  * The initial version of the language supports the full `ECL v1.3` and description `term` filters
  * `filterByQuery` method has been added to `SNOMED CT Concept Java API` 
  * `query` parameter has been added to `GET /:path/concepts` API
- Customizable SNOMED CT RF2 export bash script has been added to automate RF2 export tasks (#299)  
- Support RF2 packages where the OWLExpression files names are using the new file naming convention (c3de2d0)  
- Environment variable substitution is now supported in `snowowl_config.yml` configuration file via ${...} expressions
- Support ECL expression in filterBy `languageRefSet`, `preferredIn` and `acceptableIn` description filters. (7709d5a)
- `locales` parameter to history API (04cb537)

### Changed
- Apply classification changes from 7.x (#300)

### Removed
- `snomed.language` configuration key has been removed. APIs, commands now accept a list of locales in preference order to compute display names/labels/etc. (2ef2a4a)
- Deprecated `Export RefSet to Excel` functionality has been removed (ac9927d)
- Deprecated `RF1` and `RF2` exporter implementations (46d22e1)

### Bugs
- Fixed hot backup file path bug (23f896a, 2fff0f6)
- Fix relationship affected component label bug in SNOMED CT validation (d69b56f)
- Fix a few bugs in SNOMED CT Reference Set DSV export (94ccf64)
- Improve performance of RF2 import by reducing the amount of loaded available components during init (2772cb2) 

## 6.11.0

### Breaking changes

This section discusses the changes that you need to be aware of when migrating your application to Snow Owl 6.11.0.

#### Concrete Domain Reference Set Member changes (#288)

Property groups now can contain concrete domain properties as well, not just relationships (new column `relationshipGroup`). Also, the `attributeName` property has been renamed to `typeId` and all concrete domain labels (attribute names) must be converted to valid, existing SNOMED CT Concepts in order to support new concrete domain schema. In case you did not had any concrete domain members and you are not planning to use this feature, you can safely use your existing dataset without issues. In other cases, feel free to contact [B2i](mailto:support@b2i.sg) to support your migration to Snow Owl 6.11.0.

### Added
- Add `repository.index.clusterName` configuration key to customize the clusterName of the embedded ES instance (#281)
- Add `so.index.es.useHttp` system property to enforce usage of HTTP connection to the embedded ES instance (#281)
- Support for TCP based connection to remote Elasticsearch clusters (`clusterUrl` configuration key now supports both `tcp://` and `http://`) (#281)
  * _NOTE: TCP connection to a cluster does not yet support authentication_
- Java API methods to simplify synchronous execution of requests (8d0e15d)
- Support for `childOf` HierarchyInclusionType in MRCM rules (#287)
- Support for `dependencies` between two code system. A Code System now declare another as a dependency (#286)
- New Concept and Description inactivation indicators (102b127) 

### Changed
- Make SNOMED CT Description `term` field mutable (#284)
- Allow non-SNOMED CT identifier in `mapCategoryId` column (8b325be) 
- Export FSN in description term columns when exporting Mapping Reference Sets to DSV (#283)

### Dependencies
- Elasticsearch has been bumped to the latest 6.5 version (#281)

### Bugs
- Set `write.wait_for_active_shards` setting to `all` to fix inconsistencies in the underlying index when using replicas
- Fix serialization issue when using the class `SctId` (8284600)
- Fix missing clause for `referencedComponentType` filters (b98308a)
- Report a conflict when an inbound relationship references detached destination concept. Fixes object not found and versioning errors (a8ce29e) 
- Fix script arguments unrecognized by Elasticsearch (#289)

## 6.10.0

### Dependencies
- Bump Jackson modules to 2.8.11 and Jackson Databind module to 2.8.11.2 (fixes security vulnerability issues reported by GitHub)

### Performance
- Simplify and improve SNOMED CT Description term fuzzy matching functionality (9d7bce4) 

### Bugs
- Delete all types of referring members when component is deleted (130d938)
- Fix de/serialization of module dependency member fields (a5f4369)
- Validate versionId before creating Code System Version entry (39efad0) 
- Fix reference set DSV import related issues (bb3efa0, bc8ec3b)
- Fix potentional validation issue duplication (7956697)
- Fix error when trying to revert effective time of an RF2 component without any released versions (7f16873)
- Use single-node discovery in embedded mode by default (38075a3)

## 6.9.0

### Added
- Support for unpublished component only validation (#271)
- Inbound relationship expand for SNOMED CT Concept search and get requests (#267)
- Configuration option for Elasticsearch cluster health request timeout (2819c8c)

### Changed
- Consider additional relationship types as well when computing MRCM rules for a concept (110f2e3)
- `sourceEffectiveTime` and `targetEffectiveTime` reference set member properties serialized as effective time instead of dates (8e0e830) 

### Removed
- Merged `com.b2international.snowowl.index.api`, `com.b2international.snowowl.index.es` and `com.b2international.org.apache.lucene` bundles into a single `com.b2international.snowowl.index` module (#269)

### Performance
- Improve evaluation of ECL queries targeting large set of focus concepts (c73fd72, 35e1380)
- Improve execution of Concept search requests with both ECL and TERM filters (530eb15)  

### Bugs
- Fix FUZZY + TERM filter bug when the term consist only of escaped characters (0cc2c4c) 
- Fix NSEE when attempting to export an RF2 package with no SNOMED CT versions yet in the system (c79dc21)
- Fix server startup issue due to a bug in startup script (2530f67)
- Ignore 404 responses thrown by Elasticsearch REST client (c6be1d2)

## 6.8.0

### Added
- Support SNOMED CT component effective time filter in Validation Issues Search API (#260)
- Support affectedComponent label filter in Validation Issues Search API (20962f2)
- Complete `searchAfter` paging API support for REST API endpoints (#261)
- A new `Other` Validation Rule severity type (#263) 
- Support basic authentication configuration values in Elasticsearch configuration in case of connecting to a remote cluster (#264)

### Changed
- Use serialized `String` values for `searchAfter` parameters in search Java APIs (#261)
- RF2 export archive and file effective times are now calculated based on module dependency refset entries of the selected module IDs (dc959e1)

### Bugs
- Fix attribute group cardinality bug in ECL evaluation (#259)
- Use socket timeout as retry timeout in Elasticsearch HTTP REST client (34a6eb4)
- Fix SSH connection to OSGi console (5e9cdff)

## 6.7.0

### Added
- A new `deploymentId` configuration key to specify both DB and Index name prefix to support multi-tenant deployments (#256)
- Support MRCM reference set member properties in `SnomedRefSetMemberSearchRequest` (a14b9d9) 

### Changed
- Use the high-level Elasticsearch REST Client to communicate with either an embedded note or remote cluster (#251)  
- Check all potential reference set member properties where a component ID might be referenced during module dependency updates (17b3a2a)

### Dependencies
- Bump Virgo package version from `3.7.0.M3` to `3.7.2` (#253)
- Bump Elasticsearch version from `6.0.1` to `6.3.2` (#251)

### Performance
- Improve SNOMED CT RF2 export performance (#249)
- Reduce number of documents to load when using `snomed-query` based validation rules (4f980a1) 

### Bugs
- Fix cache cleanup (memory leak) in `SnomedEditingContext/SnomedRefSetEditingContext` (d97bf3e)
- Fix occasionally failing unit tests due to index refresh bug in `EsDocumentSearcher` (2d3cd7f)
- Fix incorrect `RF2` archive effective date when using `endEffectiveDate` filter in `RF2` Delta exports (cd54af3)

## 6.6.0

### Added
- Configuration options for validation thread pool sizes and parallel execution of rules (#246, 09c971e)
- Support field selection in low-level aggregations API (b429a46, a59cb7e)

### Changed
- Support delimiter parameter in DSV exports (7ad7d6f)
- Support latest specification changes in OWL Axiom and Ontology Reference Sets (#248, 72994dc)
- Increase number of default shards for revision indexes to `6` (11ca54d) 

### Bugs
- Use Groovy Eclipse release update site instead of snapshot (0e15090) 
- Fix missing FSNs, PTs in RefSet DSV exports, change default file names to PT of the Reference Set (1bb3fe3, 3f7cd3e, af6d9d1)

### Performance
- Reduce memory consumption and execution time of large scale validation jobs (#246, 9e35a24)

## 6.5.0

### Breaking changes

This section discusses the changes that you need to be aware of when migrating your application to Snow Owl 6.5.0.

#### Datasets created before 6.5.0   
All datasets created before 6.5.0 require a full `reindex` due to changes in the MRCM document schema.

### Added
- API:
  * Add `iconId` property to SNOMED CT component representations on all endpoints (90ccb2e)
  * Add `limit` query parameter to `GET /branches` endpoint (e7fcaf7)
  * Support released flag filter in SNOMED CT component search API (091b8ea)
  * Support `typeId` filter in `descriptions()` expand parameter (https://github.com/b2ihealthcare/snow-owl/pull/235) 
  * Support support filtering members by set of referenced component IDs via `GET /:path/members`  (710c894)
- SNOMED CT:
  * Support new MRCM reference set types (https://github.com/b2ihealthcare/snow-owl/pull/187, https://github.com/b2ihealthcare/snow-owl/pull/231)
  * Support new OWL reference set types (https://github.com/b2ihealthcare/snow-owl/pull/187, https://github.com/b2ihealthcare/snow-owl/pull/231)
  * Support a dedicated Simple map with mapTargetDescription reference set type instead of reusing Simple map type (https://github.com/b2ihealthcare/snow-owl/pull/222)
  * Support bulk itemId generation (7279d1f)
- Validation:
  * Add `resourceDir` script argument to Groovy-based validation scripts (da44b75)

### Changed
- Improved donated content detection and resolution during SNOMED CT Extension upgrade (and merge) (https://github.com/b2ihealthcare/snow-owl/pull/185)
- Redesigned MRCM constraint document schema (https://github.com/b2ihealthcare/snow-owl/pull/236)
  * Add support for source-only object mappings
  * Add concept set definitions, predicates and attribute constraints from the MRCM Ecore model as document snippets
  * Add domain-level representation for all parts as well
  * Support the interpretation of the extended domain models in clients

### Bugs
- Fix missing argument when checking cluster health status (abf0dca)
- Fix deletion of SNOMED CT Reference Set Members referring to other components (https://github.com/b2ihealthcare/snow-owl/pull/227)
- Fix deletion of unreleased but inactive reference set members (https://github.com/b2ihealthcare/snow-owl/pull/232)
- Don't update certain descriptions twice in a change set (1de6633)

### Performance
- Over 80% reduction in time for large scale changes (e.g. for updating batches of content using templates). (https://github.com/b2ihealthcare/snow-owl/pull/230, f958f53, 6b58d0a, e0d041a)
- It now takes under 30 seconds to 1) create and save 10,000 new concepts with descriptions and an IS A relationship to SNOMED CT 2) Update all 10,000 concepts, changing their module and 3) Update all 10,000 concepts again, inactivating the relationship to SNOMED CT and adding a new one to Clinical finding. (see [test case](snomed/com.b2international.snowowl.snomed.api.rest.tests/src/com/b2international/snowowl/snomed/api/rest/perf/SnomedMergePerformanceTest.java))
- Decrease execution time of scroll requests, especially when ECL evaluation is involved (39e78a5)
- Decrease execution time of branch merge operations (243509d)
- Reduce memory requirement of large scale validation requests (2abae78)
- Reduce execution time of e2e tests (b3c824c)

## 6.4.0

### Breaking changes

This section discusses the changes that you need to be aware of when migrating your application to Snow Owl 6.4.0.

#### Datasets created before 6.4.0
All datasets created before 6.4.0 require a full `reindex` due to changes in all codesystem schemas.

### Added
- Add inferred and stated parent ID arrays to the SNOMED CT Concept representations (a5f1f1f)
- Support revision expression values in search requests path parameters (6e5ab16) 

### Changes
- Reintroduce revision hashing to support proper calculation of change sets between two branch points (#219)
- Allow locally running applications to access embedded ES instance (adbf017)
- Allow SNOMED CT Descriptions as simple map referenced component types (52b6ca9)

### Bugs
- Fix ID Filter bug in search requests (773b241)
- Reduce amount of memory allocated when deserializing SNOMED CT index documents (98c4f2f, 2d4d749)
- Fix incorrect scroll state checks when scrolling documents (597f36b)
- Skip logging of script arguments to prevent possible memory leak (3c0c578)
- Properly prevent deletion of released components (#217)
- Fix line duplication issue of RF2 export (c7c802e)
- Fix ECL evaluation issues in RF2 export process (5fcec36)

### Performance
- Improve index search request execution significantly (5f6d4fe)
- Improve performance of bulk member create requests (#216)
- Remove classification results from memory when saving changes (8d2456f)

## 6.3.0

### Breaking changes

This section discusses the changes that you need to be aware of when migrating your application to Snow Owl 6.3.0.

#### Datasets created before 6.3.0
All datasets created before 6.3.0 require a full `reindex` due to changes in SNOMED CT index schema. 

### Added
- Support multiple language code files in RF2 import (#194)
- Support locale specific term based sorting in SNOMED CT Concept API (#199)
- Support running a selection of validation rules instead of all of them (#196, #212)
- Include Additional relationship types when exporting Reference Sets to DSV (#208)
- Support expansion of `preferredDescriptions()` in SNOMED CT Concept API
- Support HEAD requests on `/snowowl/admin/info` endpoint (d8e90e5)
- Track inactive memberships of SNOMED CT core components in `memberOf` index field

### Changed
- Improve SNOMED CT RF2 Export API (#210)
- Allow SNOMED CT Relationships with inactive source/destination to be imported (#205)
- Reference Set identifier concept inactivation automatically inactivates members (e445053)
- Changed default CDO's soft reference based revision cache to time/size eviction based Guava Cache (#199)
- Field selection now uses indexed `docValues` instead of `_source` to improve response time of search requests (2895245)

### Removed
- Deprecated `filteredrefset` API (998368f)

### Bugs
- Fix conceptToKeep selection logic from equivalent concept sets (#200)
- Fix singleton module/namespace assigner bug in SNOMED CT Classification (#202)
- Fix branch timestamp update when importing RF2 with unpublished content (#203)
- Fix and simplify module dependency member collection logic (#191, #214)
- Reduce memory usage of revision compare (9d5b355)
- Reduce memory usage of RF2 import (a3642e9)
- Improve Validation Whitelist API performance (#206)
- Improve performance of Validation API (#209)
- Add `60s` timeout to `EventBus` address synchronization (3cfb315)
- Fix CDORemoveFeatureDelta bug (0929669)
- Fix occasionally failing bulk updates in index commits (cba7e18)

## 6.2.0

### Added
- Set status of stale remote jobs to FAILED during startup (580d3e3)

### Bugs
- Fix missing searchAfter argument from revision index searches (56a5e03)
- Serialize ECL expressions in a synchronized block (5d05844)

## 6.1.0

### Added
- New, generic scripting API module (com.b2international.scripting.api)
  * Groovy implementation of the new scripting API module (com.b2international.scripting.groovy)
- SNOMED CT Validation API
  * Add Groovy based validation rule implementation and execution
  * Add white list support (#189) 
- `isActiveMemberOf` filter now supports ECL expressions in SNOMED CT Component search requests
- Add module and namespace assigner feature from `4.x` branch ()
- Deleted branches can be reused by creating a branch with the same path (parent + name) (dc53ade) 

### Changed
- Dependencies:
  * Kotlin OSGI 1.1.51 has been added
  * Groovy from `2.0.7` to `2.4.13`
  * Jackson from `2.8.6` to `2.8.10`
  * EMF JSON Serializer library has been removed

### Removed
- Bunch of deprecated, unused API and functionality, related commits:
  * 1de52b0
  * 268bc5d
  * 7a23851
  * 94db418
  * 2145d55
  * 7b63998
  * bbacfb5
  * 5d104f8
  * e8a3323
  * 6db7221
  * 4a790d8
  * a67de24
  * e274766
  * 56c9636
  * 613dd59
  * b39ffd2
  * c733563
  * 3f92263
- Modules:
  * `com.b2international.commons.groovy`
  * `com.b2international.snowowl.scripting.core`
  * `com.b2international.snowowl.scripting.services`
  * `com.b2international.snowowl.scripting.server.feature`

### Bugs
- Fix DSV import bugs (02180b4)
- Fix component not found exception thrown when trying to look up new components from transaction (97918c5)
- Fix HTTP method type when communicating with external identifier service (CIS) (32e9e85)


## 6.0.0

### Breaking changes

This section discusses the changes that you need to be aware of when migrating your application to Snow Owl 6.0.0.

#### Datasets created before 6.0.0
Snow Owl v6.0.0 does not support Lucene based indexes anymore. We've decided to remove that module completely in favor of the now fully supported and stable Elasticsearch based module.
All datasets (including the ones created with the experimental Elasticsearch module) need a full `reindex`. 
See [Admin Console Reference Guide](/documentation/src/main/asciidoc/administrative_console_reference.adoc#diagnostics-and-maintenance) for details.

#### API changes
Removed `offset` properties from all collection resource representation classes. `Offset+Limit` based paging is resource-intensive, therefore it has been completely removed. 
Your queries should either search for the topN hits or if you need to scroll a large result set, then use the `scrollId` returned in the collection resource (alternatively you can scroll a live result set using the `searchAfter` parameter).
Read more about scrolling here: https://www.elastic.co/guide/en/elasticsearch/reference/current/search-request-scroll.html

#### @Analyzed annotations
Replaced `@Analyzed` with `@Text` and `@Keyword` to better reflect Elasticsearch field types `text` and `keyword`.

### Added
- Generic Terminology Validation API (750806e, ed64eae, 6102b86)
- SNOMED CT Request API based Validation Rule Support (f67aee5)
- A new, improved, but **experimental** SNOMED CT RF2 importer implementation 
- SNOMED CT Java API:
  * Support filtering SNOMED CT Descriptions by their `semantic tag` (659234e)
  * Support filtering SNOMED CT Components by multiple `namespace` IDs (7a7a5c1)
  * Support filtering SNOMED CT Components by `module` ECL query and by module ID set (416d1a9)
  * Support filtering SNOMED CT Descriptions by `case significance` (ECL or ID set) (416d1a9)
  * Support filtering by `term regex` in description search (e47ae40)
  * Support multiple SNOMED CT Reference Set IDs in `isActiveMemberOf` filter
  * Support language refset, acceptableIn and preferredIn filtering in description search (59e7b57)
- SNOMED CT ECL
  * ECL implementation now compatible with latest v1.3 spec (https://confluence.ihtsdotools.org/display/DOCECL/Previous+Versions)
  * Support nested expressions in `memberOf` rules (v1.2 spec change)
  * Support nested expressions in attribute part of dotted expressions (v1.3 spec change)
  * Support nested expressions in attribute part of refinement expressions (v1.3 spec change) 
- Low-level index API:
  * Support regular expression queries (0cb1c1c786c0fd7bcbc4c7b1e7d54c64a5c0baad)
  * Support terms aggregations with tophits (50fd7338831dea7c4f84efc7aaefb4ee38e8ecd7)
  * Support `Map` and `String[]` return types in index search API (33c3bd9)
  * Scroll support to Index API (de439e9)
  * SearchAfter based paging support to Index API (ee36a25)

### Changed
- Java API changes:
  * Type of Remote Job properties `parameters` and `result` changed to `String` (contains a JSON serialized object)
  * Also added `getParameterAs` and `getReturnAs` methods to convert them to Java Objects easily
  * `Void` return types have been changed to Boolean (fixes unanswered client side requests)
  * `Empty`-ish values are accepted in `filterBy*` methods (empty `Collection`s and empty `String` values)
- SNOMED CT RF2 import console command now accepts a single Code System short name instead of a descriptor file (bd7aea3d56822b405feb4adbf039cd4ce4599729)
- SNOMED CT Identifier Generation:
  * Improve Sequential ID generation by skipping exponentially growing chunks of reserved/assigned IDs in order to find the next available ID faster (#180) 
- SNOMED CT Classification changes:
  * Enabled classification of concepts with the UK Clinical extension module
  * Improved performance of SNOMED CT Classification by keeping the initially computed taxonomy in memory until normal form generation and change (https://github.com/b2ihealthcare/snow-owl/pull/181)
  * Generate inferred relationship IDs in bulk [5.x] (#176)
- File Attachment API now accepts any kind of file not just zip files (3b814b2)
- Low-level Index API changes:
  * Improved low-level, fluent index Query API (cc7b5c1) 
  * Default number of index shards has been increased to 5 (Elasticsearch default value).
  * ES module now executes bulk updates in parallel (5c5159b601a47e0d0bfad10eee70745eaa9641f1)  
  * Scripting language from Groovy to Painless in index layer scripts (see https://www.elastic.co/guide/en/elasticsearch/reference/5.0/breaking_50_scripting.html)
- Dependencies:
  * Bump Lucene to 7.0.1
  * Bump Elasticsearch to 6.0.0
  * Bump Jackson to 2.8.6
  * Bump Netty to 4.1.13
  * Bump SnakeYAML to 1.17.0
  * Add Jackson CBOR dataformat 2.8.6
  * Remove Compression LZF

### Removed
- ESCG support (it was deprecated since the introduction of ECL queries, v5.4)
- Bunch of deprecated, unused API and functionality, related commits:
  * 1c5fe51
  * 3d6d189
  * 6ac0954
  * 895a792
  * 6d4cf50
  * 5c10b7c
  * f40d41e
  * 16748d7
  * 5bea2d3
  * dcefb54
- Modules (completely removed or merged into a corresponding core module)
  * `com.b2international.snowowl.index.lucene`
  * `com.b2international.snowowl.index.diff`
  * `com.b2international.snowowl.importer`
  * `com.b2international.snowowl.snomed.mrcm.core`
  * `com.b2international.snowowl.snomed.mrcm.core.server`
  * `com.b2international.snowowl.snomed.mrcm.core.server.tests`
  * `com.b2international.snowowl.authorization.server`
  * `com.b2international.snowowl.terminologyregistry.core.server`

### Bugs
- Retry update-by-query requests in case of version conflicts (4a4ecf1)
- Fix partial field loading issue in SNOMED CT Reference Set Member API (2302aa0) 
- Fix bootstrap initialization order issue by moving ClientPreferences init to Environment constructor (1a882e0)

## 5.11.5

### Changed
- Use ECL when creating/evaluating query type refsets (b433de3ff7af35ef8654b806e6fb774cf628c779)

### Bugs
- Fix TaxonomyDefect serialization issue (b44b90ab3d8f13bd9c33b6eef12dd1c697730552)

## 5.11.4

### Changed
- Disable classification of UK module entirely (81987250976baa11e4eff21d1310438c4c2bde90)

### Bugs
- Fix concept effective time issue when updating concept's components via concept update request (5ea826daf38c9cedd8a59110403e2e23ca9f0610)

## 5.11.3

### Added
- Support filter members by `mapTargetDescription` field (774bc0bf9463840db08f539b5d2e88b54032f2ce)

### Removed
- `com.b2international.snowowl.emf.compare` module

### Bugs
- Fixed missing remote job documents issue (00ca848f624b0879bbeba27995be98116506d0b0)
- Fixed IOOBE issues when sending bulk delete requests (9a9b166d5b86c110d6a0bc86d63d0e630305ebe7)
- Remote jobs now properly track progress via the monitor available in the given context (8524e0e0d179e6bba331919c69bd616029282fb7, 83debbec4f1e86e35910d910d9f1a3fb299ae4d7)

### Performance
- Improve rebase/merge performance significantly by disabling unnecessary taxonomy check rule (9cd76b9342cf014346aa3e6d125bb6d314cf99f1)

## 5.11.2

### Added
- `componentTypes` to SNOMED CT RF2 Export API configuration (https://github.com/b2ihealthcare/snow-owl/pull/173)

### Changed
- Allow multiple simultaneous RF2 exports (https://github.com/b2ihealthcare/snow-owl/pull/173)

### Bugs
- Exporting a single reference set with their members should export only members (https://github.com/b2ihealthcare/snow-owl/pull/173)

## 5.11.1

### Bugs
- Fixed CDO versions (builds now contain 4.1.10.b2i version of CDO bundles)

## 5.11

### Added
- `snowowl migrate` command to migrate terminology content from an external CDO repository (https://github.com/b2ihealthcare/snow-owl/pull/170)
- `UserRequests` Java API to fetch available User identities and their Roles/Permissions (https://github.com/b2ihealthcare/snow-owl/pull/168)
- Allow multiple identity providers (not just a single one)
- `com.b2international.snowowl.identity` has been added
- `com.b2international.snowowl.identity.file` has been added
- `com.b2international.snowowl.identity.ldap` has been added

### Removed
- Snow Owl JAAS file has been removed in favor of the new YAML based identity provider configuration (see the updated `documentation/src/main/asciidoc/configuration_reference.adoc`)
- `com.b2international.snowowl.authentication` module has been removed
- `com.b2international.snowowl.authentication.file` module has been removed
- `com.b2international.snowowl.authentication.ldap` module has been removed
- `com.b2international.snowowl.authorization.server` module has been removed

### Bugs
- Fixed inconsistent and exponentially growing CDO list indexes (https://github.com/b2ihealthcare/snow-owl/pull/171)

## 5.10.13

### Removed
- `com.b2international.snowowl.authorization` module has been removed

### Fixed
- Invalid Lucene startup script after reverting back to 5.9.x GC settings
- Initialization issue in authorization module (32d3e11a313b1f060601693c7df05d0e343c2192, a00d40c255635e4ba258d04b74a2f69655165bc7)

## 5.10.12

### Added
- `includeInactiveMembers` flag to include inactive reference set members in DSV exports
- GC logging configuration to Windows and Linux startup scripts

### Changed
- Revert back to 5.9.x JVM GC configuration (default GC performs better than CMS in most of our use cases)

## 5.10.11

### Changed
- Replaced permission ID constants with six default permissions (https://github.com/b2ihealthcare/snow-owl/pull/163)

### Removed
- Deleted obsolete task context API

### Bugs
- Fixed performance issue in SNOMED CT Description/Language RefSet RF2 export (https://github.com/b2ihealthcare/snow-owl/pull/158)
- Fixed memory leak in Lucene based index implementation (https://github.com/b2ihealthcare/snow-owl/pull/157)

## 5.10.10

### Bugs
- Fix zip decompression issue in FileUtils.decompressZipArchive method when there are no folder entries (8d144f4010e8a5c4a1735d0577a862065fe63a9b) 
- Fix Java serialization issue when using DSV export via the Java API (bd96aa60993e2fbef15a12a307d0a0db51adb874)
- Fix JSON serialization of ClassificationSettings, fixes missing remote job entries (df8c4e545d8b6b5af6d3ada90b97347a71069c58)

## 5.10.9

### Bugs
- Fixed issue with MRCM constraint indexing (782a6ad9bc63b003bb94e5fb23b18f548d901d3a)
- Fix missing terminology component extension point for MRCM rules (a00a7f0f492d2eacb9d63b3d0f6dbe6517072f71)
- Fix issues with language reference set export (c423bb741a5e3b78d0b7f04ee7eff294ce7c21fa, c877c9cda07200a1fa831be2df4e90974962af08)

## 5.10.8

### Added
- Support inactivation indicator and association target updates on active SNOMED CT concepts
- Support postRun application bootstrap phase (ea64e8b9fe8b172fa95fd409ca61a3fdc207c8dd)

### Bugs
- Fixed SNOMED CT Mapping Reference Set DSV export (dc1300e54cea6c6756d0898b62eca0bb26ff5b87)

## 5.10.7

### Changed
- Normalize description scores into buckets using the following order:
  * 1. Exact
  * 2. All terms present
  * 3. All term prefixes present

### Bugs
- Fixed ordering of hits returned by `/:path/concepts` and `/:path/descriptions` endpoints when filtering them by term
- Include a unique tiebreaker sort field (`_id`) in search requests (both Lucene and Elasticsearch index modules)
- Fixed stated/inferred expansion in TerminologyTree (d5f8b0fc19d754a5d2602b5d1a4607e928f620fd)
- Fixed score computation issue in index.lucene module (735551e372acd528123ec8c57f8a39fafed81ef0)
- Fixed doiFactor script to properly compute the final score of a doc (88a0d116f6eb5667f8f9b8e7bd39ec82d5241afb)

## 5.10.6

### Added
- New SNOMED CT DSV export Java API is available (see `SnomedRequests.dsv().prepareExport()`) 
- Support for SNOMED CT Reference Set Member filtering by mapTarget values (48e5179f7c70a54747177d5559bcd91c1c74bf5c) 
- Support for SNOMED CT Relationship filtering by modifier values (1dcc314707286dbe650346d26724d1160668a804)

### Bugs
- Fixed issue with `pt()` and `fsn()` expansion in `/:path/concepts` endpoint (26ce744a7f8d0948831135d84da729c42e285f86)
- Fixed CDO object deletion bug by backporting changes from eclipse/cdo commit (d7e9aa038ef50b296540620f7158f9b9b516f8d4)
- Fixed NPE thrown by Protegé module on shutdown (2e591d3148a3c81eb84d8756831d42dc5f94b093)
- Delete temp RF2 file after export finishes (reduces allocated disk space) (901750d3c1b1adf0565a8d6518f93bade3d56305)

## 5.10.5

### Bugs
- Fixed MRCM rendering issue (29ed90711b75462b277c72ce5d6b7c0e03367c57)
- Fixed version creation failure if complex map uses invalid map category ids

## 5.10.4

### Changed
- Add versioning parameters to job params object (7ac489c6f715bb7fec791deb78bd83b681440613) 
- Exclude UK products by their module ID from classifications (12164c8116701f9a21bce822810c2cfb9851b310)
- SNOMED CT Identifier service now properly moves all ASSIGNED ids to PUBLISHED and reports errors after the changes

## 5.10.3

### Changed
- Default JVM settings have been changed to recommended default elasticsearch settings (835cbc700022abdc68bf8f764d92794cf8a42254)

### Bugs
- Exact term matching should use match query instead of term query (11ffb349d2e2b778d36f0673217e0446990dc795)
- Fixed invalid parsed text predicate -> ES query mapping (72043fb0d9c9b53a31b666643e5bce270beb9241)

## 5.10.2

### Added
- SNOMED CT Description API now supports exact term matching via new option `EXACT_TERM` (3e33b974fd67671b5d8f616c2d6eeefca46ad885)
- SNOMED CT Reference Set Member API now supports user supplied UUID parameters

### Bugs
- Fixed serialization issue of SyntaxException (20dbbea9eb13acb14237864d9be69258ccd7bf1b)
- Fixed RemoteJobEntry result field mapping (3bc8b16febe593a6692873100ae32cb6f633ac40)
- Fixed invalid BoostExpression -> ES Query mapping (00c57a81a0bf4c18f0796eb7583b361af70a9fe6)

## 5.10.1

### Changed
- Elasticsearch module now properly configures dynamic mapping on Object/Map field types

### Bugs
- Searching for SNOMED CT Concept with termFilter throws score evaluation exception

## 5.10.0

### Breaking changes
- `_id` document fields now indexed with doc_values enabled
- `EMBEDDED` identifier service now operates on its own index, named `snomedids`
- SNOMED CT Description index mapping now uses more precise analyzers to provide the best possible text matches when search for description terms
  * Exact term searches now possible via the new `term.exact` index field
  * Prefix searches now return better results (via the new `term.prefix` field), because the mapping now uses the `edgengram` tokenfilter to generate `1..20` length prefixes for each SNOMED CT Description term
- NOTE: Due to the above mentioned changes, datasets created before 5.10.0 require a **full reindex**

### Added
- Full (but still **experimental**) Elasticsearch support (https://github.com/b2ihealthcare/snow-owl/pull/147)
  * Supported elasticsearch version is `2.3.3`
  * Snow Owl starts a local-only Elasticsearch node by default, but it is possible to configure the node to connect to a cluster of others
  * Configure the Elasticsearch node with the `configuration/elasticsearch.yml` ES configuration file
  * This index API implementation does not support the `SHA-1` `_hash` field on revision documents thus it cannot skip them from a revision compare result and will return them. Clients should query the base and head of the branch and remove the false positive hits manually.
- Configuration options
  * Added `numberOfShards` configuration option to repository.index node in snowowl_config.yml
  * Added `commitConcurrencyLevel` configuration option to repository.index node in snowowl_config.yml
  
### Changed
- Low-level Index API chanages (see https://github.com/b2ihealthcare/snow-owl/pull/147 for details)
- API changes:
  * JSON representations now include non-null values instead of non-empty
  * SNOMED CT Concept inactivation properties are no longer auto-expanded. The new `inactivationProperties()` expand parameter need to be added to the expand parameter list in order to retrieve them.

### Bugs
- Fixed memory leak when using `gzip` compression on the in-JVM Net4j connection
- Fixed invalid SNOMED CT Identifier state when supplying the identifiers instead of generating them (1e4488e35884cfa6f458471e73c7fd6943239722)

### Performance
- Greatly improved performance of index queries by using filter clauses in boolean queries instead of must clauses (eac279977bf2b0c49da949701136ba19260b5aef)

## 5.9.0

### Breaking changes
- Datasets created before 5.9.0 require a **full reindex** in case you would like to use the new branch compare API (or you are using the old review-based compare feature). 

### Added
- New branch compare Java API (https://github.com/b2ihealthcare/snow-owl/pull/145)
- Index documents now store a `_hash` field. The value is computed from the semantic content of the document using `SHA-1` (requires reindex).

### Changed
- Classification Java API now returns only SNOMED CT concept identifiers
- Include component type in commit notification's new/changed/deleted buckets (470f4e99e42d7c2c1c2e4159ea2003bc51c7aa08)
- Include version and codesystem changes in commit notification events
- Enable gzip compression by default on all Net4j protocols (https://github.com/b2ihealthcare/snow-owl/pull/146)

### Removed
- `compression` configuration option from RPC

### Bugs
- Branch compare now skips unchanged components even if they had changed, but got reverted to their original form in the meantime (fixed as part of https://github.com/b2ihealthcare/snow-owl/pull/145)

## 5.8.6

### Changes
- Description preferred acceptability update no longer update acceptabilities of other description (5f012c30a8346bc4905bafa92d9920ce9525dc3a)

### Bugs
- Fix concept document indexing when changing destination of a relationship in place (4a0ab3a5bcb9b5108785b799f86a1258f88c65e9)

## 5.8.5

### Changes
- Allow editing immutable properties on unreleased SNOMED CT components (0e1444b9bac543c1cf5c22e90823c4ea42edd077)
  * `typeId`, `destinationId` on SNOMED CT Relationships
  * `typeId`, `term`, `languageCode` on SNOMED CT Descriptions

### Bugs
- Always create commit info document when processing a CDO commit (03e85d11b12c236ada328f8cce6e289558573bfc)

## 5.8.4

### Bugs
- Fix serialization issue of SortField when using it in SearchRequests (e733dbec3edb831d6fba4974ff9ea8a04832f50f)

## 5.8.3

### Bugs
- Initialize repositories only in GREEN health state (3bb706585d92ecc90336f62b6fc4530392486464)
- Fix expansion of `statedDescendants` when persisting classification results (b9de8620ca59f728adaeb82c3100b09f40737304)
- Fix missing backpressure exception when using `Notifications` event stream (ce022786e59c1ad14c8dc5877c076158c053fae6)

## 5.8.2

### Changes
- Field selection now supports single fields as well (049578f483150fb566ec2c92b9e2b1659e40dccb)

### Bugs
- Fix missing SNOMED CT icons after importing RF2 files (388c13b34f4194f759239506c2e8f09a1d4cfb5e)

## 5.8.1

### Bugs
- Fixed missing internal SNOMED CT ECL package export entry (required by ide and ui plugins)
- Fixed duplicate repository ID shown in diagnostic messages
- Fixed existing db index detection when ensuring index on CDO_CREATED columns

## 5.8.0

### Added
- Health Status has been added to Repository instances (https://github.com/b2ihealthcare/snow-owl/pull/138)
 * Repositories check their health status during the bootstrap process
 * `RED` state: administrative action is required (restoring content from backup or initiating reindex on the repository). Accessing content is disallowed.
 * `YELLOW` state: administrative operation (eg. reindex) is in progress, accessing content is allowed, but the content can be incomplete  
 * `GREEN` state: terminology content is consistent
- Server Info and Repository API (https://github.com/b2ihealthcare/snow-owl/pull/138)
 * `GET /snowowl/admin/info` endpoint to retrieve version and diagnostic information of the running server
 * `GET /snowowl/admin/repositories` endpoint to retrieve all available repositories with their health statuses
 * `GET /snowowl/admin/repositories/:id` endpoint to retrieve a single repository and its health status
- Console
 * `--version` command parameter has been added to `snowowl` command to retrieve the server's version
 * `snowowl repositories [id]` subcommand has been added to retrieve repository information and health status
- Sorting support in Java API (https://github.com/b2ihealthcare/snow-owl/pull/140) 
- Low-level API changes
 * Support custom repository content initialization for empty repositories during the bootstrap process (usually primary code system entries are created here)

### Changed
- SNOMED CT RF2 importer changes
 * Importer is no longer able to create codeSystem as part of the import process, how ever they require that the codeSystem exists before starting the import. 
- SNOMED CT ECL grammar improvements (https://github.com/b2ihealthcare/snow-owl/pull/137)
 * Optional `WS` before and after the `PIPE` terminals
 * Case insensitive keyword support
 * Fixed parser error when parsing empty ECL expression
- Branching API improvements (https://github.com/b2ihealthcare/snow-owl/pull/143)
 * Pagination support has been added to `GET /snowowl/snomed-ct/v2/branches` endpoint
 * Filter by `parent` property has been added to `GET /snowowl/snomed-ct/v2/branches` endpoint
 * Filter by `name` property has been addSed to `GET /snowowl/snomed-ct/v2/branches` endpoint
- `SnomedConcept` expansions 
 * Removed `form` parameter when expanding `ancestors`/`descendants` of SNOMED CT concepts
 * Added `statedAncestors`/`statedDescendants` expand parameters to expand concepts referenced via `stated` IS_A relationships
 * `ancestors`/`descendants` expansion expands concepts referenced via `inferred` IS_A relationships 

### Removed
- `GET /snowowl/admin/repositories/:id/versions` obsolete endpoint has been removed in favor of `GET /snowowl/admin/codesystem/:id/versions`
- `snowowl listrepositories` subcommand has been replaced with `snowowl repositories`  

### Dependencies
- Xtext/Xtend changed from 2.8.4 to 2.11.0 (https://github.com/b2ihealthcare/snow-owl/pull/137)
- EMF changed from 2.11.0 to 2.12.0 (https://github.com/b2ihealthcare/snow-owl/pull/137)
- RxJava changed from 1.0.6 to 2.0.7 (https://github.com/b2ihealthcare/snow-owl/pull/141) 

### Bugs
- Validate SNOMED CT Text Definition files (if present) when import RF2 content 
- Validate `branchPath` input parameter with given `codeSystemShortName` when running SNOMED CT RF2. It should be either full path or relative to the codesystem's main branch.
- Fixed issue where files could not be opened after downloading them using the `FileRegistry.download(...)` api (c1088e217e1954952b57e70aec9f0d28f09083af)
- Skip cdo repository initializer commits when reindexing a repository (b6d1e4f9e2cd170a8516bc2bcf1cda4f6ce6888c)

## 5.7.4

### Changed
- Support UUID as return value of a RemoteJob (bee443409401b0ece88a520c009e07130b924357)

### Bugs
- Fixed invalid MRCM domain expression calculation in case of CompositeConceptSetDefinition (1600d9e1821b9d403ab3365e15f05a96f238eba1)

## 5.7.3

### Bugs
- Fixed serialization of IdGenerationStrategy (555c3a585b8ea721c396de18d18c3fad4ac9568d)

## 5.7.2

### Added
- Support reference set filtering in SNOMED CT RF2 export Java API

### Changed
- Improved repository request log output

### Bugs
- Fixed serialization of ValidationException (62d3d0516b87c62e4841a6d9b0eba6e262e18410)
- Fixed class loading issue when using BulkRequests
- Fixed Highlighting.getMatchRegions() bug (e7d449df9843c188700c11591ca7e70755f9f140)

## 5.7.1

### Bugs
- Fixed node initialization issue when connecting to a master node (client-server mode)
- Fixed serialization of GetResourceRequest classes

## 5.7.0

### Breaking changes
- SNOMED CT Complex Map Members properties, `mapGroup` and `mapPriority` are changed to integer type instead of byte. This requires migration of the SNOMED CT database (TODO ref to sql script).

### Added
- Reader/Writer database connection pool capacity configuration (see configuration guide)
- Domain representation classes for SNOMED CT MRCM Constraints
 * SnomedRelationshipConstraint
 * SnomedDescriptionConstraint
 * SnomedConcreteDomainConstraint
- Complete Java and REST support for SNOMED CT Reference Sets and Members (https://github.com/b2ihealthcare/snow-owl/pull/131)
- Generic request-based job API (https://github.com/b2ihealthcare/snow-owl/pull/132)
- RxJava-based notification observable support (part of https://github.com/b2ihealthcare/snow-owl/pull/132)
- File attachment API (https://github.com/b2ihealthcare/snow-owl/pull/129)

### Changed
- SNOMED CT RF2 export API
 * `startEffectiveTime` and `endEffectiveTime` export filters can be used for all RF2 export types
 * `codeSystemShortName` and `extensionOnly` properties to select the right content for your RF2 package
 * Export now always creates empty description, text definition and language refset files
 * Export now creates description/text definition/lang refset files per language code
 * A new RF2 API (https://github.com/b2ihealthcare/snow-owl/pull/135) 
- Index API
 * Support java.util.Date types in document mapping
- Refactored SNOMED CT API test cases (part of https://github.com/b2ihealthcare/snow-owl/pull/131)

### Removed
- The obsolete rpc-based quick search API (use SearchResourceRequests instead)

### Bugs
- Properly dispose ReviewManager instance when disposing the Repository

## 5.6.0

### Added
- GET /{path}/relationships endpoint to search SNOMED CT Relationship components
- Namespace filter support to
 * GET /{path}/descriptions
 * GET /{path}/relationships
- EffectiveTime filter support to
 * GET /{path}/concepts
 * GET /{path}/descriptions
 * GET /{path}/relationships
- ECL expression support to `GET /{path}/descriptions` endpoint's concept and type filter 
- `subclassDefinitionStatus` property to `SnomedConceptSearchRequest`
- `referenceSet` expand option to GET /{path}/concepts

### Bugs
- Fixed NPE when using only delimiter characters in `termFilter` query parameter
- Server now fails to start if multiple terminology repositories have the same storageKey namespace ID assigned
- Source and target effective time values are now indexed on module dependency reference set member index documents

## 5.5.0

### Added
- Support for inactive component creation. See endpoints:
 * POST /{path}/concepts
 * POST /{path}/descriptions
 * POST /{path}/relationships
 * POST /{path}/members
- Support the complete SNOMED CT Identifier functionality via a dedicated Java API (`SnomedRequests.identifiers()`)

### Changed
- SnomedDescription REST representation changes
 * Changed `acceptabilityMap` to `acceptability`
 * Changed `descriptionInactivationIndicator` to `inactivationIndicator`
- SnomedRelationship REST representation changes
 * Changed `sourceConcept` to `source`
 * Changed `typeConcept` to `type`
 * Changed `destinationConcept` to `destination`
 * Removed `refinability` property
- POST /{path}/concepts
 * Added support for relationship creation as part of concept creation
 * Added support for member creation as part of concept creation
- POST /{path}/concepts/{id}/updates
 * Added support for description updates
 * Added support for relationship updates
 * Added support for member updates
- Swagger API
 * Replaced ISnomed* types with the corresponding Snomed* type

### Removed
- The following endpoints have been completely removed from the REST API (equivalent requests can be initiated via expansion parameters, see GET /{path}/concepts endpoint)
 * GET /{path}/concepts/ancestors
 * GET /{path}/concepts/descriptions
 * GET /{path}/concepts/descendants
 * GET /{path}/concepts/outbound-relationships
 * GET /{path}/concepts/inbound-relationships
 * GET /{path}/concepts/pt
 * GET /{path}/concepts/fsn
- Removed `defaultModule` configuration option (requests should specify the desired module via `moduleId` parameter)
- Removed `defaultNamespace` configuration option (requests should specify the desired namespace via `namespaceId` parameter)
- Removed `enforceNamespace` configuration option

## 5.4.0

### Added
- Support for Expression Constraint Language v1.1.1 has been added, see http://snomed.org/ecl for details
- Support BigDecimal property mapping in index API

### Changed
- GET /{path}/concepts now supports filtering by ECL expressions via `ecl` query parameter
- Deprecated `escg` filter on GET /concepts endpoint. Use the `ecl` query parameter instead
- Snow Owl now uses sequential SNOMED CT identifier generation instead of random

### Bugs
- Fixed empty task branch issue when an exception occurs during rebase (rebase now works on a temporary branch until it completes and renames the branch to the original name using CDO branch rename functionality, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=422145) (https://github.com/b2ihealthcare/snow-owl/pull/118)
- Fixed missing non-stated relationship file from delta export (https://github.com/b2ihealthcare/snow-owl/pull/119)
- Request new identifiers in bulk during bulk component updates (https://github.com/b2ihealthcare/snow-owl/pull/121)
- Improved performance and memory usage of SNOMED CT RF2 importer (https://github.com/b2ihealthcare/snow-owl/pull/122)

## 5.3.0

### Changed
- SNOMED CT Concept, Description and Relationship index schema changes
 * Added `referringRefSets` field to all three main SNOMED CT component documents
 * Added `referringMappingRefSets` field to all three main SNOMED CT component documents
 * Non-mapping reference set identifiers that reference a given component will be indexed in the `referringRefSets`
 * Mapping reference set identifiers that reference a given component will be indexed in the `referringMappingRefSets`
 * NOTE: to be able to use these fields reindex `snomedStore` repository using the `snowowl reindex snomedStore` command after dropping the index directory of it

### Bugs
- Fixed review change calculation bug, deleted components will mark their container component changed by default
- Fixed bug with new/dirty reference set (re)indexing without concept changes
- Handle unordered list index calculations properly during CDO revision compare

## 5.2.0

### Added
- New Java API to get/search commit information in a repository. See class `com.b2international.snowowl.datastore.request.CommitInfoRequests`. To make the new API work, you have to reindex your dataset using the `snowowl reindex` console command

### Changed
- SNOMED CT RF2 importer now uses the same change processing/indexing as regular commits
- Support DOI score indexing during change processing (aka when committing changes)

### Bugs
- Fixed incorrect calculation of stated/inferred parent/ancestor information on SNOMED CT Concept documents in case of status change

## 5.1.0

### Java 8 support

Snow Owl is now using Java 8 both compile time and runtime. Make sure your execution (and development, if you are developing custom plug-ins for Snow Owl) environment supports and uses Java 8. From 5.1.0, the minimum required Java version is 1.8.0 update 102. 

## 5.0.0

### Breaking changes

This section discusses the changes that you need to be aware of when migrating your application to Snow Owl 5.0.0.

#### Datasets created before 5.0.0
Snow Owl v5.0.0 no longer supports nested index directory format. The new format is flat, branches do not have their own directories under the corresponding terminology repository's root index folder. Branching and revision information are coded into each document and each terminology component has multiple documents in the index, which is called `revision index` and the documents are `revisions`. Additionally with the new index format, Snow Owl moved from Lucene v4.9.0 to v5.5.0. Indexes, that still use the old index API, but depend on the new Lucene version, should be accessible and readable by Lucene v5.5.0.

To support migration of incompatible datasets, Snow Owl v5.0.0 comes with a `reindex` command, which can be used to create the new index for any dataset based on the contents of the relational database. See updated [Admin Console Reference Guide](/documentation/src/main/asciidoc/administrative_console_reference.adoc#diagnostics-and-maintenance) for details.

#### Java API changes
Due to index format changes, public APIs (generic and/or SNOMED CT terminology related) - that used the old format - become obsolete, and either marked with deprecated annotation or have been completely removed. See the affected public APIs in the Removed section. 

#### Review API changes
In general the `Review API` still works the same way as in the `4.x` versions, but the new, changed, deleted concept ID sets might contain non-Concept identifiers when a member of a `Relationship/Description` changes, but the corresponding `Relationship/Description` does not. It is recommended to fetch the container `SNOMED CT Concept` identifier by querying the *source* branch for the new or changed Relationship/Descriptions, and extracting the SNOMED CT Concept identifier from either the conceptId or sourceId properties. Querying deleted revisions from the tip of a branch is currently not supported, see next section. 

With this change and limited capabilities, Snow Owl will no longer support the current version of the `Review API` starting from the next release (`5.1.0`), and it will replace it with a more generic `Branch Compare API`. This new API will return the new/changed/deleted document identifiers directly without trying to be smart and replace the document identifier with the corresponding container (root resource, like the `SNOMED CT Concept`) component identifier. API consumers will be responsible for fetching and computing the final compare result based on the actual changes between the branches, if they would like to still show the review in the scope of a `SNOMED CT Concept`. This enables `Snow Owl` to use the same `Branch Compare API` for different terminology implementations and the API will provide access points to query the proper revision of new/changed/deleted components (currently it only supports the latest revision, which returns `HTTP 404 Not Found` for deleted components).

### Added
- Maintenance commands:
 * `snowowl reindex <repositoryId> <failedCommitTimestamp>` - Reindexes the currently available database content from scratch, or from the specified commitTimestamp (if a previously initiated reindex process has failed at some point)
 * `snowowl optimize <repositoryId> <maxSegments>` - Optimizes the underlying index for the repository to have the supplied maximum number of segments (default number is 1)
 * `snowowl purge <repositoryId> <branchPath> <purgeStrategy>` - optimizes the underlying index by deleting unnecessary documents from the given branch using the given purge strategy (default strategy is `LATEST`, available strategies are `ALL`, `LATEST`, `HISTORY`)
- New search options for SNOMED CT Reference Set Members:
 * `acceptabilityId`
 * `characteristicTypeId`
 * `correlationId`
 * `descriptionFormat`
 * `mapCategoryId`
 * `operatorId`
 * `targetComponent`
 * `unitId`
 * `valueId`
- New `revisionCache` configuration option in `repository` section. Enables/Disables CDO revision cache based on its value (default value is `true`).
- New `index` configuration options under `repository` section:
 * `commitInterval` - the interval in milliseconds, which specifies how often flush and sync index changes to disk (default is `15000`, 15 seconds, minimum allowed value is `1000`, 1 second)
 * `translogSyncInterval` - the interval in milliseconds, which specifies how often the transaction log flushes its changes to disk (default is `5000`, 5 seconds, minimum allowed value is `1000`, 1 second)
 * `queryWarnThreshold` - threshold in milliseconds, which specifies when to log a WARN level message in the log file about a slow query (default value is `400`)
 * `queryInfoThreshold` - threshold in milliseconds, which specifies when to log an INFO level message in the log file about a slow query (default value is `300`)
 * `queryDebugThreshold` - threshold in milliseconds, which specifies when to log a DEBUG level message in the log file about a slow query (default value is `100`)
 * `queryTraceThreshold` - threshold in milliseconds, which specifies when to log a TRACE level message in the log file about a slow query (default value is `50`)
 * `fetchWarnThreshold` - threshold in milliseconds, which specifies when to log a WARN level message in the log file about a slow fetch (default value is `200`)
 * `fetchInfoThreshold` - threshold in milliseconds, which specifies when to log an INFO level message in the log file about a slow fetch (default value is `100`)
 * `fetchDebugThreshold` - threshold in milliseconds, which specifies when to log a DEBUG level message in the log file about a slow fetch (default value is `50`)
 * `fetchTraceThreshold` - threshold in milliseconds, which specifies when to log a TRACE level message in the log file about a slow fetch (default value is `10`)
- New modules:
 * `com.b2international.index.api` - Generic Index API module
 * `com.b2international.index.lucene` - Lucene based implementation of Generic Index API module
 * `com.b2international.index.api.tests` - Generic test cases to verify implementation modules of the Index API module
 * `com.b2international.index.api.tests.tools` - Useful utility classes when writing Index API based test cases
 * `com.b2international.collections.jackson` - Jackson ser/deser module for `com.b2international.collections.api` module

### Changed
- Improved change processing performance by loading only the relevant revisions from the index
- Log entry format of requests has changed to the following
 * The logged entry is now a valid JSON object  
 * Format: `{"repositoryId":"string", "type":"string", "metrics": {...}, ...request specific properties}`
- Metrics
 * All values are measured in milliseconds
 * Read operations measure their execution time (`responseTime`)
 * Commit operations measure their execution time (`responseTime`) and commit subtask execution times (`preCommit`, `preRequest`, `traceability`, `indexing`, `commit`)

### Removed
- Deprecated public SNOMED CT APIs that have been replaced by the new Request based APIs
 * `SnomedTerminologyBrowser`
 * `SnomedStatementBrowser`
 * `SnomedPredicateBrowser`
 * `SnomedComponentService`
 * `SnomedTaxonomyService`
- Configuration options:
 * `indexTimeout` configuration has been removed, because the new index API uses a single index and it does not require disposal of branch specific Index Readers/Writers

## 4.7.0

### Added
- New feature, SNOMED CT Extension support, see `snomed_extension_management.adoc` for details.
 * `POST` `/codesystems` - creates a new codesystem
 * `PUT` `/codesystems` - updates an existing codesystem
- Representations
 * New `branchPath` property on CodeSystems (currently active path of a CodeSystem)
 * New `repositoryUuid` property on CodeSystems (the current repository of the CodeSystem )
 * New `extensionOf` property on CodeSystems (the base code system of the CodeSystem)
 * New `parentBranchPath` property on CodeSystemVersions (the parent branch path where the version branch is forked off)
- `effectiveTime` based filtering for all components (currently members only, other components will be support on release of 4.7)
- New module for support full javadoc of Snow Owl public APIs (`com.b2international.snowowl.javadoc`). The new module is part of the `site` Maven profile.

### Changed
- SNOMED CT Extension support
 * `GET` `/codesystems` - returns all currently known codesystems (in SNOMED CT, all releases, including extensions)
 * `GET` `/codesystems/:id` - returns a codesystem by its unique identifier, which can be its short name or its oid (both should be unique)
 * `POST` `/codesystems/:id/versions` - create a new version in a codesystem (or release in SNOMED CT)
- SNOMED CT RF2 import
 * `POST` `/imports` - new optional property `codeSystemShortName`, identifies the target code system of the import, the default value is the short name of the SNOMED CT International Release
 * 
- Revise handling of structural reference set members (language, inactivation and association members)
 * Try to reuse members where possible (reactivate if necessary)
 * Keep only one active language reference set member per description and do not create new ones when acceptability changes

### Dependencies
- Replaced custom `3.2.2` version of `org.semanticweb.owl.owlapi` module with a dependency to the `3.4.4` version of it.
 * Makes it possible to use `ELK v0.4.2` runtime and during tests
- Upgrade custom `Protegé` libraries from `4.1` to `4.3`
- Replaced the unsupported [pcj](http://pcj.sourceforge.net/) library with [FastUtil](https://github.com/vigna/fastutil) and also added a nice primitive collection API on top of it to support replacement of the primitive collection library underneath (and/or support multiple libraries with different capabilities, performance at the same time)
- Migration from old terminology registry model to updated model, migration scripts are in `/documentation/src/main/asciidoc/scripts/migration_4.6_to_4.7/`

### New modules
- `com.b2international.collections.api` - primitive collections API
- `com.b2international.collections.fastutil` - [FastUtil](https://github.com/vigna/fastutil) implementation of primitive collections API
 
### Bugs
- Reduces thread usage of SNOMED CT change processing
- Index initialization during SNOMED CT RF2 import now filters content based on the current latest system effective time, resulting in much more reliable imports and content when the import completes

### Known issues
- No RF2 import config validation when the branchPath is unrelated with the given `codeSystemShortName` property

## 4.6.0

### Added
- All references set member properties are supported (using RF2 property names)
- Support for rebase queueing
 * `GET` `/merges` - returns all merges happened since the start of the server ()
 * `GET` `/merges/:id` - return a merge by its identifier
 * `POST` `/merges` - creates and starts a new merge between two branch points
 * `DELETE` `/merges/:id` - deletes a merge object by its identifier
- Expansion support improvements
 * Expand `targetComponent` on association reference set members
 * Expand `members` of any SNOMED CT core component (Concept, Description, Relationship) (eq. `expand=members()`)
 * Support `stated` and `inferred` expansion of `descendants` and `ancestors` (both Java and REST API)
- Representations (Java and REST API)
 * New `iconId` property on SNOMED CT model components (not available in JSON representations)
 * New, expandable `typeConcept` object property on SNOMED CT Relationships (by default only `id` is available on the object)
 * New, expandable `sourceConcept` object property on SNOMED CT Relationships (by default only `id` is available on the object)
 * New, expandable `destinationConcept` object property on SNOMED CT Relationships (by default only `id` is available on the object)
 * New, expandable `type` object property on SNOMED CT Relationships (by default only `id` is available on the object)

### Changed
- REST API property changes
 * `targetComponentId` changed to `targetComponent` (became nested object, expandable)
- Search improvements (Java API only, no REST support yet)
 * Support for fuzzy matching
 * Support for parsed terms
 * Support for DOI based scoring (using a default DOI file, not configurable yet)
 * Support for search profiles
- The type of the `group` property changed from `byte` to `int` to support greater than `127` values
- Using time based rolling policy with 90 days worth of history instead of fixed window with size restriction

## 4.5.0

### Added
- Support for simple and query type reference sets and members in RESTful API
 * `GET` `/:path/refsets`
 * `GET` `/:path/refsets/:id`
 * `GET` `/:path/refsets/:id/history`
 * `POST` `/:path/refsets`
 * `POST` `/:path/refsets/:id/actions`
 * `GET` `/:path/members`
 * `GET` `/:path/members/:id`
 * `POST` `/:path/members`
 * `PUT` `/:path/members/:id`
 * `DELETE` `/:path/members/:id`
 * `POST` `/:path/members/:id/actions`
- Integration with Component Identifier Service (CIS), see configuration_guide.adoc for details on how to configure it
- Indexing term, language code and acceptability values on SNOMED CT Description documents
- Initial version of the resource expansion API is currently available (expand `fsn`, `pt`, `descriptions` and other nested resources within a single request)
- `numberOfWorkers` configuration parameter to tweak worker threads per repository (by default it will be set to `3 x number of cores`)

### Changed
- Fixed bug in `Accept-Language` header by introducing Extended Locales, they do support language reference set IDs properly (the original header spec. restricts the number of extension characters to `8`)
- Increased `asyncTimeout` in Tomcat to 60 seconds
- Performance improvements on some endpoints by utilizing the new resource expansion API
- Marked a bunch of old APIs as deprecated, they will be removed in upcoming releases

### Removed
- Removed label indexing from SNOMED CT component indexing (using Description term index field to find a label)

### Merged pull requests
- https://github.com/b2ihealthcare/snow-owl/pull/35

### Known issues
- CIS is currently unsupported in SNOMED CT RF2 imports (manual synchronization is required)
- Simple type mapping reference set membership is not tracked properly if there are multiple mappings for a single referenced component

## 4.4.0

### Added
- Support of MRCM rules import before/after SNOMED CT import (previously was part of the SNOMED CT import)
- Few missing SNOMED CT Inactivation Indicators have been added

### Changed
- RF2 validation in SNOMED CT import validates content based on effective times (fixes invalid errors/warnings)
- Concept inactivation rewires immediate children to immediate parent (keeping all STATED ISA relationships and inactivating all inferred relationships)
- Hot backup script copies entire index folder of a repository instead of just the version indexes
- IndexService inactivity default timeout value changed to 30 minutes

### Removed
- PostProcessing support has been completely removed
- Stopwords in index services have been completely removed

### Bugs
- Fixed stored mapTargetDescription values in SNOMED CT Simple Map Reference Set Members
- Fixed invalid setting of released flag to false in case of already published component import (set only the effective time)
- Removed tokenization of source field in IndexStore
- Keep dirty indexes alive when running service inactivity checker

### Merged pull requests
- https://github.com/b2ihealthcare/snow-owl/pull/21
- https://github.com/b2ihealthcare/snow-owl/pull/22
- https://github.com/b2ihealthcare/snow-owl/pull/23
- https://github.com/b2ihealthcare/snow-owl/pull/24
- https://github.com/b2ihealthcare/snow-owl/pull/25
- https://github.com/b2ihealthcare/snow-owl/pull/26
- https://github.com/b2ihealthcare/snow-owl/pull/27
- https://github.com/b2ihealthcare/snow-owl/pull/28
- https://github.com/b2ihealthcare/snow-owl/pull/33
- https://github.com/b2ihealthcare/snow-owl/pull/34

## 4.3.1

### Added
- GET /:path/descriptions - support for association targets in SNOMED CT Description representations
- POST /:path/descriptions/:id/updates - support for inactivation indicators in SNOMED CT Description updates
- POST /:path/descriptions/:id/updates - support for association targets in SNOMED CT Description updates

### Changed
- Renamed `descriptionInactivationIndicator` to `inactivationIndicator` in SNOMED CT Description representations
- Changed commit notification logging to be more readable and traceable
- Rebase across deep branches is now supported

### Bugs
- Fixed major commit processing bug (https://github.com/b2ihealthcare/snow-owl/commit/4f1ec749bd74f065f9463b75a4a54e0c7f257d0f)

## 4.3.0
### Added
- Support for relationships with stated characteristic type
 * Importing/exporting sct2_StatedRelationship*.txt files
 * Indexing stated parentage (parent and ancestor fields) on concepts
- Low-level Java API improvements
 * Revamped index APIs (document building, query building, fields)
 * Revamped change processing API (partial updates, parallel execution)

### Changed
- Stated relationships support changes
 * Classifier now uses the stated graph to produce changes to the DNF (inferred view)
 * Classifier now deletes any unpublished redundant inferred relationships instead of inactivating them
 * /browser/concepts/{id}/children now has a new parameter `form` (allowed values are `stated` and `inferred`)
- Reference set lucene documents fields merged into their corresponding identifier concept doc
 
### Merged pull requests
 * https://github.com/b2ihealthcare/snow-owl/pull/20
 * https://github.com/b2ihealthcare/snow-owl/pull/19
 * https://github.com/b2ihealthcare/snow-owl/pull/18
 * https://github.com/b2ihealthcare/snow-owl/pull/17

## 4.2.0
### Added
- Support for terminology reviews
  * `POST` `/reviews`
  * `GET` `/reviews/:id`
  * `GET` `/reviews/:id/concept-changes`
  * `DELETE` `/reviews/:id`
  
  Changes are computed according to existing version comparison logic in Snow Owl Server; in particular, reference set identifier concepts are marked as changed if any members are added, removed or updated in the reference set. The first modification immediately after versioning triggers an update to "Module dependency", making it appear in concept change resources.
  
  Inbound relationship changes do not mark the target concept as changed. Inactivating a concept marks it as changed, not deleted.
  
  Concept change sets and review resources are kept for a limited time, which is configurable using the `snowowl_configuration.yml` file.

## 4.1.0
### Added
- Deep branching support has been implemented
  * `POST` `/branches` endpoint
  * `GET` `/branches` endpoint
  * `DELETE` `/branches/{path}` endpoint
  * `GET` `/branches/{path}` endpoint
  * `GET` `/branches/{path}/children` endpoint
  * `POST` `/merges` endpoint

### Changed
- Added support for Unpublished component import. RF2 rows with empty effectiveTime column can be imported into Snow Owl SNOMED CT repository.
- Breaking RESTful API changes
  * `/{tag}[/tasks/{taskId}]/...` URLs are replaced with URL beginning with `/{path}/...`, where "path" may include an arbitrary number of `/`-separated segments
- Separate API documentation for each RESTful API
  * `Administrative`: http://localhost:8080/snowowl/admin/
  * `SNOMED CT`: http://localhost:8080/snowowl/snomed-ct/v2/
- API documentation layout
  * Two column layout, one for the API docs and one for the Swagger UI
- Deep branching support for Import/Export configuration (new `branchPath`)
- API version is now included in SNOMED CT REST service URLs; the accepted media type is `application/vnd.com.b2international.snowowl+json` for both Administrative and SNOMED CT terminology services
- Deployment changes: The preferred transaction isolation level is READ-COMMITTED for MySQL databases. For changing the corresponding server variable, refer to https://dev.mysql.com/doc/refman/5.6/en/set-transaction.html.

### Removed
- Breaking RESTful API changes
  * Removed /tasks API endpoints

### Known issues
- Associated index directories are not purged when a branch and any children are deleted
- Reopening a branch can not be rolled back; if applying changes fails after reopening a branch during rebase or merge operations, previous changes will be lost
- No additional metadata is present on version tag branches

## 2015-03-26
### Added
- `WRP-89`: both `FULL` and `DELTA` imports are now separating incoming import files into "layers" based on the effective time columns, and individual layers get imported in order. This enables importing the `20150131` INT RF2 release delta, which includes components from `20140731` as well. The import process can now create versions after importing each "layer" for all import types.
  
  Note that the above mentioned `20150131` release would require "re-tagging" of an already released `20140731` version, which is not supported, or placing the additional content on the existing `20140731` version branch as a patch. This, however, would make the extra `20140731` content available on that version and that version **only**; it would be visible neither from `MAIN`, nor from `20150131`.
  
  The current implementation issues a warning when such cases are encountered; the extra content will become visible on `MAIN` and `20150131`, but not on version `20140731`, which is left untouched. A possible workaround is to import `20140731` from a delta with disabled version creation, then import the `20150131` delta with enabled version creation.

## 2015-03-19
### Added
- `WRP-135`: added properties `taskId` and `versionId` to the export configuration object to allow exporting content from task branches. URLs for exporting no longer include the version segment.
  * version/taskId is accepted, but not applicable (no tasks on versions)
  * when taskId left out, it means MAIN or head of a particular version
- Added the `transientEffectiveTime` property to the export configuration object for specifying 
effective time values in export files, if unpublished components are present. Valid values are:
  * `""` or not setting the property: uses `Unpublished` in exported files
  * `"NOW"`: uses the current server date in exported files
  * dates in `yyyyMMdd` format, eg. `"20150319"`: uses the specified date in exported files
  * anything else result in return code 400 bad request, date is not validated in terms of earlier/later (only proper format)
  Note: unpublished components are filtered out from `DELTA` exports if the export configuration specifies an ending effective time.

### Changed
- Changed the export service to export components from _all_ modules if the `moduleIds` property of the export configuration object is not set. The previous behavior resulted in empty export files under the assumption that _no_ modules should be exported.
- Querying or exporting a non-existent export config returns now code 404 as opposed to 200 and an empty object.
- After downloading the export result, the export config is cleaned up.

## DEV2.1 - 2015-03-05
### Changed
- Make the `Accept` header mandatory for requests to "B2i" URLs, and produce `application/json` 
responses on "Browser" URLs. This means that the latter endpoints will be hit when entered in a 
web browser's URL bar, making testing easier.
- Increase concept children collection performance by using a combination of low-level calls. This 
brings the test case of `123037004`'s children down from ~22 seconds to ~350 ms.
- Sort description search results by relevance, and add offset and limit to fetch search results in 
smaller segments.
- Split the API documentation into two sets: the administrative interface with the code system 
listing is separated from SNOMED CT services. The API viewer's own URL bar can be used to switch 
between generated documentation endpoints; two shortcuts were also added. The default URL is changed 
to http://localhost:8080/snowowl/admin/api-viewer/ .

## DEV2 - 2015-02-20
### Added
- New API viewer category for URLs specific to the IHTSDO Browser:
- `GET` `/{tag}[/tasks/{taskId}]/concepts/{SCTID}` for retrieving concepts and related details
- `GET` `/{tag}[/tasks/{taskId}]/concepts/{SCTID}/children` for retrieving concept children
- `GET` `/{tag}[/tasks/{taskId}]/descriptions?query={query_string}` for retrieving matching
descriptions
- `GET` `/{tag}[/tasks/{taskId}]/constants` for getting response constant FSNs and identifiers

### Changed
- `effectiveDate` and `effectiveTime` properties on SNOMED CT components and code system
versions now universally follow the short `yyyyMMdd` format (also applies when creating new
versions), other dates should use ISO8601 
- `Accept-Language` is required on all new endpoints which return descriptions, as Snow Owl
Server stores descriptions with different language codes and acceptability side-by-side
- Constants must be retrieved on a version/task basis, as the FSN of these enumeration
values can change between versions, tasks

### Removed
- `isLeafStated` is not returned, as Snow Owl Server holds a mixed set of relationships
which matches the inferred view only

## DEV1 - 2015-02-13
### Added
- `WRP-26`: Support importing deltas without corresponding unchanged artefacts
- `WRP-82`: Enable REST operation to support imports on branches
- `WRP-88`: Add support for namespace to REST API
- Added folder `delta-import-examples` which includes a set of RF2 release .zip 
files, containing the minimally required content for each change. A separate 
`readme.txt` file within the folder has additional instructions on how these 
archives can be used.

### Changed
- URLs under and including `/snomed-ct/imports` no longer require specifying the 
version to use as a path variable;
- The input object for `POST` `/snomed-ct/imports` should now include both the 
version as well as the task identifier, if applicable:
```
{
  "version": "MAIN",
  "taskId": "SO-001",
  "type": "DELTA",
  "languageRefSetId": "900000000000508004",
  "createVersions": false
}
```
- `GET` `/snomed-ct/imports/{importId}` will return information about the ongoing
import and the original values from the import configuration:
```
{
  "type": "DELTA",
  "version": "MAIN",
  "taskId": "SO-001",
  "createVersions": false,
  "languageRefSetId": "900000000000508004",
  "id": "89feb6f4-f6a7-4652-90b9-b89b6b8587ce",
  "status": "COMPLETED",
  "startDate": "2015-02-13T15:39:56Z",
  "completionDate": "2015-02-13T15:41:05Z"
}
```
### Removed
- To keep the release file's size small, no indexes and SQL dumps have been 
attached; the ones from the previous release can be used instead. A database
reload is required.
