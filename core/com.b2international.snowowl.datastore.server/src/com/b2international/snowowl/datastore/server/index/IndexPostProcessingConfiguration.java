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
package com.b2international.snowowl.datastore.server.index;

import java.util.concurrent.atomic.AtomicBoolean;

import com.b2international.snowowl.core.api.IBranchPath;

/**
 * @since 4.3
 */
public class IndexPostProcessingConfiguration implements IIndexPostProcessingConfiguration {

	private IBranchPath branchPath;
	private long timestamp;
	private AtomicBoolean requiresPostProcessing;

	public IndexPostProcessingConfiguration(IBranchPath branchPath, long timestamp, AtomicBoolean requiresPostProcessing) {
		this.branchPath = branchPath;
		this.timestamp = timestamp;
		this.requiresPostProcessing = requiresPostProcessing;
	}
	
	@Override
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public AtomicBoolean getRequiresPostProcessing() {
		return requiresPostProcessing;
	}

	@Override
	public IBranchPath getBranchPath() {
		return branchPath;
	}
	
}
