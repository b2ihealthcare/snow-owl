# Reference Sets API

Two categories make up Snow Owl's Reference Set API:

1.  **Reference Sets** category to get, search, create and modify reference sets
2.  **Reference Set Members** category to get, search, create and modify reference set members

Basic operations like create, update, delete are supported for both category.

## Actions API

On top of the basic operations, reference sets and members support actions. Actions have an _action_ property to specify which action to execute, the rest of the JSON properties will be used as body for the Action.

Supported reference set actions are:

1.  **sync** - synchronize all members of a query type reference set by executing their query and comparing the results with the current members of their referenced target reference set

Supported reference set member actions are:

1.  **create** - create a reference set member (uses the same body as POST /members)
2.  **update** - update a reference set member (uses the same body as PUT /members)
3.  **delete** - delete a reference set member
4.  **sync** - synchronize a single member by executing the query and comparing the results with the current members of the referenced target reference set

For example the following will sync a query type reference set member's referenced component with the result of the reevaluated member's ESCG query

    POST /members/:id/actions
    {
      "commitComment": "Sync member's target reference set",
      "action": "sync"
    }

## Bulk API

Members list of a single reference set can be modified by using the following bulk-like update endpoint:

    PUT /:path/refsets/:id/members

Input

    {
      "commitComment": "Updating members of my simple type reference set",
      "requests": [
      	{
      	  "action": "create|update|delete|sync",
      	  "action-specific-props": ...
      	}
      ]
    }

The request body should contain the commitComment property and a request array. The request array must contain actions (see Actions API) that are enabled for the given set of reference set members. Member create actions can omit the _referenceSetId_ parameter, those will use the one defined as path parameter in the URL. For example by using this endpoint you can create, update and delete members of a reference set at once in one single commit.