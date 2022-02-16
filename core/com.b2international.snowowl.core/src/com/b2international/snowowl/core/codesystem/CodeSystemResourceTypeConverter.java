/*
 * Copyright 2021-2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.codesystem;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceTypeConverter;
import com.b2international.snowowl.core.domain.Concepts;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.plugin.Component;

/**
 * @since 8.0
 */
@Component
public final class CodeSystemResourceTypeConverter implements ResourceTypeConverter {

	@Override
	public String getResourceType() {
		return CodeSystem.RESOURCE_TYPE;
	}

	@Override
	public Resource toResource(ResourceDocument doc) {
		return CodeSystem.from(doc);
	}
	
	@Override
	public Integer getRank() {
		return 2;
	}
	
	@Override
	public void expand(RepositoryContext context, Options expand, List<ExtendedLocale> locales, Collection<Resource> results) {
		if (expand.containsKey("content")) {
			// allow expanding content via content expansion, for now hit count only
			results.forEach(codeSystem -> {
				final Concepts concepts = CodeSystemRequests.prepareSearchConcepts()
						.setLimit(0)
						.filterByCodeSystemUri(codeSystem.getResourceURI())
						.buildAsync()
						.execute(context);
				codeSystem.setProperties("content", concepts);
			});
		}
	}

}
