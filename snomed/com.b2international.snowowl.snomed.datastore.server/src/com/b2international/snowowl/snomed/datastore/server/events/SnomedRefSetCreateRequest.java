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
package com.b2international.snowowl.snomed.datastore.server.events;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.5
 */
public class SnomedRefSetCreateRequest extends SnomedRefSetRequest<TransactionContext, SnomedReferenceSet> {

	private final SnomedRefSetType type;
	private final String referencedComponentType;
	private SnomedConceptCreateRequest conceptReq;
	
	public SnomedRefSetCreateRequest(SnomedRefSetType type, String referencedComponentType, SnomedConceptCreateRequest conceptReq) {
		this.type = type;
		this.referencedComponentType = referencedComponentType;
		this.conceptReq = conceptReq;
	}
	
	@Override
	public SnomedReferenceSet execute(TransactionContext context) {
		return null;
	}
	
	@Override
	protected Class<SnomedReferenceSet> getReturnType() {
		return SnomedReferenceSet.class;
	}
	
}
