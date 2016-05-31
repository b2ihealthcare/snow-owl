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

import java.util.Iterator;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition;

/**
 * Processes a {@link RelationshipConceptSetDefinition} to return the actual SNOMED CT concept identifiers
 * contained in the set.
 * 
 */
public class RelationshipConceptSetProcessor extends ConceptSetProcessor<RelationshipConceptSetDefinition> {

	public RelationshipConceptSetProcessor(final RelationshipConceptSetDefinition conceptSetDefinition) {
		super(conceptSetDefinition);
	}
	
	@Override
	public Iterator<SnomedConceptDocument> getConcepts() {
		throw new UnsupportedOperationException("Relationship-based concept sets are not allowed in predicates, only domains.");
	}
	
	@Override
	public boolean contains(final SnomedConceptDocument concept) {
		final String sourceId = concept.getId();
		final String typeId = conceptSetDefinition.getTypeConceptId();
		final String destinationId = conceptSetDefinition.getDestinationConceptId();
		return SnomedRequests.prepareSearchRelationship()
				.setLimit(0)
				.filterByActive(true)
				.filterBySource(sourceId)
				.filterByType(typeId)
				.filterByDestination(destinationId)
				.build(BranchPathUtils.createActivePath(SnomedPackage.eINSTANCE).getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync().getTotal() > 0;
	}
}