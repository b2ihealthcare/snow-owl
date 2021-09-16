# SNOMED CT Concept API

## Introduction

**SNOMED CT concepts** represent ideas that are relevant in a clinical setting and have a unique concept identifier (a SNOMED CT identifier or **SCTID** for short) assigned to them. The terminology covers a wide set of domains and includes concepts that represent parts of the human body, clinical findings, medicinal products and devices, among many others. SCTIDs make it easy to refer unambiguously to the described ideas in eg. an Electronic Health Record or prescription, while SNOMED CT's highly connected nature allows complex analytics to be performed on aggregated data.

Each concept is associated with human-readable **description**s that help users select the SCTID appropriate for their use case, as well as **relationship**s that form links between other concepts in the terminology, further clarifying their intended meaning. The API for manipulating the latter two types of components are covered in sections [Descriptions](descriptions.md) and [Relationships](relationships.md), respectively.

The three component types mentioned above (also called **core components**) have a distinct set of attributes which together form the concept's definition. As an example, each concept includes an attribute (the **definition status**) which states whether the definition is sufficiently defined (and so can be computationally processed), or relies on a (human) reader to come up with the correct meaning based on the associated descriptions.

Terminology services exposed by Snow Owl allows clients to *create*, *retrieve*, *modify* or *remove* concepts from a SNOMED CT code system (concepts that are considered to be already published to consumers can only be removed with an administrative operation). Concepts can be retrieved by SCTID or description search terms; results can be further constrained via Expression Constraint Language (**ECL** for short) expressions.

## Code system paths

Snow Owl supports importing SNOMED CT content from different sources, allowing eg. multiple national **Extensions** to co-exist with the base **International Edition** provided by SNOMED International. Versioned editions can be consulted when non-current representations of concepts need to be accessed. Concept authoring and review can also be done in isolation.

To achieve this, the underlying terminology repository exposes a branching model (not unlike software development branches in Revision Control Systems); requests in the Concept API require a `path` parameter to select the content (or **substrate**) the user wishes to work with. The following formats are accepted:

### Absolute branch path

Absolute branch path parameters start with `MAIN` and point to a branch in the backing terminology repository. In the following example, all concepts are considered to be part of the substrate that are on branch `MAIN/2021-01-31/SNOMEDCT-UK-CL` or any ancestor (ie. `MAIN` or `MAIN/2021-01-31`), unless they have been modified:

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

Relative branch paths start with a short name identifying a SNOMED CT code system, and are relative to the code system's working branch. For example, if the working branch of code system `SNOMEDCT-UK-CL` is configured to `MAIN/2021-01-31/SNOMEDCT-UK-CL`, concepts visible on authoring task #100 can be retrieved using the following request:

```json
GET /snomed-ct/v3/SNOMEDCT-UK-CL/100/concepts
```

An alternative request that uses an absolute path would be the following:

```json
GET /snomed-ct/v3/MAIN/2021-01-31/SNOMEDCT-UK-CL/100/concepts
```

An important difference is that the relative `path` parameter tracks the working branch specified in the code system's settings, so requests using relative paths do not need to be adjusted when a code system is upgraded to a more recent International Edition.

### Path range

The substrate represented by a path range consists of concepts that were created or modified between a starting and ending point, each identified by an absolute branch path (relative paths are not supported). The format of a path range is `fromPath...toPath`.

To retrieve concepts authored or edited following version 2020-08-05 of code system SNOMEDCT-UK-CL, the following path expression should be used:

```json
GET /snomed-ct/v3/MAIN/2019-07-31/SNOMEDCT-UK-CL/2020-08-05...MAIN/2021-01-31/SNOMEDCT-UK-CL/concepts
```

The result set includes the ones appearing or changing between versions 2019-07-31 and 2021-01-31 of the International Edition; if this is not desired, additional constraints can be added to exclude them.

### Path with timestamp

To refer to a branch state at a specific point in time, use the `path@timestamp` format. The timestamp is an integer value expressing the number of milliseconds since the UNIX epoch, 1970-01-01 00:00:00 UTC, and corresponds to "wall clock" time, not component effective time. As an example, if the SNOMED CT International version 2021-07-31 is imported on 2021-09-01 13:50:00 UTC, the following request to retrieve concepts will not include any new or changed concepts appearing in this release:

```json
GET /snomed-ct/v3/MAIN@1630504199999/concepts
```

Both absolute and relative paths are supported in the `path` part of the expression.

### Branch base point

Concept requests using a branch base point reflect the state of the branch at its beginning, before any changes on it were made. The format of a base path is `path^` (only absolute paths are supported):

```json
GET /snomed-ct/v3/MAIN/2019-07-31/SNOMEDCT-UK-CL/101^/concepts
```

Returned concepts include all additions and modifications made on SNOMEDCT-UK-CL's working branch, up to point where task #101 starts; neither changes committed to the working branch after task #101, nor changes on task #101 itself are reflected in the result set.

## Resource format

A concept resource without any expanded properties looks like the following:

