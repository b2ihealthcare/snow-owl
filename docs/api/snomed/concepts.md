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
SNOMED CT code system (concepts that are considered to be already published to consumers can only be removed with an 
administrative operation). Concepts can be retrieved by SCTID or description search terms; results can be further 
constrained via Expression Constraint Language (**ECL** for short) expressions.

## Code system paths

Snow Owl supports importing SNOMED CT content from different sources, allowing eg. multiple national **Extensions** to 
co-exist with the base **International Edition** provided by SNOMED International. Versioned editions can be consulted 
when non-current representations of concepts need to be accessed. Concept authoring and review can also be done in 
isolation.

To achieve this, the underlying terminology repository exposes a branching model (not unlike software development 
branches in Revision Control Systems); requests in the Concept API require a `path` parameter to select the content 
(or **substrate**) the user wishes to work with. The following formats are accepted:

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
GET /snomed-ct/v3/MAIN@1630504199999/concepts
```

Both absolute and relative paths are supported in the `path` part of the expression.

### Branch base point

Concept requests using a branch base point reflect the state of the branch at its beginning, before any changes on it
were made. The format of a base path is `path^` (only absolute paths are supported):

```json
GET /snomed-ct/v3/MAIN/2019-07-31/SNOMEDCT-UK-CL/101^/concepts
```

Returned concepts include all additions and modifications made on SNOMEDCT-UK-CL's working branch, up to point where 
task #101 starts; neither changes committed to the working branch after task #101, nor changes on task #101 itself are
reflected in the result set.

## Operations

### Retrieve single concept by ID

A GET request that includes a concept identifier as its last path parameter will return information about the concept 
in question:

```json
GET /snomed-ct/v3/MAIN/2019-07-31/concepts/138875005
{
  "id": "138875005",
  "released": true,
  "active": true,
  "effectiveTime": "20020131",
  "moduleId": "900000000000207008",
  "iconId": "snomed_rt_ctv3",
  "definitionStatus": {
    "id": "900000000000074008"
  },
  "subclassDefinitionStatus": "NON_DISJOINT_SUBCLASSES",
  "ancestorIds": [],
  "parentIds": [
    "-1"
  ],
  "statedAncestorIds": [],
  "statedParentIds": [
    "-1"
  ],
  "definitionStatusId": "900000000000074008"
}
```

The returned JSON object includes all properties defined in SNOMED International's
[Release File Specification](https://confluence.ihtsdotools.org/display/DOCRELFMT/4.2.1+Concept+File+Specification):

- `id`
- `effectiveTime`
- `active`
- `moduleId`
- `definitionStatusId`

It also contains the following supplementary information:

- `parentIds`, `ancestorIds`

These arrays hold a set of SCTIDs representing the concept's direct and indirect ancestors in the 
inferred taxonomy. The (direct) parents array contains all `destinationId`s from active and inferred IS A 
relationships where the `sourceId` matches this concept's SCTID, while the ancestor array contains all SCTIDs 
taken from the parent and ancestor array of direct parents. The arrays are sorted by SCTID. A value of `-1` means 
that the concept is a **root concept** that does not have any concepts defined as its parent. Typically, this only 
applies to `138875005|Snomed CT Concept|` in SNOMED CT content.

See the following example response for a concept placed deeper in the tree:

```json
GET /snomed-ct/v3/MAIN/concepts/425758004 // Diagnostic blood test
{
  [...]
  "ancestorIds": [
    "-1",        // Special value for taxonomy root
    "15220000",  // Laboratory test
    "71388002",  // Procedure
    "108252007", // Laboratory procedure (not pictured below)
    "128927009", // Procedure by method
    "138875005", // SNOMED CT Concept
    "362961001", // Procedure by intent
    "386053000"  // Evaluation procedure
  ],
  "parentIds": [
    "103693007", // Diagnostic procedure
    "396550006"  // Blood test
  ],
  [...]
}
```

Compare the output with a rendering from a user interface, where the concept appears in two different places after 
exploring alternative routes in the hierarchy. Parents are marked with blue, while ancestors are highlighted with 
orange:

![Parents and ancestors](images/parents_ancestors.png)

- `statedParentIds`, `statedAncestorIds`

Same as the above, but for the stated taxonomy view.

- `released`

A boolean value indicating whether this concept was part of at least one SNOMED CT release. New concepts 
start with a value of `false`, which is set to `true` as part of the code system versioning process. Released 
concepts can only be deleted by an administrator.

- `iconId`

A descriptive key for the concept's icon. The icon identifier typically corresponds to the lowercase, 
underscore-separated form of the [hierarchy tag](https://confluence.ihtsdotools.org/display/DOCGLOSS/hierarchy+tag) 
contained in each concept's Fully Specified Name (or **FSN** for short). The following keys are currently expected 
to appear in responses (subject to change):

`administration_method`,
`assessment_scale`,
`attribute`,
`basic_dose_form`,
`body_structure`,
`cell`,
`cell_structure`,
`clinical_drug`,
`disorder`,
`disposition`,
`dose_form`,
`environment`,
`environment_location`,
`ethnic_group`,
`event`,
`finding`,
`geographic_location`,
`inactive_concept`,
`intended_site`,
`life_style`,
`link_assertion`,
`linkage_concept`,
`medicinal_product`,
`medicinal_product_form`,
`metadata`,
`morphologic_abnormality`,
`namespace_concept`,
`navigational_concept`,
`observable_entity`,
`occupation`,
`organism`,
`owl_metadata_concept`,
`person`,
`physical_force`,
`physical_object`,
`procedure`,
`product`,
`product_name`,
`qualifier_value`,
`racial_group`,
`record_artifact`,
`regime_therapy`,
`release_characteristic`,
`religion_philosophy`,
`role`,
`situation`,
`snomed_rt_ctv3`,
`social_concept`,
`special_concept`,
`specimen`,
`staging_scale`,
`state_of_matter`,
`substance`,
`supplier`,
`transformation`,
`tumor_staging`,
`unit_of_presentation`

- `subclassDefinitionStatus`

{% hint style="warning" %}
**Currently unsupported.** Indicates whether a parent concept's direct descendants form a 
[disjoint union](https://www.w3.org/TR/owl2-syntax/#Disjoint_Union_of_Class_Expressions) in OWL 2 terms; when set to 
`DISJOINT_SUBCLASSES`, child concepts are assumed to be pairwise disjoint and together cover all possible cases of 
the parent concept.

The default value is `NON_DISJOINT_SUBCLASSES` where no such assumption is made.
{% endhint %}