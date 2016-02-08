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
package com.b2international.snowowl.snomed.core.store;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.eclipse.emf.cdo.CDOObject;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * @since 4.5
 */
public abstract class SnomedReferenceSetBuilder<B extends SnomedReferenceSetBuilder<B, T>, T extends CDOObject> extends SnomedBaseComponentBuilder<B, T> {

	private String identifierConceptId;
	protected String referencedComponentType;
	protected SnomedRefSetType type;

	protected SnomedReferenceSetBuilder() {}

	/**
	 * Specifies the component type referenced by the created SNOMED CT Reference Set.
	 * 
	 * @param referencedComponentType
	 * @return
	 */
	public B setReferencedComponentType(final String referencedComponentType) {
		this.referencedComponentType = referencedComponentType;
		return getSelf();
	}

	/**
	 * Specifies the SNOMED CT Identifier Concept ID of this SNOMED CT Reference Set.
	 * 
	 * @param identifierConceptId
	 * @return
	 */
	public B setIdentifierConceptId(final String identifierConceptId) {
		this.identifierConceptId = identifierConceptId;
		return getSelf();
	}

	/**
	 * Specifies the type of the new SNOMED CT Reference Set.
	 * 
	 * @param type - the type of the refset
	 * @return
	 */
	public B setType(final SnomedRefSetType type) {
		this.type = type;
		return getSelf();
	}

	@Override
	@OverridingMethodsMustInvokeSuper
	protected void init(final T component, final TransactionContext context) {
		checkNotNull(identifierConceptId, "Specify the identifier concept ID");
		checkNotNull(referencedComponentType, "Specify the referenced component type");
		checkNotNull(type, "Specify the reference set type");

		if (component instanceof SnomedRefSet) {
			final SnomedRefSet refSet = (SnomedRefSet) component;
			
			final CoreTerminologyBroker terminologies = context.service(CoreTerminologyBroker.class);
			refSet.setType(type);
			refSet.setReferencedComponentType(terminologies.getTerminologyComponentIdAsShort(referencedComponentType));
			refSet.setIdentifierId(identifierConceptId);
		}
	}
}
