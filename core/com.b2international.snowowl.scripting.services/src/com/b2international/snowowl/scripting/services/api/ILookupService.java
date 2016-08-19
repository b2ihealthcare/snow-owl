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

import java.util.Date;
import java.util.List;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;

/**
 * 
 * This service provides basic lookup operations to Snow Owl.
 * This service is SNOMED CT specific.
 * 
 *
 */
public interface ILookupService {
	
	List<Concept> getSupportedNamespaces();
	
	List<Concept> getSupportedModules();
	
	List<Concept> getSupportedModules(long namespaceConceptId);
	
	Concept getConcept(long conceptId);

	IComponent<Concept> getConceptComponent(long conceptId);
	
	SnomedConceptIndexEntry getConceptMini(long conceptId);
	
	Relationship getRelationship(long relationshipId);
	
	Description getDescription(long descriptionId);
	
	List<IComponent<Concept>> getRootConcepts();
	
	String getPreferredTerm(long conceptId, final String languageCode);
	
	Description getPreferredTermDescription(long conceptId, final String languageCode);
	
	String getFullySpecifiedNameTerm(long conceptId);
	
	Description getFullySpecifiedName(long conceptId);
	
	List<Description> getDescriptionTypes();
	
	List<Relationship> getRelationshipTypes();
	
	List<Concept> getConcreteDomainTypes();
	
	List<Description> getDescription(final long conceptId, final long descriptionTypeId, final String languageCode);
	
	List<String> getDescriptionTerm(final long conceptId, final long descriptionTypeId, final String languageCode);

	//TODO: ask Orsi or Akos for exact definition
	boolean isEffective(final long conceptId);

	//TODO: ask Orsi or Akos for exact definition
	boolean isActive(final long conceptId);
	
	/**
	 * Returns the active concept that replaced the deactivated concept specified.
	 * If an active concept is specified the method returns the concept itself.
	 * If no active concept is found, the method returns null.
	 * 
	 * @param inactiveConceptId
	 * @return the active {@link Concept}
	 * @throws SnowowlServiceException in case the deactivated concept not found
	 */
	Concept getActiveConcept(final long inactiveConcept) throws SnowowlServiceException;
	
	/**
	 * Returns the last effective concept before the date specified. The returned concept can be 
	 * inactive.
	 * @param conceptId
	 * @param effectiveDate
	 * @return the last effective {@link Concept} before the date
	 */
	Concept getConcept(final long conceptId, final Date effectiveDate);
	
	/**
	 * Returns the history of a {@link Concept} as an ordered list.  
	 * The first element of the list is the latest revision.
	 * @param conceptId
	 * @return
	 */
	List<Concept> getConceptHistory(final long conceptId);
}