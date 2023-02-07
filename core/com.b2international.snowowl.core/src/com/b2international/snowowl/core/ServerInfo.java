/*
 * Copyright 2017-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core;

import java.io.Serializable;
import java.util.Objects;

import com.b2international.index.es.client.EsClusterStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.8
 */
public final class ServerInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String version;
	private final String description;
	private final Repositories repositories;
	private final EsClusterStatus cluster;
	
	@JsonCreator
	public ServerInfo(
		@JsonProperty("version") String version,
		@JsonProperty("description") String description,
		@JsonProperty("repositories") Repositories repositories,
		@JsonProperty("cluster") EsClusterStatus cluster
	) {
		this.version = version;
		this.description = description;
		this.repositories = repositories;
		this.cluster = cluster;
	}

	public String getVersion() {
		return version;
	}

	public String getDescription() {
		return description;
	}
	
	public Repositories getRepositories() {
		return repositories;
	}
	
	public EsClusterStatus getCluster() {
		return cluster;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cluster, description, repositories.getItems(), version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerInfo other = (ServerInfo) obj;
		return Objects.equals(cluster, other.cluster) && Objects.equals(description, other.description)
				&& Objects.equals(repositories.getItems(), other.repositories.getItems()) && Objects.equals(version, other.version);
	}
	
}
