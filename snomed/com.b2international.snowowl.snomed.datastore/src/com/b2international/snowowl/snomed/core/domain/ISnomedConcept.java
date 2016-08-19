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
package com.b2international.snowowl.snomed.core.domain;

import com.b2international.snowowl.core.domain.IComponentNode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Multimap;

/**
 * Represents a SNOMED CT concept.
 * <p>
 * If the component status is not active, additional information about the inactivation reason and associated concepts can also be retrieved from this
 * object.
 */
public interface ISnomedConcept extends SnomedCoreComponent, IComponentNode, DefinitionStatusProvider {

	/**
	 * Returns the subclass definition status of the concept.
	 * 
	 * @return {@link SubclassDefinitionStatus#DISJOINT_SUBCLASSES} if the subclasses form a disjoint union,
	 *         {@link SubclassDefinitionStatus#NON_DISJOINT_SUBCLASSES} otherwise
	 */
	SubclassDefinitionStatus getSubclassDefinitionStatus();

	/**
	 * Returns the concept's corresponding inactivation indicator member value.
	 * 
	 * @return the inactivation indicator value, or {@code null} if the concept is still active
	 */
	InactivationIndicator getInactivationIndicator();

	/**
	 * Returns association reference set member targets keyed by the association type.
	 * 
	 * @return related association targets, or {@code null} if the concept is still active
	 */
	Multimap<AssociationType, String> getAssociationTargets();

	/**
	 * Returns the fully specified name of the SNOMED CT Concept.
	 * 
	 * @return
	 */
	ISnomedDescription getFsn();
	
	/**
	 * Returns the preferred term of the SNOMED CT Concept.
	 * 
	 * @return
	 */
	ISnomedDescription getPt();

	/**
	 * Returns the descriptions of the SNOMED CT Concept.
	 * 
	 * @return
	 */
	SnomedDescriptions getDescriptions();
	
	/**
	 * Returns the relationships of the SNOMED CT Concept.
	 * 
	 * @return
	 */
	SnomedRelationships getRelationships();

	/**
	 * @return the ancestors of the SNOMED CT concept
	 */
	SnomedConcepts getAncestors();
	
	/**
	 * @return the descendants of the SNOMED CT concept
	 */
	SnomedConcepts getDescendants();

	/**
	 * @return the concept IDs of the ancestors
	 */
	@JsonIgnore
	long[] getAncestorIds();
	
	/**
	 * @return the concept IDs of the parents
	 */
	@JsonIgnore
	long[] getParentIds();

	/**
	 * @return the concept IDs of the stated parents
	 */
	@JsonIgnore
	long[] getStatedParentIds();
	
	/**
	 * @return the concept IDs of the stated ancestors
	 */
	@JsonIgnore
	long[] getStatedAncestorIds();

}
