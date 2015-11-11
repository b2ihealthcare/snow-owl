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
package com.b2international.snowowl.snomed.refset.core.compare;

import java.util.List;
import java.util.Set;

import bak.pcj.map.LongKeyMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.SnomedClientStatementBrowser;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This class implements a recursive search for relationships between the constrained and the master refset during the comparison of two reference sets.
 * 
 * 
 */
public class RelationTester {

	private final SnomedClientStatementBrowser statementBrowser;

	public RelationTester() {
		this.statementBrowser = ApplicationContext.getInstance().getService(SnomedClientStatementBrowser.class);
	}

	public boolean isRelated(String predicateId, String candidateId) {
		Set<String> visited = Sets.newHashSet();
		visited.add(candidateId);
		List<SnomedRelationshipIndexEntry> relationships = statementBrowser.getOutboundStatementsById(candidateId);
		for (SnomedRelationshipIndexEntry relationship : relationships) {
			// Ignore 'Is a' relationships, they are in the subsumption category.
			if (!Concepts.IS_A.equals(relationship.getAttributeId())) {
				if (predicateId.equals(relationship.getValueId())) {
					return true;
				} else if (isRelated(predicateId, relationship.getValueId(), relationship.getAttributeId(), visited)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isRelated(String predicateId, String candidateId, String relationshipTypeId) {
		List<SnomedRelationshipIndexEntry> relationships = statementBrowser.getOutboundStatementsById(candidateId);
		for (SnomedRelationshipIndexEntry relationship : relationships) {
			// Ignore 'Is a' relationships, they are in the subsumption category.
			if (relationshipTypeId.equals(relationship.getAttributeId())) {
				Set<String> visited = Sets.newHashSet();
				visited.add(candidateId);
				if (predicateId.equals(relationship.getValueId())) {
					return true;
				} else if (isRelated(predicateId, relationship.getValueId(), relationshipTypeId, visited)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isRelated(String predicateId, String candidateId, String relationshipTypeId, Set<String> visitedIds) {
		if (visitedIds.contains(candidateId)) {
			return false;
		} else {
			visitedIds.add(candidateId);
		}
		List<SnomedRelationshipIndexEntry> relationships = statementBrowser.getOutboundStatementsById(candidateId);
		for (SnomedRelationshipIndexEntry relationship : relationships) {
			if (relationshipTypeId.equals(relationship.getAttributeId())) {
				String relationshipTargetId = relationship.getValueId();
				if (predicateId.equals(relationshipTargetId)) {
					return true;
				} else if (isRelated(predicateId, relationshipTargetId, relationshipTypeId, visitedIds)) {
					return true;
				}
			}
		}
		return false;
	}

	
	private static final long IS_A = Long.parseLong(Concepts.IS_A) ;
	
	public static boolean isRelated(long predicateId, long candidateId, final LongKeyMap statements) {
		
		final LongSet visited = new LongOpenHashSet();
		visited.add(candidateId);
		
		for (final StatementFragment relationship : getStatements(candidateId, statements)) {
			// Ignore 'Is a' relationships, they are in the subsumption category.
			if (IS_A != relationship.getTypeId()) {
				if (predicateId == relationship.getDestinationId()) {
					return true;
				} else if (isRelated(predicateId, relationship.getDestinationId(), relationship.getTypeId(), visited, statements)) {
					return true;
				}
			}
		}
		
		return false;
	}

	public static boolean isRelated(long predicateId, long candidateId, long relationshipTypeId, final LongKeyMap statements) {

		for (StatementFragment relationship : getStatements(candidateId, statements)) {
			// Ignore 'Is a' relationships, they are in the subsumption category.
			if (relationshipTypeId == relationship.getTypeId()) {
				
				final LongSet visited = new LongOpenHashSet();
				visited.add(candidateId);
				
				if (predicateId == relationship.getDestinationId()) {
					return true;
				} else if (isRelated(predicateId, relationship.getDestinationId(), relationshipTypeId, visited, statements)) {
					return true;
				}
				
			}
		}
		
		return false;
	}

	public static boolean isRelated(long predicateId, long candidateId, long relationshipTypeId, final LongSet visitedIds, final LongKeyMap statements) {
		
		if (visitedIds.contains(candidateId)) {
			return false;
		} else {
			visitedIds.add(candidateId);
		}
		
		for (final StatementFragment relationship : getStatements(candidateId, statements)) {
			
			if (relationshipTypeId == relationship.getTypeId()) {
				
				final long relationshipTargetId = relationship.getDestinationId();
				
				if (predicateId == relationshipTargetId) {
					return true;
				} else if (isRelated(predicateId, relationshipTargetId, relationshipTypeId, visitedIds, statements)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	@SuppressWarnings("unchecked")
	private static List<StatementFragment> getStatements(final long sourceConceptId, final LongKeyMap statements) {
		final Object object = statements.get(sourceConceptId);
		return (List<StatementFragment>) (object instanceof List<?> ? object : Lists.newArrayList());
	}
	
	
}