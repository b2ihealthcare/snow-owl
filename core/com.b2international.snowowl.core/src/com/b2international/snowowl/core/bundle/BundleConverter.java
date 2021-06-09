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
package com.b2international.snowowl.core.bundle;

import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.Resources;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.internal.ResourceDocument;
import com.b2international.snowowl.core.request.BaseResourceConverter;
import com.b2international.snowowl.core.request.ResourceRequests;

/**
 * @since 8.0
 */
public class BundleConverter extends BaseResourceConverter<ResourceDocument, Bundle, Bundles> {

	public BundleConverter(final ServiceProvider context, final Options expand, final List<ExtendedLocale> locales) {
		super(context, expand, locales);
	}
	
	@Override
	protected Bundles createCollectionResource(List<Bundle> results, String searchAfter, int limit, int total) {
		return new Bundles(results, searchAfter, limit, total);
	}

	@Override
	protected Bundle toResource(final ResourceDocument doc) {
		final Bundle bundle = new Bundle();
		
		bundle.setId(doc.getId());
		bundle.setUrl(doc.getUrl());
		bundle.setTitle(doc.getTitle());
		bundle.setLanguage(doc.getLanguage());
		bundle.setDescription(doc.getDescription());
		bundle.setStatus(doc.getStatus());
		bundle.setCopyright(doc.getCopyright());
		bundle.setOwner(doc.getOwner());
		bundle.setContact(doc.getContact());
		bundle.setUsage(doc.getUsage());
		bundle.setPurpose(doc.getPurpose());
		bundle.setBundleId(doc.getBundleId());

		return bundle;
	}

	@Override
	protected void expand(final List<Bundle> results) {
		if (expand().isEmpty() || results.isEmpty()) {
			return;
		}
		
		expandContents(results);
	}

	private void expandContents(final List<Bundle> results) {
		if (!expand().containsKey(Bundle.Expand.RESOURCES)) {
			return;
		}
		
		results.forEach(result -> {
			final Resources resources = ResourceRequests.prepareSearch()
				.setLimit(100)
				.filterByBundleId(result.getId())
				.buildAsync()
				.getRequest()
				.execute(context());
			
			result.setResources(resources);
		});
	}
}
