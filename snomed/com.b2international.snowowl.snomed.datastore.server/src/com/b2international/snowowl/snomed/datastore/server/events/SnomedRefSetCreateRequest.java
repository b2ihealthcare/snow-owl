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
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;

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
		checkType(type);
		final ISnomedConcept identifierConcept = this.conceptReq.execute(context);
		
		// FIXME due to different resource lists we have to access to the specific editing context (which will be removed later on)
		final SnomedRefSetEditingContext refSetContext = context.service(SnomedEditingContext.class).getRefSetEditingContext();
		
		final SnomedRegularRefSet refSet = SnomedComponents
			.newSimpleTypeReferenceSet()
			.setReferencedComponentType(referencedComponentType)
			.setIdentifierConceptId(identifierConcept.getId())
			.build(context);
		
		refSetContext.add(refSet);
		return new SnomedReferenceSetConverter().apply(refSet, identifierConcept);
	}
	
	private void checkType(SnomedRefSetType type) {
		switch (type) {
		case SIMPLE: return;
		default: throw new NotImplementedException("SNOMED CT Reference Set type '%s' is unsupported");
		}
	}

	@Override
	protected Class<SnomedReferenceSet> getReturnType() {
		return SnomedReferenceSet.class;
	}
	
}
