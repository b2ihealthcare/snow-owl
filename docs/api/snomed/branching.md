# Branching API

Snow Owl provides branching support for terminology repositories. In each repository there is an always existing and `UP_TO_DATE` branch called **MAIN**. The `MAIN` branch represents the latest working version of your terminology (similar to a `master` branch on GitHub).

You can create your own branches and create/edit/delete components and other resources on them. Branches are identified with their full path, which should always start with `MAIN`. For example the branch `MAIN/a/b/c/d` represents a branch under the parent `MAIN/a/b/c` with name `d`.

Later you can decide to either delete the branch or merge the branch back to its parent. To properly merge a branch back into its parent, sometimes it is required to rebase (synchronize) it first with its parent to get the latest changes. This can be decided via the state attribute of the branch, which represents the current state compared to its parent state.

## Branch states

There are five different branch states available:

1.  UP_TO_DATE - the branch is up-to-date with its parent there are no changes neither on the branch or on its parent
2.  FORWARD - the branch has at least one commit while the parent is still unchanged. Merging a branch requires this state, otherwise it will return a HTTP 409 Conflict.
3.  BEHIND - the parent of the branch has at least one commit while the branch is still unchanged. The branch can be safely rebased with its parent.
4.  DIVERGED - both parent and branch have at least one commit. The branch must be rebased first before it can be safely merged back to its parent.
5.  STALE - the branch is no longer in relation with its former parent, and should be deleted.

{% hint style="info" %}
Snow Owl supports merging of unrelated (STALE) branches. So branch `MAIN/a` can be merged into `MAIN/b`, there does not have to be a direct parent-child relationship between the two branches.
{% endhint %}

## Basics

### Get a branch

    GET /branches/:path

Response

    Status: 200 OK
    {
      "name": "MAIN",
      "baseTimestamp": 1431957421204,
      "headTimestamp": 1431957421204,
      "deleted": false,
      "path": "MAIN",
      "state": "UP_TO_DATE"
    }

### Get all branches

    GET /branches

Response

    Status: 200 OK
    {
      "items": [
        {
          "name": "MAIN",
          "baseTimestamp": 1431957421204,
          "headTimestamp": 1431957421204,
          "deleted": false,
          "path": "MAIN",
          "state": "UP_TO_DATE"
        }
      ]
    }

### Create a branch

    POST /branches

Input

    {
      "parent" : "MAIN",
      "name" : "branchName",
      "metadata": {}
    }

Response

    Status: 201 Created
    Location: http://localhost:8080/snowowl/snomedct/branches/MAIN/branchName

### Delete a branch

    DELETE /branches/:path

Response

    Status: 204 No content

## Merging

### Perform a merge

    POST /merges

Input

    {
      "source" : "MAIN/branchName",
      "target" : "MAIN"
    }

Response

    Status: 202 Accepted
    Location: http://localhost:8080/snowowl/snomedct/merges/2f4d3b5b-3020-4e8e-b046-b8266967d7dc

### Perform a rebase

    POST /merges

Input

    {
      "source" : "MAIN",
      "target" : "MAIN/branchName"
    }

Response

    Status: 202 Accepted
    Location: http://localhost:8080/snowowl/snomedct/merges/c82c443d-f3f4-4409-9cdb-a744da336936

### Monitor progress of a merge or rebase

    GET /merges/c82c443d-f3f4-4409-9cdb-a744da336936

Response

    {
      "id": "c82c443d-f3f4-4409-9cdb-a744da336936",
      "source": "MAIN",
      "target": "MAIN/branchName",
      "status": "COMPLETED",
      "scheduledDate": "2016-02-29T13:52:45Z",
      "startDate": "2016-02-29T13:52:45Z",
      "endDate": "2016-02-29T13:53:06Z"
    }

### Remove merge or rebase queue item

    DELETE /merges/c82c443d-f3f4-4409-9cdb-a744da336936

Response

    Status: 204 No content
