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
package com.b2international.snowowl.datastore.history;

import java.util.Collection;

import com.b2international.snowowl.core.api.IHistoryInfo;


/**
 * Representation of a service responsible for supplying historical information
 * for a terminology independent component.
 *
 */
public interface HistoryService {

	/**
	 * Returns with a collection of historical information for a terminology independent component
	 * represented as the {@link HistoryInfoConfiguration configuration}.
	 * @param configuration the configuration clearly identifying a terminology independent
	 * component and the branch configuration of the caller.
	 * @return a collection of historical information for a terminology independent component.
	 */
	Collection<IHistoryInfo> getHistory(final HistoryInfoConfiguration configuration);
	
}