# SNOMED CT REST API

This describes the resources that make up the official Snow Owl® SNOMED CT Terminology API.

{% hint style="info" %}
Swagger documentation available on your Snow Owl instance at [/snowowl/snomed-ct/v3](http://localhost:8080/snowowl/snomed-ct/v3).
{% endhint %}

## Current Version

SNOMED CT API endpoints currently have version **v3**. You have to explicitly set the version of the API via path parameter. For example:

    GET /snomed-ct/v3/branches

## Available resources

* [Branching](./branching.md)
* [Compare](./compare.md)
* [Commits](./commits.md)
* [Concepts](./concepts.md)
* [Descriptions](./descriptions.md)
* [Relationships](./relationships.md)
* [RefSets](./refsets.md)
* [Classification](./classifications.md)
* [Import](./imports.md)
* [Export](./emports.md)