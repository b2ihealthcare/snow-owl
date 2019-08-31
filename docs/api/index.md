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

    http://example.com/snowowl/snomed-ct/v3/MAIN/concepts/123456789

## Pagination

Requests that return multiple items will be paginated to `50` items by default. You can request further pages with the `searchAfter` query parameter.

## Link/Resource expansion

Where applicable, the `expand` query parameter will include nested objects in the response, to avoid having to issue multiple requests to the server.

Expanded properties should be followed by parentheses and separated by commas; any options for the expanded property should be given within the parentheses, including properties to expand. Typical values for parameters are given in the "Implementation Notes" section of each endpoint.

    GET /snowowl/snomed-ct/v3/MAIN/concepts?offset=0&limit=50&expand=fsn(),descriptions()

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