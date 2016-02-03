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

import java.io.Serializable;
import java.util.Collection;

import com.b2international.snowowl.core.api.index.IIndexEntry;

/**
 * Terminology independent reference set membership lookup service.
 * @param <K> serializable unique identifier of the component.
 */
public interface IRefSetMembershipLookupService<K extends Serializable> {

	/**
	 * Returns with a collection of {@link IIndexEntry members} whose application specific terminology component
	 * ID and the unique component identifier matches with the given arguments. 
	 * @param terminologyComponentId the application specific terminology component identifier.
	 * @param componentId the unique component ID.
	 * @return a collection of members.
	 */
	Collection<IIndexEntry> getMembers(final String terminologyComponentId, final K componentId);

	/**
	 * The reference membership search result provider singleton.
	 */
	static enum RefSetMembershipSearchResultProvider  {

		/**
		 * The search result provider instance.
		 */
		INSTANCE;
		
		/**
		 * Returns with a collection of reference set members and their 
		 * globally unique identifier referencing to a terminology independent component
		 * identified by the specified <b>terminologyComponentId</b> and the <b>componentId</b>.
		 * @param terminologyComponentId the terminology component identifier of the component.
		 * @param componentId the identifier of the component.
		 * @return a collection of reference set members with their globally unique storage key referencing to the component.
		 * @deprecated - UNSUPPORTED API
		 */
		public Collection<IIndexEntry> getRefSetMembers(final String terminologyComponentId, final String componentId) {
			throw new UnsupportedOperationException();
//			final Set<IIndexEntry> result = Sets.newHashSet();
//			final Collection<IRefSetMembershipLookupService<String>> lookupServices = 
//					CoreTerminologyBroker.getInstance().getRefSetMembershipLookupServices();
//			
//			for (final IRefSetMembershipLookupService<String> service : lookupServices) {
//				result.addAll(service.getMembers(terminologyComponentId, componentId));
//			}
//			return Collections.unmodifiableSet(result);
		}
		
		
	} 
	
}