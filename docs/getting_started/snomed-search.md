# Search SNOMED CT Content

## GET the ROOT concept:

```bash
curl 'http://localhost:8080/snowowl/snomed-ct/v3/MAIN/concepts/138875005'
```

And the response:

```json
{
  "id": "138875005",
  "released": true,
  "active": true,
  "effectiveTime": "20020131",
  "moduleId": "900000000000207008",
  "iconId": "138875005",
  "definitionStatus": "PRIMITIVE",
  "subclassDefinitionStatus": "NON_DISJOINT_SUBCLASSES"
}
```

## Search by ECL:

```bash
curl 'http://localhost:8080/snowowl/snomed-ct/v3/MAIN/concepts?active=true&ecl=%3C&#33;138875005&limit=1'
```

And the response:

```json
{
  "items": [
    {
      "id": "308916002",
      "released": true,
      "active": true,
      "effectiveTime": "20020131",
      "moduleId": "900000000000207008",
      "iconId": "138875005",
      "definitionStatus": "PRIMITIVE",
      "subclassDefinitionStatus": "NON_DISJOINT_SUBCLASSES"
    }
  ],
  "searchAfter": "AoE_BWVlYzI3Mjc0LTYyZTctNDg3NS05NmVlLThhNTk3OTcxOTJiNw==",
  "limit": 1,
  "total": 19
}
```