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
package com.b2international.snowowl.snomed.datastore;

import java.util.Collection;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * Representation of a server-side service for the SNOMED&nbsp;CT taxonomy.
 */
@Deprecated
public interface SnomedTaxonomyService {

	boolean isActive(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with the ID of the SNOMED&nbsp;CT root concept.
	 * @param branchPath the branch path.
	 * @return the root concept ID.
	 */
	String getSnomedRoot(final IBranchPath branchPath);
	
	/**
	 * Returns with the direct descendants IDs of a given concept.
	 * @param branchPath the branch path. 
	 * @param conceptId the concept ID.
	 * @return the direct descendant IDs.
	 */
	Collection<String> getSubtypes(final IBranchPath branchPath, final String conceptId);

	/**
	 * Returns with all descendant IDs of a given concept.
	 * @param branchPath the branch path. 
	 * @param conceptId the concept ID.
	 * @return all descendant IDs.
	 */
	Collection<String> getAllSubtypes(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with the number of direct descendants of a given concept.
	 * @param branchPath the branch path. 
	 * @param conceptId the concept ID.
	 * @return the number of direct descendants.
	 */
	int getSubtypesCount(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with the number of all descendants of a given concept.
	 * @param branchPath the branch path. 
	 * @param conceptId the concept ID.
	 * @return the number of all descendants.
	 */
	int getAllSubtypesCount(final IBranchPath branchPath, final String conceptId);

	/**
	 * Returns with the direct ancestors IDs of a given concept.
	 * @param branchPath the branch path. 
	 * @param conceptId the concept ID.
	 * @return the direct ancestor IDs.
	 */
	Collection<String> getSupertypes(final IBranchPath branchPath, final String conceptId);

	/**
	 * Returns with all ancestor IDs of a given concept.
	 * @param branchPath the branch path.
	 * @param conceptId the concept ID.
	 * @return all ancestor IDs.
	 */
	Collection<String> getAllSupertypes(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with the number of direct ancestors of a given concept.
	 * @param branchPath the branch path.
	 * @param conceptId the concept ID.
	 * @return the number of direct ancestors.
	 */
	int getSupertypesCount(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with the number of all ancestors of a given concept.
	 * @param branchPath the branch path.
	 * @param conceptId the concept ID.
	 * @return the number of all ancestors.
	 */
	int getAllSupertypesCount(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with the number of non-unique concepts that can be accessed from the specified 
	 * concept's outbound, active and non-IS_A relationships.
	 * @param branchPath the branch path.
	 * @param conceptId the focus concept identifier.
	 * @return the number of non-unique concepts which are reachable from the focus concept via 
	 * an outbound active and non-IS_A relationship (never negative)
	 */
	int getOutboundConceptsCount(final IBranchPath branchPath, final String conceptId);

	/**
	 * Returns with all concept IDs that can be accessed from a given focus concept's
	 * active outbound/source relationships. The IS_A relationships will be excluded 
	 * when accessing the outbound/source relationships.
	 * @param branchPath the branch path.
	 * @param conceptId the focus concept ID.
	 * @return all concept IDs that can be accessed from a focus concept's active non IS_A
	 * outbound/source relationships.
	 */
	Collection<String> getOutboundConcepts(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with all concept IDs that can be accessed from a given focus concept's
	 * active outbound/source relationships for a given type. The IS_A relationships will be ignored.
	 * @param branchPath the branch path.
	 * @param conceptId the focus concept ID.
	 * @param typeId the relationship type concept ID.
	 * @return all concept IDs that can be accessed from a focus concept's active non IS_A
	 * outbound/source relationships for a given relationship type.
	 */
	Collection<String> getOutboundConcepts(final IBranchPath branchPath, final String conceptId, final String typeId);
	
	/**
	 * Returns with all concept IDs that can be accessed from a given focus concept's
	 * active outbound/source relationships. 
	 * @param branchPath the branch path.
	 * @param conceptId the focus concept ID.
	 * @return all concept IDs that can be accessed from a focus concept's active outbound/source relationships.
	 */
	Collection<String> getAllOutboundConcepts(IBranchPath branchPath, String conceptId);
	
	/**
	 * Returns with {@code true} only and if only the focus concept given with the concept ID argument 
	 * has at least one active source/outbound relationship with the given relationship type. Otherwise {@code false}.
	 * @param branchPath the branch path.
	 * @param conceptId the unique focus concept ID which relationship has to check.
	 * @param typeId the relationship type concept ID of a particular source relationship.
	 * @return {@code true} if the concept has active source/outbound relationship from the 
	 * given relationship type, otherwise {@code false}.
	 */
	boolean hasOutboundRelationshipOfType(final IBranchPath branchPath, final String conceptId, final String typeId);
	
	/**
	 * Returns with a collection of relationship type concept IDs extracted from the active outbound/source
	 * relationships of the given concept. IS_A relationships will be excluded.
	 * @param branchPath the branch path.
	 * @param conceptId the unique ID of the concept.
	 * @return a collection of relationship type concept identifiers.
	 */
	Collection<String> getOutboundRelationshipTypes(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with all concept IDs that can be accessed from a given focus concept's
	 * active inbound/destination relationships. The IS_A relationships will be excluded 
	 * when accessing the inbound/destination relationships.
	 * @param branchPath the branch path.
	 * @param conceptId the focus concept ID.
	 * @return all concept IDs that can be accessed from a focus concept's active non IS_A
	 * inbound/destination relationships.
	 */
	Collection<String> getInboundConcepts(final IBranchPath branchPath, final String conceptId);

	/**
	 * Returns with all concept IDs that can be accessed from a given focus concept's
	 * active inbound/destination relationships for a given type. The IS_A relationships will be ignored.
	 * @param branchPath the branch path.
	 * @param conceptId the focus concept ID.
	 * @param typeId the relationship type concept ID.
	 * @return all concept IDs that can be accessed from a focus concept's active non IS_A
	 * inbound/destination relationships for a given relationship type.
	 */
	Collection<String> getInboundConcepts(final IBranchPath branchPath, final String conceptId, final String typeId);

	/**
	 * Returns with all concept IDs that can be accessed from a given focus concept's
	 * active inbound/destination relationships.
	 * @param branchPath the branch path.
	 * @param conceptId the focus concept ID.
	 * @return all concept IDs that can be accessed from a focus concept's active inbound/destination relationships.
	 */
	Collection<String> getAllInboundConcepts(IBranchPath branchPath, String conceptId);
	
	/**
	 * Returns with {@code true} only and if only the focus concept given with the concept ID argument 
	 * has at least one active destination/inbound relationship with the given relationship type. Otherwise {@code false}.
	 * @param branchPath the branch path.
	 * @param conceptId the unique focus concept ID which relationship has to check.
	 * @param typeId the relationship type concept ID of a particular destination relationship.
	 * @return {@code true} if the concept has active destination/inbound relationship from the 
	 * given relationship type, otherwise {@code false}.
	 */
	boolean hasInboundRelationshipOfType(final IBranchPath branchPath, final String conceptId, final String typeId);
	
	/**
	 * Returns with the depth of the current concept from the taxonomy.
	 * The depth of a node is the number of edges from the node to the tree's root node.
	 * <br>A root node will have a depth of 0.
	 * @param branchPath the branch path.
	 * @param conceptId the concept ID of the focus concept/node.
	 * @return the height of the node in the taxonomy.
	 */
	int getDepth(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with the height of the current concept from the taxonomy.
	 * The height of a node is the number of edges on the longest path from the node to a leaf.
	 * <br>A leaf node will have a height of 0.
	 * @param branchPath the branch path.
	 * @param conceptId the concept ID of the focus concept/node.
	 * @return the height of the node in the taxonomy.
	 */
	int getHeight(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Returns with {@code true} if the concept given with the unique concept ID argument is 
	 * a leaf node. In other words, it does not have any active descendants. Otherwise {@code false}.
	 * @param branchPath the branch path.
	 * @param conceptId the ID of the concept to check.
	 * @return {@code true} if the concept is a leaf in the ontology. Otherwise {@code false}.
	 */
	boolean isLeaf(final IBranchPath branchPath, final String conceptId);
	
	Collection<String> getContainerRefSetIds(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * Evaluates an ESCG expression and returns with the ID of the matching concept results.
	 * @param branchPath the branch path.
	 * @param expression the expression to evaluate.
	 * @return the IDs of the matching concepts for the ESCG expression. 
	 */
	Collection<String> evaluateEscg(final IBranchPath branchPath, final String expression);
	
}