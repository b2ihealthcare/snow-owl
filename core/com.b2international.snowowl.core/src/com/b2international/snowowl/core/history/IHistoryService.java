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
package com.b2international.snowowl.core.history;

import java.util.List;

import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.core.history.domain.IHistoryInfo;

/**
 * History service implementations provide a list of past modifications for a particular component type.
 * <p>
 * The detail of the returned list can be coarse eg. for RF2 imports and promoted tasks (which usually have a single,
 * large commit), or more fine-grained in case of edits on a task (described with multiple, smaller commits).
 */
public interface IHistoryService {

	/**
	 * Collects component history, which describes past modifications for the specified component.
	 * 
	 * @param ref the {@code IComponentRef} pointing to the component (may not be {@code null})
	 * 
	 * @return an object wrapping historical information, describing all changes made to the component
	 * 
	 * @throws CodeSystemNotFoundException        if a code system with the given short name is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for the code system with the given identifier
	 *                                            is not registered
	 * @throws ComponentNotFoundException         if the component identifier does not match any component on the given task
	 */
	List<IHistoryInfo> getHistory(String branch, String componentId);
}
