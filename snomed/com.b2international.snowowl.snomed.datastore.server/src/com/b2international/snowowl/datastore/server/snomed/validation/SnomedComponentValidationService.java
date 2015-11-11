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
package com.b2international.snowowl.datastore.server.snomed.validation;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.datastore.server.validation.ComponentValidationService;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.validation.ISnomedComponentValidationService;
import com.google.common.base.Predicate;

/**
 * Server side SNOMED&nbsp;CT component validation service interface.
 * 
 */
public class SnomedComponentValidationService extends ComponentValidationService<SnomedConceptIndexEntry> implements ISnomedComponentValidationService {

	private static final Predicate<SnomedConceptIndexEntry> ACTIVE_CONCEPT_PREDICATE = new Predicate<SnomedConceptIndexEntry>() {
		public boolean apply(final SnomedConceptIndexEntry concept) {
			return concept.isActive();
		}
	};
	
	@Override
	protected Collection<ComponentValidationDiagnostic> doValidateAll(final IBranchPath branchPath, final IProgressMonitor monitor) {
		final SnomedTerminologyBrowser snomedTerminologyBrowser = ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class);
		final Iterable<SnomedConceptIndexEntry> concepts = filter(snomedTerminologyBrowser.getConcepts(branchPath), ACTIVE_CONCEPT_PREDICATE);
		return validate(branchPath, newArrayList(concepts), monitor);
	}
}