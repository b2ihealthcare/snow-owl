/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.datastore.ContentAvailabilityInfoProvider;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * {@link ContentAvailabilityInfoProvider} for SNOMED&nbsp;CT.
 *
 */
public class SnomedAvailabilityInfoProvider implements ContentAvailabilityInfoProvider {

	@Override
	public boolean isAvailable() {
		return SnomedRequests.prepareSearchConcept()
				.setLimit(0)
				.filterById(Concepts.ROOT_CONCEPT)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, Branch.MAIN_PATH)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync().getTotal() > 0;
	}

	@Override
	public String getRepositoryUuid() {
		return SnomedDatastoreActivator.REPOSITORY_UUID;
	}

}