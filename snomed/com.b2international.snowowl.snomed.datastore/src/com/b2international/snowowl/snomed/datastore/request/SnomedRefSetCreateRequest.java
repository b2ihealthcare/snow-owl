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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Collection;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.collections.PrimitiveSets;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;

/**
 * @since 4.5
 */
final class SnomedRefSetCreateRequest extends BaseRequest<TransactionContext, String> {

	@NotNull
	private final SnomedRefSetType type;
	
	@NotNull
	private final String referencedComponentType;
	
	@Valid
	private final SnomedConceptCreateRequest conceptReq;
	
	SnomedRefSetCreateRequest(SnomedRefSetType type, String referencedComponentType, SnomedConceptCreateRequest conceptReq) {
		this.type = type;
		this.referencedComponentType = referencedComponentType;
		this.conceptReq = conceptReq;
	}
	
	@Override
	public String execute(TransactionContext context) {
		RefSetSupport.check(type);
		RefSetSupport.checkType(type, referencedComponentType);
		checkParent(context);
		
		final String identifierConceptId = this.conceptReq.execute(context);
		
		// FIXME due to different resource lists we need access to the specific editing context (which will be removed later)
		final SnomedRefSetEditingContext refSetContext = context.service(SnomedEditingContext.class).getRefSetEditingContext();
		
		final SnomedRegularRefSet refSet = SnomedComponents
			.newRegularReferenceSet()
			.withType(type)
			.withReferencedComponentType(referencedComponentType)
			.withIdentifierConceptId(identifierConceptId)
			.build(context);
		
		refSetContext.add(refSet);
		return identifierConceptId;
	}
	
	private void checkParent(TransactionContext context) {
		final String refSetTypeRootParent = SnomedRefSetUtil.getConceptId(type);
		final Set<String> parents = conceptReq.getParents();
		if (!isValidParentage(context, refSetTypeRootParent, parents)) {
			throw new BadRequestException("'%s' type reference sets should be subtype of '%s' concept.", type, refSetTypeRootParent);
		}
	}

	private boolean isValidParentage(TransactionContext context, String requiredSuperType, Collection<String> parents) {
		// first check if the requiredSuperType is specified in the parents collection
		if (parents.contains(requiredSuperType)) {
			return true;
		}
		
		// if not, then check if any of the specified parents is subTypeOf the requiredSuperType
		final long superTypeIdLong = Long.parseLong(requiredSuperType);
		final SnomedConcepts parentConcepts = SnomedRequests.prepareSearchConcept().setLimit(parents.size()).setComponentIds(parents).build().execute(context);
		for (SnomedConcept parentConcept : parentConcepts) {
			if (parentConcept.getParentIds() != null) {
				if (PrimitiveSets.newLongOpenHashSet(parentConcept.getParentIds()).contains(superTypeIdLong)) {
					return true;
				}
			}
			if (parentConcept.getAncestorIds() != null) {
				if (PrimitiveSets.newLongOpenHashSet(parentConcept.getAncestorIds()).contains(superTypeIdLong)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected Class<String> getReturnType() {
		return String.class;
	}
	
}
