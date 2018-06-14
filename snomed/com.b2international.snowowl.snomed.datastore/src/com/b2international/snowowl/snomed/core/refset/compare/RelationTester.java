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
package com.b2international.snowowl.snomed.core.refset.compare;

import java.util.List;
import java.util.Set;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongKeyMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.StatementFragment;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * This class implements a recursive search for relationships between the constrained and the master refset during the comparison of two reference sets.
 * 
 * 
 */
public class RelationTester {

	public boolean isRelated(String branch, String predicateId, String candidateId) {
		Set<String> visited = Sets.newHashSet();
		visited.add(candidateId);
		for (SnomedRelationship relationship : LinkageRefSetGenerator.getOutboundStatementsById(branch, candidateId)) {
			// Ignore 'Is a' relationships, they are in the subsumption category.
			if (!Concepts.IS_A.equals(relationship.getTypeId())) {
				if (predicateId.equals(relationship.getDestinationId())) {
					return true;
				} else if (isRelated(branch, predicateId, relationship.getDestinationId(), relationship.getTypeId(), visited)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isRelated(String branch, String predicateId, String candidateId, String relationshipTypeId) {
		for (SnomedRelationship relationship : LinkageRefSetGenerator.getActiveOutboundStatements(branch, candidateId, relationshipTypeId)) {
			// Ignore 'Is a' relationships, they are in the subsumption category.
			if (relationshipTypeId.equals(relationship.getTypeId())) {
				Set<String> visited = Sets.newHashSet();
				visited.add(candidateId);
				if (predicateId.equals(relationship.getDestinationId())) {
					return true;
				} else if (isRelated(branch, predicateId, relationship.getDestinationId(), relationshipTypeId, visited)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isRelated(String branch, String predicateId, String candidateId, String relationshipTypeId, Set<String> visitedIds) {
		if (!visitedIds.add(candidateId)) {
			return false;
		}
		
		for (SnomedRelationship relationship : LinkageRefSetGenerator.getActiveOutboundStatements(branch, candidateId, relationshipTypeId)) {
			if (relationshipTypeId.equals(relationship.getTypeId())) {
				String relationshipTargetId = relationship.getDestinationId();
				if (predicateId.equals(relationshipTargetId)) {
					return true;
				} else if (isRelated(branch, predicateId, relationshipTargetId, relationshipTypeId, visitedIds)) {
					return true;
				}
			}
		}
		return false;
	}

	
	private static final long IS_A = Long.parseLong(Concepts.IS_A) ;
	
	public static boolean isRelated(long predicateId, long candidateId, final LongKeyMap<List<StatementFragment>> statements) {
		
		final LongSet visited = PrimitiveSets.newLongOpenHashSet();
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

	public static boolean isRelated(long predicateId, long candidateId, long relationshipTypeId, final LongKeyMap<List<StatementFragment>> statements) {

		for (StatementFragment relationship : getStatements(candidateId, statements)) {
			// Ignore 'Is a' relationships, they are in the subsumption category.
			if (relationshipTypeId == relationship.getTypeId()) {
				
				final LongSet visited = PrimitiveSets.newLongOpenHashSet();
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

	public static boolean isRelated(long predicateId, long candidateId, long relationshipTypeId, final LongSet visitedIds, final LongKeyMap<List<StatementFragment>> statements) {
		
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
	
	
	private static List<StatementFragment> getStatements(final long sourceConceptId, final LongKeyMap<List<StatementFragment>> statements) {
		final List<StatementFragment> object = statements.get(sourceConceptId);
		return object != null ? object : Lists.<StatementFragment>newArrayList();
	}
	
	
}