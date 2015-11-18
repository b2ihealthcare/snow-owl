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
package com.b2international.snowowl.snomed.validation.constraints.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationConstraint;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnostic;
import com.b2international.snowowl.core.validation.ComponentValidationDiagnosticImpl;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * No concept may have more than one active fully-specified name in a particular language.
 * Note: "en", "en-gb", "en-au" and "en-us" should be treated as the same language.
 * 
 */
public class SnomedConceptOneActiveFsnPerLanguageConstraint extends ComponentValidationConstraint<SnomedConceptIndexEntry> {

	public static final String ID = "com.b2international.snowowl.snomed.validation.constraints.component.SnomedConceptOneActiveFsnPerLanguageConstraint";
	
	private static final class LargerThanOnePredicate implements Predicate<Integer> {
		@Override
		public boolean apply(Integer input) {
			return input > 1;
		}
	}

	private static final String EN_LANGUAGE_CODE = "en";
	private static final Set<String> EN_ALTERNATIVE_LANGUAGE_CODES = ImmutableSet.of("en-gb", "en-us", "en-au", "en-sg");
	
	// predicate for filtering out language codes, which have more than one FSN
	private final Predicate<Integer> largerThanOnePredicate = new LargerThanOnePredicate();
	
	@Override
	public ComponentValidationDiagnostic validate(IBranchPath branchPath, SnomedConceptIndexEntry component) {
		// TODO: put description language codes into the index to avoid using CDO
		// look up concept in CDO
		ICDOConnection connection = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(SnomedPackage.eINSTANCE);
		CDOBranch branch = connection.getBranch(branchPath);
		CDOView view = null;
		try {
			view = connection.createView(branch);
			SnomedConceptLookupService lookupService = new SnomedConceptLookupService();
			Concept concept = lookupService.getComponent(component.getId(), view);
			
			Map<String, Integer> languageToActiveFSNCountMap = new HashMap<String, Integer>();
			
			for (Description description : concept.getDescriptions()) {
				if (description.getType().getId().equals(SnomedConstants.Concepts.FULLY_SPECIFIED_NAME)
						&& description.isActive()) {
					// normalize country-specific "en" codes
					String languageCode = getNormalizedLanguageCode(description);
					Integer activeFSNCountFromMap = languageToActiveFSNCountMap.get(languageCode);
					// add language code if not already present in map, or increment value
					if (activeFSNCountFromMap == null)
						languageToActiveFSNCountMap.put(languageCode, 1);
					else
						languageToActiveFSNCountMap.put(languageCode, ++activeFSNCountFromMap);
				}
			}
			
			// filtered map will only contain entries, where the value is greater than one, and hence should be reported
			Map<String, Integer> filteredMap = Maps.filterValues(languageToActiveFSNCountMap, largerThanOnePredicate);
			if (filteredMap.size() > 0) {
				StringBuffer languageCodeMessageBuffer = new StringBuffer();
				for (Iterator<String> iterator = filteredMap.keySet().iterator(); iterator.hasNext();) {
					String languageCode = iterator.next();
					languageCodeMessageBuffer.append(languageCode);
					if (iterator.hasNext())
						languageCodeMessageBuffer.append(", ");
					return new ComponentValidationDiagnosticImpl(component.getId(), languageCodeMessageBuffer.toString(), ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, error());
				}
			}
			
			return createOk(component.getId(), ID, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
		} finally {
			LifecycleUtil.deactivate(view);
		}
	}

	private String getNormalizedLanguageCode(Description description) {
		if (EN_ALTERNATIVE_LANGUAGE_CODES.contains(description.getLanguageCode())) {
			return EN_LANGUAGE_CODE;
		} else {
			return description.getLanguageCode();
		}
	}
}