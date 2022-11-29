# Import RF2 distribution

Let's import an RF2 release in `SNAPSHOT` mode so that we can further explore the available SNOMED CT APIs! To do so, use the appropriate request from the [SNOMED CT Import API](../../../index-3/index-1/importing-rf2.md) as follows:

```bash
curl -v http://localhost:8080/snowowl/snomedct/SNOMEDCT/import?type=snapshot \
-F file=@SnomedCT_RF2Release_INT_20170731.zip
```

Curl will display the entire interaction between it and the server, including many request and response headers. We are interested in these two (response) rows in particular:

```
< HTTP/1.1 201 Created
< Location: http://localhost:8080/snowowl/snomedct/SNOMEDCT/import/107f6efa69886bfdd73db5586dcf0e15f738efed
```

The first one indicates that the file was uploaded successfully and a resource has been created to track import progress, while the second row indicates the location of this resource.

{% hint style="info" %}
Depending on the size and type of the RF2 package, hardware and Snow Owl configuration, RF2 imports might take hours to complete. Official SNAPSHOT distributions can be imported in less than 30 minutes by allocating 6 GB of heap size to Snow Owl and configuring it to use a solid state disk for the data directory.
{% endhint %}

The process itself is asynchronous and its status can be checked by periodically sending a GET request to the location indicated by the response header:

```bash
curl http://localhost:8080/snowowl/snomedct/SNOMEDCT/import/107f6efa69886bfdd73db5586dcf0e15f738efed?pretty
```

The expected response while the import is running:

```json
{
  "id" : "107f6efa69886bfdd73db5586dcf0e15f738efed",
  "status" : "RUNNING"
}
```

Upon completion, you should receive a different response which lists component identifiers visited during the import as well as any defects encountered in uploaded release files:

```json
{
  "id" : "107f6efa69886bfdd73db5586dcf0e15f738efed",
  "status" : "FINISHED",
  "response" : {
    "visitedComponents" : [ ... ],
    "defects" : [ ],
    "success" : true
  }
}
```
