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

import java.util.Collection;

/**
 * Strategy interface for component set membership search result providers.
 * 
 * @param <S> membership lookup service type
 * 
 * @see MappingSetMembershipSearchResultProvider
 * @see ValueSetMembershipSearchResultProvider
 * 
 * @since 3.0
 */
public interface MembershipSearchResultProviderStrategy<S> {

	/**
	 * Returns components from the given service.
	 * 
	 * @param service
	 * @param terminologyComponentId
	 * @param componentId
	 * @return
	 */
	Collection<IComponent<String>> getComponents(S service, String terminologyComponentId, String componentId);

}