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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.snomed.core.domain.IdGenerationStrategy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.0
 */
public abstract class BaseSnomedComponentCreateRequest implements SnomedCoreComponentCreateRequest {

	@Nonnull
	private Boolean active = Boolean.TRUE;
	
	@NotNull
	private IdGenerationStrategy idGenerationStrategy;
	
	private String moduleId;

	@Override
	public IdGenerationStrategy getIdGenerationStrategy() {
		return idGenerationStrategy;
	}

	@Override
	public String getModuleId() {
		return moduleId;
	}
	
	@Override
	public Boolean isActive() {
		return active;
	}

	@JsonIgnore
	final void setIdGenerationStrategy(final IdGenerationStrategy idGenerationStrategy) {
		this.idGenerationStrategy = idGenerationStrategy;
	}

	final void setModuleId(final String moduleId) {
		this.moduleId = moduleId;
	}
	
	final void setActive(Boolean active) {
		this.active = active;
	}
	
	@JsonIgnore
	public Collection<SnomedCoreComponentCreateRequest> getNestedRequests() {
		return ImmutableList.of(this);
	}

}
