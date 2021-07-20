/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.conceptmap;

import java.util.List;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.compare.ConceptMapCompareResultItem;
import com.b2international.snowowl.core.request.ConceptMapMappingSearchRequestBuilder;

/**
 * @since 8.0
 */
public class ConceptMapRequests {

	/**
	 * Creates a new generic set mapping search request builder.
	 * 
	 * @return the builder to configure for generic mappings search
	 */
	public static ConceptMapMappingSearchRequestBuilder prepareSearchConceptMapMappings() {
		return new ConceptMapMappingSearchRequestBuilder();
	}
	
	public static ConceptMapCompareRequestBuilder prepareConceptMapCompare(ResourceURI baseConceptMapURI, ResourceURI compareConceptMapURI){
		return new ConceptMapCompareRequestBuilder(baseConceptMapURI, compareConceptMapURI);
	}

	public static ConceptMapCompareDsvExportRequestBuilder prepareConceptMapCompareDsvExport(final List<ConceptMapCompareResultItem> items, final String filePath){
		return new ConceptMapCompareDsvExportRequestBuilder(items, filePath);
	}
	
}
