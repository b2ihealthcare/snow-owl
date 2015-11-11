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

import com.b2international.snowowl.datastore.index.IndexQueryBuilder;
import com.b2international.snowowl.datastore.index.IndexUtils;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedRelationshipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition;

/**
 * Processes a {@link RelationshipConceptSetDefinition} to return the actual SNOMED CT concept identifiers
 * contained in the set.
 * 
 */
public class RelationshipConceptSetProcessor extends ConceptSetProcessor<RelationshipConceptSetDefinition> {

	private static final class SnomedRelationshipIndexQueryAdapterExtension extends SnomedRelationshipIndexQueryAdapter {

		private static final long serialVersionUID = 1L;
		
		private final String typeId;

		private final String destinationId;

		private SnomedRelationshipIndexQueryAdapterExtension(final String sourceId, final String typeId, final String destinationId) {
			super(sourceId, SEARCH_SOURCE_ID | SEARCH_ACTIVE_RELATIONSHIPS_ONLY);
			this.typeId = typeId;
			this.destinationId = destinationId;
		}

		@Override
		protected IndexQueryBuilder createIndexQueryBuilder() {
			return super.createIndexQueryBuilder()
					.require(SnomedMappings.newQuery().relationshipType(typeId).matchAll())
					.requireExactTerm(SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID, IndexUtils.longToPrefixCoded(destinationId));
		}
	}

	private final SnomedClientIndexService indexService;

	public RelationshipConceptSetProcessor(final RelationshipConceptSetDefinition conceptSetDefinition, final SnomedClientIndexService indexService) {
		super(conceptSetDefinition);
		this.indexService = indexService;
	}
	
	@Override
	public Iterator<SnomedConceptIndexEntry> getConcepts() {
		throw new UnsupportedOperationException("Relationship-based concept sets are not allowed in predicates, only domains.");
	}
	
	@Override
	public boolean contains(final SnomedConceptIndexEntry concept) {
		
		final SnomedRelationshipIndexQueryAdapterExtension adapter = new SnomedRelationshipIndexQueryAdapterExtension(concept.getId(),
				conceptSetDefinition.getTypeConceptId(),
				conceptSetDefinition.getDestinationConceptId());
		
		return indexService.getHitCount(adapter) > 0;
	}
}