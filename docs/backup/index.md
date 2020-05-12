# Backup and Restore

Snow Owl 7 uses a single data source, an Elasticsearch cluster (either embedded or external).
To backup and restore the data, we highly recommend the official [Snapshot and Restore](https://www.elastic.co/guide/en/elasticsearch/reference/7.5/snapshot-restore.html) feature from Elasticsearch.
On top of that API, we highly recommend using tools, like Curator to ease the lifecycle management of your Elasticsearch cluster and your indices. 
See Curator [here](https://www.elastic.co/guide/en/elasticsearch/client/curator/5.8/index.html).

{% hint style="info" %}
Reminder: for production environment we highly recommend using an external Elasticsearch cluster as opposed to the embedded one.
External Elasticsearch clusters are more customizable and can be configured to use other snapshot repository types, like Amazon S3, HDFS, etc.
{% endhint %}

Below you can find a very simple guide on how to configure the backup and restore process for your Snow Owl Terminology Server using Curator.