/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Representation of a set of new, dirty and detached components as an outcome of a successful commit into the persistence layer. 
 *
 */
public interface ICDOCommitChangeSet {

	/**
	 * @return the {@link CDOView} used for retrieving new and dirty components (can be used if these two collections are empty)
	 */
	CDOView getView();
	
	/**
	 * @return the user identifier
	 */
	String getUserId();
	
	/**
	 * @return the commit message
	 */
	String getCommitComment();
	
	/**
	 * Returns with the new components.
	 * @return the new components.
	 */
	Collection<CDOObject> getNewComponents();

	/**
	 * Returns with the dirty components.
	 * @return the dirty components.
	 */
	Collection<CDOObject> getDirtyComponents();

	/**
	 * Returns with the detached components.
	 * @return the detached components.
	 */
	Map<CDOID, EClass> getDetachedComponents();
	
	/**
	 * Returns with a map of revision deltas mapped via {@link CDOID}s.
	 * @return the revision deltas.
	 */
	Map<CDOID, CDORevisionDelta> getRevisionDeltas();
	
	/**
	 * Returns with the timestamp that can be associated with the commit change set.
	 * @return the timestamp.
	 */
	long getTimestamp();

	boolean isEmpty();

	<T extends CDOObject> Iterable<T> getNewComponents(Class<T> type);
	
	<T extends CDOObject> Iterable<T> getDirtyComponents(Class<T> type);

	<T extends CDOObject> Iterable<T> getDirtyComponents(Class<T> type, Set<EStructuralFeature> allowedFeatures);
	
	Collection<CDOID> getDetachedComponents(EClass eClass);
	
}