```json
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

### Properties

The resource includes all RF2 properties that are defined in SNOMED International's [Release File Specification](https://confluence.ihtsdotools.org/display/DOCRELFMT/4.2.1+Concept+File+Specification)🌎:

- `id`
- `effectiveTime`
- `active`
- `moduleId`
- `definitionStatusId`

It also contains the following supplementary information:

- `parentIds`, `ancestorIds`

These arrays hold a set of SCTIDs representing the concept's direct and indirect ancestors in the inferred taxonomy. The (direct) parents array contains all `destinationId`s from active and inferred IS A relationships where the `sourceId` matches this concept's SCTID, while the ancestor array contains all SCTIDs taken from the parent and ancestor array of direct parents. The arrays are sorted by SCTID. A value of `-1` means that the concept is a **root concept** that does not have any concepts defined as its parent. Typically, this only applies to `138875005|Snomed CT Concept|` in SNOMED CT content.

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

Compare the output with a rendering from a user interface, where the concept appears in two different places after exploring alternative routes in the hierarchy. Parents are marked with blue, while ancestors are highlighted with orange:

![Parents and ancestors](images/parents_ancestors.png)

- `statedParentIds`, `statedAncestorIds`

Same as the above, but for the stated taxonomy view.

- `released`

A boolean value indicating whether this concept was part of at least one SNOMED CT release. New concepts start with a value of `false`, which is set to `true` as part of the code system versioning process. Released concepts can only be deleted by an administrator.

- `iconId`

A descriptive key for the concept's icon. The icon identifier typically corresponds to the lowercase, underscore-separated form of the [hierarchy tag](https://confluence.ihtsdotools.org/display/DOCGLOSS/hierarchy+tag)🌎 contained in each concept's Fully Specified Name (or **FSN** for short). The following keys are currently expected to appear in responses (subject to change):

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

In the metadata hierarchy, the use of a hierarchy tag alone would not distinguish concepts finely enough, as lots of them will have eg. "foundation metadata concept" set as their tag. In these cases, concept identifiers may be used as the icon identifier.

- `subclassDefinitionStatus`

{% hint style="warning" %}
**Currently unsupported.** Indicates whether a parent concept's direct descendants form a [disjoint union](https://www.w3.org/TR/owl2-syntax/#Disjoint_Union_of_Class_Expressions)🌎 in OWL 2 terms; when set to `DISJOINT_SUBCLASSES`, child concepts are assumed to be pairwise disjoint and together cover all possible cases of the parent concept.

The default value is `NON_DISJOINT_SUBCLASSES` where no such assumption is made.
{% endhint %}

### Property expansion

Core component information related to the current concept can be attached to the response by using the `expand` query parameter, allowing clients to retrieve more data in a single roundtrip. Property expansion runs the necessary requests internally, and attaches results to the original response object.

Expand options are expected to appear in the form of `propertyName1(option1: value1, option2: value2, expand(...)), propertyName2()` where:

- `propertyNameN` stands for the property to expand;
- `optionN: valueN` are key-value pairs providing additional filtering for the expanded property;
- optionally, `expand`s can be nested, and the options will apply to the components returned under the parent property;
- when no expand options are given, an empty set of `()` parentheses need to be added after the property name.

Supported expandable property names are:

#### `referenceSet()`

Expands reference set metadata and content, available on [identifier concepts](https://confluence.ihtsdotools.org/display/DOCRFSPG/4.2.1.+Reference+Set+Identification)🌎.

If a corresponding reference set was already created for an identifier concept (a subtype of `900000000000455006|Reference set`), information about the reference set will appear in the response:

```json
GET /snomed-ct/v3/MAIN/concepts/900000000000497000?expand=referenceSet() // CTV3 simple map
{
  "id": "900000000000497000",
  "active": true,
  [...]
  "referenceSet": {
    "id": "900000000000497000",
    "released": true,
    "active": true,
    "effectiveTime": "20020131",
    "moduleId": "900000000000012004",
    "iconId": "900000000000496009",
    "type": "SIMPLE_MAP",                    // Reference set type
    "referencedComponentType": "concept",    // Referenced component type
    "mapTargetComponentType": "__UNKNOWN__"  // Map target component type
                                             // (applicable to map type reference sets only)
  },
  [...]
}
```

Note that the response object for property `referenceSet` can also be retrieved directly using the [Reference Sets API](refsets.md).

To retrieve reference set members along with the reference set in a single request, use a nested `expand` property named `members`:

```json
GET /snomed-ct/v3/MAIN/concepts/900000000000497000?expand=referenceSet(expand(members()))
{
  "id": "900000000000497000",
  [...]
  "referenceSet": {
    [...]
    "type": "SIMPLE_MAP",
    "referencedComponentType": "concept",
    "mapTargetComponentType": "__UNKNOWN__",
    "members": {
      "items": [
        {
          "id": "00000193-e889-4d3f-b07f-e0f45eb77940",
          "released": true,
          "active": true,
          "effectiveTime": "20190131",
          "moduleId": "900000000000207008",
          "iconId": "776792002",
          "referencedComponent": {
            "id": "776792002"
          },
          "refsetId": "900000000000497000", // Reference set ID matches the identifier concept's ID
                                            // for all members of the reference set
          "referencedComponentId": "776792002",
          "mapTarget": "XV8E7"
        },
        [...]
      ],
      "searchAfter": "AoE_BTAwMDcyYWIzLWM5NDgtNTVhYy04MTBkLTlhOGNhMmU5YjQ5Yg==",
      "limit": 50,
      "total": 481508
    }
  },
}
```

Reference set members can also be fetched via the [SNOMED CT Reference Set Member API](refsets.md).

#### `preferredDescriptions()`

Expands descriptions with preferred acceptability.

Returns all active descriptions that have at least one active language reference set member with an acceptabilityId of `900000000000548007|Preferred|`, in compact form, along with the concept. Preferred descriptions are frequently used on UIs when a display label is required for a concept.

This information is also returned when expand options `pt()` or `fsn()` (described later) are present.

```json
GET /snomed-ct/v3/MAIN/2011-07-31/concepts/86299006?expand=preferredDescriptions()
{
  "id": "86299006", // Concept SCTID
  [...]
  "preferredDescriptions": {
    "items": [
      {
        "id": "828532012",                        // Description SCTID
        "term": "Tetralogy of Fallot (disorder)", // Description term
        "concept": {
          "id": "86299006"
        },
        "type": {
          "id": "900000000000003001"
        },
        "typeId": "900000000000003001",           // Type: Fully Specified Name
        "conceptId": "86299006",                  // "conceptId" matches the returned concept's SCTID
        "acceptability": {
          "900000000000509007": "PREFERRED",      // Acceptability in reference set "US English"
          "900000000000508004": "PREFERRED"       // Acceptability in reference set "GB English"
        }
      },
      {
        "id": "143123019",
        "term": "Tetralogy of Fallot",
        "concept": {
          "id": "86299006"
        },
        "type": {
          "id": "900000000000013009"
        },
        "typeId": "900000000000013009",           // Type: Synonym
        "conceptId": "86299006",
        "acceptability": {
          "900000000000509007": "PREFERRED",
          "900000000000508004": "PREFERRED"
        }
      }
    ],
    "limit": 2,
    "total": 2
  },
  [...]
}
```

#### `semanticTags()`

Returns hierarchy tags extracted from FSNs.

An array containing the hierarchy tags from all Fully Specified Name-typed descriptions of the concept is added as an expanded property if this option is present:

```json
GET /snomed-ct/v3/MAIN/concepts/103981000119101?expand=preferredDescriptions(),semanticTags()
{
  "id": "103981000119101",
  "released": true,
  "active": true,
  "effectiveTime": "20200131",
  "preferredDescriptions": {
    "items": [
      {
        "id": "3781804016",
        "term": "Proliferative retinopathy following surgery due to diabetes mellitus (disorder)",
        [...]
      },
      [...]
    ]
  }
  [...]
  "semanticTags": [ "disorder" ], // Extracted from the Fully Specified Name; see term above
  [...]
}
```

#### `inactivationProperties()`

Collects information from concept inactivation indicator and historical association reference set members referencing this concept.

Members of `900000000000489007|Concept inactivation indicator attribute value reference set|` and subtypes of `900000000000522004 |Historical association reference set|` hold information about a reason a concept is being retired in a release, as well as suggest potential replacement(s) for future use.

The concept stating the reason for inactivation is placed under `inactivationProperties.inactivationIndicator.id` (a short-hand property exists without an extra nesting, named `inactivationProperties.inactivationIndicatorId`). It is expected that only a single active inactivation indicator exists for an inactive concept.

Historical associations are returned under the property `inactivationProperties.associationTargets` as an array of objects. Each object includes the identifier of the historical association reference set and the target component identifier, in the same manner as described above &ndash; as an object with a single `id` property and as a string value.

```json
GET /snomed-ct/v3/MAIN/concepts/99999003?expand=inactivationProperties()
{
  "id": "99999003",
  "active": false,
  "effectiveTime": "20090731",
  [...]
  "inactivationProperties": {
    "inactivationIndicator": {
      "id": "900000000000487009"
    },
    "associationTargets": [
      {
        "referenceSet": {
          "id": "900000000000524003"
        },
        "targetComponent": {
          "id": "416516009"
        },
        "referenceSetId": "900000000000524003",     // MOVED TO association reference set
        "targetComponentId": "416516009"            // Extension Namespace 1000009
      }
    ],
    "inactivationIndicatorId": "900000000000487009" // Moved elsewhere
  },
  [...]
}
```

{% hint style="warning" %}
While most object values where a single `id` key is present indicate that the property can be expanded to a full resource representation, this is currently **not supported** for inactivation properties; an expand option of `inactivationProperties(expand(inactivationIndicator()))` will not retrieve additional data for the indicator concept.
{% endhint %}

#### `members()`

Expands reference set members referencing this concept.

Note that this is different from reference set member expansion on a reference set, ie. `referenceSet(expand(members()))`, as this option will return reference set members where the `referencedComponentId` property matches the concept SCTID, from multiple reference sets (if permitted by other expand options). Inactivation and historical association members can also be returned here, in their entirety (as opposed to the summarized form described in `inactivationProperties()` above).

Reference set members can also be fetched in a "standalone" fashion via the [SNOMED CT Reference Set Member API](refsets.md).

Compare the output with the one returned when inactivation indicators were expanded. The last two reference set members correspond to the historical association and the inactivation reason, respectively:

```json
GET /snomed-ct/v3/MAIN/concepts/99999003?expand=members()
{
  "id": "99999003",
  [...]
  "members": {
    "items": [
      {
        "id": "f2b12ff9-794a-5a05-8027-88f0492f3766",
        "released": true,
        "active": true,
        "effectiveTime": "20020131",
        "moduleId": "900000000000207008",
        "iconId": "99999003",
        "referencedComponent": {
          "id": "99999003"
        },
        "refsetId": "900000000000497000",    // CTV3 simple map
        "referencedComponentId": "99999003", // all referencedComponentIds match the concept's SCTID
        "mapTarget": "XUPhG"                 // additional properties are displayed depending on the
                                             // reference set type
      },
      {
        "id": "5e9787df-11af-54ed-ae92-0ea3bc83f2ac",
        "released": true,
        "active": true,
        "effectiveTime": "20090731",
        "moduleId": "900000000000207008",
        "iconId": "99999003",
        "referencedComponent": {
          "id": "99999003"
        },
        "refsetId": "900000000000524003",    // MOVED TO association reference set
        "referencedComponentId": "99999003",
        "targetComponentId": "416516009"     // Extension Namespace 1000009
      },
      {
        "id": "9ffd949a-27d0-5811-ad48-47ff43e1bded",
        "released": true,
        "active": true,
        "effectiveTime": "20090731",
        "moduleId": "900000000000207008",
        "iconId": "99999003",
        "referencedComponent": {
          "id": "99999003"
        },
        "refsetId": "900000000000489007",    // Concept inactivation indicator reference set
        "referencedComponentId": "99999003",
        "valueId": "900000000000487009"      // Moved elsewhere
      }
    ],
    "limit": 3,
    "total": 3
  },
  [...]
}
```

The following expand options are supported within `members(...)`:

- `active: true | false`

Controls whether only active or inactive reference set members should be returned.

- `refSetType: "{type}" | [ "{type}"(,"{type}")* ]`

The reference set type(s) as a string, to be included in the expanded output; when multiple types are accepted, values must be enclosed in square brackets and separated by a comma.

- `expand(...)`

Allows nested expansion of reference set member properties.

Allowed reference set type constants are (these are described in the [Reference Set Types](https://confluence.ihtsdotools.org/display/DOCRFSPG/5.+Reference+Set+Types)🌎 section of SNOMED International's "Reference Sets Practical Guide" and the [Reference Set Types](https://confluence.ihtsdotools.org/display/DOCRELFMT/5.2+Reference+Set+Types)🌎 section of "Release File Specification" in more detail):

- `SIMPLE` - simple type
- `SIMPLE_MAP` - simple map type
- `LANGUAGE` - language type
- `ATTRIBUTE_VALUE` - attribute-value type
- `QUERY` - query specification type
- `COMPLEX_MAP` - complex map type
- `DESCRIPTION_TYPE` - description type
- `CONCRETE_DATA_TYPE` - concrete data type (vendor extension for representing concrete values in Snow Owl)
- `ASSOCIATION` - association type
- `MODULE_DEPENDENCY` - module dependency type
- `EXTENDED_MAP` - extended map type
- `SIMPLE_MAP_WITH_DESCRIPTION` - simple map type with map target description (vendor extension for storing a descriptive label with map targets, suitable for display)
- `OWL_AXIOM` - OWL axiom type
- `OWL_ONTOLOGY` - OWL ontology declaration type
- `MRCM_DOMAIN` - MRCM domain type
- `MRCM_ATTRIBUTE_DOMAIN` - MRCM attribute domain type
- `MRCM_ATTRIBUTE_RANGE` - MRCM attribute range type
- `MRCM_MODULE_SCOPE` - MRCM module scope type
- `ANNOTATION` - annotation type
- `COMPLEX_BLOCK_MAP` - complex map with map block type (added for national extension support)

See the following example for combining reference set member status filtering and reference set type restriction:

```json
GET /snomed-ct/v3/MAIN/concepts/99999003?expand=members(active:true, refSetType:["ASSOCIATION","ATTRIBUTE_VALUE"])
{
  "id": "99999003",
  [...]
  "members": {
    [
      {
        "id": "5e9787df-11af-54ed-ae92-0ea3bc83f2ac",
        "released": true,
        "active": true,
        "effectiveTime": "20090731",
        "moduleId": "900000000000207008",
        "iconId": "99999003",
        "referencedComponent": {
          "id": "99999003"
        },
        "refsetId": "900000000000524003",    // MOVED TO association reference set
        "referencedComponentId": "99999003",
        "targetComponentId": "416516009"     // Extension Namespace 1000009
      },
      {
        "id": "9ffd949a-27d0-5811-ad48-47ff43e1bded",
        "released": true,
        "active": true,
        "effectiveTime": "20090731",
        "moduleId": "900000000000207008",
        "iconId": "99999003",
        "referencedComponent": {
          "id": "99999003"
        },
        "refsetId": "900000000000489007",    // Concept inactivation indicator reference set
        "referencedComponentId": "99999003",
        "valueId": "900000000000487009"      // Moved elsewhere
      }
    ],
    "limit": 2,
    "total": 2
  },
  [...]
}
```

#### `module()`

Expands the concept's module identified by property `moduleId`, and places it under the property `module`. As the returned resource is a concept itself, property expansion can apply to modules as well by using a nested `expand()` option.

{% hint style="warning" %}
Property `module` does not appear in compact form (with a single `id` key) in the standard representation.
{% endhint %}

```json
GET /snomed-ct/v3/MAIN/concepts/138875005?expand=module()
{
  "id": "138875005",
  "active": true,
  [...]
  // The moduleId of the requested concept
  "moduleId": "900000000000207008",
  "module": {                   // Expanded module concept resource
    "id": "900000000000207008", // SCTID matches 138875005's moduleId
    "released": true,
    "active": true,
    "effectiveTime": "20020131",
    // The moduleId of the module concept
    "moduleId": "900000000000012004",
    "iconId": "900000000000445007",
    "definitionStatus": {
      "id": "900000000000074008"
    },
    "subclassDefinitionStatus": "NON_DISJOINT_SUBCLASSES",
    "ancestorIds": [...],
    [...]
    "definitionStatusId": "900000000000074008"
  },
  [...]
  "definitionStatusId": "900000000000074008"
}
```

#### `definitionStatus()`

Expands the definition status concept identified by the property `definitionStatusId`, and places it under the property `definitionStatus`. When this property is not expanded, a smaller placeholder object with a single `id` property is returned in the response. Nested `expand()` options work the same way as in the case of `module()`.

```json
GET /snomed-ct/v3/MAIN/concepts/138875005?expand=definitionStatus()
{
  "id": "138875005",
  "active": true,
  // The definitionStatusId of the requested concept
  "definitionStatusId": "900000000000074008",
  "definitionStatus": {         // Expanded definition status concept resource
    "id": "900000000000074008", // SCTID matches 138875005's definitionStatusId
    "active": true,
    "effectiveTime": "20020131",
    [...]
    // The definitionStatusId of the definition status concept
    "definitionStatusId": "900000000000074008"
  },
  [...]
}
```

#### `pt()` and `fsn()`

Expands the [Preferred Term](https://confluence.ihtsdotools.org/display/DOCEG/Preferred+Term)🌎 (**PT** for short) and the [Fully Specified Name](https://confluence.ihtsdotools.org/display/DOCEG/Fully+Specified+Name)🌎 (**FSN** for short) of the concept, respectively.

These descriptions are language context-dependent; the use of certain descriptions can be preferred in one dialect and acceptable or discouraged in others. The final output is controlled by the [Accept-Language](https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Accept-Language)🌎 request header, which clients can use to supply a list of locales in order of preference.

In addition to the standard locales like `en-US`, Snow Owl uses an extension to allow referring to language reference sets by identifier, in the form of `{language code}-x-{language reference set ID}`. "Traditional" language tags are resolved to language reference set IDs as part of executing the request by consulting the code system settings:

```json
GET /codesystems/SNOMEDCT-UK-CL
{
  "id": "SNOMEDCT-UK-CL",
  "title": "SNOMED CT UK Clinical Extension",
  [...]
  "settings": {
    "languages": [
      {
        "languageTag": "en",   // the language tag
        "languageRefSetIds": [ // the corresponding language reference sets, in order of preference
          "900000000000509007",
          "900000000000508004",
          "999001261000000100",
          "999000691000001104"
        ]
      },
      {
        "languageTag": "en-us",
        "languageRefSetIds": [
          "900000000000509007"
        ]
      },
      {
        "languageTag": "en-gb",
        "languageRefSetIds": [
          "900000000000508004",
          "999001261000000100",
          "999000691000001104"
        ]
      },
      {
        "languageTag": "en-nhs-pharmacy",
        "languageRefSetIds": [
          "999000691000001104"
        ]
      },
      {
        "languageTag": "en-nhs-clinical",
        "languageRefSetIds": [
          "999001261000000100"
        ]
      }
    ],
    [...]
  },
  [...]
}
```

An example response pair demonstrating cases where the PT is different in certain dialects:

```json
GET /snomed-ct/v3/MAIN/concepts/703247007?expand=pt()
// Accept-Language: en-US
{
  "id": "703247007",
  [...]
  "pt": {
    "id": "3007370016",
    "term": "Color",
    [...]
    "conceptId": "703247007", // conceptId matches the concept's SCTID
    "acceptability": {
      // Use of "Color" is preferred in the US English language reference set,
      // but not acceptable in others
      "900000000000509007": "PREFERRED"
    }
  },
  [...]
}
```

```json
GET /snomed-ct/v3/MAIN/concepts/703247007?expand=pt()
// Accept-Language: en-x-900000000000508004
{
  "id": "703247007",
  [...]
  "pt": {
    "id": "3007469016",
    "term": "Colour",
    [...]
    "conceptId": "703247007",
    "acceptability": {
      // Use of "Colour" is preferred in the GB English language reference set,
      // but not acceptable in others
      "900000000000508004": "PREFERRED"
    }
  },
  [...]
}
```

#### `descriptions()`

Expands all descriptions associated with the concept, and adds them to a collection resource (that includes an element limit and a total hit count) under the property `descriptions`. These can also be retrieved separately by the use of the [SNOMED CT Description API](descriptions.md).

{% hint style="warning" %}
The collection resource's `limit` and `total` values are set to the same value (the number of descriptions returned for the concept) because a description fetch limit can not be set via a property expand option.
{% endhint %}

The following expand options are supported within `descriptions(...)`:

- `active: true | false`

Controls whether only active or inactive descriptions should be included in the response. (If both are required, do not set any value for this expand property.)

- `typeId: "{expression}"`

An ECL expression that restricts the `typeId` property of each returned description. The simplest expression is a single SCTID, eg. when this option has a value of `"900000000000013009"`, only [Synonyms](https://confluence.ihtsdotools.org/display/DOCEG/Synonym)🌎 will be expanded.

- `sort: "{field}(:{asc | desc})?"(, "{field}(:{asc | desc})")*`

Items in the collection resource are sorted based on the sort configuration given in this option. A single, comma-separated string value is expected; field names and sort order must be separated by a colon (`:`) character. When no sort order is given, ascending order (`asc`) is assumed.

- `expand(...)`

Allows nested expansion of description properties.

```json
GET /snomed-ct/v3/MAIN/concepts/86299006?expand=descriptions(active: true, sort: "term.exact:asc")
{
  "id": "86299006",
  [...]
  "descriptions": {
    "items": [
      {
        "id": "1235125018",
        "released": true,
        "active": true,
        "effectiveTime": "20070731",
        "moduleId": "900000000000207008",
        "iconId": "900000000000013009",
        "term": "Fallot's tetralogy",   // Descriptions are sorted by term (case insensitive)
        "semanticTag": "",
        "languageCode": "en",
        "caseSignificance": {
          "id": "900000000000017005"
        },
        "concept": {
          "id": "86299006"
        },
        "type": {
          "id": "900000000000013009"
        },
        "typeId": "900000000000013009", // Synonym
        "conceptId": "86299006",        // conceptId property matches the concept's SCTID
        "caseSignificanceId": "900000000000017005",
        "acceptability": {
          "900000000000509007": "ACCEPTABLE",
          "900000000000508004": "ACCEPTABLE"
        }
      },
      {
        "id": "143125014",
        "active": true,
        "term": "Subpulmonic stenosis, ventricular septal defect, overriding aorta, AND right ventricular hypertrophy",
        [...]
      },
      {
        "id": "143123019",
        "active": true,
        "term": "Tetralogy of Fallot",
        [...]
      },
      {
        "id": "828532012",
        "active": true,
        "term": "Tetralogy of Fallot (disorder)",
        "typeId": "900000000000003001", // Fully Specified Name
        [...]
      },
      {
        "id": "1235124019",
        "active": true,
        "term": "TOF - Tetralogy of Fallot",
        [...]
      }
    ],
    "limit": 5,
    "total": 5
  },
  [...]
}
```

#### `relationships()`

Retrieves all "outbound" relationships, where the `sourceId` property matches the SCTID of the concept(s), adding them to a property named `relationships` as a collection resource object. The same set of relationships can also be retrieved in standalone form via Snow Owl's [SNOMED CT Relationship API](relationships.md).

{% hint style="warning" %}
`limit` and `total` values on `relationships` are set to the same value (the number of relationships returned for the concept) because a relationship fetch limit can not be set via an expand option.
{% endhint %}

The following expand options are supported within `relationships(...)`:

- `active: true | false`

Controls whether only active or inactive relationships should be included in the response. (If both are required, do not set any value for this expand property.)

- `characteristicTypeId: "{expression}"`

An ECL expression that restricts the `characteristicTypeId` property of each returned relationship. As an example, when this value is set to `"<<900000000000006009"`, both stated and inferred relationships will be returned, as their characteristic type concepts are descendants of `900000000000006009|Defining relationship|`.

- `typeId: "{expression}"`

An ECL expression that restricts the `typeId` property of each returned relationship.

- `destinationId: "{expression}"`

An ECL expression that restricts the `destinationId` property of each returned relationship.

- `sort: "{field}(:{asc | desc})?"(, "{field}(:{asc | desc})")*`

Items in the collection resource are sorted based on the sort configuration given in this option. A single, comma-separated string value is expected; field names and sort order must be separated by a colon (`:`) character. When no sort order is given, ascending order (`asc`) is assumed.

- `expand(...)`

Allows nested expansion of relationship properties.

```json
GET /snomed-ct/v3/MAIN/concepts/404684003?expand=relationships(active: true)
{
  "id": "404684003", // Clinical finding
  "active": true,
  [...]
  "relationships": {
    "items": [
      {
        "id": "2472459022",
        "released": true,
        "active": true,
        "effectiveTime": "20040131",
        "moduleId": "900000000000207008",
        "iconId": "116680003",
        "destinationNegated": false,
        "relationshipGroup": 0,
        "unionGroup": 0,
        "characteristicType": {
          "id": "900000000000011006"
        },
        "modifier": {
          "id": "900000000000451002"
        },
        "source": {
          "id": "404684003"
        },
        "type": {
          "id": "116680003"
        },
        "destination": {
          "id": "138875005"
        },
        "typeId": "116680003",
        "modifierId": "900000000000451002",
        "sourceId": "404684003", // sourceId property matches concept's SCTID
        "destinationId": "138875005",
        "characteristicTypeId": "900000000000011006"
      }
    ],
    "limit": 1,
    "total": 1
  },
  [...]
}
```

#### `inboundRelationships()`

Retrieves all "inbound" relationships, where the `destinationId` property matches the SCTID of the concept(s), adding them to property `inboundRelationships`.

{% hint style="warning" %}
`limit` and `total` values on `inboundRelationships` are set to the same value (the number of inbound relationships returned for the concept), but differently from options above, **a fetch limit is applied** when it is specified.
{% endhint %}

The same set of options are supported within `inboundRelationships` as in `relationships` (see [above](#relationships)), with three important differences:

- ~~`destinationId: "{expression}"`~~

This option is not supported on `inboundRelationships`; all destination IDs match the concept's SCTID.

- `sourceId: "{expression}"`

An ECL expression that restricts the `sourceId` property of each returned relationship.

- `limit: {limit}`

Limits the maximum number of inbound relationships to be returned. Not recommended for use when the expand option applies to a collection of concepts, not just a single one, as the limit is not applied individually for each concept.

#### `descendants()` / `statedDescendants()`

Depending on which `direct` setting is used, retrieves all concepts whose `[stated]parentIds` and/or `[stated]AncestorIds` array contains this concept's SCTID. Results are added to property `descendants` or `statedDescendants`, based on the option name used.

Only active concepts are returned, as these are expected to have active "IS A" relationships or OWL axioms that describe the relative position of the concept within the terminology graph.

The following options are available:

- `direct: true | false` (required)

Controls whether only direct descendants should be collected or a transitive closure of concept subtypes.

When set to `true`, property `[stated]parentIds` will be searched only, otherwise both `[stated]parentIds` and `[stated]AncestorIds` are used. The presence or absence of the "stated" prefix in the search field depends on the option name.

- `limit: 0`

Applicable only when a single concept's properties are expanded. Collects the number of descendants in an efficient manner, and sets the `total` property of the returned collection resource without including any concepts in it. **Not used when a collection of concepts are expanded in a single request, or any other value is given.**

- `expand(...)`

Allows nested expansion of concept properties on each collected descendant.

```json
GET /snomed-ct/v3/MAIN/concepts/138875005?expand=descendants(direct: true)
{
  "id": "138875005", // SNOMED CT Concept
  "active": true,
  [...]
  "descendants": {
    "items": [
      {
        "id": "105590001", // Substance
        "released": true,
        "active": true,
        "effectiveTime": "20020131",
        "moduleId": "900000000000207008",
        "iconId": "substance",
        "definitionStatus": {
          "id": "900000000000074008"
        },
        "subclassDefinitionStatus": "NON_DISJOINT_SUBCLASSES",
        "ancestorIds": [
          "-1"
        ],
        "parentIds": [
          "138875005" // parentIds contains SNOMED CT Concept's SCTID, meaning this concept
                      // is a direct (inferred) descendant of it
        ],
        "statedAncestorIds": [
          "-1"
        ],
        "statedParentIds": [
          "138875005"
        ],
        "definitionStatusId": "900000000000074008"
      },
      [...]
    ],
    "limit": 50,
    "total": 19 // Total number of descendants
  },
  [...]
}
```

#### `ancestors()` / `statedAncestors()`

Depending on which `direct` setting is used, retrieves all concepts that appear in this concept's `[stated]parentIds` and/or `[stated]AncestorIds` array. Results are added to property `ancestors` or `statedAncestors`, based on the option name used.

The following options are available:

- `direct: true | false` (required)

Controls whether only direct ancestors should be collected or a transitive closure of concept supertypes.

When set to `true`, property `[stated]parentIds` will be used only for concept retrieval, otherwise the union of `[stated]parentIds` and `[stated]AncestorIds` are collected (the special placeholder value "-1" is ignored). The presence or absence of the "stated" prefix in the search field depends on the option name.

- `limit: 0`

Collects the number of ancestors in an efficient manner, and sets the `total` property of the returned collection resource without including any concepts in it. **Not used when any other value is given** (however, this property expansion supports cases where multiple concepts' ancestors need to be returned).

- `expand(...)`

Allows nested expansion of concept properties on each collected ancestor.

## Operations

### Retrieve single concept by ID (GET)

A GET request that includes a concept identifier as its last path parameter will return information about the concept in question:

```json
GET /snomed-ct/v3/MAIN/2019-07-31/concepts/138875005
```

#### **Query parameters**

- `expand={options}`

Concept properties that should be returned along with the original request, as part of the concept resource. See available options in section [Property expansion](#property-expansion) above.

- `field={field1}[,{fieldN}]*`

Restricts the set of fields returned from the index. Results in a smaller response object when only specific information is needed.

Supported names for field selection are the following:

- `active`
- `activeMemberOf`
- `ancestors` - controls the appearance of `ancestorIds` as well
- `definitionStatusId`
- `doi`
- `effectiveTime`
- `exhaustive`
- `iconId`
- `id` - always included in the response, even when not present as a `field` parameter
- `mapTargetComponentType`
- `memberOf`
- `moduleId`
- `namespace`
- `parents` - controls the appearance of `parentIds` as well
- `preferredDescriptions`
- `refSetType`
- `referencedComponentType`
- `released`
- `score`
- `semanticTags`
- `statedAncestors` - controls the appearance of `statedAncestorIds` as well
- `statedParents` - controls the appearance of `statedParentIds` as well
- ~~`created`~~ and ~~`revised`~~ - these fields are associated with revision control, and even though they are listed as supported fields, they do not appear in the response even when explicitly requested.

Specifying any other field name results in a `400 Bad Request` response:

```json
GET /snomed-ct/v3/MAIN/2019-07-31/concepts/138875005?field=xyz
{
  "status": 400,
  "code": 0,
  "message": "Unrecognized concept model property '[xyz]'.",
  "developerMessage": "Supported properties are '[active, activeMemberOf, ancestors, ...]'.",
  "errorCode": 0,
  "statusCode": 400
}
```

Fields with a value of `null` do not appear in the response, even if they are selected for inclusion.

```json
GET /snomed-ct/v3/MAIN/2019-07-31/concepts/138875005?field=id,active,score
{
  "id": "138875005",
  "active": true
  // score was not calculated, and so is not present
}
```

#### **Request headers**

- `Accept-Language: {language-range}[;q={weight}](, {language-range}[;q={weight}])*`

Controls the logic behind Preferred Term and Fully Specified Name selection for the concept. See the documentation for expand options [pt() and fsn()](#pt-and-fsn) for details.

Specifying an unknown language or dialect results in a `400 Bad Request` response:

```json
GET /snomed-ct/v3/MAIN/2019-07-31/concepts/138875005?expand=fsn()
// Accept-Language: hu-HU
{
  "status": 400,
  "code": 0,
  "message": "Don't know how to convert extended locale [hu-hu] to a language reference set identifier.",
  "developerMessage": "Input representation syntax or validation errors. Check input values.",
  "errorCode": 0,
  "statusCode": 400
}
```

### Find concepts (GET)

A GET request that ends with `concepts` as its last path parameter will search for concepts matching all of the constraints supplied as query parameters. By default (when no query parameter is added) it returns all concepts.

The response consists of a collection of concept resources, a `searchAfter` key (described in section "Query parameters" below), the limit used when computing response items and the total hit count:

```json
GET /snomed-ct/v3/SNOMEDCT/2021-01-31/concepts
{
  "items": [
    {
      "id": "100000000", // Each item represents a concept resource
      "released": true,
      "active": false,
      "effectiveTime": "20090731",
      "moduleId": "900000000000207008",
      "iconId": "138875005",
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
    },
    [...] // at most 50 items are returned when no limit is specified
  ],
  "searchAfter": "AoEpMTAwMDQyMDAz", // key can be used for paged results
  "limit": 50,                       // the limit given in the original request
                                     // (or the default limit if not specified)
  "total": 481509                    // the total number of concept matches
}
```

#### **Query parameters**

- `definitionStatus={eclExpression} | {id1}[,{idN}]*`

An ECL expression or enumerated list that describes the allowed set of SCTIDs that must appear in matching concepts' `definitionStatusId` property. Since there are only two values used, `900000000000074008|Primitive|` and `900000000000073002|Defined|` for primitive and fully defined concepts, respectively, a single SCTID is usually entered here.

- `ecl={eclExpression}`

Restricts the returned set of concepts to those that match the specified ECL expression. The query parameter can be used on its own for evaluation of expressions, or in combination with other query parameters. Expressions conforming to the short form of ECL 1.5 syntax are accepted. The expression is evaluated over the [inferred view](https://confluence.ihtsdotools.org/display/DOCGLOSS/Inferred+view)🌎, based on the currently persisted inferred relationships.

{% hint style="warning" %}
As ECL syntax uses special symbols, query parameters should be encoded to URL-safe characters. The examples in this section are using the cleartext form for better readability.
{% endhint %}

```json
GET /snomed-ct/v3/SNOMEDCT/2021-01-31/concepts?ecl=<<404684003|Clinical finding|:363698007|Finding site|=40238009|Hand joint structure|
{
  "items": [
    [...]
    {
      "id": "129157005",
      "active": true,
      [...]
      "pt": {
        "id": "2664900016",
        "term": "Traumatic dislocation of joint of hand", // Concept match based on ECL expression
        [...]
      },
      [...]
    },
    [...]
  ],
  "searchAfter": "AoEpNDQ4NDUzMDA0",
  "limit": 50,
  "total": 58
}
```

- `statedEcl={eclExpression}`

Same as `ecl`, but the input expression is evaluated over the [stated view](https://confluence.ihtsdotools.org/display/DOCGLOSS/stated+view)🌎 by using stated relationships (if present) and OWL axioms for evaluation.

- `semanticTag={tag1}[,{tagN}]*`

Filters concepts by a comma-separated list of allowed hierarchy tags. Matching concepts can have any of the supplied tags present (at least one) on their Fully Specified Names.

- `term={searchTerm}`

Matching concepts must have an active description whose term matches the string specified here. The search is executed in "smart match" mode; the following examples show which search expresssions match which description terms:

```
Search term       → Term of matched description
-----------------   ---------------------------
"Ångström"          "angstrom"                  (case insensitive, ASCII-folding)
"sys blo pre"       "Systolic blood pressure"   (prefix of each word, matching order)
"broken arm"        "Fracture of arm"           (synonym filter, ignored stopwords)
"greenstick frac"   "Greenstick fracture"       (prefix match for final query keyword,
                                                exact match for all others)
