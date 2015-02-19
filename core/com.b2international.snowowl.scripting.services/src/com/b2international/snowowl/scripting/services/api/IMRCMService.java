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
package com.b2international.snowowl.scripting.services.api;

import java.util.List;

import com.b2international.snowowl.snomed.Concept;

/**
 * This service provides access to the Machine Readable Concept Model (MRCM) engine of Snow Owl.
 * This service is SNOMED CT specific.
 * 
 *
 */
public interface IMRCMService {
	
	List<Long> getSanctionedRelationshipTypes(long conceptId);
	List<Concept> getSanctionedTargetConcepts(long conceptId, long relationshipId);
	List<Long> getSanctionedDescriptionTypes(long conceptId);
	List<Long> getSanctionedConcreteDomainTypes(long conceptId);
	
	List<Long> getUnSanctionedRelationshipTypes(long conceptId);
	List<Concept> getUnSanctionedTargetConcepts(long conceptId, long relationshipId);
	List<Long> getUnSanctionedDescriptionTypes(long conceptId);
	List<Long> getUnSanctionedConcreteDomainTypes(long conceptId);
	
	boolean isSanctioned(long sourceConceptId, long relationshipTypeId, long targetConceptId);
	boolean isSanctionedDescription(long sourceConceptId, long descriptionTypeId);
	boolean isSanctionedConcreteDomain(long sourceConceptId, long concreteDomainType); //?
	
	/**
	 * Returns true if the concept is valid based on the available MRCM rules.
	 *  
	 * @param concept
	 * @return
	 */
	boolean isValid(Concept concept);
	
	/**
	 * Returns a list of human readable MRCM validation results.
	 * 
	 * @param concept
	 * @return
	 */
	List<String> validate(Concept concept);

}