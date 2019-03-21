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

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.CodeSystemEntry;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.datastore.CodeSystems;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.terminologyregistry.core.request.CodeSystemRequests;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.primitives.Longs;

/** 
 * @since 4.5
 * @param <B>
 */
public abstract class SnomedComponentUpdateRequest implements SnomedComponentRequest<Boolean> {

	private final String componentId;
	
	private String moduleId;
	private Boolean active;
	
	protected SnomedComponentUpdateRequest(String componentId) {
		this.componentId = componentId;
	}
	
	void setActive(Boolean active) {
		this.active = active;
	}
	
	void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	
	/**
	 * @deprecated - visibility will be reduced to protected in 4.6
	 * @return
	 */
	public Boolean isActive() {
		return active;
	}
	
	protected String getModuleId() {
		return moduleId;
	}
	
	protected String getComponentId() {
		return componentId;
	}
	
	protected static <T extends SnomedComponent, U extends SnomedRefSetMember> void tryUpdateMemberEffectiveTime(
			TransactionContext context,
			U component, // CDO component
			Function<String, T> fetchFunction, // request to get previous version
			BiPredicate<U,T> predicate) // predicate to test whether the previous version is the same as the current component
		{
		
		if (component.isSetEffectiveTime()) {
			component.unsetEffectiveTime();
		} else if (component.isReleased()) {
			
			List<String> branchesForPreviousVersion = getAvailableVersionPaths(context);
			
			if (branchesForPreviousVersion.isEmpty()) {
				return; // nothing we can do
			}
			
			T previousVersion = null;
			
			for (String branch : branchesForPreviousVersion) {
				
				try {
					
					previousVersion = fetchFunction.apply(branch);
					
					if (previousVersion != null) {
						break;
					}
					
				} catch (NotFoundException e) {
					// check next available branch if possible
				}
				
			}
			
			if (previousVersion == null) {
				throw new IllegalStateException("Previous version of released member could not be found. ID: " + component.getUuid() + ", branch: " + context.branchPath());
			} else {
				
				if (predicate.test(component, previousVersion)) {
					component.setEffectiveTime(previousVersion.getEffectiveTime());
				} else if (component.isSetEffectiveTime()) {
					component.unsetEffectiveTime();
				}
				
			}
			
		}
		
	}
	
	protected static <T extends SnomedComponent, U extends Component> void tryUpdateEffectiveTime(
			TransactionContext context,
			U component, // CDO component
			Function<String, T> fetchFunction, // request to get previous version
			BiPredicate<U,T> predicate) // predicate to test whether the previous version is the same as the current component
		{
		
		if (component.isSetEffectiveTime()) {
			component.unsetEffectiveTime();
		} else if (component.isReleased()) {
			
			List<String> branchesForPreviousVersion = getAvailableVersionPaths(context);
			
			if (branchesForPreviousVersion.isEmpty()) {
				return; // nothing we can do
			}
			
			T previousVersion = null;
			
			for (String branch : branchesForPreviousVersion) {
				
				try {
					
					previousVersion = fetchFunction.apply(branch);
					
					if (previousVersion != null) {
						break;
					}
					
				} catch (NotFoundException e) {
					// check next available branch if possible
				}
				
			}
			
			if (previousVersion == null) {
				throw new IllegalStateException("Previous version of released component could not be found. ID: " + component.getId() + ", branch: " + context.branchPath());
			} else {
				
				if (predicate.test(component, previousVersion)) {
					component.setEffectiveTime(previousVersion.getEffectiveTime());
				} else if (component.isSetEffectiveTime()) {
					component.unsetEffectiveTime();
				}
				
			}
			
		}
		
	}
	