```

- `descriptionType={eclExpression} | {id1}[,{idN}]*`

Restricts the result set by description type; matches must have at least one active description whose `typeId` property is included in the evaluated ECL result set or SCTID list. It is typically used in combination with `term` (see above) to control which type of descriptions should be matched by term.

- `parent={id1}[,{idN}]*`
- `statedParent={id1}[,{idN}]*`
- `ancestor={id1}[,{idN}]*`
- `statedAncestor={id1}[,{idN}]*`

Filters concepts by hierarchy. All four query parameters accept a comma-separated list of SCTIDs; the result set will contain direct descendants of the specified values in the case of `parent` and `statedParent`, and a transitive closure of descendants for `ancestor` and `statedAncestor` (including direct children). Parameters starting with `stated...` will use the stated IS A hierarchy for computations.

```json
GET /snomed-ct/v3/SNOMEDCT/2021-01-31/concepts?parent=138875005&field=id
{
  "items": [
    // Inferred direct descendants of 138875005|Snomed CT Concept|
    { "id": "105590001" }, // Substance
    { "id": "123037004" }, // Body structure
    { "id": "123038009" }, // Specimen
    [...]
  ],
  "searchAfter": "AoEyOTAwMDAwMDAwMDAwNDQxMDAz",
  "limit": 50,
  "total": 19 // 19 top-level concepts returned in total
}
```

- `doi=true | false`

Controls whether relevance-based sorting should take **Degree of Interest** (DOI for short) into account. When enabled, concepts that are used frequently in a clinical environment are favored over concepts with a lower likelihood of use.

- `namespace={namespaceIdentifier}`
- `namespaceConceptId={id1}[,{idN}]*`

The SCTID of matching concepts must have the specified 7-digit [namespace identifier](https://confluence.ihtsdotools.org/display/DOCRELFMT/6.6+Namespace-Identifier)🌎, eg. `1000154`. When matching by namespace concept ID, a comma-separated list of SCTIDs are expected, and the associated 7-digit identifier will be extracted from the active FSNs of each concept entered here.

```json
GET /snomed-ct/v3/SNOMEDCT-UK-CL/concepts?namespaceConceptId=370138007&field=id
{
  "items": [
    // Concept IDs with a namespace identifier of "1000001", corresponding to
    // namespace concept 370138007|Extension Namespace {1000001}|
    {
      "id": "999000011000001104" // 99900001>>1000001<<104
    },
    [...]
  ],
  "searchAfter": "AoEyOTk5MDAwODcxMDAwMDAxMTAy",
  "limit": 50,
  "total": 4
}
```

- `isActiveMemberOf={eclExpression} | {id1}[,{idN}]*`

This filter accepts either a single ECL expression, or a comma-separated list of reference set SCTIDs. For each matching concept at least one active reference set member must exist where the `referenceComponentId` points to the concept and the `referenceSetId` property is listed in the filter, or is a member of the evaluated ECL expression's result set.

- `effectiveTime={yyyyMMdd} | Unpublished`

Filters concepts by effective time. The query parameter accepts a single effective time in `yyyyMMdd` (short) format, or the literal `Unpublished` when searching for concepts that have been modified since they were last published as part of a code system version.

Note that only the concept's effective time is taken into account, not any of its related core components (descriptions, relationships) or reference set members. If the concept's status, definition status or module did not change since the last release, its effective time will not change either.

{% hint style="warning" %}
When searching for `Unpublished` concepts, the `effectiveTime` property will not appear on returned concept resources, as the value is `null` for all unpublished components.
{% endhint %}

```json
GET /snomed-ct/v3/SNOMEDCT/2021-01-31/concepts?effectiveTime=20170131&field=id,effectiveTime
{
  "items": [
    {
      "id": "10151000132103",
      "effectiveTime": "20170131" // Concept effective time matches query parameter
    },
    {
      "id": "10231000132102",
      "effectiveTime": "20170131"
    },
    [...]
  ],
  "searchAfter": "AoEwMTA3NTQ3MTAwMDExOTEwNw==",
  "limit": 50,
  "total": 5580 // Total number of concepts with effective time 2017-01-31
}
```

- `active=true | false`

Filters concepts by status. When set to `true`, only active concepts are added to the resulting collection, while a value of `false` collects inactive concepts only. (If both active and inactive concepts should be returned, do not add this parameter to the query.)

- `module={eclExpression} | {id1}[,{idN}]*`

Filters concepts by `moduleId`. The query parameter accepts either a single ECL expression, or a comma-separated list of module SCTIDs; concepts must have a `moduleId` property that is included in the ID list or the evaluated ECL result.

- `id={id1}[,{idN}]*`

Filters concepts by SCTID. The parameter accepts a comma-separated list of IDs; matching concepts must have an `id` property that matches any of the specified identifiers.

- `sort: "{field}(:{asc | desc})?"(, "{field}(:{asc | desc})")*`

Sorts returned concept resources based on the sort configuration given in this parameter. Field names and sort order must be separated by a colon (`:`) character. When no sort order is given, ascending order (`asc`) is assumed.

Field names supported for sorting are the same that are used for field selection; please see [above](#retrieve-single-concept-by-id-get) for the complete list.

{% hint style="warning" %}
The default behavior is to sort results by `id`, in ascending order. SCTIDs are sorted lexicographically, not as numbers; this means that eg. `10683591000119104` will appear before `10724008`, as their first two digits are the same, but the third digit is smaller in the former identifier.
{% endhint %}

- `limit={limit}`

Controls the maximum number of items that should be returned in the collection. When not specified, the default limit is `50` items.

- `searchAfter={searchAfter}`

Supports **keyset pagination**, ie. retrieving the next page of items based on the response for the current page. To use, set `limit` to the number of items expected on a single page, then run the first search request without setting a `searchAfter` key. The returned response will include the value to be inserted into the next request:

```json
GET /snomed-ct/v3/SNOMEDCT/2021-01-31/concepts?effectiveTime=20170131&field=id,effectiveTime
{
  "items": [
    {
      "id": "10151000132103",
      "effectiveTime": "20170131"
    },
    {
      "id": "10231000132102",
      "effectiveTime": "20170131"
    },
    [...]
  ],
  // Key to use in the request for the second page
  "searchAfter": "AoEwMTA3NTQ3MTAwMDExOTEwNw==",
  "limit": 50,
  "total": 5580
}

