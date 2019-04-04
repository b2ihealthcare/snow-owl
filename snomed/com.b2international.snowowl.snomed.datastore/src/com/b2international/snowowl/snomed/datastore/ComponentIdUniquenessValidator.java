/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Suppliers.memoize;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import com.b2international.snowowl.snomed.Annotatable;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifier;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.base.Supplier;

/**
 * Class to validate the uniqueness of a SNOMED CT component's ID, and replace it if needed.
 * @deprecated - new ID gen services ensure unique IDs to be generated among all editingContexts
 */
final class ComponentIdUniquenessValidator {
	
	private final SnomedEditingContext editingContext;
	private final Collection<String> newComponentIdsInTransaction;
	private final Supplier<Iterable<SnomedRefSet>> newRefSetsSupplier;

	/*package*/ ComponentIdUniquenessValidator(final SnomedEditingContext editingContext) {
		this.editingContext = checkNotNull(editingContext, "editingContext");
		newComponentIdsInTransaction = newHashSet();
		newRefSetsSupplier = memoize(new Supplier<Iterable<SnomedRefSet>>() {
			public Iterable<SnomedRefSet> get() {
				return filter(editingContext.getTransaction().getNewObjects().values(), SnomedRefSet.class);
			}
		});
	}
	
	/**
	 * Validates the uniqueness of a SNOMED CT component's ID, and replaces it
	 * if it turns out to be a duplicate.
	 * 
	 * @param component
	 *            the SNOMED CT component (concept, relationship, description)
	 *            to validate
	 *            
	 * @return {@code true} if the component ID is unique, {@code false} otherwise
	 */
	public boolean validateAndReplaceComponentId(final Component component) {
		
		if (!validateComponentId(component)) {
			
			final SnomedIdentifier originalIdentifier = SnomedIdentifiers.create(component.getId());
			String newComponentId = editingContext.generateComponentId(originalIdentifier.getComponentCategory(), originalIdentifier.getNamespace());
			
			while (!validateComponentId(newComponentId)) {
				newComponentId = editingContext.generateComponentId(originalIdentifier.getComponentCategory(), originalIdentifier.getNamespace());
			}
			
			//update reference set ID as well if concept ID is not unique
			if (component instanceof Concept) {
				for (final SnomedRefSet refSet : newRefSetsSupplier.get()) {
					if (refSet.getIdentifierId().equals(component.getId())) {
						refSet.setIdentifierId(newComponentId);
						break;
					}
				} 
			}
			
			component.setId(newComponentId);
			
			if (component instanceof Annotatable) {
				updateConcreteDomainMembers((Annotatable) component, newComponentId);
			}
			
			if (component instanceof Description) {
				updateLanguageRefSetMembers((Description) component, newComponentId);
			}
			
			// XXX: It is not expected that inactivation reason and association members are added to a brand new component with a generated ID
			
			validateAndReplaceComponentId(component);
			return false;
			
		} else {
			newComponentIdsInTransaction.add(component.getId());
			return true;
		}
	}

	private void updateConcreteDomainMembers(final Annotatable annotatableComponent, final String newComponentId) {
		for (final SnomedConcreteDataTypeRefSetMember member : annotatableComponent.getConcreteDomainRefSetMembers()) {
			member.setReferencedComponentId(newComponentId);
		}
	}

	private void updateLanguageRefSetMembers(final Description description, final String newDescriptionId) {
		for (final SnomedLanguageRefSetMember member : description.getLanguageRefSetMembers()) {
			member.setReferencedComponentId(newDescriptionId);
		}
	}

	/**
	 * Validates the uniqueness of a SNOMED CT component's ID.
	 * 
	 * @param component
	 *            the SNOMED CT component to validate
	 * 
	 * @return {@code true} if the component ID is unique, {@code false}
	 *         otherwise
	 */
	private boolean validateComponentId(final Component component) {
		return validateComponentId(component.getId());
	}

	private boolean validateComponentId(final String componentId) {
		return isUniqueInTransaction(componentId);
	}

	private boolean isUniqueInTransaction(final String newComponentId) {
		return !newComponentIdsInTransaction.contains(newComponentId);
	}

}