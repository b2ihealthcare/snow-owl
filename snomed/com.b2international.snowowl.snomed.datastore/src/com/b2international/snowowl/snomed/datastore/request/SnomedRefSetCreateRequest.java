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

import java.util.Set;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.5
 */
final class SnomedRefSetCreateRequest implements Request<TransactionContext, String> {

	public static final Set<String> STRUCTURAL_ATTRIBUTE_VALUE_SETS = ImmutableSet.of(
			Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR,
			Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR,
			Concepts.REFSET_RELATIONSHIP_REFINABILITY);	
	
	@NotNull
	private final SnomedRefSetType type;
	
	@NotEmpty
	private final String referencedComponentType;
	
	private String mapTargetComponentType = CoreTerminologyBroker.UNSPECIFIED;
	
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
	
	void setMapTargetComponentType(String mapTargetComponentType) {
		this.mapTargetComponentType = mapTargetComponentType;
	}

	@Override
	public String execute(TransactionContext context) {
		RefSetSupport.checkType(type, referencedComponentType);
		
		if (Strings.isNullOrEmpty(identifierId)) {
			throw new BadRequestException("Reference set identifier ID may not be null or empty.");
		} else {
			try {
				context.lookup(identifierId, Concept.class);
			} catch (ComponentNotFoundException e) {
				throw e.toBadRequestException();
			}
		}
		
		// FIXME due to different resource lists we need access to the specific editing context (which will be removed later)
		final SnomedRefSetEditingContext refSetContext = context.service(SnomedEditingContext.class).getRefSetEditingContext();
		final SnomedRefSet refSet;
		
		switch (type) {
			case SIMPLE:
			case QUERY:
			case DESCRIPTION_TYPE:
			case MODULE_DEPENDENCY:
			case OWL_AXIOM:
			case MRCM_DOMAIN:
			case MRCM_ATTRIBUTE_DOMAIN:
			case MRCM_ATTRIBUTE_RANGE:
			case MRCM_MODULE_SCOPE:
				refSet = createRegularRefSet(context);
				break;
			case CONCRETE_DATA_TYPE:
				refSet = createConcreteDomainRefSet(context);
				break;
			case COMPLEX_MAP:
			case EXTENDED_MAP:
			case SIMPLE_MAP:
				refSet = createMappingRefSet(context);
				break;
			case ASSOCIATION:
			case LANGUAGE:
				refSet = createStructuralRefSet(context);
				break;
			case ATTRIBUTE_VALUE:
				if (STRUCTURAL_ATTRIBUTE_VALUE_SETS.contains(identifierId)) {
					refSet = createStructuralRefSet(context);
				} else {
					refSet = createRegularRefSet(context);
				}
				break;
			default:
				throw new IllegalArgumentException("Unsupported reference set type " + type + " for reference set identifier " + identifierId);
		}
		
		refSetContext.add(refSet);
		return identifierId;
	}

	private SnomedRegularRefSet createRegularRefSet(TransactionContext context) {
		return SnomedComponents.newRegularReferenceSet()
			.withType(type)
			.withReferencedComponentType(referencedComponentType)
			.withIdentifierConceptId(identifierId)
			.build(context);
	}

	private SnomedConcreteDataTypeRefSet createConcreteDomainRefSet(TransactionContext context) {
		return SnomedComponents.newConcreteDomainReferenceSet()
			.withDataType(SnomedRefSetUtil.getDataType(identifierId))
			.withIdentifierConceptId(identifierId)
			.build(context);
	}

	private SnomedMappingRefSet createMappingRefSet(TransactionContext context) {
		return SnomedComponents.newMappingReferenceSet()
			.withType(type)
			.withIdentifierConceptId(identifierId)
			.withReferencedComponentType(referencedComponentType)
			.withMapTargetComponentType(mapTargetComponentType)
			.build(context);
	}

	private SnomedRefSet createStructuralRefSet(TransactionContext context) {
		return SnomedComponents.newStructuralReferenceSet()
				.withType(type)
				.withReferencedComponentType(referencedComponentType)
				.withIdentifierConceptId(identifierId)
				.build(context);
	}

}
