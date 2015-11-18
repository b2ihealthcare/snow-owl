/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.scripting.services.api;

import java.util.Collection;
import java.util.List;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;

/**
 * This service provides functionality to navigate the SNOMED&nbsp;CT ontology's
 * hierarchical representation.
 */
public interface IHierarchicalService {
	
	/**
	 * Returns the topmost root node of the SNOMED&nbsp;CT terminology.
	 * @return the SNOMED&nbsp;CT root node. Should never be null.
	 */
	SnomedConceptIndexEntry getSnomedRoot();
	
	/**
	 * Returns the root concepts of the SNOMED&nbsp;CT terminology.
	 * @return a list of the root concept in the terminology.
	 */
	List<SnomedConceptIndexEntry> getRootConcepts();
	
	/**
	 * Returns the concept specified by its unique ID.
	 * @param conceptId the concept ID.
	 * @return the SNOMED&nbsp;CT concept.
	 */
	SnomedConceptIndexEntry getConcept(final long conceptId);

	/**
	 * Returns the concept specified by its unique ID.
	 * @param conceptId the concept ID.
	 * @return the SNOMED&nbsp;CT concept.
	 */
	SnomedConceptIndexEntry getConcept(final String conceptId);

	/**
	 * Returns with the direct descendants of a concept. A descendant is 
	 * a concept that is connected via an outbound/source IS_A relationship to the concept specified.
	 * The relationship must be stated and active.
	 *
	 * The method returns an empty collection if no direct descendants are found. 
	 * @param conceptId the concept ID.
	 * @return direct descendants of the concept.
	 */
	Collection<SnomedConceptIndexEntry> getSubtypes(final long conceptId);

	/**
	 * Returns with the direct descendants of a concept. A descendant is 
	 * a concept that is connected via an outbound/source IS_A relationship to the concept specified.
	 * The relationship must be stated and active.
	 *
	 * The method returns an empty collection if no direct descendants are found. 
	 * @param conceptId the concept ID.
	 * @return direct descendants of the concept.
	 */
	Collection<SnomedConceptIndexEntry> getSubtypes(final String conceptId);
	
	/**
	 * Returns with the direct descendant count of a concept. A descendant is 
	 * a concept that is connected via an outbound/source IS_A relationship to the concept specified.
	 * The relationship must be stated and active.
	 *
	 * The method returns with {@code 0} if no direct descendants are found. 
	 * @param conceptId the concept ID.
	 * @return direct descendant count of the concept.
	 */
	long getDirectSubtypeCount(final long conceptId);

	/**
	 * Returns with the direct descendant count of a concept. A descendant is 
	 * a concept that is connected via an outbound/source IS_A relationship to the concept specified.
	 * The relationship must be stated and active.
	 *
	 * The method returns with {@code 0} if no direct descendants are found. 
	 * @param conceptId the concept ID.
	 * @return direct descendant count of the concept.
	 */
	long getDirectSubtypeCount(final String conceptId);
	
	/**
	 * Returns with all (direct and transitive) descendant count of a concept.
	 *
	 * The method returns with {@code 0} if no descendants are found. 
	 * @param conceptId the concept ID.
	 * @return all descendant count of the concept.
	 */
	long getAllSubtypeCount(final long conceptId);

	/**
	 * Returns with all (direct and transitive) descendant count of a concept.
	 *
	 * The method returns with {@code 0} if no descendants are found. 
	 * @param conceptId the concept ID.
	 * @return all descendant count of the concept.
	 */
	long getAllSubtypeCount(final String conceptId);
	
	/**
	 * Returns with all (direct and transitive) descendants of a concept.
	 *
	 * The method returns with an empty collection if no descendants are found. 
	 * @param conceptId the concept ID.
	 * @return all descendants of the concept.
	 */
	Collection<SnomedConceptIndexEntry> getAllSubtypes(final long conceptId);

	/**
	 * Returns with all (direct and transitive) descendants of a concept.
	 *
	 * The method returns with an empty collection if no descendants are found. 
	 * @param conceptId the concept ID.
	 * @return all descendants of the concept.
	 */
	Collection<SnomedConceptIndexEntry> getAllSupertypes(final long conceptId);
	
	/**
	 * Returns with all (direct and transitive) descendants of a concept.
	 *
	 * The method returns with an empty collection if no descendants are found. 
	 * @param conceptId the concept ID.
	 * @return all descendants of the concept.
	 */
	Collection<SnomedConceptIndexEntry> getAllSubtypes(final String conceptId);

