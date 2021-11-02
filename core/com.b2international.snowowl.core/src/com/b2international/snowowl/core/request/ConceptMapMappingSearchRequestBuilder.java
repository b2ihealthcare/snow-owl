/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.ConceptMapMappings;
import com.b2international.snowowl.core.request.MemberSearchRequestEvaluator.OptionKey;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.google.common.collect.FluentIterable;

/**
* @since 7.8
*/
public final class ConceptMapMappingSearchRequestBuilder 
		extends SearchPageableCollectionResourceRequestBuilder<ConceptMapMappingSearchRequestBuilder, ServiceProvider, ConceptMapMappings> 
		implements SystemRequestBuilder<ConceptMapMappings> {
	
	public ConceptMapMappingSearchRequestBuilder filterByConceptMap(String conceptMap) {
		return addOption(OptionKey.URI, conceptMap);
	}
	
	public ConceptMapMappingSearchRequestBuilder filterByConceptMaps(Iterable<String> conceptMaps) {
		return addOption(OptionKey.URI, conceptMaps);
	}
	
	//TODO: filterByConceptMap and filterByConceptMapUri should use a common uri because they use the same OptionKey
	@Deprecated
	public ConceptMapMappingSearchRequestBuilder filterByConceptMapUri(ResourceURI conceptMapUri) {
		return addOption(OptionKey.URI, conceptMapUri == null ? null : conceptMapUri.toString());
	}
	
	@Deprecated
	public ConceptMapMappingSearchRequestBuilder filterByConceptMapUris(Iterable<ResourceURI> conceptMapUris) {
		return addOption(OptionKey.URI, conceptMapUris == null ? null : FluentIterable.from(conceptMapUris).transform(ResourceURI::toString).toSet());
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
		return filterByComponentIds(Set.of(componentId));
	}
	
	public ConceptMapMappingSearchRequestBuilder filterByComponentId(ComponentURI uri) {
		return filterByComponentIds(Set.of(uri.toString()));
	}

	public ConceptMapMappingSearchRequestBuilder filterByComponentIds(Iterable<String> componentIds) {
		return addOption(OptionKey.COMPONENT, componentIds);
	}
	
	public ConceptMapMappingSearchRequestBuilder filterByActive(Boolean active) {
		return addOption(OptionKey.ACTIVE, active);
	}
	
	public ConceptMapMappingSearchRequestBuilder filterBySourceToolingId(String toolingId) {
		return addOption(OptionKey.SOURCE_TOOLING_ID, toolingId);
	}
	
	public ConceptMapMappingSearchRequestBuilder setPreferredDisplay(String displayType) {
		return addOption(OptionKey.DISPLAY, displayType);
	}
	
	@Override
	protected SearchResourceRequest<ServiceProvider, ConceptMapMappings> createSearch() {
		return new ConceptMapMappingSearchRequest();
	}

}
