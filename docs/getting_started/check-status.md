# Check Health

Let’s start with a basic health check, which we can use to see how our instance is doing. We’ll be using `curl` to do this but you can use any tool that allows you to make HTTP/REST calls. Let’s assume that we are still on the same node where we started Snow Owl on and open another command shell window.

We will be using Snow Owl's [Core API](../api/admin/index.md) to check its status. You can run the following command by clicking the "Copy" link on the right side and pasting it into a terminal.

```bash
curl http://localhost:8080/snowowl/info?pretty
```

And the response:

```json
{
  "version": "<version>",
  "description": "You Know, for Terminologies",
  "repositories": {
    "items": [ {
      "id" : "snomed",
      "health" : "GREEN",
      "diagnosis" : "",
      "indices" : [ {
        "index" : "snomed-relationship",
        "status" : "GREEN"
      }, {
        "index" : "snomed-commit",
        "status" : "GREEN"
      }, ...
    } ]
  }
}
```

We can see the installed version along with available repositories, their overall health (eg. `"snomed"` with health `"GREEN"`), associated indices and status (eg. `"snomed-relationship"` with status `"GREEN"`).

Repository indices store content for any number of code systems that share the same data structure and API, in the case of `"snomed"` the International Edition of SNOMED CT and its extensions.

Whenever we ask for repository status, we either get `GREEN`, `YELLOW`, or `RED` and an optional `diagnosis` message.

* `GREEN` - everything is good (repository is fully functional)
* `YELLOW` - some data or functionality is not available, or diagnostic operation is in progress (repository is partially functional)
* `RED` - diagnostic operation required in order to continue (repository is not functional)