	/**
	 * Returns with the direct ancestors of a concept. An ancestor is 
	 * a concept that is connected via an inbound/destination IS_A relationship to the concept specified.
	 * The relationship must be stated and active.
	 *
	 * The method returns an empty collection if no direct ancestors are found. 
	 * @param conceptId the concept ID.
	 * @return direct ancestors of the concept.
	 */
	Collection<SnomedConceptIndexEntry> getSupertypes(final long conceptId);

	/**
	 * Returns with the direct ancestors of a concept. An ancestor is 
	 * a concept that is connected via an inbound/destination IS_A relationship to the concept specified.
	 * The relationship must be stated and active.
	 *
	 * The method returns an empty collection if no direct ancestors are found. 
	 * @param conceptId the concept ID.
	 * @return direct ancestors of the concept.
	 */
	Collection<SnomedConceptIndexEntry> getSupertypes(final String conceptId);
	

	/**
	 * Returns with all (direct and transitive) ancestors of a concept.
	 *
	 * The method returns with an empty collection if no ancestors are found. 
	 * @param conceptId the concept ID.
	 * @return all ancestors of the concept.
	 */
	Collection<SnomedConceptIndexEntry> getAllSupertypes(final String conceptId);
	
	/**
	 * Returns with the direct ancestor count of a concept. An ancestor is 
	 * a concept that is connected via an inbound/destination IS_A relationship to the concept specified.
	 * The relationship must be stated and active.
	 *
	 * The method returns  {@code 0} if no direct ancestors are found. 
	 * @param conceptId the concept ID.
	 * @return direct ancestor count of the concept.
	 */
	long getDirectSupertypeCount(final long conceptId);

	/**
	 * Returns with the direct ancestor count of a concept. An ancestor is 
	 * a concept that is connected via an inbound/destination IS_A relationship to the concept specified.
	 * The relationship must be stated and active.
	 *
	 * The method returns  {@code 0} if no direct ancestors are found. 
	 * @param conceptId the concept ID.
	 * @return direct ancestor count of the concept.
	 */
	long getDirectSupertypeCount(final String conceptId);
	
	/**
	 * Returns with all (direct and transitive) ancestor count of a concept.
	 *
	 * The method returns with {@code 0} if no ancestors are found. 
	 * @param conceptId the concept ID.
	 * @return all ancestor count of the concept.
	 */
	long getAllSupertypeCount(final long conceptId);

	/**
	 * Returns with all (direct and transitive) ancestor count of a concept.
	 *
	 * The method returns with {@code 0} if no ancestors are found. 
	 * @param conceptId the concept ID.
	 * @return all ancestor count of the concept.
	 */
	long getAllSupertypeCount(final String conceptId);

	/**
	 * Returns {@code true} if the parent reference set completely subsumes the subsumed reference set.
	 * @param parentRefsetId the ancestor reference set identifier concept ID 
	 * @param subsumedRefsetId the reference set identifier concept ID.
	 * @return {@code true} if the reference set is subsumed by the specified ancestor reference set.
	 */
	boolean isSubsumed(final long parentRefsetId, final long subsumedRefsetId);

	/**
	 * Returns {@code true} if the parent reference set completely subsumes the subsumed reference set.
	 * @param parentRefsetId the ancestor reference set identifier concept ID 
	 * @param subsumedRefsetId the reference set identifier concept ID.
	 * @return {@code true} if the reference set is subsumed by the specified ancestor reference set.
	 */
	boolean isSubsumed(final String parentRefsetId, final String subsumedRefsetId);
	
	/**
	 * Returns true if the parent concept is an ancestor of the child concept.
	 * (The child is connected via a path of outbound active IS_A relationships 
	 * to the parent concept)
	 *  
	 * @param parentConceptId the ancestor concept ID.
	 * @param childConceptId the unique ID of a SNOMED&nbsp;CT concept.
	 * @return {@code true} if the ancestor concept given by its ID is an ancestor of the other SNOMED&nbsp;CT concept.
	 */
	boolean isAncestor(final long parentConceptId, final long childConceptId);

