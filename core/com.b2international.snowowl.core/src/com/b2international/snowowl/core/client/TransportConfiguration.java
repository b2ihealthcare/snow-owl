/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.client;

import java.util.concurrent.TimeUnit;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Configuration parameters for repository connection.
 * 
 * @since 3.4
 */
public class TransportConfiguration {

	/*
	 * XXX (apeteri): 15 minutes for a single signal exchange might seem a bit too much, but this is what we had set for
	 * the CDO protocol, and our custom protocols also require a large enough number that is still smaller than the
	 * "infinite" value to give at least a small chance for things to recover.
	 */
	private static final long DEFAULT_SIGNAL_TIMEOUT_MILLIS = TimeUnit.MINUTES.toMillis(15L);
	private static final long DEFAULT_CONNECTION_TIMEOUT_MILLIS = TimeUnit.SECONDS.toMillis(10L);
	private static final long DEFAULT_WATCHDOG_RATE_MILLIS = TimeUnit.SECONDS.toMillis(30L);
	private static final long DEFAULT_WATCHDOG_TIMEOUT_MILLIS = TimeUnit.MINUTES.toMillis(5L);
	private static final int DEFAULT_RESOLVE_CHUNK_SIZE = 10_000;
	
	@Min(0)
	private long connectionTimeout = DEFAULT_CONNECTION_TIMEOUT_MILLIS;
	
	@Min(0)
	private long signalTimeout = DEFAULT_SIGNAL_TIMEOUT_MILLIS;
	
	@Min(0)
	private long watchdogRate = DEFAULT_WATCHDOG_RATE_MILLIS;
	
	@Min(0)
	private long watchdogTimeout = DEFAULT_WATCHDOG_TIMEOUT_MILLIS;
	
	@Min(0)
	@Max(20_000)
	private int resolveChunkSize = DEFAULT_RESOLVE_CHUNK_SIZE;
	
	/**
	 * @return the timeout in milliseconds, after which a connection, that seems to be working correctly, is
	 * determined to be broken.
	 */
	@JsonProperty
	public long getWatchdogTimeout() {
		return watchdogTimeout;
	}
	
	/**
	 * @param watchdogTimeout the watchdogTimeout to set
	 */
	@JsonProperty
	public void setWatchdogTimeout(long watchdogTimeout) {
		this.watchdogTimeout = watchdogTimeout;
	}
	
	/**
	 * @return the number of milliseconds to wait before a connection attempt times out at login.
	 */
	@JsonProperty
	public long getConnectionTimeout() {
		return connectionTimeout;
	}
	
	/**
	 * @param connectionTimeout the connectionTimeout to set
	 */
	@JsonProperty
	public void setConnectionTimeout(long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	
	/**
	 * @return the timeout in milliseconds, after which a serviced Net4j signal is decided to be unrecoverable and an
	 * exception is thrown.
	 */
	@JsonProperty
	public long getSignalTimeout() {
		return signalTimeout;
	}
	
	/**
	 * @param signalTimeout the signalTimeout to set
	 */
	@JsonProperty
	public void setSignalTimeout(long signalTimeout) {
		this.signalTimeout = signalTimeout;
	}
	
	/**
	 * @return the interval in milliseconds, at which messages should be exchanged to determine whether a connection
	 * is working correctly.
	 */
	@JsonProperty
	public long getWatchdogRate() {
		return watchdogRate;
	}
	
	/**
	 * @param watchdogRate the watchdogRate to set
	 */
	@JsonProperty
	public void setWatchdogRate(long watchdogRate) {
		this.watchdogRate = watchdogRate;
	}

	/**
	 * @param resolveChunkSize the resolveChunkSize to set
	 */
	@JsonProperty
	public void setResolveChunkSize(int resolveChunkSize) {
		this.resolveChunkSize = resolveChunkSize;
	}
	
	/**
	 * @return the number of CDOIDs to resolve in a CDOObject's list feature once it is being accessed.
	 */
	@JsonProperty
	public int getResolveChunkSize() {
		return resolveChunkSize;
	}
}
