# Change Log
All notable changes to this project will be documented in this file.

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
