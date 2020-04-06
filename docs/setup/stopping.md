# Stopping Snow Owl

An orderly shutdown of Snow Owl ensures that Snow Owl has a chance to cleanup and close outstanding resources. For example, an instance that is shutdown in an orderly fashion will initiate an orderly shutdown of the embedded Elasticsearch instance, gracefully close and disconnect connections and perform other related cleanup activities. You can help ensure an orderly shutdown by properly stopping Snow Owl.

If you’re running Snow Owl as a service, you can stop Snow Owl via the service management functionality provided by your installation.

If you’re running Snow Owl directly, you can stop Snow Owl by sending `Ctrl-C` if you’re running Snow Owl in the console, or by invoking the provided `shutdown` script as follows:

```
$ ./bin/shutdown
```