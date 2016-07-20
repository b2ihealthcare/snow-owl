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
package com.b2international.snowowl.snomed.importer.net4j;

import java.util.List;
import java.util.Set;

import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SnomedImportResult {

	private final List<SnomedConceptDocument> visitedConcepts = Lists.newArrayList();
	private final Set<SnomedValidationDefect> validationDefects = Sets.newHashSet();
	
	public List<SnomedConceptDocument> getVisitedConcepts() {
		return visitedConcepts;
	}
	
	public boolean hasValidationDefects() {
		return !validationDefects.isEmpty();
	}
	
	public Set<SnomedValidationDefect> getValidationDefects() {
		return validationDefects;
	}
}