GET /snomed-ct/v3/SNOMEDCT/2021-01-31/concepts?effectiveTime=20170131&field=id,effectiveTime&searchAfter=AoEwMTA3NTQ3MTAwMDExOTEwNw==
{
  "items": [
    // List continues from the last item of the previous request
    // (but the item itself is not included)
    {
      "id": "1075481000119105",
      "effectiveTime": "20170131"
    },
    {
      "id": "10759271000119104",
      "effectiveTime": "20170131"
    },
    [...]
  ],
  // Different key returned for the third page
  "searchAfter": "AoEwMTA4MTgxMTAwMDExOTEwNw==",
  "limit": 50,
  "total": 5580
}
```

The process can be repeated until the `items` array turns up empty, indicating that there are no more pages to return.

{% hint style="warning" %}
`searchAfter` keys should be considered opaque; they can not be constructed to jump to an arbitrary point in the enumeration. Keyset pagination also doesn't handle cases gracefully where eg. concepts with "smaller" SCTIDs are inserted while pages are retrieved from the server. If a consistent result set is expected, a [point-in-time](#path-with-timestamp) path parameter should be used in consecutive search requests.
{% endhint %}

- `expand={options}`

Concept properties that should be returned along with the original request, as part of the concept resource. See available options in section [Property expansion](#property-expansion) above.

- `field={field1}[,{fieldN}]*`

Restricts the set of fields returned from the index. Results in a smaller response object when only specific information is needed. See [above](#retrieve-single-concept-by-id-get) for the list of supported field names.

#### **Request headers**

- `Accept-Language: {language-range}[;q={weight}](, {language-range}[;q={weight}])*`

Controls the logic behind Preferred Term and Fully Specified Name selection for returned concepts. See the documentation for expand options [pt() and fsn()](#pt-and-fsn) for details.

### Find concepts (POST)

POST requests submitted to `concepts/search` perform the same search operation as described for the GET request above, but each query parameter is replaced by a property in the JSON request body:

```json
POST /snomed-ct/v3/SNOMEDCT/2021-01-31/concepts/search
// Request body
{
  // Query parameters allowing multiple values must be passed as arrays
  "expand": [ "pt()" ],
  "field": [ "id", "preferredDescriptions" ],
  "limit": 100,
  "active": true,
  "module": [ "900000000000012004" ]
}

