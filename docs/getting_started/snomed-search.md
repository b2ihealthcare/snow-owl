# Search SNOMED CT Content

Now that we have a SNOMED CT Code System, let's take a look at its content. We can query its content using either the [SNOMED CT API](../api/snomed.md) or the [FHIR API](../api/fhir.md). 

For sake of simplicity, let's search for the available concepts using the [SNOMED CT API](../api/snomed.md). For that we will need the branch we would like to query, but fortunately we already know the value from our previous call to the Code Systems API, it was `MAIN`. To list all available concepts in a SNOMED CT Code System, use the following command:

```bash
curl http://localhost:8080/snowowl/snomed-ct/v3/MAIN/concepts
```

And the response is:

```json
{
  "items": [],
  "limit": 50,
  "total": 0
}
```

Which simply means we have no SNOMED CT concepts yet in our instance.
