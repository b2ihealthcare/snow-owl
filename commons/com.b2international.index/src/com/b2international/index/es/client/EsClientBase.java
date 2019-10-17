/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.index.es.client;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.rest.RestStatus;

import com.b2international.commons.exceptions.BadRequestException;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;

/**
 * @since 7.2
 */
public abstract class EsClientBase implements EsClient {

	private final HttpHost host;
	private final Supplier<EsClusterStatus> status = Suppliers.memoizeWithExpiration(this::checkStatus, 5, TimeUnit.MINUTES);
	
	public EsClientBase(String clusterUrl) {
		this.host = HttpHost.create(clusterUrl);
	}
	
	protected final HttpHost host() {
		return host;
	}
	
	@Override
	public final EsClusterStatus status() {
		return status.get();
	}
	
	public final void checkAvailable() {
		if (!status().isAvailable()) {
			throw new BadRequestException("Cluster at '%s' is not available.", host.toURI());
		}
	}
	
	public final void checkHealthy(String...indices) {
		checkAvailable();
		final boolean checkAll = indices == null || indices.length == 0;
		EsClusterStatus latestStatus = status();
		boolean healthy = checkAll ? latestStatus.isHealthy() : latestStatus.isHealthy(indices);
		// check status again, if an index is not present in the list of indices in the cluster status
		if (!healthy) {
			Set<String> indicesSet = Sets.newHashSet(indices);
			// if not all
			if (!latestStatus.getHealthByIndex().keySet().containsAll(indicesSet)) {
				latestStatus = checkStatus();
				healthy = checkAll ? latestStatus.isHealthy() : latestStatus.isHealthy(indices);
			}
		}
		
		if (!healthy) {
			throw new BadRequestException("Indices '%s' are not healthy.", checkAll ? "*" : Arrays.toString(indices), host.toURI());
		}
	}
	
	private EsClusterStatus checkStatus() {
		// first ping the server
		boolean available = false;
		String diagnosis = "";
		Map<String, ClusterHealthStatus> healthByIndex = Collections.emptyMap();
		try {
			available = ping();
			if (!available) {
				diagnosis = String.format("The cluster at '%s' is not available.", host.toURI());
			}
		} catch (Exception e) {
			if (e instanceof ElasticsearchStatusException && ((ElasticsearchStatusException) e).status() == RestStatus.UNAUTHORIZED) {
				diagnosis = String.format("Unable to authenticate with cluster '%s' using the given credentials", host.toURI());
			} else {
				diagnosis = String.format("The cluster at '%s' reported an error: '%s'", host.toURI(), e.getMessage());
			}
		}
		
		// then check index health states
		if (available) {
			try {
				healthByIndex = cluster().health(new ClusterHealthRequest()).getIndices().entrySet()
						.stream()
						.collect(Collectors.toMap(index -> index.getKey(), status -> status.getValue().getStatus()));
			} catch (IOException e) {
				diagnosis = String.format("The cluster at '%s' reported an error: '%s'", host.toURI(), e.getMessage());
			}
		}
		
		return new EsClusterStatus(available, diagnosis, healthByIndex);
	}

	/**
	 * Ping the Elasticsearch cluster.
	 * @return <code>true</code> if the cluster is available, up and running, <code>false</code> otherwise.
	 * @throws IOException 
	 */
	protected abstract boolean ping() throws IOException;
	
}
