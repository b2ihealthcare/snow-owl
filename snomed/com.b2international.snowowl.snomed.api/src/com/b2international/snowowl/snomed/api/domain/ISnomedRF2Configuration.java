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
package com.b2international.snowowl.snomed.api.domain;

import com.b2international.snowowl.api.domain.IBranchAwareConfig;

/**
 * Common super interface for RF2 based import-export configurations.
 * 
 * @since 2.0
 */
public interface ISnomedRF2Configuration extends IBranchAwareConfig {

	/**
	 * Returns the RF2 release type of the current export configuration.
	 * 
	 * @return the desired RF2 release type
	 */
	Rf2ReleaseType getRf2ReleaseType();
	
}
