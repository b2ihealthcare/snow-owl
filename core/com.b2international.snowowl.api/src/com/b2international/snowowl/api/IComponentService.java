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
package com.b2international.snowowl.api;

import com.b2international.snowowl.api.codesystem.exception.CodeSystemNotFoundException;
import com.b2international.snowowl.api.codesystem.exception.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.api.domain.IComponent;
import com.b2international.snowowl.api.domain.IComponentInput;
import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.history.IHistoryService;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;

/**
 * Component service implementations provide methods for <b>c</b>reating, <b>r</b>eading, <b>u</b>pdating and
 * <b>d</b>eleting a single item of a particular component type.
 * <p>
 * Components are parts of a code system which can be identified uniquely by a {@link IComponentRef component reference}.
 * 
 * @param <C> the input model type (used when creating a new component; must implement {@link IComponentInput})
 * @param <R> the read model type (used when retrieving component details; must implement {@link IComponent})
 * @param <U> the update model type (used when updating an existing component)
 */
public interface IComponentService<C extends IComponentInput, R extends IComponent, U> {

	/**
	 * Creates a new component using the specified input model, then commits changes with the given commit comment to
	 * the data store under the specified user ID.
	 * <p>
	 * Input models undergo validation to ensure that no required information is missing or malformed before committing.
	 * Commit comments and a committer identifier must be provided for auditing purposes; changes related to the
	 * component, including comments can be retrieved via a {@link IHistoryService history service}, if it's available
	 * for this component type.
	 * <p>
	 * Client-generated identifiers are accepted, although they might change as part of the commit process if an
	 * identifier collision occurs. The client should inspect the returned object for the assigned identifier.
	 * 
	 * @param input         the properties of the component to create (may not be {@code null})
	 * @param userId        the identifier of the user who creates the component (may not be {@code null})
	 * @param commitComment the commit comment entered for this change (may not be {@code null})
	 * 
	 * @return the created component's details (as returned by {@link #read(IComponentRef)})
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 * @throws ComponentValidationException       if the component input fails validation
	 * @throws ComponentCreationException         if creating the component fails for any other reason
	 */
	R create(C input, String userId, String commitComment);

	/**
	 * Retrieves component details identified by the given {@link IComponentRef component reference}, if it exists.
	 * 
	 * @param ref the {@code IComponentRef} pointing to the component to read (may not be {@code null})
	 * 
	 * @return the component details
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 * @throws ComponentNotFoundException         if the component identifier does not match any component on the given task
	 */
	R read(IComponentRef ref);

	/**
	 * Updates an existing component using the specified update model, if it exists.
	 * <p>
	 * Update models only carry the editable subset of component properties, and are also validated before committing
	 * changes. If no property is changed as part of this method call, the operation is equivalent to 
	 * calling {@link #read(IComponentRef)}, otherwise the returned object will reflect the updates which were made. 
	 * 
	 * @param ref           the {@code IComponentRef} pointing to the component to update (may not be {@code null})
	 * @param update        the properties of the component to update (may not be {@code null})
	 * @param userId        the identifier of the user who updates the component (may not be {@code null})
	 * @param commitComment the commit comment entered for this change (may not be {@code null})
	 * 
	 * @return the component's details after updating (as returned by {@link #read(IComponentRef)})
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 * @throws ComponentNotFoundException         if the component identifier does not match any component on the given task
	 * @throws ComponentValidationException       if the supplied update model fails validation
	 * @throws ComponentUpdateException           if updating the component fails for any other reason
	 */
	R update(IComponentRef ref, U update, String userId, String commitComment);

	/**
	 * Permanently deletes the specified component from the specified code system version and task.
	 * <p>
	 * Component services may deny deletion requests, eg. if the component was already published as part of a 
	 * release. 
	 * 
	 * @param ref           the {@code IComponentRef} pointing to the component to update (may not be {@code null})
	 * @param userId        the identifier of the user who deletes the component (may not be {@code null})
	 * @param commitComment the commit comment entered for this change (may not be {@code null})
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 * @throws ComponentNotFoundException         if the component identifier does not match any component on the given task
	 * @throws ComponentDeleteException           if deleting the component fails for any other reason
	 */
	void delete(IComponentRef ref, String userId, String commitComment);
}
