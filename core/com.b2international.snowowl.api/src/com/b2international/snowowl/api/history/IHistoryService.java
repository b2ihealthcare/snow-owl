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
package com.b2international.snowowl.api.history;

import java.util.List;

import com.b2international.snowowl.api.domain.IComponentRef;
import com.b2international.snowowl.api.exception.ComponentNotFoundException;
import com.b2international.snowowl.api.history.domain.IHistoryInfo;

/**
 * Terminology independent interface of the History Service.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link #getHistory(IComponentRef) <em>Retrieve history information</em>}</li>
 * </ul>
 * 
 */
public interface IHistoryService {

	/**
	 * Returns information about all historical modifications made on the specified component.
	 * @param ref the reference pointing to the component (may not be {@code null})
	 * @return an object wrapping historical information, describing all changes made to the component
	 * @throws ComponentNotFoundException if the component reference can not be resolved
	 */
	List<IHistoryInfo> getHistory(final IComponentRef ref);
}