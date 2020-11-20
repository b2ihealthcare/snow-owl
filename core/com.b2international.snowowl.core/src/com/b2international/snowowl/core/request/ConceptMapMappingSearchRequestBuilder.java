/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.ConceptMapMappings;
import com.b2international.snowowl.core.request.SetSearchRequestEvaluator.OptionKey;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.google.common.collect.ImmutableSet;

/**
* @since 7.8
*/
public final class ConceptMapMappingSearchRequestBuilder extends SearchResourceRequestBuilder<ConceptMapMappingSearchRequestBuilder, BranchContext, ConceptMapMappings> 
		implements RevisionIndexRequestBuilder<ConceptMapMappings> {
	
	public ConceptMapMappingSearchRequestBuilder filterByConceptMap(String conceptMapId) {
		return addOption(OptionKey.SET, conceptMapId);
	}
	
	public ConceptMapMappingSearchRequestBuilder filterByConceptMaps(Iterable<String> conceptMapIds) {
		return addOption(OptionKey.SET, conceptMapIds);
	}
	
	public ConceptMapMappingSearchRequestBuilder filterBySourceToolingId(String toolingId) {
		return addOption(OptionKey.SOURCE_TOOLING_ID, toolingId);
	}
	
	public ConceptMapMappingSearchRequestBuilder filterByReferencedComponentId(String componentId) {
		return addOption(OptionKey.REFERENCED_COMPONENT, componentId);
	}
	
	public ConceptMapMappingSearchRequestBuilder filterByReferencedComponentIds(Iterable<String> componentIds) {
		return addOption(OptionKey.REFERENCED_COMPONENT, componentIds);
	}
	
	public ConceptMapMappingSearchRequestBuilder filterByMapTarget(String mapTarget) {
		return addOption(OptionKey.MAP_TARGET, mapTarget);
	}
	
	public ConceptMapMappingSearchRequestBuilder filterByMapTargets(Iterable<String> mapTargets) {
		return addOption(OptionKey.MAP_TARGET, mapTargets);
	}

	public ConceptMapMappingSearchRequestBuilder filterByComponentId(String componentId) {
		return filterByComponentIds(ImmutableSet.of(componentId));
	}
	
	public ConceptMapMappingSearchRequestBuilder filterByComponentId(ComponentURI componentId) {
		return filterByComponentIds(ImmutableSet.of(componentId.toString()));
	}

	public ConceptMapMappingSearchRequestBuilder filterByComponentIds(Iterable<String> componentIds) {
		return addOption(OptionKey.COMPONENT, componentIds);
	}
	
	public ConceptMapMappingSearchRequestBuilder setPreferredDisplay(String displayType) {
		return addOption(OptionKey.DISPLAY, displayType);
	}
	
	@Override
	protected SearchResourceRequest<BranchContext, ConceptMapMappings> createSearch() {
		return new ConceptMapMappingSearchRequest();
	}

}
