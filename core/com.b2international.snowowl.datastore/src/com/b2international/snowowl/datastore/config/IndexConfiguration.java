/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.index.IndexClientFactory;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.0
 */
public class IndexConfiguration {

	@NotEmpty
	private String commitInterval = IndexClientFactory.DEFAULT_TRANSLOG_SYNC_INTERVAL;
	@Min(1)
	private Integer numberOfShards = 6;
	@Min(1)
	private int commitConcurrencyLevel = Math.max(1, Runtime.getRuntime().availableProcessors() / 4);

	@NotEmpty
	private String clusterName = IndexClientFactory.DEFAULT_CLUSTER_NAME;
	// @Nullable
	private String clusterUrl;
	// @Nullable
	private String clusterUsername;
	// @Nullable
	private String clusterPassword;
	
	@Min(1_000)
	private int connectTimeout = IndexClientFactory.DEFAULT_CONNECT_TIMEOUT;
	@Min(1_000)
	private int socketTimeout = IndexClientFactory.DEFAULT_SOCKET_TIMEOUT;
	@Min(2_001)
	private int clusterHealthTimeout = IndexClientFactory.DEFAULT_CLUSTER_HEALTH_TIMEOUT;
	
	private int maxTermsCount = IndexClientFactory.DEFAULT_MAX_TERMS_COUNT;
	
	private int resultWindow = IndexClientFactory.DEFAULT_RESULT_WINDOW;

	@JsonProperty
	public String getCommitInterval() {
		return commitInterval;
	}

	@JsonProperty
	public void setCommitInterval(String commitInterval) {
		this.commitInterval = commitInterval;
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

	@JsonProperty
	public String getClusterName() {
		return clusterName;
	}
	
	@JsonProperty
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	
	@JsonProperty
	public String getClusterUrl() {
		return clusterUrl;
	}
	
	@JsonProperty
	public void setClusterUrl(String clusterUrl) {
		this.clusterUrl = clusterUrl;
	}
	
	@JsonProperty
	public int getConnectTimeout() {
		return connectTimeout;
	}
	
	@JsonProperty
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
	@JsonProperty
	public int getSocketTimeout() {
		return socketTimeout;
	}
	
	@JsonProperty
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
	
	@JsonProperty
	public String getClusterUsername() {
		return clusterUsername;
	}
	
	@JsonProperty
	public void setClusterUsername(String clusterUsername) {
		this.clusterUsername = clusterUsername;
	}
	
	@JsonProperty
	public String getClusterPassword() {
		return clusterPassword;
	}
	
	@JsonProperty
	public void setClusterPassword(String clusterPassword) {
		this.clusterPassword = clusterPassword;
	}
	
	@JsonProperty
	public int getClusterHealthTimeout() {
		return clusterHealthTimeout;
	}
	
	@JsonProperty
	public void setClusterHealthTimeout(int clusterHealthTimeout) {
		this.clusterHealthTimeout = clusterHealthTimeout;
	}
	
	public int getMaxTermsCount() {
		return maxTermsCount;
	}
	
	public int getResultWindow() {
		return resultWindow;
	}
	
	public void setMaxTermsCount(int maxTermsCount) {
		this.maxTermsCount = maxTermsCount;
	}
	
	public void setResultWindow(int resultWindow) {
		this.resultWindow = resultWindow;
	}
	
}
