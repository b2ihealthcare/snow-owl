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
package com.b2international.snowowl.datastore.server.history;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;

import org.slf4j.Logger;

import com.b2international.snowowl.core.api.IHistoryInfo;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.history.HistoryInfoConfiguration;
import com.b2international.snowowl.datastore.history.HistoryService;

/**
 * Delegates computing component history to {@link HistoryInfoProvider}.
 */
public class HistoryServiceImpl implements HistoryService {

	private static final Logger LOGGER = getLogger(HistoryServiceImpl.class);
	
	@Override
	public Collection<IHistoryInfo> getHistory(final HistoryInfoConfiguration configuration) {
		checkNotNull(configuration, "History configuration object may not be null.");
		
		try {
			return HistoryInfoProvider.INSTANCE.getHistoryInfo(configuration);
		} catch (final SnowowlServiceException e) {
			LOGGER.error("Error while getting history for component: '" + configuration.getStorageKey() + "'.", e);
			return emptyList();
		}
	}
}