	/**
	 * Returns true if the parent concept is an ancestor of the child concept.
	 * (The child is connected via a path of outbound active IS_A relationships 
	 * to the parent concept)
	 *  
	 * @param parentConceptId the ancestor concept ID.
	 * @param childConceptId the unique ID of a SNOMED&nbsp;CT concept.
	 * @return {@code true} if the ancestor concept given by its ID is an ancestor of the other SNOMED&nbsp;CT concept.
	 */
	boolean isAncestor(final String parentConceptId, final String childConceptId);
	
	/**
	 * Returns with a collection of SNOMED&nbsp;CT active target concepts for a source concept given by its unique SNOMED&nbsp;CT concept ID
	 * where the relationship type concept ID is matching with the specified one.
	 * <p>This method returns with an empty collection of concepts if either the source concept or the relationship type concept
	 * does not exist in the store.   
	 * @param sourceConceptId the unique SNOMED&nbsp;CT concept ID of the source concept.
	 * @param relationshipTypeId the unique SNOMED&nbsp;CT concept ID of the relationship type concept.
	 * @return a collection of active target concept.
	 */
	Collection<SnomedConceptIndexEntry> getTargetConcepts(final String sourceConceptId, final String relationshipTypeId);
	
	/**
	 * Returns with a collection of SNOMED&nbsp;CT active target concepts for a source concept given by its unique SNOMED&nbsp;CT concept ID
	 * where the relationship type concept ID is matching with the specified one.
	 * <p>This method returns with an empty collection of concepts if either the source concept or the relationship type concept
	 * does not exist in the store.   
	 * @param sourceConceptId the unique SNOMED&nbsp;CT concept ID of the source concept.
	 * @param relationshipTypeId the unique SNOMED&nbsp;CT concept ID of the relationship type concept.
	 * @return a collection of active target concept.
	 */
	Collection<SnomedConceptIndexEntry> getTargetConcepts(final long sourceConceptId, final long relationshipTypeId);
	
	/**
	 * Returns with a collection of SNOMED&nbsp;CT active target concepts for a source concept given by its unique SNOMED&nbsp;CT concept ID
	 * where the relationship type concept ID is matching with the specified one.
	 * <p>This method returns with an empty collection of concepts if either the target concept or the relationship type concept
	 * does not exist in the store.   
	 * @param targetConceptId the unique SNOMED&nbsp;CT concept ID of the target concept.
	 * @param relationshipTypeId the unique SNOMED&nbsp;CT concept ID of the relationship type concept.
	 * @return a collection of active source concept.
	 */
	Collection<SnomedConceptIndexEntry> getSourceConcepts(final String targetConceptId, final String relationshipTypeId);
	
	/**
	 * Returns with a collection of active SNOMED&nbsp;CT source concepts for a source concept given by its unique SNOMED&nbsp;CT concept ID
	 * where the relationship type concept ID is matching with the specified one.
	 * <p>This method returns with an empty collection of concepts if either the target concept or the relationship type concept
	 * does not exist in the store.   
	 * @param targetConceptId the unique SNOMED&nbsp;CT concept ID of the target concept.
	 * @param relationshipTypeId the unique SNOMED&nbsp;CT concept ID of the relationship type concept.
	 * @return a collection of active source concept.
	 */
	Collection<SnomedConceptIndexEntry> getSourceConcepts(final long targetConceptId, final long relationshipTypeId);

	/**
	 * Returns a list of ordered concepts representing the shortest path using IS_A relationships
	 * between two arbitrary concepts.  There should always be a path even if neither concept subsumes
	 * the other in which case the path needs to include the closest common parent - in some cases this would
	 * be the root SNOMED&nbsp;CT concept.
	 * 
	 * @param startingConcept the unique ID of the starting concept.
	 * @param endConcept the unique ID of the end SNOMED&nbsp;CT concept. 
	 * @return a list of the shortest path between two given SNOMED&nbsp;CT concept. 
	 */
	List<SnomedConceptIndexEntry> getShortestPath(final long startingConcept, final long endConcept);
	
	/**
	 * Returns a list of ordered concepts representing the shortest path using IS_A relationships
	 * between two arbitrary concepts.  There should always be a path even if neither concept subsumes
	 * the other in which case the path needs to include the closest common parent - in some cases this would
	 * be the root SNOMED&nbsp;CT concept.
	 * 
	 * @param startingConcept the unique ID of the starting concept.
	 * @param endConcept the unique ID of the end SNOMED&nbsp;CT concept. 
	 * @return a list of the shortest path between two given SNOMED&nbsp;CT concept. 
	 */
	List<SnomedConceptIndexEntry> getShortestPath(final String startingConcept, final String endConcept);
	
