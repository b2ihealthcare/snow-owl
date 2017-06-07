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
package com.b2international.snowowl.snomed.reasoner.server.diff.concretedomain;

import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndModuleAssigner;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;

/**
 * Applies changes related to concrete domain elements using the specified SNOMED CT editing context.
 */
public class ConcreteDomainPersister {

	private final SnomedRefSetEditingContext context;
	private final NamespaceAndModuleAssigner namespaceAndModuleAssigner;

	public ConcreteDomainPersister(final SnomedEditingContext context, NamespaceAndModuleAssigner namespaceAndModuleAssigner) {
		this.context = context.getRefSetEditingContext();
		this.namespaceAndModuleAssigner = namespaceAndModuleAssigner;
	}
	
	public void handleRemovedSubject(final String conceptId, final ConcreteDomainFragment removedEntry) {
		final SnomedConcreteDataTypeRefSetMember existingMember = (SnomedConcreteDataTypeRefSetMember) context.lookup(removedEntry.getStorageKey());
		SnomedModelExtensions.removeOrDeactivate(existingMember);
	}
	
	public void handleAddedSubject(final String conceptId, final ConcreteDomainFragment addedEntry) {
		final Concept moduleConcept = namespaceAndModuleAssigner.getConcreteDomainModule(conceptId);
		
		final SnomedConcreteDataTypeRefSet concreteDataTypeRefSet = context.lookup(Long.toString(addedEntry.getRefSetId()), SnomedConcreteDataTypeRefSet.class);
		final SnomedConcreteDataTypeRefSetMember refSetMember = context.createConcreteDataTypeRefSetMember(
				conceptId,
				nullIfUnset(addedEntry.getUomId()),
				Concepts.CD_EQUAL,
				SnomedRefSetUtil.deserializeValue(addedEntry.getDataType(), addedEntry.getValue()), 
				Concepts.INFERRED_RELATIONSHIP, 
				addedEntry.getLabel(), 
				moduleConcept.getId(), 
				concreteDataTypeRefSet);
		
		final Concept referencedComponent = context.lookup(conceptId, Concept.class);
		referencedComponent.getConcreteDomainRefSetMembers().add(refSetMember);
	}

	private String nullIfUnset(final long uomId) {
		return (uomId == ConcreteDomainFragment.UNSET_UOM_ID) ? null : Long.toString(uomId);
	}
}
