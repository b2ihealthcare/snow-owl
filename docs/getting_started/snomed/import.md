# Import SNOMED CT Content from RF2

Now let's import an official SNOMED CT RF2 `SNAPSHOT` distribution archive so that we can further explore the available SNOMED CT APIs. 

To import an RF2 archive you must first create an import configuration using the [SNOMED CT Import API](../api/snomed.md) as follows:

```bash
curl -X POST http://localhost:8080/snowowl/snomed-ct/v3/imports -d
{
  "type": "SNAPSHOT",
  "branchPath": "MAIN"
}
```

And the response:
```
HTTP 204 No Content
Location: "http://localhost:8080/snowowl/snomed-ct/v3/imports/96406e91-84a0-49d3-9e6a-c5c652a36eba"
```

The import configuration specifies the `type` of the RF2 release (in this case `SNAPSHOT`) and the target `branchPath` where the content should imported.
The response returns an empty body along with a `Location` header with a URL pointing to the created import configuration. You can extract the last part of the URL to get the import configuration ID which can be used to retrieve the configuration and to upload the actual archive and start the import.

{% hint style="warn" %}
Depending on the size and type of the RF2 package, hardware and Snow Owl configuration, RF2 imports might take hours to complete.
Official SNAPSHOT distributions can be imported in less than 30 minutes by allocating 6 GB of heap size to Snow Owl and configuring Snow Owl to use a solid state disk for its data directory. 
{% endhint %}

The import will start automatically when you upload the archive to the `/imports/:id/archive` endpoint:

```
curl -X POST -F file=@SnomedCT_RF2Release_INT_20170731.zip 'http://localhost:8080/snowowl/snomed-ct/v3/imports/96406e91-84a0-49d3-9e6a-c5c652a36eba/archive'
```

The import process is asynchronous and its status can be checked by sending a GET request to the `/imports/:id` endpoint with the extracted import identifier as follows:

```
curl TODO
```

And the response:

```json
{
  "type": "SNAPSHOT",
  "branchPath": "MAIN",
  "createVersions": false,
  "codeSystemShortName": "SNOMEDCT",
  "id": "ec702c17-88b7-454b-9ebc-d2d1e338658e",
  "status": "RUNNING",
  "startDate": "2018-10-10T10:01:08Z"
}
```

The `status` field describes the current state of the import, while the `startDate` and `completionDate` fields specify `start` and `completion` timestamps.
