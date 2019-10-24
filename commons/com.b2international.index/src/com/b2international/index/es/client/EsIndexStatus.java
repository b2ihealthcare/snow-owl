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

import java.util.Comparator;

import org.elasticsearch.cluster.health.ClusterHealthStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 7.2
 */
public final class EsIndexStatus implements Comparable<EsIndexStatus> {

	// first by status then by name
	private static final Comparator<EsIndexStatus> COMPARATOR = Comparator.comparing(EsIndexStatus::getStatus).thenComparing(Comparator.comparing(EsIndexStatus::getIndex));
	
	private final String index;
	private final ClusterHealthStatus status;
	private final String diagnosis;

	public EsIndexStatus(String index, ClusterHealthStatus status, String diagnosis) {
		this.index = index;
		this.status = status;
		this.diagnosis = diagnosis;
	}
	
	public String getIndex() {
		return index;
	}
	
	public ClusterHealthStatus getStatus() {
		return status;
	}
	
	public String getDiagnosis() {
		return diagnosis;
	}

	@JsonIgnore
	public boolean isHealthy() {
		return ClusterHealthStatus.GREEN == status;
	}
	
	@Override
	public int compareTo(EsIndexStatus other) {
		return COMPARATOR.compare(this, other);
	}
	
	@Override
	public String toString() {
		return String.format("%s[status: '%s', diagnosis: '%s']", index, status, diagnosis);
	}

}
