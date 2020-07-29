/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;

/**
 * @since 7.8
 */
public final class MapTypeRefSetUpdateRequestBuilder extends BaseRequestBuilder<MapTypeRefSetUpdateRequestBuilder, TransactionContext, Boolean> 
	implements SnomedTransactionalRequestBuilder<Boolean> {

	private String referenceSetId;
	private String mapTargetComponent;
	
	public MapTypeRefSetUpdateRequestBuilder setMapTargetComponent(final String mapTargetComponent) {
		this.mapTargetComponent = mapTargetComponent;
		return this;
	}
	
	public MapTypeRefSetUpdateRequestBuilder setReferenceSetId(String refSetId) {
		this.referenceSetId = refSetId;
		return this;
	}
	
	@Override
	protected Request<TransactionContext, Boolean> doBuild() {
		return new MapTypeRefSetUpdateRequest(referenceSetId, mapTargetComponent);
	}
	
}
