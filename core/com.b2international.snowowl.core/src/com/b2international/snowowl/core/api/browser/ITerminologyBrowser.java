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
package com.b2international.snowowl.core.api.browser;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponentWithChildFlag;

/**
 * Interface for browsing a hierarchy of concepts on the server side.
 * 
 * @param <C> the concept type
 * @param <K> the concept unique identifier type 
 */
public interface ITerminologyBrowser<C, K> extends ExtendedComponentProvider, SuperTypeIdProvider<K> {
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null}) 
	 * @return the root concepts
	 */
	Collection<C> getRootConcepts(final IBranchPath branchPath);
	
	/**
	 * Returns with the component IDs of the root components from the terminology.
	 * @param branchPath the branch path.
	 * @return a set of component IDs for the root components.
	 */
	Collection<K> getRootConceptIds(final IBranchPath branchPath);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null}) 
	 * @param key
	 * @return the concept with the given unique identifier
	 */
	C getConcept(final IBranchPath branchPath, final K id);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the parent concepts for the specified concept
	 */
	Collection<C> getSuperTypes(final IBranchPath branchPath, final C concept);
	
	/**
	 * Returns with the unique storage key of the component. 
	 * @param branchPath 
	 * @param conceptId the terminology specific unique ID of the component.
	 * @return the storage key, or {@code -1L};
	 */
	long getStorageKey(final IBranchPath branchPath, final String conceptId);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the child concepts for the specified concept
	 */
	Collection<C> getSubTypes(final IBranchPath branchPath, final C concept);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the child concepts for the specified concept as a list.
	 */
	List<C> getSubTypesAsList(final IBranchPath branchPath, final C concept);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the parent concepts for the concept with the specified unique identifier
	 */
	Collection<C> getSuperTypesById(final IBranchPath branchPath, final K id);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the child concepts for the concept with the specified unique identifier
	 */
	Collection<C> getSubTypesById(final IBranchPath branchPath, final K id);

	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return all the ancestor concepts for the specified concept, including its direct parents
	 */
	Collection<C> getAllSuperTypes(final IBranchPath branchPath, final C concept);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param id
	 * @return all the ancestor concepts for the concept with the specified unique identifier, including its direct parents
	 */
	Collection<C> getAllSuperTypesById(final IBranchPath branchPath, final K id);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return all the descendant concepts for the specified concept, including its direct children
	 */
	Collection<C> getAllSubTypes(final IBranchPath branchPath, final C concept);

	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param id
	 * @return all the descendant concepts for the concept with the specified unique identifier, including its direct children
	 */
	Collection<C> getAllSubTypesById(final IBranchPath branchPath, final K id);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the number of descendants for the specified concept, including its direct children
	 */
	int getAllSubTypeCount(final IBranchPath branchPath, final C concept);

	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the number of direct children for the specified concept
	 */
	int getSubTypeCount(final IBranchPath branchPath, final C concept);

	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the number of ancestors for the specified concept, including its direct parents
	 */	
	int getAllSuperTypeCount(final IBranchPath branchPath, final C concept);

	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the number of direct parents for the specified concept
	 */
	int getSuperTypeCount(final IBranchPath branchPath, final C concept);

	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the number of descendants for the concept with the specified unique identifier, including its direct children
	 */
	int getAllSubTypeCountById(final IBranchPath branchPath, final K id);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the number of direct children for the concept with the specified unique identifier
	 */
	int getSubTypeCountById(final IBranchPath branchPath, final K id);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the number of ancestors for the concept with the specified unique identifier, including its direct parents
	 */	
	int getAllSuperTypeCountById(final IBranchPath branchPath, final K id);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the number of direct parents for the concept with the specified unique identifier
	 */
	int getSuperTypeCountById(final IBranchPath branchPath, final K id);
	
	/**
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param concept
	 * @return the top level ancestor of the specified concept
	 */
	C getTopLevelConcept(final IBranchPath branchPath, final C concept);
	
	/**
	 * Returns {@code true} if the terminology is available. Otherwise returns with {@code false}.
	 * @param branchPath the branch path.
	 * @return {@code true} if the terminology is available, otherwise {@code false}.
	 */
	boolean isTerminologyAvailable(final IBranchPath branchPath);

	/**
	 * Returns true if the first specified concept is the super type of the second specified concept.
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param superType the super type concept
	 * @param suBType the sub type concept
	 * @return true if the first specified concept is the super type of the second specified concept, false otherwise
	 */
	boolean isSuperTypeOf(final IBranchPath branchPath, final C superType, final C subType);

	/**
	 * Returns true if the concept identified by the first specified unique identifier is the super type of the concept 
	 * identified by the second specified unique identifier.
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param superType the super type concept's unique identifier
	 * @param suBType the sub type concept's unique identifier
	 * @return true if the first specified concept is the super type of the second specified concept, false otherwise
	 */
	boolean isSuperTypeOfById(final IBranchPath branchPath, final String superTypeId, final String subTypeId);
	
	/**
	 * Creates a filtered representation of the current terminology based on the specified expression.
	 * @param branchPath the branch path reference limiting visibility to a particular branch (final may not be {@code null})
	 * @param expression the expression to filter out components.
	 * @param monitor monitor for the progress.
	 * @return the filtered snapshot of the current terminology.
	 */
	IFilterClientTerminologyBrowser<C, K> filterTerminologyBrowser(final IBranchPath branchPath, @Nullable final String expression, @Nullable final IProgressMonitor monitor);

	Collection<IComponentWithChildFlag<K>> getSubTypesWithChildFlag(final IBranchPath branchPath, final C concept);

	/**
	 * Checks whether a component identified by its terminology specific unique ID exits on the given branch.
	 * Returns with {@code true} if the component exists, otherwise returns with {@code false}.
	 * @param branchPath the branch path identifying the branch to check the existence.
	 * @param componentId the terminology specific unique ID.
	 * @return {@code true} if the component exists, otherwise returns with {@code false}.
	 */
	boolean exists(final IBranchPath branchPath, final String componentId);
}
