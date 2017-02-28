/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.store;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;

/**
 * @since 5.0
 */
public final class SnomedStructuralReferenceSetBuilder extends SnomedBaseComponentBuilder<SnomedStructuralReferenceSetBuilder, SnomedStructuralRefSet> {

	private String referencedComponentType = SnomedTerminologyComponentConstants.CONCEPT;
	private String identifierConceptId;
	private SnomedRefSetType type;

	protected SnomedStructuralReferenceSetBuilder() {}

	/**
	 * Specifies the component type referenced by the created SNOMED CT Reference Set. It is CONCEPT by default.
	 * 
	 * @param referencedComponentType
	 * @return
	 */
	public SnomedStructuralReferenceSetBuilder withReferencedComponentType(final String referencedComponentType) {
		this.referencedComponentType = referencedComponentType;
		return getSelf();
	}

	/**
	 * Specifies the SNOMED CT Identifier Concept ID of this SNOMED CT Reference Set.
	 * 
	 * @param identifierConceptId
	 * @return
	 */
	public SnomedStructuralReferenceSetBuilder withIdentifierConceptId(final String identifierConceptId) {
		this.identifierConceptId = identifierConceptId;
		return getSelf();
	}

	/**
	 * Specifies the type of the new SNOMED CT Reference Set.
	 * 
	 * @param type - the type of the refset
	 * @return
	 */
	public SnomedStructuralReferenceSetBuilder withType(final SnomedRefSetType type) {
		this.type = type;
		return getSelf();
	}

	@Override
	protected SnomedStructuralRefSet create() {
		return SnomedRefSetFactory.eINSTANCE.createSnomedStructuralRefSet();
	}

	@Override
	protected void init(final SnomedStructuralRefSet component, final TransactionContext context) {
		checkNotNull(identifierConceptId, "Specify the identifier concept ID");
		checkNotNull(referencedComponentType, "Specify the referenced component type");
		final CoreTerminologyBroker terminologies = context.service(CoreTerminologyBroker.class);
		component.setType(type);
		component.setReferencedComponentType(terminologies.getTerminologyComponentIdAsShort(referencedComponentType));
		component.setIdentifierId(identifierConceptId);
	}

}
