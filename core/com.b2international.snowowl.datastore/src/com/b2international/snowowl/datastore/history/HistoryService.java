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
 * Service interface for retrieving historical changes related to a component.
 */
public interface HistoryService {

	/**
	 * Computes history for the specified history configuration.
	 * <p>
	 * History configuration objects capture the component's type, identifier, storage key and the branch path on which
	 * history should be computed.
	 * 
	 * @param configuration the history configuration object (may not be {@code null})
	 * @return a collection of history information rows describing changes to the component itself, as well as any
	 * closely related components
	 */
	Collection<IHistoryInfo> getHistory(final HistoryInfoConfiguration configuration);
}
