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
package com.b2international.snowowl.core.request;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.Resource;
import com.b2international.snowowl.core.ResourceTypeConverter;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.version.Versions;

/**
 * @since 8.0
 */
public final class ResourceConverter extends BaseResourceConverter<ResourceDocument, Resource, Resources> {

	private final ResourceTypeConverter.Registry converters;

	public ResourceConverter(RepositoryContext context, Options expand, List<ExtendedLocale> locales) {
		super(context, expand, locales);
		this.converters = context().service(ResourceTypeConverter.Registry.class);
	}
	
	@Override
	protected RepositoryContext context() {
		return (RepositoryContext) super.context();
	}

	@Override
	protected Resources createCollectionResource(List<Resource> results, String searchAfter, int limit, int total) {
		return new Resources(results, searchAfter, limit, total);
	}

	@Override
	protected Resource toResource(ResourceDocument doc) {
		return converters.toResource(doc);
	}
	
	@Override
	protected void expand(List<Resource> results) {
		if (results.isEmpty()) {
			return;
		}

		expandVersions(results);
	}

	private void expandVersions(List<Resource> results) {
		if (expand().containsKey(TerminologyResource.Expand.VERSIONS)) {
			Options expandOptions = expand().getOptions(TerminologyResource.Expand.VERSIONS);
			// version searches must be performed on individual terminology resources to provide correct results
			results.stream()
				.filter(TerminologyResource.class::isInstance)
				.map(TerminologyResource.class::cast)
				.forEach(res -> {
					Versions versions = ResourceRequests.prepareSearchVersion()
						.filterByResource(res.getResourceURI())
						.setLimit(getLimit(expandOptions))
						.setFields(expandOptions.containsKey("fields") ? expandOptions.getList("fields", String.class) : null)
						.sortBy(expandOptions.containsKey("sort") ? expandOptions.getString("sort") : null)
						.setLocales(locales())
						.build()
						.execute(context());
					res.setVersions(versions);
				});
		}
	}
	
}
