/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.core.domain.IdGenerationStrategy;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.0
 */
public abstract class BaseSnomedComponentCreateRequest implements SnomedCoreComponentCreateRequest {

	@Nonnull
	private Boolean active = Boolean.TRUE;
	
	@NotNull
	private IdGenerationStrategy idGenerationStrategy;
	
	private String moduleId;
	
	private List<SnomedRefSetMemberCreateRequest> members = Collections.emptyList();

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
	
	final void setActive(final Boolean active) {
		this.active = active;
	}
	
	void setMembers(final List<SnomedRefSetMemberCreateRequest> members) {
		this.members = ImmutableList.copyOf(members);
	}
	
	@JsonIgnore
	public Collection<SnomedCoreComponentCreateRequest> getNestedRequests() {
		return ImmutableList.of(this);
	}

	@Override
	public Set<String> getRequiredComponentIds(final TransactionContext context) {
		return ImmutableSet.<String>builder()
				.add(getModuleId())
				.addAll(members.stream().flatMap(req -> req.getRequiredComponentIds(context).stream()).collect(Collectors.toSet()))
				.build();
	}
	
	protected void convertMembers(final TransactionContext context, final String referencedComponentId) {
		for (final SnomedRefSetMemberCreateRequest memberRequest : members) {
			memberRequest.setReferencedComponentId(referencedComponentId);
			if (null == memberRequest.getModuleId()) {
				memberRequest.setModuleId(getModuleId());
			}
			
			memberRequest.execute(context);
		}
	}
}