	private static List<String> getAvailableVersionPaths(TransactionContext context) {
		
		final CodeSystems codeSystems = CodeSystemRequests.prepareSearchCodeSystem().all().build().execute(context);
		final Map<String, CodeSystemEntry> codeSystemsByMainBranch = Maps.uniqueIndex(codeSystems, CodeSystemEntry::getBranchPath);
		
		List<CodeSystemEntry> relativeCodeSystems = newArrayList();
		
		Iterator<IBranchPath> bottomToTop = BranchPathUtils.bottomToTopIterator(BranchPathUtils.createPath(context.branchPath()));
		
		while (bottomToTop.hasNext()) {
			final IBranchPath candidate = bottomToTop.next();
			if (codeSystemsByMainBranch.containsKey(candidate.getPath())) {
				relativeCodeSystems.add(codeSystemsByMainBranch.get(candidate.getPath()));
			}
		}
		
		if (relativeCodeSystems.isEmpty()) {
			throw new IllegalStateException("No relative code system has been found for branch '" + context.branchPath() + "'");
		}
		
		// the first code system in the list is the working codesystem
		CodeSystemEntry workingCodeSystem = relativeCodeSystems.stream().findFirst().get();
		
		Optional<CodeSystemVersionEntry> workingCodeSystemVersion = CodeSystemRequests.prepareSearchCodeSystemVersion()
			.one()
			.filterByCodeSystemShortName(workingCodeSystem.getShortName())
			.sortBy(SearchResourceRequest.SortField.descending(CodeSystemVersionEntry.Fields.EFFECTIVE_DATE))
			.build()
			.execute(context)
			.first();
		
		List<CodeSystemVersionEntry> relativeCodeSystemVersions = newArrayList();
		
		if (workingCodeSystemVersion.isPresent() && !Strings.isNullOrEmpty(workingCodeSystemVersion.get().getPath())) {
			relativeCodeSystemVersions.add(workingCodeSystemVersion.get());
		}
		
		if (relativeCodeSystems.size() > 1) {
			
			relativeCodeSystems.stream()
				.skip(1)
				.forEach( codeSystem -> {
					
					Map<String, CodeSystemVersionEntry> pathToVersionMap = CodeSystemRequests.prepareSearchCodeSystemVersion()
						.all()
						.filterByCodeSystemShortName(codeSystem.getShortName())
						.build()
						.execute(context)
						.stream()
						.collect(toMap(version -> version.getPath(), v -> v));
					
					Iterator<IBranchPath> branchPathIterator = BranchPathUtils.bottomToTopIterator(BranchPathUtils.createPath(context.branchPath()));
					
					while (branchPathIterator.hasNext()) {
						final IBranchPath candidate = branchPathIterator.next();
						if (pathToVersionMap.containsKey(candidate.getPath())) {
							relativeCodeSystemVersions.add(pathToVersionMap.get(candidate.getPath()));
							break;
						}
					}
					
				});
			
		}
		
		return relativeCodeSystemVersions.stream()
					// sort versions by effective date in reversed order 
					.sorted( (v1, v2) -> Longs.compare(v2.getEffectiveDate(), v1.getEffectiveDate()))
					.map(CodeSystemVersionEntry::getPath)
					.collect(toList());
	}
		
	protected boolean updateModule(final TransactionContext context, final Component component) {
		if (null == moduleId) {
			return false;
		}

		final String currentModuleId = component.getModule().getId();
		if (!currentModuleId.equals(moduleId)) {
			component.setModule(context.lookup(moduleId, Concept.class));
			return true;
		} else {
			return false;
		}
	}

	protected boolean updateStatus(final TransactionContext context, final Component component) {
		if (null == active) {
			return false;
		}

		if (component.isActive() != active) {
			component.setActive(active);
			return true;
		} else {
			return false;
		}
	}
	
	protected void checkUpdateOnReleased(Component component, String field, Object value) {
		if (component.isReleased()) {
			throw new BadRequestException("Cannot update '%s' to '%s' on released %s '%s'", field, value, component.eClass().getName(), component.getId());
		}
	}
	
}
