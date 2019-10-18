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

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.core.util.Throwables;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.BadRequestException;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.function.CheckedSupplier;

/**
 * @since 7.2
 */
public abstract class EsClientBase implements EsClient {

	private final HttpHost host;
	private final Logger log;
	private final Supplier<String> clusterAvailable = Suppliers.memoizeWithExpiration(this::checkClusterAvailable, 5, TimeUnit.MINUTES);
	private final LoadingCache<String, EsIndexStatus> healthByIndex = CacheBuilder.newBuilder()
			.expireAfterWrite(5, TimeUnit.MINUTES)
			.build(new CacheLoader<String, EsIndexStatus>() {
				@Override
				public EsIndexStatus load(String index) throws Exception {
					try {
						return waitFor(1 * 60L /*seconds*/, result -> !result.isHealthy(), () -> {
							log.trace("Retrieving '{}' index health from '{}'...", index, host.toURI());
							return new EsIndexStatus(index, cluster().health(new ClusterHealthRequest(index)).getIndices().get(index).getStatus(), "");
						});
					} catch (Exception e) {
						final Throwable cause = Throwables.getRootCause(e);
						return new EsIndexStatus(index, ClusterHealthStatus.RED, String.format("Health check for index '%s' reported an error: %s", index, cause.getMessage())); 
					}
				}
			});
	
	public EsClientBase(String clusterUrl) {
		this.host = HttpHost.create(clusterUrl);
		this.log = LoggerFactory.getLogger(getClass());
	}
	
	protected final HttpHost host() {
		return host;
	}
	
	@Override
	public final EsClusterStatus status(String...indices) {
		final String clusterDiagnosis = clusterAvailable.get();
		final boolean available = Strings.isNullOrEmpty(clusterDiagnosis);
		if (available && indices != null && indices.length > 0) {
			final Map<String, EsIndexStatus> healthByIndex = newHashMap();
			for (String index : indices) {
				healthByIndex.put(index, this.healthByIndex.getUnchecked(index));
			}
			return new EsClusterStatus(available, clusterDiagnosis, healthByIndex);
		} else {
			return new EsClusterStatus(available, clusterDiagnosis, Collections.emptyMap());
		}
	}
	
	public final void checkAvailable() {
		if (!Strings.isNullOrEmpty(clusterAvailable.get())) {
			throw new BadRequestException("Cluster at '%s' is not available.", host.toURI());
		}
	}
	
	public final void checkHealthy(String...indices) {
		checkAvailable();
		if (!status(indices).isHealthy()) {
			throw new BadRequestException("Indices '%s' are not healthy at '%s'.", Arrays.toString(indices), host.toURI());
		}
	}
	
	private final <T> T waitFor(long seconds, Predicate<T> handleIf, CheckedSupplier<T> onCheck) {
		final RetryPolicy<T> retryPolicy = new RetryPolicy<T>()
				.handleResultIf(handleIf)
				.withMaxAttempts(-1)
				.withMaxDuration(Duration.of(seconds, ChronoUnit.SECONDS))
				.withBackoff(1, Math.max(2, seconds / 3), ChronoUnit.SECONDS);
		return Failsafe.with(retryPolicy).get(onCheck);
	}
	
	private String checkClusterAvailable() {
		try {
			boolean available = waitFor(1 * 60L /*seconds*/, result -> !result /*keep waiting until ping returns true*/, () -> {
				log.info("Pinging cluster at '{}'...", host.toURI());
				return ping();
			});
			if (!available) {
				return String.format("The cluster at '%s' is not available.", host.toURI());
			} else {
				return "";
			}
		} catch (Exception e) {
			if (e instanceof ElasticsearchStatusException && ((ElasticsearchStatusException) e).status() == RestStatus.UNAUTHORIZED) {
				return String.format("Unable to authenticate with cluster '%s' using the given credentials", host.toURI());
			} else {
				return String.format("The cluster at '%s' reported an error: '%s'", host.toURI(), e.getMessage());
			}
		}
	}
	
	/**
	 * Ping the Elasticsearch cluster.
	 * @return <code>true</code> if the cluster is available, up and running, <code>false</code> otherwise.
	 * @throws IOException 
	 */
	protected abstract boolean ping() throws IOException;
	
}