// Response
{
  "items": [
    {
      "id": "1003316002",
      "moduleId": "900000000000012004",
      "pt": {
        "id": "4167978019",
        "term": "Extension Namespace 1000256",
        [...]
      }
    },
    {
      "id": "1003317006",
      "moduleId": "900000000000012004",
      "pt": {
        "id": "4167981012",
        "term": "Extension Namespace 1000257",
        [...]
      }
    }
  ],
  "searchAfter": "AoEqMTAwMzMxNzAwNg==",
  "limit": 2,
  "total": 1802
}
```

#### **Request headers**

- `Accept-Language: {language-range}[;q={weight}](, {language-range}[;q={weight}])*`

Controls the logic behind Preferred Term and Fully Specified Name selection for returned concepts. See the documentation for expand options [pt() and fsn()](#pt-and-fsn) for details.

### Create concept (POST)

POST requests submitted to `concepts` create a new concept with the specified parameters, then commit the result to the terminology repository.

The resource path typically consists of a single code system identifier for these requests, indicating that changes should go directly to the working branch of the code system, or a direct child of the working branch for isolating a set of changes that can be reviewed and merged in a single request.

The request body needs to conform to the following requirements:

- include at least one Fully Specified Name (FSN)
- include at least one preferred synonym (Preferred Term, PT)

The SCTID of created components can be specified in two ways:

1. Explicitly by setting the `id` property on the component object; the request fails when an existing component in the repository already has the same SCTID assigned to it;
2. Allowing the server to generate an identifier by leaving `id` unset and populating `namespaceId` with the expected namespace identifier, eg. `"1000154"`. Requests using `namespaceId` should not fail due to an SCTID collision, as generated identifiers are checked for uniqueness.

When a `namespaceId` is set on the concept level, descriptions and relationships will use this value by default, so in this case neither `id` nor `namespaceId` needs to be set on them. The same holds true for `moduleId` &ndash; the concept's module identifier is applied to all related descriptions, relationships and reference set members in the request, unless it is set to a different value on the component object.

Please see the example below for required properties. (Note that it is non-executable in its current form, as the OWL axiom reference set member can not be created without knowing the concept's SCTID in advance.)

A successful commit will result in a `201 Created` response; the response header `Location` can be used to extract the generated concept identifier. Validation errors in the request body cause a `400 Bad Request` response.

```json
// Create a concept on the working branch of code system SNOMEDCT-B2I
POST /snomed-ct/v3/SNOMEDCT-B2I/concepts
// Request body
{
  "active": true,
  "moduleId": "636635721000154103", // SNOMED CT B2i extension module
  "namespaceId": "1000154",         // B2i Healthcare's namespace identifier
  "definitionStatusId": "900000000000074008", // Primitive
  "descriptions": [
    // Create mandatory FSN and PT
    {
      "active": true,
      // "moduleId", "namespaceId" will be set from the concept
      // "id" will be generated for the description
      // "conceptId" will be automatically populated with the new concept's SCTID
      "typeId": "900000000000003001", // Fully specified name
      "term": "Example concept (disorder)",
      "languageCode": "en",
      "caseSignificanceId": "900000000000448009", // Case insensitive
      "acceptability": {
        /*
           Acceptability map entries are keyed by language reference set ID.
           Allowed values are "PREFERRED" and "ACCEPTABLE".
        */
        "900000000000509007": "PREFERRED" // US English
      }
    },
    {
      "active": true,
      "typeId": "900000000000013009", // Synonym
      "term": "Example concept",
      "languageCode": "en",
      "caseSignificanceId": "900000000000448009", // Case insensitive
      "acceptability": {
        "900000000000509007": "PREFERRED" // US English
      }
    }
  ],
  "relationships": [
    /*
       Including relationships on a new concept request is optional.

       However, when no inferred IS A relationship is created, the concept will not
       be visible in the inferred hierarchy (and not show up in eg. ECL evaluations)
       until a classification is run on the branch, and suggested changes are saved.
    */
    {
      "active": true,
      // "moduleId", "namespaceId" will be set from the concept
      // "id" will be generated for the relationship
      // "sourceId" will be automatically populated with the new concept's SCTID
      "typeId": "116680003",       // IS A
      "destinationId": "64572001", // Disease
      "destinationNegated": false,
      "relationshipGroup": 0,
      "unionGroup": 0,
      "characteristicTypeId": "900000000000011006", // Inferred relationship
      "modifierId": "900000000000451002" // Some (existential restriction)
    }
  ],
  "members": [
    {
      // "id" is an UUID, will be automatically generated when not given
      "active": true,
      // "moduleId" needs to be set for reference set members; it is not propagated
      "moduleId": "636635721000154103",
      // "referencedComponentId" will be automatically populated with the new concept's SCTID
      "refsetId": "733073007",
      /*
         Additional properties of the reference set should be added here. For an OWL
         axiom reference set member, the property to the reference set type is called
         "owlExpression".

         At the moment we can not create an OWL axiom member for concepts that do not include
         a concept ID in advance.
      */
      "owlExpression": "SubClassOf(:<conceptId> :64572001)"
    }
  ],
  "commitComment": "Create new example concept"
}

