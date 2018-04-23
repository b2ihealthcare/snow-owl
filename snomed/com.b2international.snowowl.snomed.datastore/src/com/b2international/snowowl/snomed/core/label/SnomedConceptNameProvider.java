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
package com.b2international.snowowl.snomed.core.label;

import java.util.concurrent.TimeUnit;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.DescriptionRequestHelper;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * Component name provider implementation for SNOMED CT concepts.
 * @deprecated - if possible use {@link SnomedRequests} API directly
 */
public final class SnomedConceptNameProvider {

	private static final long NAME_PROVIDER_TIMEOUT = TimeUnit.SECONDS.toMillis(10L);
	
	private final IEventBus bus;
	private final LanguageSetting languageSetting;

	public SnomedConceptNameProvider(final IEventBus bus, final LanguageSetting languageSetting) {
		this.bus = bus;
		this.languageSetting = languageSetting;
	}
	
	public String getComponentLabel(final IBranchPath branchPath, final String componentId) {
		final SnomedDescription pt = new DescriptionRequestHelper() {
			@Override
			protected SnomedDescriptions execute(final SnomedDescriptionSearchRequestBuilder req) {
				return req.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath()).execute(bus).getSync(NAME_PROVIDER_TIMEOUT, TimeUnit.MILLISECONDS);
			}
		}.getPreferredTerm(componentId, languageSetting.getLanguagePreference());
		
		return (pt != null) ? pt.getTerm() : componentId;
	}
}
