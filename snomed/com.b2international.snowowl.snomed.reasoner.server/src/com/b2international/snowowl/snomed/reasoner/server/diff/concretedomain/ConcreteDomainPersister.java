/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.ConcreteDomainFragment;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.model.SnomedModelExtensions;
import com.b2international.snowowl.snomed.reasoner.server.NamespaceAndMolduleAssigner;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChange.Nature;
import com.b2international.snowowl.snomed.reasoner.server.diff.OntologyChangeProcessor;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;

/**
 * Applies changes related to concrete domain elements using the specified SNOMED CT editing context.
 */
public class ConcreteDomainPersister extends OntologyChangeProcessor<ConcreteDomainFragment> {

	private final Nature nature;
	private final SnomedRefSetEditingContext refSetEditingContext;
	
	public ConcreteDomainPersister(final SnomedEditingContext context, final Nature nature, NamespaceAndMolduleAssigner namespaceAndModuleAssigner) {
		super(namespaceAndModuleAssigner);
		this.nature = nature;
		this.refSetEditingContext = context.getRefSetEditingContext();
	}
	
	@Override
	protected void handleRemovedSubject(final String conceptId, final ConcreteDomainFragment removedEntry) {
		
		if (!Nature.REMOVE.equals(nature)) {
			return;
		}
		
		final SnomedConcreteDataTypeRefSetMember existingMember = (SnomedConcreteDataTypeRefSetMember) refSetEditingContext.lookup(removedEntry.getStorageKey());
		SnomedModelExtensions.removeOrDeactivate(existingMember);
	}
	
	@Override
	protected void beforeHandleAddedSubjects(Set<String> conceptIds) {
		//pre-allocate namespaces for the new concrete domains per each concept
		getRelationshipNamespaceAssigner().allocateConcreteDomainModules(conceptIds, refSetEditingContext.getSnomedEditingContext());
	}
	
	@Override
	protected void handleAddedSubject(final String conceptId, final ConcreteDomainFragment addedEntry) {
		
		if (!Nature.ADD.equals(nature)) {
			return;
		}
		
		final Concept moduleConcept = getRelationshipNamespaceAssigner().getConcreteDomainModule(conceptId, refSetEditingContext.getBranchPath());
		final SnomedConcreteDataTypeRefSet concreteDataTypeRefSet = refSetEditingContext.lookup(Long.toString(addedEntry.getRefSetId()), SnomedConcreteDataTypeRefSet.class);
		final SnomedConcreteDataTypeRefSetMember refSetMember = refSetEditingContext.createConcreteDataTypeRefSetMember(
				conceptId,
				nullIfUnset(addedEntry.getUomId()),
				Concepts.CD_EQUAL,
				SnomedRefSetUtil.deserializeValue(addedEntry.getDataType(), addedEntry.getValue()), 
				Concepts.INFERRED_RELATIONSHIP, 
				addedEntry.getLabel(), 
				moduleConcept.getId(), 
				concreteDataTypeRefSet);
		
		final Concept referencedComponent = refSetEditingContext.lookup(conceptId, Concept.class);
		referencedComponent.getConcreteDomainRefSetMembers().add(refSetMember);
	}

	private String nullIfUnset(final long uomId) {
		return (uomId == ConcreteDomainFragment.UNSET_UOM_ID) ? null : Long.toString(uomId);
	}
}
