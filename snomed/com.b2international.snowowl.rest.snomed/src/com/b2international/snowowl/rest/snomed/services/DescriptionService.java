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
package com.b2international.snowowl.rest.snomed.services;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.DescriptionRequestHelper;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionSearchRequestBuilder;

/**
 * @since 4.5
 */
public class DescriptionService extends DescriptionRequestHelper {
	
	private final IEventBus bus;
	private final String branch;
	
	public DescriptionService(IEventBus bus, String branch) {
		this.bus = bus;
		this.branch = branch;
	}

	@Override
	protected SnomedDescriptions execute(SnomedDescriptionSearchRequestBuilder req) {
		return req.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch).execute(bus).getSync();
	}

}
