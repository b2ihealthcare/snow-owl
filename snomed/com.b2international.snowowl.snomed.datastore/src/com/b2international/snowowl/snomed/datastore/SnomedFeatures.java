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
package com.b2international.snowowl.snomed.datastore;

public final class SnomedFeatures {
	
	/**
	 * Creates a feature toggle key for SNOMED CT import
	 * 
	 * @param branchPath - the branch where the import is executed
	 * @return a unique identifier for the import feature
	 */
	public static String getImportFeatureToggle(String branchPath) {
		return String.format("%s-%s.import", SnomedDatastoreActivator.REPOSITORY_UUID, branchPath);
	}
	
	/**
	 * Creates a feature toggle key for SNOMED CT classification
	 * 
	 * @return a unique identifier for the classification feature 
	 */
	public static String getClassifyFeatureToggle() {
		return String.format("classify.%s", SnomedDatastoreActivator.REPOSITORY_UUID);
	}
	
	private SnomedFeatures() { /* prevent instantiation */ }
}