	/**
	 * Returns with all outbound/source relationships of a concept.
	 * @param conceptId the concept ID.
	 * @return a collection of all outbound/source relationships of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getOutboundRelationships(final String conceptId);

	/**
	 * Returns with all active outbound/source relationships of a concept.
	 * @param conceptId the concept ID.
	 * @return a collection of all active outbound/source relationships of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getActiveOutboundRelationships(final String conceptId);
	
	/**
	 * Returns with all outbound/source relationships from a give type of a concept.
	 * @param conceptId the concept ID.
	 * @param relationshipTypeId relationship type ID.
	 * @return a collection of all outbound/source relationships from a given type of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getOutboundRelationships(final String conceptId, final String relationshipTypeId);
	
	/**
	 * Returns with all active outbound/source relationships from a give type of a concept.
	 * @param conceptId the concept ID.
	 * @param relationshipTypeId relationship type ID.
	 * @return a collection of all active outbound/source relationships from a given type of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getActiveOutboundRelationships(final String conceptId, final String relationshipTypeId);

	/**
	 * Returns with all inbound/destination relationships of a concept.
	 * @param conceptId the concept ID.
	 * @return a collection of all inbound/destination relationships of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getInboundRelationships(final String conceptId);

	/**
	 * Returns with all active inbound/destination relationships of a concept.
	 * @param conceptId the concept ID.
	 * @return a collection of all active inbound/destination relationships of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getActiveInboundRelationships(final String conceptId);
	
	/**
	 * Returns with all inbound/destination relationships from a give type of a concept.
	 * @param conceptId the concept ID.
	 * @param relationshipTypeId relationship type ID.
	 * @return a collection of all inbound/destination relationships from a given type of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getInboundRelationships(final String conceptId, final String relationshipTypeId);
	
	/**
	 * Returns with all active inbound/destination relationships from a give type of a concept.
	 * @param conceptId the concept ID.
	 * @param relationshipTypeId relationship type ID.
	 * @return a collection of all active inbound/destination relationships from a given type of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getActiveInboundRelationships(final String conceptId, final String relationshipTypeId);
	
	/**
	 * Returns with all outbound/source relationships of a concept.
	 * @param conceptId the concept ID.
	 * @return a collection of all outbound/source relationships of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getOutboundRelationships(final long conceptId);

	/**
	 * Returns with all active outbound/source relationships of a concept.
	 * @param conceptId the concept ID.
	 * @return a collection of all active outbound/source relationships of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getActiveOutboundRelationships(final long conceptId);
	
	/**
	 * Returns with all outbound/source relationships from a give type of a concept.
	 * @param conceptId the concept ID.
	 * @param relationshipTypeId relationship type ID.
	 * @return a collection of all outbound/source relationships from a given type of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getOutboundRelationships(final long conceptId, final long relationshipTypeId);
	
	/**
	 * Returns with all active outbound/source relationships from a give type of a concept.
	 * @param conceptId the concept ID.
	 * @param relationshipTypeId relationship type ID.
	 * @return a collection of all active outbound/source relationships from a given type of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getActiveOutboundRelationships(final long conceptId, final long relationshipTypeId);

	/**
	 * Returns with all inbound/destination relationships of a concept.
	 * @param conceptId the concept ID.
	 * @return a collection of all inbound/destination relationships of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getInboundRelationships(final long conceptId);

	/**
	 * Returns with all active inbound/destination relationships of a concept.
	 * @param conceptId the concept ID.
	 * @return a collection of all active inbound/destination relationships of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getActiveInboundRelationships(final long conceptId);
	
	/**
	 * Returns with all inbound/destination relationships from a give type of a concept.
	 * @param conceptId the concept ID.
	 * @param relationshipTypeId relationship type ID.
	 * @return a collection of all inbound/destination relationships from a given type of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getInboundRelationships(final long conceptId, final long relationshipTypeId);
	
	/**
	 * Returns with all active inbound/destination relationships from a give type of a concept.
	 * @param conceptId the concept ID.
	 * @param relationshipTypeId relationship type ID.
	 * @return a collection of all active inbound/destination relationships from a given type of a concept.
	 */
	List<SnomedRelationshipIndexEntry> getActiveInboundRelationships(final long conceptId, final long relationshipTypeId);
	
	
}