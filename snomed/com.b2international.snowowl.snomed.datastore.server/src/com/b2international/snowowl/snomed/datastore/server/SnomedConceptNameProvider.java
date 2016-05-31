/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.server;

import java.util.concurrent.TimeUnit;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.request.DescriptionRequestHelper;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.services.ISnomedConceptNameProvider;
import com.google.inject.Provider;

/**
 * Component name provider implementation for SNOMED CT concepts.
 */
public class SnomedConceptNameProvider implements ISnomedConceptNameProvider {

	private static final long NAME_PROVIDER_TIMEOUT = TimeUnit.SECONDS.toMillis(10L);
	
	private final Provider<IEventBus> bus;
	private final Provider<LanguageSetting> languageSetting;

	public SnomedConceptNameProvider(final Provider<IEventBus> bus, final Provider<LanguageSetting> languageSetting) {
		this.bus = bus;
		this.languageSetting = languageSetting;
	}
	
	@Override
	public String getComponentLabel(final IBranchPath branchPath, final String componentId) {
		final ISnomedDescription pt = new DescriptionRequestHelper() {
			@Override
			protected SnomedDescriptions execute(final SnomedDescriptionSearchRequestBuilder req) {
				return req.build(branchPath.getPath()).executeSync(bus.get(), NAME_PROVIDER_TIMEOUT);
			}
		}.getPreferredTerm(componentId, languageSetting.get().getLanguagePreference());
		
		return (pt != null) ? pt.getTerm() : componentId;
	}
}