// Response: 201 Created
// Location: /snomed-ct/v3/SNOMEDCT-B2I/concepts/<SCTID of created concept>
```

#### **Request headers**

- `X-Author: {author_id}`

Changes the author recorded in the commit message from the authenticated user (default) to the specified user.

### Update concept (PUT)

### Delete concept (DELETE)

DELETE requests sent to a URI where the last path parameter is an existing concept ID will remove the concept and all of its associated components (descriptions, relationships, reference set members referring to the concept) from the terminology repository.

Deletes are acknowledged with a `204 No Content` response on success. Deletion can be verified by trying to retrieve concept information from the same resource path &ndash; a `404 Not Found` should be returned in this case.

Note that resource branches maintain content in isolation, and so deleting a concept on eg. a task branch will not remove the concept from the code system's working branch, until work on the task branch is approved and merged into mainline.

#### **Query parameters**

- `force=true | false`

Specifies whether deletion of the concept should be allowed, if it has components that were already part of an RF2 release (or code system version). This is indicated by the `released` property on each component.

The default value is `false`; with the option disabled, attempting to delete a released component results in a `409 Conflict` response:

```json
DELETE /snomed-ct/v3/SNOMEDCT/2021-01-31/concepts/138875005
{
  "status": 409,
  "code": 0,
  "message": "'concept' '138875005' cannot be deleted.",
  "developerMessage": "'concept' '138875005' cannot be deleted.",
  "errorCode": 0,
  "statusCode": 409
}
```

{% hint style="warning" %}
Only administrators should set this parameter to `true`. It is advised to delete redundant or erroneous components before they are put in circulation as part of a SNOMED CT RF2 release. In other cases, inactivation should be preferred over removal.
{% endhint %}

#### **Request headers**

- `X-Author: {author_id}`

Changes the author recorded in the commit message from the authenticated user (default) to the specified user.
