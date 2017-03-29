/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.impl.info.domain;

import com.b2international.snowowl.api.info.domain.IRepositoryInfo;
import com.b2international.snowowl.core.Repository;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 5.8
 */
@JsonDeserialize(builder = RepositoryInfo.Builder.class)
public class RepositoryInfo implements IRepositoryInfo {

	public static Builder builder() {
		return new Builder();
	}

	private final String repositoryId;
	private final long headTimestampForDatabase;
	private final long headTimestampForIndex;
	private final Repository.Health health;
	
	private RepositoryInfo(final String repositoryId, final long headTimestampForDatabase,final long headTimestampForIndex, final Repository.Health health) {
		this.repositoryId = repositoryId;
		this.headTimestampForDatabase = headTimestampForDatabase;
		this.headTimestampForIndex = headTimestampForIndex;
		this.health = health;
		
	}
	
	@Override
	public String getRepositoryId() {
		return repositoryId;
	}

	@Override
	public long getHeadTimestampForDatabase() {
		return headTimestampForDatabase;
	}

	@Override
	public long getHeadTimestampForIndex() {
		return headTimestampForIndex;
	}

	@Override
	public Repository.Health getHealth() {
		return health;
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		
		private String repositoryId;
		private long headTimestampForDatabase;
		private long headTimestampForIndex;
		private Repository.Health state;
		
		public Builder repositoryId(String repositoryId){
			this.repositoryId = repositoryId;
			return this;
		}
		
		public Builder headTimestampForDatabase(long headTimestampForDatabase){
			this.headTimestampForDatabase = headTimestampForDatabase;
			return this;
		}
		
		public Builder headTimestampForIndex(long headTimestampForIndex){
			this.headTimestampForIndex = headTimestampForIndex;
			return this;
		}
		
		public Builder state(Repository.Health state) {
			this.state = state;
			return this;
		}
		
		public RepositoryInfo build() {
			return new RepositoryInfo(repositoryId, headTimestampForDatabase, headTimestampForIndex, state); 
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("RepositoryState [repositoryId=");
		stringBuilder.append(repositoryId);
		stringBuilder.append(", headTimestampForDatabase=");
		stringBuilder.append(headTimestampForDatabase);
		stringBuilder.append(", headTimestampForIndex=");
		stringBuilder.append(headTimestampForIndex);
		stringBuilder.append(", state=");
		stringBuilder.append(health);
		stringBuilder.append("]");
		return stringBuilder.toString();
	}
	

}
