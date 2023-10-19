/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.compare;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ResourceURIWithQuery;
import com.b2international.snowowl.core.domain.RepositoryContext;

/**
 * Implementations of this interface can be used to generate
 * {@link AnalysisCompareResult} instances.
 * 
 * @since 9.0.0
 */
@FunctionalInterface
public interface ResourceContentComparer {

	/** 
	 * The "no-op" implementation returns an unpopulated comparison summary.
	 */
	ResourceContentComparer NOOP = (context, fromUri, toUri, includeChanges, termType, locales) -> new AnalysisCompareResult(fromUri, toUri);

	/**
	 * Generates a resource content compare result based on the contents of the resource represented
	 * by the two resource URI arguments.
	 * 
	 * @param repositoryContext - the context to use for completing the request
	 * (must match the tooling of both resource URIs)
	 * @param fromUri - the resource URI representing the comparison baseline
	 * @param toUri - the resource URI representing the comparison target
	 * @param includeChanges - set to <code>true</code> when itemized component
	 * changes should be included in the response
	 * @param termType - term type for changed component labels
	 * @param locales - locales to use for changed component labels
	 * @return the populated change summary
	 */
	AnalysisCompareResult compareResource(
		RepositoryContext repositoryContext, 
		ResourceURIWithQuery fromUri, 
		ResourceURIWithQuery toUri,
		boolean includeChanges, 
		String termType,
		List<ExtendedLocale> locales);
}
