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

import java.util.Collection;
import java.util.Iterator;

import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.google.common.collect.Iterators;

/**
 * Processes a {@link HierarchyConceptSetDefinition} to return the actual SNOMED CT concept identifiers
 * contained in the set.
 * 
 */
public class HierarchyConceptSetProcessor extends ConceptSetProcessor<HierarchyConceptSetDefinition> {

	private final IClientTerminologyBrowser<SnomedConceptIndexEntry, String> terminologyBrowser;

	public HierarchyConceptSetProcessor(HierarchyConceptSetDefinition conceptSetDefinition, IClientTerminologyBrowser<SnomedConceptIndexEntry, String> terminologyBrowser) {
		super(conceptSetDefinition);
		this.terminologyBrowser = terminologyBrowser;
	}
	
	@Override
	public Iterator<SnomedConceptIndexEntry> getConcepts() {
		if (conceptSetDefinition.getFocusConceptId() == null) {
			System.out.println("Null focus concept ID for: " + conceptSetDefinition);
			return Iterators.<SnomedConceptIndexEntry>emptyIterator();
		}
		
		SnomedConceptIndexEntry focusConcept = terminologyBrowser.getConcept(conceptSetDefinition.getFocusConceptId());
		
		if (focusConcept == null) {
			return Iterators.<SnomedConceptIndexEntry>emptyIterator();
		}
			
		switch (conceptSetDefinition.getInclusionType()) {
			case SELF:
				return Iterators.singletonIterator(focusConcept);
			case DESCENDANT:
				Collection<SnomedConceptIndexEntry> allSubTypes = terminologyBrowser.getAllSubTypes(focusConcept);
				return allSubTypes.iterator();
			case SELF_OR_DESCENDANT:
				allSubTypes = terminologyBrowser.getAllSubTypes(focusConcept);
				return Iterators.concat(Iterators.singletonIterator(focusConcept), allSubTypes.iterator());
			default:
				throw new IllegalArgumentException("Unexpected hierarchy inclusion type: " + conceptSetDefinition.getInclusionType());
		}
	}
	
	@Override
	public boolean contains(SnomedConceptIndexEntry concept) {
		final String focusConceptId = conceptSetDefinition.getFocusConceptId();
		
		//if the rule applies to the whole SNOMED CT terminology
		if (Concepts.ROOT_CONCEPT.equals(focusConceptId)) {
			return true;
		}
		
		//if the concept is a non existing one
		if (null == concept) {
			return false;
		}
		
		SnomedConceptIndexEntry focusConcept = terminologyBrowser.getConcept(focusConceptId);
		
		if (focusConcept == null) {
			return false;
		}
		
		switch (conceptSetDefinition.getInclusionType()) {
			case SELF:
				return focusConcept.equals(concept);
			case DESCENDANT:
				return terminologyBrowser.isSuperTypeOf(focusConcept, concept);
//				Collection<SnomedConceptIndexEntry> allSubTypes = terminologyBrowser.getAllSubTypes(focusConcept);
//				return allSubTypes.contains(concept);
			case SELF_OR_DESCENDANT:
				if (focusConcept.equals(concept)) {
					return true;
				} else {
					return terminologyBrowser.isSuperTypeOf(focusConcept, concept);
//					return terminologyBrowser.getAllSubTypes(focusConcept).contains(concept);
				}
			default:
				throw new IllegalArgumentException("Unexpected hierarchy inclusion type: " + conceptSetDefinition.getInclusionType());
		}
	}
}