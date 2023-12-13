/*
 * Copyright 2017-2023 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.branch.compare;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.index.revision.*;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.authorization.AccessControl;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.repository.RepositoryRequests;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

/**
 * @since 5.9
 */
final class BranchCompareRequest implements Request<RepositoryContext, BranchCompareResult>, AccessControl {

	private static final long serialVersionUID = 1L;

	@JsonProperty
	private String base;
	
	@NotEmpty
	@JsonProperty
	private String compare;
	
	@Min(0)
	@JsonProperty
	private int limit;
	
	@JsonProperty
	private boolean includeComponentChanges;
	
	@JsonProperty
	private boolean includeDerivedComponentChanges;
	
	@JsonProperty
	private Set<String> types;
	
	@JsonProperty
	private Set<String> ids;

	@JsonProperty
	private Set<String> statsFor;
	
	BranchCompareRequest() {
	}
	
	void setBaseBranch(String baseBranch) {
		this.base = baseBranch;
	}
	
	void setCompareBranch(String compareBranch) {
		this.compare = compareBranch;
	}
	
	void setLimit(int limit) {
		this.limit = limit;
	}
	
	void setIncludeComponentChanges(boolean includeComponentChanges) {
		this.includeComponentChanges = includeComponentChanges;
	}
	
	void setIncludeDerivedComponentChanges(boolean includeDerivedComponentChanges) {
		this.includeDerivedComponentChanges = includeDerivedComponentChanges;
	}
	
	void setTypes(Set<String> types) {
		this.types = types;
	}
	
	void setIds(Set<String> ids) {
		this.ids = ids;
	}
	
	void setStatsFor(Set<String> statsFor) {
		this.statsFor = statsFor;
	}
	
	@Override
	public BranchCompareResult execute(RepositoryContext context) {
		final RevisionIndex index = context.service(RevisionIndex.class);
		final Branch branchToCompare = RepositoryRequests.branching().prepareGet(compare).build().execute(context);
		final long compareHeadTimestamp = branchToCompare.headTimestamp();
		
		RevisionCompareOptions options = RevisionCompareOptions.builder()
			.limit(limit)
			.includeComponentChanges(includeComponentChanges)
			.includeDerivedComponentChanges(includeDerivedComponentChanges)
			.types(types)
			.ids(ids)
			.build();
		
		final RevisionCompare compareResult;
		final String baseBranchPath;
		if (base != null) {
			compareResult = index.compare(base, compare, options);
			baseBranchPath = base;
		} else {
			compareResult = index.compare(compare, options);
			baseBranchPath = branchToCompare.parentPath();
		}
		
		final BranchCompareResult.Builder result = BranchCompareResult.builder(baseBranchPath, compare, compareHeadTimestamp);
		
		Multimap<String, ObjectId> changesByProperty = HashMultimap.create();
		
		final Set<ComponentIdentifier> changedContainers = Sets.newHashSet();
		for (RevisionCompareDetail detail : compareResult.getDetails()) {
			final ObjectId affectedId;
			if (detail.isComponentChange()) {
				affectedId = detail.getComponent();
				if (!detail.getObject().isRoot() && includeComponentChanges) {
					changedContainers.add(ComponentIdentifier.of(detail.getObject().type(), detail.getObject().id()));
					
					if (statsFor != null && statsFor.contains(detail.getComponent().type())) {
						changesByProperty.put(detail.getComponent().type(), detail.getObject());
					}
				}
			} else {
				affectedId = detail.getObject();
				
				if (statsFor != null && statsFor.contains(detail.getProperty())) {
					changesByProperty.put(detail.getProperty(), detail.getObject());
				}
			}
			
			// component should not be registered if not requested via type filter
			if (types != null && !types.contains(affectedId.type())) {
				continue;
			}
			
			final ComponentIdentifier identifier = ComponentIdentifier.of(affectedId.type(), affectedId.id());
			
			switch (detail.getOp()) {
			case ADD:
				result.putNewComponent(identifier);
				break;
			case CHANGE:
				result.putChangedComponent(identifier);			
				break;
			case REMOVE:
				result.putDeletedComponent(identifier);
				break;
			}
		}

		if (statsFor != null) {
			for (String property : statsFor) {
				result.addStats(new BranchCompareChangeStatistic(property, changesByProperty.get(property).stream().map(ComponentIdentifier::of).collect(Collectors.toSet())));
			}
		}
		
		return result
				.build(changedContainers);
	}
	
	@Override
	public String getOperation() {
		return Permission.OPERATION_BROWSE;
	}

}
