# Change Log
All notable changes to this project will be documented in this file.

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
