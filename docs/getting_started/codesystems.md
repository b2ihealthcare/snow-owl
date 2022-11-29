# List available Code Systems

Now let's take a peek at our code systems:

```bash
curl http://localhost:8080/snowowl/codesystems?pretty
```

The response:

```javascript
{
  "items" : [ ],
  "limit" : 0,
  "total" : 0
}
```

...it sure looks empty! This is expected, as Snow Owl does not contain any predefined code system metadata out of the box. We can create the first code system with the following request:

```
curl -X POST \
-H "Content-type: application/json" \
http://localhost:8080/snowowl/codesystems \
-d '{
  "id": "SNOMEDCT",
  "url": "http://snomed.info/sct/900000000000207008",
  "title": "SNOMED CT International Edition",
  "language": "en",
  "description": "SNOMED CT International Edition",
  "status": "active",
  "copyright": "(C) 2022 International Health Terminology Standards Development Organisation 2002-2022. All rights reserved.",
  "owner": "snowowl",
  "contact": "https://snomed.org",
  "oid": "2.16.840.1.113883.6.96",
  "toolingId": "snomed",
  "settings": {
    "moduleIds": [
      "900000000000207008",
      "900000000000012004"
    ],
    "locales": [
      "en-x-900000000000508004",
      "en-x-900000000000509007"
    ],
    "languages": [
      {
        "languageTag": "en",
        "languageRefSetIds": [
          "900000000000509007",
          "900000000000508004"
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
          "900000000000508004"
        ]
      }
    ],
    "publisher": "SNOMED International",
    "ownerProfileName": "snowowl",
    "namespace": "373872000",
    "maintainerType": "SNOMED_INTERNATIONAL",
    "defaultRefsetModuleId": "900000000000012004",
    "defaultQueryTypeRefsetId": "900000000000513000"
  }
}'
```

{% hint style="info" %}
Use of SNOMED CT is subject to additional conditions not listed here, and the full copyright notice has been shortened for brevity in the request above. Please see [https://www.snomed.org/snomed-ct/get-snomed](https://www.snomed.org/snomed-ct/get-snomed) for details.
{% endhint %}

The request body includes:

* The code system identifier `"SNOMEDCT"`
* Various pieces of metadata offering a human-readable title, ownership and contact information, code system status, URL and OID for identification, etc.
* The tooling identifier `"snomed"` that points to the repository that will store content
* Additional code system settings stored as key-value pairs

If everything goes well, the command will run without any errors (the server returns a "204 No Content" response). We can double-check that code system metadata has been registered correctly with the following request:

```
curl http://localhost:8080/snowowl/codesystems/SNOMEDCT?pretty
```

The expected response is:

```
{
  "id": "SNOMEDCT",
  "url": "http://snomed.info/sct/900000000000207008",
  "title": "SNOMED CT International Edition",
  "language": "en",
  ...
  "branchPath": "MAIN/SNOMEDCT",
  ...
}
```

In addition to the submitted values, you will find that additional administrative properties also appear in the output. One example is `branchPath` which specifies the working branch of the code system within the repository.
