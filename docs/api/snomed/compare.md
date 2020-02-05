## Compare API

Comparison for current terminology changes committed to a source or target branch can be conducted by creating a compare resource.

A review identifier can be added to merge requests as an optional property. If the source or target branch state is different from the values captured when creating the review, the merge/rebase attempt will be rejected. This can happen, for example, when additional commits are added to the source or the target branch while a review is in progress; the review resource state becomes STALE in such cases.

Reviews and concept change sets have a limited lifetime. CURRENT reviews are kept for 15 minutes, while review objects in any other states are valid for 5 minutes by default. The values can be changed in the server's configuration file.

## Compare two branches

    POST /compare 
    {
      "baseBranch": "MAIN",
      "compareBranch": "MAIN/a",
      "limit": 100
    }

Response

    Status: 200 OK
    {
      "baseBranch": "MAIN",
      "compareBranch": "MAIN/a",
      "compareHeadTimestamp": 1567282434400,
      "newComponents": [],
      "changedComponents": ["138875005"],
      "deletedComponents": [],
      "totalNew": 0,
      "totalChanged": 1,
      "totalDeleted": 0
    }

## Read component state from comparison

Terminology components (and in fact any content) can be read from any point in time by using the special path expression: `{branch}@{timestamp}`. 
To get the state of a SNOMED CT Concept from the previous comparison on the `compareBranch` at the returned `compareHeadTimestamp`, you can use the following request:

Request

    GET /snomed-ct/v3/MAIN@1567282434400/concepts/138875005

Response

    Status: 200 OK
    {
      "id": "138875005",
      ...
    }

To get the state of the same SNOMED CT Concept but on the base branch, you can use the following request:

Request

    GET /snomed-ct/v3/MAIN/concepts/138875005

Response

    Status: 200 OK
    {
      "id": "138875005",
      ...
    }

Additionally, if required to compute what's changed on the component since the creation of the task, it is possible to get back the base version of the changed component by using another special path expression: `{branch}^`.

Request

    GET /snomed-ct/v3/MAIN/a^/concepts/138875005

Response

    Status: 200 OK
    {
      "id": "138875005",
      ...
    }

{% hint style="warn" %}
These characters are not URL safe characters, thus they must be encoded before sending the HTTP request.
{% endhint %}