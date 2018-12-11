/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.classification;

import java.io.Serializable;
import java.util.List;

import com.b2international.snowowl.snomed.reasoner.classification.entry.ConcreteDomainChangeEntry;
import com.b2international.snowowl.snomed.reasoner.classification.entry.RelationshipChangeEntry;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * Captures a reasoner change set for review purposes.
 */
public class GetResultResponseChanges implements Serializable {

	private static final long serialVersionUID = 1L;

	private long elapsedTimeMillis;
	private final List<AbstractEquivalenceSet> equivalenceSets;
	private final List<RelationshipChangeEntry> relationshipEntries;
	private final List<ConcreteDomainChangeEntry> concreteDomainEntries;

	//@JsonCreator
	//private GetResultResponseChanges(@JsonProperty List<RelationshipChangeEntry> relationshipEntries) {
	//	this(0L, ImmutableList.of(), relationshipEntries, ImmutableList.of());	
	//}
	
	/**
	 * Creates a new change set with the specified arguments.
	 * @param elapsedTimeMillis elapsed time measured on the server in milliseconds
	 * @param equivalenceSets the list of equivalence sets (including both regular and unsatisfiable ones)
	 * @param relationshipEntries the list of inferred or redundant SNOMED CT relationship entries
	 * @param concreteDomainEntries the list of inferred or redundant SNOMED CT concrete domain reference set member entries
	 */
	@JsonCreator
	public GetResultResponseChanges(@JsonProperty("elapsedTimeMillis") long elapsedTimeMillis, 
			@JsonProperty("equivalenceSets") List<? extends AbstractEquivalenceSet> equivalenceSets,
			@JsonProperty("relationshipEntries") List<RelationshipChangeEntry> relationshipEntries,
			@JsonProperty("concreteDomainEntries") List<ConcreteDomainChangeEntry> concreteDomainEntries) {

		this.elapsedTimeMillis = elapsedTimeMillis;
		this.equivalenceSets = ImmutableList.copyOf(equivalenceSets);
		this.relationshipEntries = ImmutableList.copyOf(relationshipEntries);
		this.concreteDomainEntries = ImmutableList.copyOf(concreteDomainEntries);
	}

	/**
	 * @return elapsed time measured on the server in milliseconds
	 */
	public long getElapsedTimeMillis() {
		return elapsedTimeMillis;
	}

	/**
	 * @return the list of equivalence sets (including both regular and unsatisfiable ones)
	 */
	public List<AbstractEquivalenceSet> getEquivalenceSets() {
		return equivalenceSets;
	}

	/**
	 * @return the list of inferred or redundant SNOMED CT relationship entries
	 */
	public List<RelationshipChangeEntry> getRelationshipEntries() {
		return relationshipEntries;
	}

	/**
	 * @return the list of inferred or redundant SNOMED CT concrete domain reference set member entries
	 */
	public List<ConcreteDomainChangeEntry> getConcreteDomainElementEntries() {
		return concreteDomainEntries;
	}
}
