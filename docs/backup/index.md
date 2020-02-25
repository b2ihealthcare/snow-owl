# Backup and Restore

Snow Owl 7 uses a single data source, an Elasticsearch cluster (either embedded or external).
To backup and restore the data, we highly recommend the official [Snapshot and Restore](https://www.elastic.co/guide/en/elasticsearch/reference/7.5/snapshot-restore.html) feature from Elasticsearch.
Additionally, you can experiment with any Elasticsearch compatible index management software, like [Curator](https://www.elastic.co/guide/en/elasticsearch/client/curator/5.8/index.html) for example.

## Recommended snapshot configuration

We highly recommend using two remotely accessible repositories (Amazon S3, network attached storage, etc.) to back up and restore data to/from.
The back up interval depends on your use case and how you are accessing the data. If you have write-heavy scenarios, we recommend a hourly backup interval, otherwise some value between hourly - daily is preferrable.