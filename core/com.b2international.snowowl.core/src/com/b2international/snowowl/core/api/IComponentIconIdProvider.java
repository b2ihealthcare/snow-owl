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
package com.b2international.snowowl.core.api;

import javax.annotation.Nullable;

/**
 * Provides terminology specific component IDs for resolving icons on the UI. 
 * @param <K> - type of the unique component identifier
 */
public interface IComponentIconIdProvider<K> {

	/**
	 * Returns with the ID of a component who's icon should be revealed for a particular component
	 * given by it terminology wise unique component identifier.
	 * <p>May return with {@code null} e.g.: the component does not exist at the branch in the given time. 
	 * @param branchPoint the branch point uniquely identifying a branch and a point in time. 
	 * @param componentId the unique identifier of the component.
	 * @return the unique ID of a component who's icon should be associated with the current component.
	 */
	@Nullable K getIconId(final IBranchPoint branchPoint, final K componentId);
	
	/**
	 * Returns with the ID of the component whos's icon is associated with the given terminology independent component.
	 * <p>NOTE: this method only searches for icon IDs only the most recent state of the given branch. If client has to lookup 
	 * icon IDs for historical revisions of a particular component, then clients should use {@link #getIconId(IBranchPoint, Object)} instead.  
	 * @param branchPath the branch path for the operation.
	 * @param componentId the unique component ID.
	 * @return the unique ID of a component who's icon should be associated with the current component. 
	 */
	@Nullable K getIconId(final IBranchPath branchPath, final K componentId);
	
	/**
	 * No operation component icon ID provider.
	 */
	public static final class NoopComponentIconIdProvider {

		/**Noop instance.*/
		private static final IComponentIconIdProvider<Object> INSTANCE = new IComponentIconIdProvider<Object>() {
			@Override public Object getIconId(final IBranchPoint branchPoint, final Object componentId) {
				return componentId;
			}
			@Override public Object getIconId(final IBranchPath branchPath, final Object componentId) {
				return componentId;
			};
		};
		
		/**Returns with the {@link #INSTANCE NOOP} instance.*/
		@SuppressWarnings("unchecked")
		public static <K> IComponentIconIdProvider<K> getInstance() {
			return (IComponentIconIdProvider<K>) INSTANCE;
		}
	}
	
}