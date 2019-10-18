/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.index.es.client.EsClusterStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.8
 */
public interface RepositoryInfo {

	/**
	 * @return the ID of the repository
	 */
	@JsonProperty
	String id();
	
	/**
	 * @returns the {@link Health health} state for this repository. The repository is considered healthy if it is in {@link Health#GREEN} state.
	 */
	@JsonProperty
	Health health();
	
	/**
	 * @return the diagnosis message if {@link #health()} is not {@link Health#GREEN}. 
	 */
	@JsonProperty
	default String diagnosis() {
		return null;
	}
	
	@JsonProperty
	EsClusterStatus cluster();
	
	/**
	 * @since 5.8 
	 */
	enum Health {
		RED, YELLOW, GREEN;
	}
	
	/**
	 * @since 5.8
	 */
	class Default implements RepositoryInfo, Serializable {

		private static final long serialVersionUID = 1L;
		
		private final String id;
		private final Health health;
		private final String diagnosis;
		private final EsClusterStatus cluster;

		private Default(String id, Health health, String diagnosis, EsClusterStatus cluster) {
			this.id = id;
			this.health = health;
			this.diagnosis = diagnosis;
			this.cluster = cluster;
		}
		
		@Override
		public String id() {
			return id;
		}

		@Override
		public Health health() {
			return health;
		}
		
		@Override
		public String diagnosis() {
			return diagnosis;
		}
		
		@Override
		public EsClusterStatus cluster() {
			return cluster;
		}
		
	}
	
	static RepositoryInfo of(String id, Health health, String diagnosis, EsClusterStatus cluster) {
		return new Default(id, health, diagnosis, cluster);
	}
	
	static RepositoryInfo of(RepositoryInfo info) {
		return of(info.id(), info.health(), info.diagnosis(), info.cluster());
	}
	
}
