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

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSets;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

/**
 * Processes a {@link ReferenceSetConceptSetDefinition} to return the actual SNOMED CT concept identifiers
 * contained in the set.
 * 
 */
public class ReferenceSetConceptSetProcessor extends ConceptSetProcessor<ReferenceSetConceptSetDefinition> {

	public ReferenceSetConceptSetProcessor(final ReferenceSetConceptSetDefinition conceptSetDefinition) {
		super(conceptSetDefinition);
	}
	
	@Override
	public Iterator<SnomedConceptDocument> getConcepts() {
		final Set<SnomedConceptDocument> memberConcepts = getReferenceSetMemberReferencedConceptSet();
		return memberConcepts.iterator();
	}

	private Set<SnomedConceptDocument> getReferenceSetMemberReferencedConceptSet() {
		final String refSetIdentifierConceptId = conceptSetDefinition.getRefSetIdentifierConceptId();
		final SnomedReferenceSets refSets = SnomedRequests.prepareSearchRefSet()
			.setLimit(1)
			.filterByActive(true)
			.setExpand("members(expand(referencedComponent()))")
			.filterByReferencedComponentType(SnomedTerminologyComponentConstants.CONCEPT)
			.setComponentIds(Collections.singleton(refSetIdentifierConceptId))
			.build(getBranch())
			.execute(getBus())
			.getSync();
		
		final SnomedReferenceSet refSet = Iterables.getOnlyElement(refSets, null); 
		return refSet == null ? Collections.<SnomedConceptDocument>emptySet() : FluentIterable.from(refSet.getMembers()).transform(new Function<SnomedReferenceSetMember, SnomedConceptDocument>() {
			@Override
			public SnomedConceptDocument apply(SnomedReferenceSetMember input) {
				return SnomedConceptDocument.builder((ISnomedConcept) input.getReferencedComponent()).build();
			}
		}).toSet();
	}

	private IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}

	private String getBranch() {
		return BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE).getPath();
	}
	
	@Override
	public boolean contains(final SnomedConceptDocument concept) {
		return SnomedRequests.prepareSearchMember()
				.setLimit(0)
				.filterByActive(true)
				.filterByReferencedComponent(concept.getId())
				.filterByRefSet(conceptSetDefinition.getRefSetIdentifierConceptId())
				.build(getBranch())
				.execute(getBus())
				.getSync().getTotal() > 0;
	}
}