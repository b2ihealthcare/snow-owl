/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.config;

import javax.validation.constraints.Min;

import com.b2international.index.IndexClientFactory;
import com.b2international.index.query.slowlog.SlowLogConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.0
 */
public class IndexConfiguration {

	@Min(1000)
	private long commitInterval = IndexClientFactory.DEFAULT_COMMIT_INTERVAL;
	@Min(1000)
	private long translogSyncInterval = IndexClientFactory.DEFAULT_TRANSLOG_SYNC_INTERVAL;
	@Min(10)
	private long queryWarnThreshold = SlowLogConfig.QUERY_WARN_THRESHOLD_DEFAULT;
	@Min(10)
	private long queryInfoThreshold = SlowLogConfig.QUERY_INFO_THRESHOLD_DEFAULT;
	@Min(10)
	private long queryDebugThreshold = SlowLogConfig.QUERY_DEBUG_THRESHOLD_DEFAULT;
	@Min(10)
	private long queryTraceThreshold = SlowLogConfig.QUERY_TRACE_THRESHOLD_DEFAULT;
	@Min(10)
	private long fetchWarnThreshold = SlowLogConfig.FETCH_WARN_THRESHOLD_DEFAULT;
	@Min(10)
	private long fetchInfoThreshold = SlowLogConfig.FETCH_INFO_THRESHOLD_DEFAULT;
	@Min(10)
	private long fetchDebugThreshold = SlowLogConfig.FETCH_DEBUG_THRESHOLD_DEFAULT;
	@Min(10)
	private long fetchTraceThreshold = SlowLogConfig.FETCH_TRACE_THRESHOLD_DEFAULT;
	
	@Min(1)
	private Integer numberOfShards = 3;
	
	@Min(1)
	private int commitConcurrencyLevel = Math.max(1, Runtime.getRuntime().availableProcessors() / 4);

	@JsonProperty
	public long getCommitInterval() {
		return commitInterval;
	}

	@JsonProperty
	public void setCommitInterval(long commitInterval) {
		this.commitInterval = commitInterval;
	}

	@JsonProperty
	public long getTranslogSyncInterval() {
		return translogSyncInterval;
	}

	@JsonProperty
	public void setTranslogSyncInterval(long translogSyncInterval) {
		this.translogSyncInterval = translogSyncInterval;
	}

	@JsonProperty
	public long getQueryWarnThreshold() {
		return queryWarnThreshold;
	}

	@JsonProperty
	public void setQueryWarnThreshold(long queryWarnThreshold) {
		this.queryWarnThreshold = queryWarnThreshold;
	}

	@JsonProperty
	public long getQueryInfoThreshold() {
		return queryInfoThreshold;
	}

	@JsonProperty
	public void setQueryInfoThreshold(long queryInfoThreshold) {
		this.queryInfoThreshold = queryInfoThreshold;
	}

	@JsonProperty
	public long getQueryDebugThreshold() {
		return queryDebugThreshold;
	}

	@JsonProperty
	public void setQueryDebugThreshold(long queryDebugThreshold) {
		this.queryDebugThreshold = queryDebugThreshold;
	}

	@JsonProperty
	public long getQueryTraceThreshold() {
		return queryTraceThreshold;
	}

	@JsonProperty
	public void setQueryTraceThreshold(long queryTraceThreshold) {
		this.queryTraceThreshold = queryTraceThreshold;
	}

	@JsonProperty
	public long getFetchWarnThreshold() {
		return fetchWarnThreshold;
	}

	@JsonProperty
	public void setFetchWarnThreshold(long fetchWarnThreshold) {
		this.fetchWarnThreshold = fetchWarnThreshold;
	}

	@JsonProperty
	public long getFetchInfoThreshold() {
		return fetchInfoThreshold;
	}

	@JsonProperty
	public void setFetchInfoThreshold(long fetchInfoThreshold) {
		this.fetchInfoThreshold = fetchInfoThreshold;
	}

	@JsonProperty
	public long getFetchDebugThreshold() {
		return fetchDebugThreshold;
	}

	@JsonProperty
	public void setFetchDebugThreshold(long fetchDebugThreshold) {
		this.fetchDebugThreshold = fetchDebugThreshold;
	}

	@JsonProperty
	public long getFetchTraceThreshold() {
		return fetchTraceThreshold;
	}

	@JsonProperty
	public void setFetchTraceThreshold(long fetchTraceThreshold) {
		this.fetchTraceThreshold = fetchTraceThreshold;
	}

	@JsonProperty
	public void setNumberOfShards(Integer numberOfShards) {
		this.numberOfShards = numberOfShards;
	}
	
	@JsonProperty
	public Integer getNumberOfShards() {
		return numberOfShards;
	}
	
	@JsonProperty
	public int getCommitConcurrencyLevel() {
		return commitConcurrencyLevel;
	}
	
	@JsonProperty
	public void setCommitConcurrencyLevel(int commitConcurrencyLevel) {
		this.commitConcurrencyLevel = commitConcurrencyLevel;
	}

}
