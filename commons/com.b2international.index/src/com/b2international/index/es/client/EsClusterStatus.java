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

import java.util.Map;

import org.elasticsearch.cluster.health.ClusterHealthStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 7.2
 */
public final class EsClusterStatus {

	private final boolean available;
	private final String diagnosis;
	private final Map<String, ClusterHealthStatus> healthByIndex;
	
	public EsClusterStatus(final boolean available, final String diagnosis, final Map<String, ClusterHealthStatus> healthByIndex) {
		this.available = available;
		this.diagnosis = diagnosis;
		this.healthByIndex = healthByIndex;
	}
	
	/**
	 * @return <code>true</code> if the Elasticsearch cluster is available, up and running, <code>false</code> otherwise.
	 */
	public boolean isAvailable() {
		return available;
	}
	
	/**
	 * @return <code>true</code> if all indices report back GREEN healthy state, <code>false</code> if at least one index reports back non-GREEN status.
	 */
	public boolean isHealthy() {
		return false;
	}
	
	/**
	 * @return a non-null diagnostic message reported by the underlying client in case of non-available ES Cluster, <code>null</code> if the cluster is available.  
	 */
	public String getDiagnosis() {
		return diagnosis;
	}
	
	/**
	 * @return all index health states
	 */
	@JsonProperty("indices")
	public Map<String, ClusterHealthStatus> getHealthByIndex() {
		return healthByIndex;
	}
	
	/**
	 * @param indices
	 * @return <code>true</code> if all of the given indices are healthy, <code>false</code> otherwise.
	 */
	public boolean isHealthy(String...indices) {
		for (String index : indices) {
			if (ClusterHealthStatus.GREEN != healthByIndex.get(index)) {
				return false;
			}
		}
		return true;
	}

}
