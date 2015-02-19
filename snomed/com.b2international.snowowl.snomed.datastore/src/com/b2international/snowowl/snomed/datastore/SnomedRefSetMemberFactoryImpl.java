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
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MAP_CATEGORY_NOT_CLASSIFIED;
import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.ComponentIdentifierPair;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.snomed.datastore.services.SnomedModuleDependencyRefSetService;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;

/**
 * Factory for creating reference set members.
 *
 */
public enum SnomedRefSetMemberFactoryImpl implements SnomedRefSetMemberFactory {

	/**Shared factory instance.*/
	INSTANCE;
	
	@Override
	public SnomedRefSetMember createMember(final SnomedEditingContext context, final SnomedRefSet refSet) {
		return createMember(
				checkNotNull(context, "context").getRefSetEditingContext(), 
				checkNotNull(refSet, "refSet"));
	}
	
	@Override
	public SnomedRefSetMember createMember(final SnomedRefSetEditingContext context, final SnomedRefSet refSet) {
		
		final ComponentIdentifierPair<String> referencedComponentPair = createReferencedComponentIdentifierPair(refSet);
		String moduleId = getModuleId(context);
		
		SnomedRefSetMember member = null;
		switch (checkNotNull(refSet, "refSet").getType()) {

			case ASSOCIATION:
				 member = context.createAssociationRefSetMember(referencedComponentPair, null, moduleId, (SnomedStructuralRefSet) refSet);
				 break;
			case ATTRIBUTE_VALUE:
				member = context.createAttributeValueRefSetMember(referencedComponentPair, null, moduleId, refSet);
				break;
			case COMPLEX_MAP:
				member = context.createComplexMapRefSetMember(referencedComponentPair, null, moduleId, (SnomedMappingRefSet) refSet);
				break;
			case EXTENDED_MAP:
				member = context.createComplexMapRefSetMember(referencedComponentPair, null, moduleId, (SnomedMappingRefSet) refSet);
				((SnomedComplexMapRefSetMember) member).setMapCategoryId(MAP_CATEGORY_NOT_CLASSIFIED);
				break;
			case CONCRETE_DATA_TYPE:
				member = context.createConcreteDataTypeRefSetMember(referencedComponentPair, null, null, null, null, null, moduleId, (SnomedConcreteDataTypeRefSet) refSet);
				break;
			case DESCRIPTION_TYPE:
				member = context.createDescriptionTypeRefSetMember(referencedComponentPair, moduleId, (SnomedRegularRefSet) refSet);
				break;
			case LANGUAGE:
				member = context.createLanguageRefSetMember(referencedComponentPair, null, moduleId, (SnomedStructuralRefSet) refSet);
				break;
			case MODULE_DEPENDENCY:
				throw new UnsupportedOperationException("Implementation error. Use " + SnomedModuleDependencyRefSetService.class.getSimpleName() + " instead."); //this should be created via SnomedModuleDependencyRefSetService
			case QUERY:
				member = context.createQueryRefSetMember(referencedComponentPair, null, moduleId, (SnomedRegularRefSet) refSet);
				break;
			case SIMPLE:
				member = context.createSimpleTypeRefSetMember(referencedComponentPair, moduleId, (SnomedRegularRefSet) refSet);
				break;
			case SIMPLE_MAP:
				member = context.createSimpleMapRefSetMember(referencedComponentPair, null, moduleId, (SnomedMappingRefSet) refSet);
				break;
			default:
				throw new IllegalArgumentException("Unknown reference set type: " + refSet.getType());
		}
		
		return checkNotNull(member, "member");
	}

	private String getModuleId(final SnomedRefSetEditingContext context) {
		return context.getSnomedEditingContext().getDefaultModuleConcept().getId();
	}

	private ComponentIdentifierPair<String> createReferencedComponentIdentifierPair(final SnomedRefSet refSet) {
		final short referencedComponentType = refSet.getReferencedComponentType();
		final String terminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(referencedComponentType);
		return ComponentIdentifierPair.createWithUncheckedComponentId(terminologyComponentId, null);
	}
	
}