# Server Status and Version

Let’s start with a basic health check, which we can use to see how our instance is doing. We’ll be using `curl` to do this but you can use any tool that allows you to make HTTP/REST calls. Let’s assume that we are still on the same node where we started Snow Owl on and open another command shell window.

To check the instance status/health, we will be using the [Admin API](../api/admin.md). You can run the command by clicking the "Copy" link on the right side and pasting it into a terminal.

```
curl http://localhost:8080/snowowl/admin/info
```

And the response:

```
{
  "version": "<version>",
  "description": "You Know, for Terminologies",
  "repositories": {
    "items": [
      {
        "id": "snomedStore",
        "health": "GREEN"
      }
    ]
  }
}
```

In the response, we can see the version of our instance along with the available repositories and their health status (eg. `SNOMED CT` with status `GREEN`).

Whenever we ask for the status, we either get `GREEN`, `YELLOW`, or `RED` and an optional `diagnosis` message.

* Green - everything is good (repository is fully functional)
* Yellow - some data or functionality is not available, or diagnostic operation is in progress (repository is partially functional)
* Red - diagnostic operation required in order to continue (repository is not functional)