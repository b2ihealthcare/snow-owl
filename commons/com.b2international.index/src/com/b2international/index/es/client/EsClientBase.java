/*
 * Copyright 2019-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest.Level;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsRequest;
import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
import org.elasticsearch.cluster.health.ClusterHealthStatus;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.index.IndexException;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;
import net.jodah.failsafe.function.CheckedSupplier;

/**
 * @since 7.2
 */
public abstract class EsClientBase implements EsClient {

	private static final String READ_ONLY_SETTING = "index.blocks.read_only_allow_delete";
	
	private final HttpHost host;
	private final Logger log;
	
	private final ExpiringMemoizingSupplier<String> clusterAvailable = memoizeWithExpiration(this::checkClusterAvailable, 5, TimeUnit.MINUTES);
	private final ExpiringMemoizingSupplier<ClusterHealthResponse> clusterHealth = memoizeWithExpiration(this::checkClusterHealth, 5, TimeUnit.MINUTES);
	private final ExpiringMemoizingSupplier<GetSettingsResponse> indicesSettings = memoizeWithExpiration(this::checkIndicesSettings, 5, TimeUnit.MINUTES);
	
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
		if (available) {
			final List<EsIndexStatus> indexStatuses = newArrayList();
			for (String index : indices == null || indices.length == 0 ? clusterHealth.get().getIndices().keySet() : Arrays.asList(indices)) {
				ClusterHealthStatus indexHealth = getIndexHealth(index);
				String diagnosis = null;
				// if not in red health state, check the read only flag
				if (indexHealth != ClusterHealthStatus.RED && isIndexReadOnly(index)) {
					indexHealth = ClusterHealthStatus.RED;
					diagnosis = String.format("Index is read-only. Check/Fix source of the error (eg. run out of disk space), then run `curl -XPUT \"%s/%s/_settings\" -d '{ \"index.blocks.read_only_allow_delete\": null }'` to remove read-only flag.", host.toURI(), index);
				}
				indexStatuses.add(new EsIndexStatus(index, indexHealth, diagnosis));
			}
			return new EsClusterStatus(available, clusterDiagnosis, indexStatuses);
		} else {
			return new EsClusterStatus(available, clusterDiagnosis, Collections.emptyList());
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
	
	private String checkClusterAvailable(String previousAvailability) {
		try {
			boolean available = waitFor(1 * 60L /*seconds*/, result -> !result /*keep waiting until ping returns true*/, () -> {
				log.trace("Pinging cluster at '{}'...", host.toURI());
				return ping();
			});
			if (!available) {
				log.warn("Cluster at '{}' is not available.", host.toURI());
				return String.format("Cluster at '%s' is not available.", host.toURI());
			} else {
				if (!Strings.isNullOrEmpty(previousAvailability)) {
					log.info("Cluster at '{}' became available.", host.toURI());
				}
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
	
	private ClusterHealthResponse checkClusterHealth(ClusterHealthResponse previousHealth) {
		try {
			log.info("Checking cluster health at '{}'...", host.toURI());
			ClusterHealthRequest req = new ClusterHealthRequest();
			req.level(Level.INDICES);
			return cluster().health(req);
		} catch (IOException e) {
			throw new IndexException("Failed to get cluster health", e);
		}
	}
	
	private GetSettingsResponse checkIndicesSettings(GetSettingsResponse previousSettings) {
		try {
			log.info("Checking indices settings at '{}'...", host.toURI());
			return indices().settings(new GetSettingsRequest().indices(MetaData.ALL));
		} catch (IOException e) {
			throw new IndexException("Failed to get indices settings", e);
		}
	}
	
	private ClusterHealthStatus getIndexHealth(String index) {
		return this.clusterHealth.waitUntilValue(result -> result.getIndices().containsKey(index), 1 * 60L /*seconds*/).getIndices().get(index).getStatus();
	}
	
	private boolean isIndexReadOnly(String index) {
		final String readOnly = this.indicesSettings.waitUntilValue(result -> result.getIndexToSettings().containsKey(index), 1 * 60L /*seconds*/).getSetting(index, READ_ONLY_SETTING);
		return !Strings.isNullOrEmpty(readOnly) && !Boolean.valueOf(readOnly);
	}
	
	/**
	 * Ping the Elasticsearch cluster.
	 * @return <code>true</code> if the cluster is available, up and running, <code>false</code> otherwise.
	 * @throws IOException 
	 */
	protected abstract boolean ping() throws IOException;

	private static final <T> T waitFor(long seconds, Predicate<T> handleIf, CheckedSupplier<T> onCheck) {
		final RetryPolicy<T> retryPolicy = new RetryPolicy<T>()
				.handleResultIf(handleIf)
				.withMaxAttempts(-1)
				.withMaxDuration(Duration.of(seconds, ChronoUnit.SECONDS))
				.withBackoff(1, Math.max(2, seconds / 3), ChronoUnit.SECONDS);
		return Failsafe.with(retryPolicy).get(onCheck);
	}
	
	private static <T> ExpiringMemoizingSupplier<T> memoizeWithExpiration(Function<T, T> delegate, long duration, TimeUnit unit) {
		return new ExpiringMemoizingSupplier<>(delegate, duration, unit);
	}
	
	private static final class ExpiringMemoizingSupplier<T> implements Supplier<T> {

		final Function<T, T> delegate;
		final long durationNanos;
		transient volatile T value;
		// The special value 0 means "not yet initialized".
		transient volatile long expirationNanos;

		ExpiringMemoizingSupplier(Function<T, T> delegate, long duration, TimeUnit unit) {
			this.delegate = Preconditions.checkNotNull(delegate);
			this.durationNanos = unit.toNanos(duration);
			Preconditions.checkArgument(duration > 0);
		}

		@Override
		public T get() {
			// Another variant of Double Checked Locking.
			//
			// We use two volatile reads. We could reduce this to one by
			// putting our fields into a holder class, but (at least on x86)
			// the extra memory consumption and indirection are more
			// expensive than the extra volatile reads.
			long nanos = expirationNanos;
			long now = System.nanoTime();
			if (nanos == 0 || now - nanos >= 0) {
				synchronized (this) {
					if (nanos == expirationNanos) { // recheck for lost race
						T t = delegate.apply(value);
						value = t;
						nanos = now + durationNanos;
						// In the very unlikely event that nanos is 0, set it to 1;
						// no one will notice 1 ns of tardiness.
						expirationNanos = (nanos == 0) ? 1 : nanos;
						return t;
					}
				}
			}
			return value;
		}
		
		public T waitUntilValue(Predicate<T> test, long seconds) {
			T currentValue = get();
			if (test.test(currentValue)) {
				return currentValue;
			} else {
				return waitFor(seconds, result -> !test.test(result), () -> {
					synchronized (this) {
						expirationNanos = 0L; // reset
						return get();
					}
				});
			}
		}
		
	}
	
}
