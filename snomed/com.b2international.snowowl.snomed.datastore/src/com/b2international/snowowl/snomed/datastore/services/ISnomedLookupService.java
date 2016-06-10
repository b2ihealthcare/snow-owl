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
package com.b2international.snowowl.snomed.datastore.services;

import javax.annotation.Nullable;

import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.datastore.CaseSignificance;

/**
 * Lookup service for SNOMED&nbsp;CT ontology.
 *
 */
public interface ISnomedLookupService {

	/**
	 * Checks whether a relationship with the given relationship type exists between the two SNOMED&nbsp;CT concept. 
	 * Returns {@code true} if a relationship between the source and destination concepts given by their unique ID exists.
	 * Otherwise returns with {@code false}.  
	 * 
	 * @param sourceConceptId the source concept ID.
	 * @param destinationConceptId the destination concept ID.
	 * @param relationshipTypeId the relation type concept ID.
	 * @return return {@code true}, if there is a relationship between the two concepts, otherwise returns with {@code false}.
	 */
	boolean hasRelationship(final long sourceConceptId, final long destinationConceptId, final long relationshipTypeId);

	/**
	 * Checks whether a relationship with the given relationship type exists between the two SNOMED&nbsp;CT concept. 
	 * Returns {@code true} if a relationship between the source and destination concepts given by their unique ID exists.
	 * Otherwise returns with {@code false}.  
	 * 
	 * @param sourceConceptId the source concept ID.
	 * @param destinationConceptId the destination concept ID.
	 * @param relationshipTypeId the relation type concept ID.
	 * @return return {@code true}, if there is a relationship between the two concepts, otherwise returns with {@code false}.
	 */	
	boolean hasRelationship(final String sourceConceptId, final String destinationConceptId, final String relationshipTypeId);

	/**
	 * Returns the owner SNOMED&nbsp;CT concept ID associated with the given SNOMED&nbsp;CT description ID.
	 * Returns with {@code null} if not found.
	 * 
	 * @param descriptionId the unique ID of the SNOMED&nbsp;CT description.
	 * @return concept ID the particular description belongs to.
	 */
	@Nullable String getConceptId(final String descriptionId);

	/**
	 * Returns with an array all the description terms of the concept.
	 * 
	 * @param conceptId SNOMED CT id
	 * @return array of description terms.
	 */
	String[] getDescriptionTerms(final String conceptId);

	/**
	 * Returns with an array of description terms for a particular SNOMED&nbsp;CT concept given by its ID where the 
	 * description type concept ID matches with the specified one.
	 * 
	 * @param conceptId SNOMED&nbsp;CT concept ID.
	 * @param descriptionTypeConceptId the description type concept ID
	 * @return an array of description terms.
	 */
	String[] getDescriptionTerms(final long conceptId, final long descriptionTypeConceptId);

	/**
	 * Returns the preferred terms of a concept.
	 * @param conceptId the unique SNOMED&nbsp;CT ID of the concept.
	 * @return the preferred term of a SNOMED&nbsp;CT concept identified by its unique ID.
	 */
	String getPreferredTerm(final String conceptId);

	/**
	 * Returns with the term of the fully specified name of a SNOMED&nbsp;CT concept.
	 * @param conceptId the unique ID of the concept.
	 * @return the fully specified name of a concept.
	 */
	String getFullySpecifiedName(final long conceptId);

	/**
	 * Returns with the fully specified name of a SNOMED&nbsp;CT concept as a description.
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept.
	 * @return the fully specified name description of a concept.
	 */
	Description getFsnDescription(final long conceptId);

	/**
	 * Returns with the terms of all the synonym type descriptions of a SNOMED&nbsp;CT concept.
	 * @param conceptId the unique ID of a concept.
	 * @return an array of synonym terms.
	 */
	String[] getSynonyms(final long conceptId);

