# Elasticsearch configuration

By default, Snow Owl is starting and connecting to an embedded `Elasticsearch` cluster available on `http://localhost:9200`. This cluster has only a single node and its discovery method is set to `single-node`, which means it is not able to connect to other Elasticsearch clusters and will be used exclusively by Snow Owl.

This single node Elasticsearch cluster can easily serve Snow Owl in testing, evaluation and small authoring environments, but it is recommended to customize how Snow Owl connects to an Elasticsearch cluster in larger environments (especially when planning to scale with user demand).

You have two options to configure Elasticsearch used by Snow Owl.

## Configure the embedded instance

The first option is to configure the underlying Elasticsearch instance by editing the configuration file `elasticsearch.yml`, which depending on your installation is available in the configuration directory (you can create the file, if it is not available, Snow Owl will pick it up during the next startup).

{% hint style="info" %}
The embedded Elasticsearch version is `6.3.2`. If you are configuring it to connect to an existing Elasticsearch cluster, then make sure that the cluster version matches with this version.
{% endhint %}

## Connect to a remote cluster

The second option is to configure Snow Owl to use a remote Elasticsearch cluster without the embedded instance. In order to use this feature you need to set the `repository.index.clusterUrl` configuration parameter to the remote address of your Elasticsearch cluster. When Snow Owl is configured to connect to a remote Elasticsearch cluster, it won't boot up the embedded instance, which reduces the memory requirements of Snow Owl slightly. 

You can connect to self-hosted clusters or hosted solutions provided by [AWS](https://aws.amazon.com/elasticsearch-service/) and [Elastic.co](https://www.elastic.co/cloud/elasticsearch-service) for example.
