/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.datastore.cdo.CDOIDUtils.checkId;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;

/**
 * {@link HistoryInfoConfiguration} implementation. 
 */
public final class HistoryInfoConfigurationImpl implements HistoryInfoConfiguration, Serializable {

	private static final long serialVersionUID = 737924958133045175L;
	
	private final long storageKey;
	private final String terminologyComponentId;
	private final IBranchPath branchPath;
	private final String componentId;

	public static HistoryInfoConfiguration create(final IBranchPath branchPath, final long storageKey, final ComponentIdentifier componentIdentifier) {
		checkNotNull(branchPath, "branchPath");
		String terminologyComponentId = CoreTerminologyBroker.getInstance().getTerminologyComponentId(componentIdentifier.getTerminologyComponentId());
		return new HistoryInfoConfigurationImpl(storageKey, componentIdentifier.getComponentId(), terminologyComponentId, branchPath);
	}

	public HistoryInfoConfigurationImpl(final long storageKey, final String componentId, final String terminologyComponentId, final IBranchPath branchPath) {
		checkArgument(checkId(storageKey), "Invalid storage key of: " + storageKey);
		this.storageKey = storageKey;
		this.componentId = checkNotNull(componentId, "componentId");
		this.terminologyComponentId = checkNotNull(terminologyComponentId, "terminologyComponentId");
		this.branchPath = checkNotNull(branchPath, "branchPath");
	}
	
	@Override
	public long getStorageKey() {
		return storageKey;
	}

	@Override
	public String getComponentId() {
		return componentId;
	}
	
	@Override
	public String getTerminologyComponentId() {
		return terminologyComponentId;
	}
	
	@Override
	public IBranchPath getBranchPath() {
		return branchPath;
	}
	
}