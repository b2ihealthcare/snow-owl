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
package com.b2international.snowowl.datastore.delta;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.Change;
import com.b2international.commons.Triple;
import com.google.common.base.Predicate;

/**
 * interface for terminology specified component delta managers.
 * 
 */
public interface IComponentDeltaBuilder<C extends ComponentDelta> {

	Collection<C> processChanges(final CDOChangeSetData changeSetData, final CDOView baseView, final CDOView currentView);
	
	/**
	 * Null implementation. Does nothing.
	 */
	public static final class NullComponentDeltaBuilder {
		
		/**Provides an empty collection of deltas.*/
		private static final IComponentDeltaBuilder<ComponentDelta> NULL_IMPL = new IComponentDeltaBuilder<ComponentDelta>() {
			@Override public Collection<ComponentDelta> processChanges(final CDOChangeSetData changeSetData, final CDOView baseView, final CDOView currentView) {
				return Collections.emptySet();
			};
		};
		
		/**
		 * Returns with a builder that does nothing. 
		 * @return the {@link #NULL_IMPL <em>NULL</em>} implementation.
		 */
		@SuppressWarnings("unchecked")
		public static <C extends ComponentDelta> IComponentDeltaBuilder<C> getNullImpl() {
			return (IComponentDeltaBuilder<C>) NULL_IMPL;
		}
		
	};
	
	/**
	 * Extended {@link Predicate} working on {@link Triple} instances. 
	 * @see #ACCEPT_ALL_PREDICATE
	 */
	public static interface IComponentDeltaPredicate extends Predicate<Triple<CDOIDAndVersion, CDOView, Change>> {
		
		@Override public boolean apply(final Triple<CDOIDAndVersion, CDOView, Change> input);
		
		/***
		 * Sugar for {@link #apply(Triple)}.
		 * @param cdoidAndVersion {@link CDOIDAndVersion} representing a {@link CDOObject}.
		 * @param view the view to resolve the {@link CDOIDAndVersion}.
		 * @param change the change on the component.
		 */
		public boolean apply(final CDOIDAndVersion cdoidAndVersion, final CDOView view, final Change change);
		
		/**
		 * Predicate accepting everything.
		 * @see IComponentDeltaPredicate
		 */
		public static IComponentDeltaPredicate ACCEPT_ALL_PREDICATE = new IComponentDeltaPredicate() {
			@Override public boolean apply(final CDOIDAndVersion cdoidAndVersion, final CDOView view, final Change change) { return true; }
			@Override public boolean apply(final Triple<CDOIDAndVersion, CDOView, Change> input) { return true; }
		};
		
	}
	
}