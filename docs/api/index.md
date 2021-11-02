# Snow Owl API

This describes the resources that make up the official Snow Owl® RESTful API.

## Media Types

Custom media types are used in the API to let consumers choose the format of the data they wish to receive. This is done by adding one of the following types to the Accept header when you make a request. Media types are specific to resources, allowing them to change independently and support formats that other resources don’t.

The most basic media types the API supports are:

1.  application/json;charset=UTF-8 (default)
2.  text/plain;charset=UTF-8
3.  text/csv;charset=UTF-8
4.  application/octet-stream (for file downloads)
5.  multipart/form-data (for file uploads)

The generic JSON media type (application/json) is available as well, but we encourage you to explicitly set the accepted content type before sending your request.

## Schema

All data is sent and received as JSON. Blank fields are omitted instead of being included as `null`.

All non-effective time timestamps are returned in ISO 8601 format:

    YYYY-MM-DDTHH:MM:SSZ

Effective Time values are sent and received in short format:

    yyyyMMdd

## Hypermedia

All POST requests return `Location` headers pointing to the created resource instead of including either the identifier or the entire created resource in the response body. These are meant to provide explicit URLs so that proper API clients don’t need to construct URLs on their own. It is highly recommended that API clients use these. Doing so will make future upgrades of the API easier for developers. All URLs are expected to be proper `RFC 6570 URI` templates.

Example Location Header:

    http://example.com/snowowl/snomedct/MAIN/concepts/123456789

## Pagination

Requests that return multiple items will be paginated to `50` items by default. You can request further pages with the `searchAfter` query parameter.

## Link/Resource expansion

Where applicable, the `expand` query parameter will include nested objects in the response, to avoid having to issue multiple requests to the server.

Expanded properties should be followed by parentheses and separated by commas; any options for the expanded property should be given within the parentheses, including properties to expand. Typical values for parameters are given in the "Implementation Notes" section of each endpoint.

    GET /snowowl/snomedct/MAIN/concepts?offset=0&limit=50&expand=fsn(),descriptions()

Response:

    {
      "items": [
        {
          "id": "100005",
          "released": true,
          ...
          "fsn": {
            "id": "2709997016",
            "term": "SNOMED RT Concept (special concept)",
            ...
          },
          "descriptions": {
            "items": [
              {
                "id": "208187016",
                "released": true,
                ...
              },
            ],
            "offset": 0,
            "limit": 5,
            "total": 5
          }
        },
        ...
      ],
      "offset": 0,
      "limit": 50,
      "total": 421657
    }

## Client Errors

There are three possible types of client errors on API calls that receive request bodies:

### Invalid JSON

    Status: 400 Bad Request
    {
      "status" : "400",
      "message" : "Invalid JSON representation",
      "developerMessage" : "detailed information about the error for developers"
    }

### Valid JSON but invalid representation

    Status: 400 Bad Request 
    {
      "status" : "400",
      "message" : "2 Validation errors",
      "developerMessage" : "Input representation syntax or validation errors. Check input values.",
      "violations" : ["violation_message_1", "violation_message_2"]
    }

### Conflicts

    Status: 409 Conflict 
    {
      "status" : "409",
      "message" : "Cannot merge source 'branch1' into target 'MAIN'."
    }

## Server Errors

In certain circumstances, Snow Owl might fail to process and respond to a request and responds with a `500 Internal Server Error`.

    Status: 500 Internal Server Error 
    {
      "status" : "500",
      "message" : "Something went wrong during the processing of your request.",
      "developerMessage" : "detailed information about the error for developers"
    }

To troubleshoot these please examine the log files at `{SERVER_HOME}/serviceability/logs/log.log` and/or [raise an issue on GitHub](https://github.com/b2ihealthcare/snow-owl/issues/new).

## Path expressions

Snow Owl is a revision-based terminology server, where each stored terminology data (concepts, descriptions, etc.) is stored in multiple revisions, across multiple branches. When requesting content from the terminology server, clients are able to specify a path value or expression to select the content they'd like to access and receive. For example, Snow Owl supports importing SNOMED CT content from different sources, allowing eg. multiple national **Extensions** to co-exist with the base **International Edition** provided by SNOMED International. Versioned editions can be consulted when non-current representations of concepts need to be accessed. Concept authoring and review can also be done in isolation. Both Java and REST API endpoints require a `path` parameter to select the content (or **substrate**) the user wishes to work with.

The following formats are accepted:

### Absolute branch path

Absolute branch path parameters start with `MAIN` and point to a branch in the backing terminology repository. In the following example, all concepts are considered to be part of the substrate that are on branch `MAIN/2021-01-31/SNOMEDCT-UK-CL` or any ancestor (ie. `MAIN` or `MAIN/2021-01-31`), unless they have been modified:

```json
GET /snomedct/MAIN/2021-01-31/SNOMEDCT-UK-CL/concepts
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
GET /snomedct/SNOMEDCT-UK-CL/100/concepts
```

An alternative request that uses an absolute path would be the following:

```json
GET /snomedct/MAIN/2021-01-31/SNOMEDCT-UK-CL/100/concepts
```

An important difference is that the relative `path` parameter tracks the working branch specified in the code system's settings, so requests using relative paths do not need to be adjusted when a code system is upgraded to a more recent International Edition.

### Path range

The substrate represented by a path range consists of concepts that were created or modified between a starting and ending point, each identified by an absolute branch path (relative paths are not supported). The format of a path range is `fromPath...toPath`.

To retrieve concepts authored or edited following version 2020-08-05 of code system SNOMEDCT-UK-CL, the following path expression should be used:

```json
GET /snomedct/MAIN/2019-07-31/SNOMEDCT-UK-CL/2020-08-05...MAIN/2021-01-31/SNOMEDCT-UK-CL/concepts
```

The result set includes the ones appearing or changing between versions 2019-07-31 and 2021-01-31 of the International Edition; if this is not desired, additional constraints can be added to exclude them.

### Path with timestamp

To refer to a branch state at a specific point in time, use the `path@timestamp` format. The timestamp is an integer value expressing the number of milliseconds since the UNIX epoch, 1970-01-01 00:00:00 UTC, and corresponds to "wall clock" time, not component effective time. As an example, if the SNOMED CT International version 2021-07-31 is imported on 2021-09-01 13:50:00 UTC, the following request to retrieve concepts will not include any new or changed concepts appearing in this release:

```json
GET /snomedct/MAIN@1630504199999/concepts
```

Both absolute and relative paths are supported in the `path` part of the expression.

### Branch base point

Concept requests using a branch base point reflect the state of the branch at its beginning, before any changes on it were made. The format of a base path is `path^` (only absolute paths are supported):

```json
GET /snomedct/MAIN/2019-07-31/SNOMEDCT-UK-CL/101^/concepts
```

Returned concepts include all additions and modifications made on SNOMEDCT-UK-CL's working branch, up to point where task #101 starts; neither changes committed to the working branch after task #101, nor changes on task #101 itself are reflected in the result set.