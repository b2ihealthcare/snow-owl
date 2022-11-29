# SNOMED CT

Now that we have a code system, let's take a look at its content! We can list concepts using either the [SNOMED CT API](../../api/snomed/index.md) tailored to this tooling, or the [FHIR API](../../api/fhir/index.md) for a representation that is uniform across different kinds of code systems. For the sake of simplicity, we will use the former in this example.

To list all available concepts in a code system, use the following command (the second `SNOMEDCT` in the request path represents the code system identifier):

```bash
curl http://localhost:8080/snowowl/snomedct/SNOMEDCT/concepts?pretty
```

The expected response is:

```javascript
{
  "items": [ ],
  "limit": 50,
  "total": 0
}
```

The concept list is empty, indicating that we haven't imported anything into Snow Owl - yet.
