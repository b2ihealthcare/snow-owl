# List available Code Systems

Now let's take a peek at our code systems:

```bash
curl http://localhost:8080/snowowl/admin/codesystems
```

And the response:

```json
{
  "items": [
    {
      "oid": "2.16.840.1.113883.6.96",
      "name": "SNOMED CT",
      "shortName": "SNOMEDCT",
      "organizationLink": "http://www.snomed.org",
      "primaryLanguage": "ENG",
      "citation": "SNOMED CT contributes to the improvement of patient care by underpinning the development of Electronic Health Records that record clinical information in ways that enable meaning-based retrieval. This provides effective access to information required for decision support and consistent reporting and analysis. Patients benefit from the use of SNOMED CT because it improves the recording of EHR information and facilitates better communication, leading to improvements in the quality of care.",
      "branchPath": "MAIN",
      "iconPath": "icons/snomed.png",
      "terminologyId": "com.b2international.snowowl.terminology.snomed",
      "repositoryUuid": "snomedStore"
    }
  ]
}
```

Which means, we have a single Code System in Snow Owl, called `SNOMED CT`. It has been created by the SNOMED CT module by default on the first startup of your instance. A Code System lives in a repository and its working `branchPath` is currently associated with the default `MAIN` branch in the `snomedStore` repository.
