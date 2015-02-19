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
import com.b2international.snowowl.datastore.BranchPathUtils;

/**
 * Configuration for an index post processing operation.
 */
public interface IIndexPostProcessingConfiguration {

	/**Returns with the timestamp of the configuration.*/
	long getTimestamp();
	
	/**Returns with a boolean representing whether the index post processing has to be performed or not.*/
	AtomicBoolean getRequiresPostProcessing();
	
	/**Returns with the branch path.*/
	IBranchPath getBranchPath();
	
	/**Default timestamp. Value: {@value}*/
	int DEFAULT_TIMESTAMP = Integer.valueOf(0);
	
	/**Default configuration implementation.*/
	IIndexPostProcessingConfiguration DEFAULT_CONFIGURATION = new IIndexPostProcessingConfiguration() {
		@Override public long getTimestamp() {
			return DEFAULT_TIMESTAMP;
		}
		@Override public AtomicBoolean getRequiresPostProcessing() {
			return new AtomicBoolean(false);
		};
		@Override public IBranchPath getBranchPath() {
			return BranchPathUtils.createMainPath();
		};
	};
	
}