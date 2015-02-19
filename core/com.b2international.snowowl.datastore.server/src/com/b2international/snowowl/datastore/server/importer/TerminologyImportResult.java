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
package com.b2international.snowowl.datastore.server.importer;

import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;

import com.b2international.snowowl.datastore.importer.TerminologyImportValidationDefect;
import com.google.common.collect.Sets;

/**
 * Import model for terminology import that stores the visited components
 * and the validation defects.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public class TerminologyImportResult {
	
	private Set<CDOObject> visitedComponents;
	private Set<TerminologyImportValidationDefect> validationDefects;
	
	public TerminologyImportResult() {
		visitedComponents = Sets.newHashSet();
		validationDefects = Sets.newHashSet();
	}
	
	public Set<CDOObject> getVisitedComponents() {
		return visitedComponents;
	}
	
	public boolean hasValidationDefects() {
		return !validationDefects.isEmpty();
	}
	
	public Set<TerminologyImportValidationDefect> getValidationDefects() {
		return validationDefects;
	}
}