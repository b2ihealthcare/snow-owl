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
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetIndexEntry;
import com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition;
import com.google.common.collect.Sets;

/**
 * Processes a {@link ReferenceSetConceptSetDefinition} to return the actual SNOMED CT concept identifiers
 * contained in the set.
 * 
 */
public class ReferenceSetConceptSetProcessor extends ConceptSetProcessor<ReferenceSetConceptSetDefinition> {

	private final SnomedClientRefSetBrowser refSetBrowser;

	public ReferenceSetConceptSetProcessor(final ReferenceSetConceptSetDefinition conceptSetDefinition, final SnomedClientRefSetBrowser refSetBrowser) {
		super(conceptSetDefinition);
		this.refSetBrowser = refSetBrowser;
	}
	
	@Override
	public Iterator<SnomedConceptIndexEntry> getConcepts() {
		final Set<SnomedConceptIndexEntry> memberConcepts = getReferenceSetMemberReferencedConceptSet();
		return memberConcepts.iterator();
	}

	private Set<SnomedConceptIndexEntry> getReferenceSetMemberReferencedConceptSet() {
		final String refSetIdentifierConceptId = conceptSetDefinition.getRefSetIdentifierConceptId();
		final SnomedRefSetIndexEntry refSet = refSetBrowser.getRefSet(refSetIdentifierConceptId);
		
		if (refSet == null) {
			return Collections.emptySet();
		}
		
		if (refSet.getReferencedComponentType() != SnomedTerminologyComponentConstants.CONCEPT_NUMBER) {
			throw new IllegalArgumentException(String.format("The reference set %s does not have concepts as its referenced components.", 
					refSetIdentifierConceptId));
		}
		
		final Collection<SnomedConceptIndexEntry> memberConcepts = refSetBrowser.getMemberConcepts(refSetIdentifierConceptId);
		return Sets.newHashSet(memberConcepts);
	}
	
	@Override
	public boolean contains(final SnomedConceptIndexEntry concept) {
		return refSetBrowser.isReferenced(conceptSetDefinition.getRefSetIdentifierConceptId(), concept.getId());
	}
}