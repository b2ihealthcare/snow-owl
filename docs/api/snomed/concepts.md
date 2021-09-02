# SNOMED CT Concept API

## Introduction

**SNOMED CT concepts** represent ideas that are relevant in a clinical setting and have a unique concept identifier 
(a SNOMED CT identifier or **SCTID** for short) assigned to them. The terminology covers a wide set of domains and 
includes concepts that represent parts of the human body, clinical findings, medicinal products and devices, among 
many others. SCTIDs make it easy to refer unambiguously to the described ideas in eg. an Electronic Health Record or 
prescription, while SNOMED CT's highly connected nature allows complex analytics to be performed on aggregated data.

Each concept is associated with human-readable **description**s that help users select the SCTID appropriate for their
use case, as well as **relationship**s that form links between other concepts in the terminology, further clarifying
their intended meaning. The API for manipulating the latter two types of components are covered in sections 
[Descriptions](descriptions.md) and [Relationships](relationships.md), respectively.

The three component types mentioned above (also called **core components**) have a distinct set of attributes which
together form the concept's definition. As an example, each concept includes an attribute (the **definition status**) 
which states whether the definition is sufficiently defined (and so can be computationally processed), or relies on 
a (human) reader to come up with the correct meaning based on the associated descriptions.

Terminology services exposed by Snow Owl allows clients to *create*, *retrieve*, *modify* or *remove* concepts from a 
SNOMED CT code system (concepts that are considered to be already published to consumers can only be removed with an administrative operation). Concepts can be retrieved by SCTID or description search terms; results can be further 
constrained via Expression Constraint Language (**ECL** for short) expressions.

## Code system paths

Snow Owl supports importing SNOMED CT content from different sources, allowing eg. multiple national **Extensions** to 
co-exist with the base **International Edition** provided by SNOMED International. Versioned editions can be consulted 
when non-current representations of concepts need to be accessed. Concept authoring and review can also be done in 
isolation.

To achieve this, the underlying terminology repository exposes a branching model (not unlike software development 
branches in Revision Control Systems); requests in the Concept API require a `path` parameter to select the content (or **substrate**) the user wishes to work with. The following formats are accepted:

### Absolute branch path

Absolute branch path parameters start with `MAIN` and point to a branch in the backing terminology repository. In the 
following example, all concepts are considered to be part of the substrate that are on branch 
`MAIN/2021-01-31/SNOMEDCT-UK-CL` or any ancestor (ie. `MAIN` or `MAIN/2021-01-31`), unless they have been modified:

```json
GET /snomed-ct/v3/MAIN/2021-01-31/SNOMEDCT-UK-CL/concepts
{
  "items": [
    {
      "id": "100000000",
      "released": true,
      "active": false,
      "effectiveTime": "20090731",
[...]
```

### Relative branch path

Relative branch paths start with a short name identifying a SNOMED CT code system, and are relative to the code 
system's working branch. For example, if the working branch of code system `SNOMEDCT-UK-CL` is configured to 
`MAIN/2021-01-31/SNOMEDCT-UK-CL`, concepts visible on authoring task #100 can be retrieved using the following request:

```json
GET /snomed-ct/v3/SNOMEDCT-UK-CL/100/concepts
```

An alternative request that uses an absolute path would be the following:

```json
GET /snomed-ct/v3/MAIN/2021-01-31/SNOMEDCT-UK-CL/100/concepts
```

An important difference is that the relative `path` parameter tracks the working branch specified in the code 
system's settings, so requests using relative paths do not need to be adjusted when a code system is upgraded to a
more recent International Edition.

### Path range

The substrate represented by a path range consists of concepts that were created or modified between a starting and
ending point, each identified by an absolute branch path (relative paths are not supported). The format of a path range 
is `fromPath...toPath`.

To retrieve concepts authored or edited following version 2020-08-05 of code system SNOMEDCT-UK-CL, the following path
expression should be used:

```json
GET /snomed-ct/v3/MAIN/2019-07-31/SNOMEDCT-UK-CL/2020-08-05...MAIN/2021-01-31/SNOMEDCT-UK-CL/concepts
```

The result set includes the ones appearing or changing between versions 2019-07-31 and 2021-01-31 of the International 
Edition; if this is not desired, additional constraints can be added to exclude them.

### Path with timestamp

To refer to a branch state at a specific point in time, use the `path@timestamp` format. The timestamp is an integer 
value expressing the number of milliseconds since the UNIX epoch, 1970-01-01 00:00:00 UTC, and corresponds to "wall 
clock" time, not component effective time. As an example, if the SNOMED CT International version 2021-07-31 is imported 
on 2021-09-01 13:50:00 UTC, the following request to retrieve concepts will not include any new or changed concepts 
appearing in this release:

```json
GET /snomed-ct/v3/MAIN@1630504199999
```

Both absolute and relative paths are supported in the `path` part of the expression.

### Branch base point

Concept requests using a branch base point reflect the state of the branch at its beginning, before any changes on it
were made. The format of a base path is `path^` (only absolute paths are supported):

```json
GET /snomed-ct/v3/MAIN/2019-07-31/SNOMEDCT-UK-CL/101^
```

Returned concepts include all additions and modifications made on SNOMEDCT-UK-CL's working branch, up to point where 
task #101 starts; neither changes committed to the working branch after task #101, nor changes on task #101 itself are
reflected in the result set.
