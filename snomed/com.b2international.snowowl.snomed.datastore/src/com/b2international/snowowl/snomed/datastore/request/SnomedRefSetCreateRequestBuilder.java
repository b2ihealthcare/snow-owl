/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * <i>Builder</i> class to build requests responsible for creating SNOMED CT reference sets.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * 
 * @since 4.5
 */
public final class SnomedRefSetCreateRequestBuilder extends BaseRequestBuilder<SnomedRefSetCreateRequestBuilder, TransactionContext, String> {

	private SnomedRefSetType type;
	private String referencedComponentType = CoreTerminologyBroker.UNSPECIFIED;
	private String mapTargetComponentType = CoreTerminologyBroker.UNSPECIFIED;
	private String identifierId;
	
	SnomedRefSetCreateRequestBuilder() {
		super();
	}

	public SnomedRefSetCreateRequestBuilder setType(SnomedRefSetType type) {
		this.type = type;
		return getSelf();
	}
	
	public SnomedRefSetCreateRequestBuilder setReferencedComponentType(String referencedComponentType) {
		this.referencedComponentType = referencedComponentType;
		return getSelf();
	}
	
	public SnomedRefSetCreateRequestBuilder setMapTargetComponentType(String mapTargetComponentType) {
		this.mapTargetComponentType = mapTargetComponentType;
		return getSelf();
	}
	
	public SnomedRefSetCreateRequestBuilder setIdentifierId(String identifierId) {
		this.identifierId = identifierId;
		return getSelf();
	}
	
	@Override
	public Request<TransactionContext, String> doBuild() {
		final SnomedRefSetCreateRequest createRequest = new SnomedRefSetCreateRequest(type, referencedComponentType);
		createRequest.setIdentifierId(identifierId);
		createRequest.setMapTargetComponentType(mapTargetComponentType);
		return createRequest;
	}
	
}
