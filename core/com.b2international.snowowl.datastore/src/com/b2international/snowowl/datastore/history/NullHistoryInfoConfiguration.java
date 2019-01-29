/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.commons.StringUtils.EMPTY_STRING;
import static com.b2international.snowowl.core.CoreTerminologyBroker.UNSPECIFIED;
import static com.b2international.snowowl.datastore.cdo.CDOUtils.NO_STORAGE_KEY;

import java.util.Collections;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.NullBranchPath;

/**
 * Null implementation of a {@link HistoryInfoConfiguration historical information configuration}.
 * <p>With this configuration {@link HistoryService history service} will provide a zero 
 * element {@link HistoryInfo historical information}.
 *
 */
public enum NullHistoryInfoConfiguration implements HistoryInfoConfiguration {

	INSTANCE;
	
	@Override
	public long getStorageKey() {
		return NO_STORAGE_KEY;
	}

	@Override
	public String getComponentId() {
		return EMPTY_STRING;
	}

	@Override
	public String getTerminologyComponentId() {
		return UNSPECIFIED;
	}

	@Override
	public IBranchPath getBranchPath() {
		return NullBranchPath.INSTANCE;
	}
	
	@Override
	public List<ExtendedLocale> getLocales() {
		return Collections.emptyList();
	}
	
	/**
	 * Returns with {@code true} if the configuration argument is either {@code null} or
	 * a {@link NullHistoryInfoConfiguration}. Otherwise returns with {@code false}.
	 * @param configuration the configuration to check.
	 * @return {@code true} if {@code null} configuration. Otherwise {@code false}.
	 */
	public static boolean isNullConfiguration(final HistoryInfoConfiguration configuration) {
		return null == configuration || configuration instanceof NullHistoryInfoConfiguration;
	}

}