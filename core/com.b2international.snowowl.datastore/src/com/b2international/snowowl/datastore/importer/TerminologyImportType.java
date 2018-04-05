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
package com.b2international.snowowl.datastore.importer;

/**
 * Import type enum for imports where the user can select the type of the import.
 * 
 * @since Snow&nbsp;Owl 3.0
 */
public enum TerminologyImportType {
	
	CLEAR ("Clear existing database and import into empty database"),
	MERGE ("Merge components into existing database"),
	REPLACE ("Replace attributes of the found components in the database");
	
	private final String label;
	
	private TerminologyImportType(final String label) {
		this.label = label;
	}
	
	@Override
	public String toString() {
		return label;
	}

	public boolean isClear() {
		return CLEAR == this;
	}
	
	public boolean isMerge() {
		return MERGE == this;
	}
	
	public boolean isReplace() {
		return REPLACE == this;
	}
	
}