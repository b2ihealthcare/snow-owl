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

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
final class SnomedRefSetCreateRequest implements Request<TransactionContext, String> {

	@NotNull
	private final SnomedRefSetType type;
	
	@NotEmpty
	private final String referencedComponentType;
	
	private String identifierId;
	
	SnomedRefSetCreateRequest(SnomedRefSetType type, String referencedComponentType) {
		this.type = type;
		this.referencedComponentType = referencedComponentType;
	}

	SnomedRefSetType getRefSetType() {
		return type;
	}

	void setIdentifierId(String identifierId) {
		this.identifierId = identifierId;
	}
	
	@Override
	public String execute(TransactionContext context) {
		RefSetSupport.check(type);
		
		if (Strings.isNullOrEmpty(identifierId)) {
			throw new BadRequestException("Reference set identifier ID may not be null or empty.");
		}
		
		// FIXME due to different resource lists we need access to the specific editing context (which will be removed later)
		final SnomedRefSetEditingContext refSetContext = context.service(SnomedEditingContext.class).getRefSetEditingContext();
		final SnomedRefSet refSet;
		
		switch (type) {
			case SIMPLE:
			case QUERY:
				RefSetSupport.checkType(type, referencedComponentType);
				
				refSet = SnomedComponents
					.newRegularReferenceSet()
					.withType(type)
					.withReferencedComponentType(referencedComponentType)
					.withIdentifierConceptId(identifierId)
					.build(context);
				break;
			case CONCRETE_DATA_TYPE:
				refSet = SnomedComponents
					.newConcreteDomainReferenceSet()
					.withDataType(SnomedRefSetUtil.getDataType(identifierId))
					.withIdentifierConceptId(identifierId)
					.build(context);
				break;
			default:
				throw new IllegalArgumentException("Unsupported reference set type " + type + " for reference set identifier " + identifierId);
		}
		
		refSetContext.add(refSet);
		return identifierId;
	}

}