	/**
	 * Returns {@code true} if the term of the given concept exists, otherwise returns with {@code false}.
	 * 
	 * @param conceptId the unique ID of the SNOMED&nbsp;CT concept.
	 * @param termToMatch the term of one of the descriptions to match against.
	 * @param caseSensitivity the description case significance settings.
	 * @return {@code true} if the term of specified concept exists, otherwise returns {@code false}.
	 */
	boolean isDescriptionExist(final String conceptId, final String termToMatch, final CaseSignificance caseSensitivity);

	/**
	 * Returns {@code true} if the specific term of the given concept exists. Otherwise returns with {@code false}.
	 * 
	 * @param conceptId unique ID of the SNOMED&nbsp;CT concept. 
	 * @param termToMatch the term of one of the descriptions to match against.
	 * @param caseSensitivity the description case significance settings.
	 * @param descriptionTypeConceptId the description type concept ID.
	 * @return {@code true} if the term of specified concept exists, otherwise returns {@code false}.
	 */
	boolean isDescriptionExist(final long conceptId, String termToMatch, final CaseSignificance caseSensitivity, final long descriptionTypeConceptId);

	/**
	 * Returns {@code true} if the specific term of the given concept exists. Otherwise returns with {@code false}.
	 * 
	 * @param conceptId unique ID of the SNOMED&nbsp;CT concept. 
	 * @param termToMatch the term of one of the descriptions to match against.
	 * @param caseSensitivity the description case significance settings.
	 * @param descriptionTypeConceptId the description type concept ID.
	 * @return {@code true} if the term of specified concept exists, otherwise returns {@code false}.
	 */
	boolean isDescriptionExist(final String conceptId, final String termToMatch, final CaseSignificance caseSensitivity, final String descriptionTypeConceptId);

	/**
	 * Returns {@code true} two description terms matches based on the desired case significance.
	 * @param descriptionTerm the term to check.
	 * @param termToMatch the term to match.
	 * @param caseSensitivity the desired case significance settings.
	 * @return {@code true} if the two specified descriptions match, otherwise {@code false}.
	 */
	boolean descriptionTermMatches(final String descriptionTerm, final String termToMatch, final CaseSignificance caseSensitivity);

	/**
	 * Returns with the SNOMED&nbsp;CT concept identified by its unique ID.
	 * <br>This method may return with {@code null} if error occurred while creating the editing context or
	 * concept does not exists in the database with the specified SNOMED CT ID.
	 * 
	 * @param conceptId the unique ID of the concept.
	 * @return the SNOMED&nbsp;CT concept. 
	 */
	@Nullable Concept getConcept(final long conceptId);

	/**
	 * Returns with the SNOMED&nbsp;CT concept identified by its unique ID.
	 * <br>This method may return with {@code null} if error occurred while creating the editing context or
	 * concept does not exists in the database with the specified SNOMED CT ID.
	 * 
	 * @param conceptId the unique ID of the concept.
	 * @return the SNOMED&nbsp;CT concept. 
	 */
	@Nullable Concept getConceptById(final String conceptId);

	/**
	 * Return true if the SNOMED CT concept exist identified by its unique ID.
	 * 
	 * @param conceptId the unique ID of the concept.
	 * @return {@code true} if the SNOMED&nbsp;CT concept exists with the given ID, otherwise {@code false}.
	 */
	boolean isConceptExist(final long conceptId);

	/**
	 * Return true if the SNOMED CT concept exist identified by its unique ID.
	 * 
	 * @param conceptId the unique ID of the concept.
	 * @return {@code true} if the SNOMED&nbsp;CT concept exists with the given ID, otherwise {@code false}.
	 */
	boolean isConceptExist(final String conceptId);
	
	/**
	 * Generates and returns with a brand new non-existing SNOMED&nbsp;CT concept ID.
	 * @return a new non-existing concept ID.
	 */
	String generateNewConceptId();
	
	/**
	 * Generates and returns with a brand new non-existing SNOMED&nbsp;CT description ID.
	 * @return a new non-existing description ID.
	 */
	String generateNewDescriptionId();
	
	/**
	 * Generates and returns with a brand new non-existing SNOMED&nbsp;CT relationship ID.
	 * @return a new non-existing relationship ID.
	 */
	String generateNewRelationshipId();

}