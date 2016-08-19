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
import java.util.Map;

import javax.annotation.Nullable;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.api.IComponentWithChildFlag;

/**
 * Interface for browsing a hierarchy of concepts on the client side.
 * 
 * @param <C> the concept type
 * @param <K> the concept unique identifier type 
 */
public interface IClientTerminologyBrowser<C, K> {
	
	/**
	 * Returns the root concepts.
	 * @return the root concepts.
	 */
	public Collection<C> getRootConcepts();
	
	/**
	 * Returns the concept with the given unique identifier.
	 * @param key
	 * @return
	 */
	public C getConcept(final K id);
	
	public Iterable<C> getComponents(Iterable<K> ids);
	
	/**
	 * Returns the parent concepts for the specified concept.
	 * @param concept
	 * @return the parent concepts for the specified concept
	 */
	public Collection<C> getSuperTypes(final C concept);
	
	/**
	 * Returns the child concepts for the specified concept.
	 * @param concept
	 * @return the child concepts for the specified concept
	 */
	public Collection<C> getSubTypes(final C concept);
	
	/**
	 * Returns all ancestor component IDs of the specified component.
	 * @param conceptId the component ID.
	 * @return a collection of all ancestor component IDs
	 */
	public Collection<K> getSuperTypeIds(final K conceptId);
	
	/**
	 * Returns the child concepts for the specified concept as a list.
	 * @param concept
	 * @return the list of child concepts for the specified concept
	 */
	public List<C> getSubTypesAsList(final C concept);
	
	/**
	 * Returns the parent concepts for the concept with the specified unique identifier.
	 * @param concept
	 * @return the parent concepts for the concept with the specified unique identifier
	 */
	public Collection<C> getSuperTypesById(final K id);
	
	/**
	 * Returns the child concepts for the concept with the specified unique identifier.
	 * @param concept
	 * @return the child concepts for the concept with the specified unique identifier
	 */
	public Collection<C> getSubTypesById(final K id);

	/**
	 * Returns all the ancestor concepts for the specified concept, including its direct parents.
	 * @param concept
	 * @return all the ancestor concepts for the specified concept, including its direct parents
	 */
	public Collection<C> getAllSuperTypes(final C concept);
	
	/**
	 * Returns all the ancestor concepts for the concept with the specified unique identifier, including its direct parents.
	 * @param id
	 * @return all the ancestor concepts for the concept with the specified unique identifier, including its direct parents
	 */
	public Collection<C> getAllSuperTypesById(final K id);
	
	/**
	 * Returns all the descendant concepts for the specified concept, including its direct children.
	 * @param concept
	 * @return all the descendant concepts for the specified concept, including its direct children
	 */
	public Collection<C> getAllSubTypes(final C concept);

	/**
	 * Returns all the descendant concepts for the concept with the specified unique identifier, including its direct children.
	 * @param id
	 * @return all the descendant concepts for the concept with the specified unique identifier, including its direct children
	 */
	public Collection<C> getAllSubTypesById(final K id);
	
	/**
	 * Returns the number of descendants for the specified concept, including its direct children.
	 * @param concept
	 * @return the number of descendant for the specified concept, including its direct children
	 */
	public int getAllSubTypeCount(final C concept);

	/**
	 * Returns the number of direct children for the specified concept.
	 * @param concept
	 * @return the number of direct children for the specified concept.
	 */
	public int getSubTypeCount(final C concept);

	/**
	 * Returns the number of ancestors for the specified concept, including its direct parents.
	 * @param concept
	 * @return the number of ancestors for the specified concept, including its direct parents
	 */	
	public int getAllSuperTypeCount(final C concept);

	/**
	 * Returns the number of direct parents for the specified concept.
	 * @param concept
	 * @return the number of direct parents for the specified concept.
	 */
	public int getSuperTypeCount(final C concept);

	/**
	 * Returns the number of descendants for the concept with the specified unique identifier, including its direct children.
	 * @param concept
	 * @return the number of descendants for the concept with the specified unique identifier, including its direct children
	 */
	public int getAllSubTypeCountById(final K id);
	
	/**
	 * Returns the number of direct children for the concept with the specified unique identifier.
	 * @param concept
	 * @return the number of direct children for the concept with the specified unique identifier
	 */
	public int getSubTypeCountById(final K id);
	
	/**
	 * Returns the number of ancestors for the concept with the specified unique identifier, including its direct parents.
	 * @param concept
	 * @return the number of ancestors for the concept with the specified unique identifier, including its direct parents
	 */	
	public int getAllSuperTypeCountById(final K id);
	
	/**
	 * Returns the number of direct parents for the concept with the specified unique identifier.
	 * @param concept
	 * @return the number of direct parents for the concept with the specified unique identifier
	 */
	public int getSuperTypeCountById(final K id);
	
	/**
	 * Returns the top level ancestor of the specified concept.
	 * @param concept
	 * @return the top level ancestor of the specified concept
	 */
	public C getTopLevelConcept(final C concept);
	
	public boolean isTerminologyAvailable();
	
	/**
	 * Returns true if the first specified concept is the super type of the second specified concept.
	 * @param superType the super type concept
	 * @param suBType the sub type concept
	 * @return true if the first specified concept is the super type of the second specified concept, false otherwise
	 */
	public boolean isSuperTypeOf(final C superType, final C subType);

	/**
	 * Returns true if the concept identified by the first specified unique identifier is the super type of the concept 
	 * identified by the second specified unique identifier.
	 * @param superType the super type concept's unique identifier
	 * @param suBType the sub type concept's unique identifier
	 * @return true if the first specified concept is the super type of the second specified concept, false otherwise
	 */
	public boolean isSuperTypeOfById(final String superTypeId, final String subTypeId);
	
	public IFilterClientTerminologyBrowser<C, K> filterTerminologyBrowser(@Nullable final String expression, @Nullable final IProgressMonitor monitor);

	/**
	 * Returns the direct children of the specified concept, with an additional flag to indicate whether the returned concepts have children or not.
	 * 
	 * @param concept the concept
	 * @return the direct children of the specified concept, with an additional flag
	 */
	public Collection<IComponentWithChildFlag<K>> getSubTypesWithChildFlag(C concept);
	
	/**
	 * Checks whether a component identified by its terminology specific unique ID exits on the currently active branch.
	 * Returns with {@code true} if the component exists, otherwise returns with {@code false}.
	 * @param componentId the terminology specific unique ID.
	 * @return {@code true} if the component exists, otherwise returns with {@code false}.
	 */
	boolean exists(final String componentId);
	
	/**
	 * Checks whether the components identified by their terminology specific
	 * unique IDs exist on the given branch. Returns with a map, where the keys
	 * are the component IDs and the values are <code>true</code> or
	 * <code>false</code> whether the component exists or not.
	 * 
	 * @param componentIds
	 *            the terminology specific unique IDs.
	 * @return map where each ID is mapped to <code>true</code> or
	 *         <code>false</code> whether it exists or not.
	 */
	Map<String, Boolean> exist(Collection<String> componentIds);
	
}
