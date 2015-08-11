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
package com.b2international.snowowl.snomed.mrcm.core.server.validator;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.mrcm.core.validator.IWidgetBeanValidator;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ModeledWidgetBean;
import com.google.common.collect.Multimap;
import com.google.inject.Provider;

/**
 * Server side {@link ConceptWidgetBean} validator.
 */
public class WidgetBeanValidator implements IWidgetBeanValidator {

	private Provider<SnomedTerminologyBrowser> browserProvider;

	public WidgetBeanValidator(Provider<SnomedTerminologyBrowser> browserProvider) {
		this.browserProvider = browserProvider;
	}

	/**
	 * Validates a {@link ConceptWidgetBean} and returns with a multimap of status informations. <br>
	 * Keys are the {@link ModeledWidgetBean} instances. <br>
	 * Values are the associated validation status.
	 * 
	 * @param concept
	 *            the {@link ConceptWidgetBean} to validate. Represents a SNOMED&nbsp;CT concept.
	 * @return a multimap of validation status. Can be empty.
	 */
	@Override
	public Multimap<ModeledWidgetBean, IStatus> validate(IBranchPath branchPath, ConceptWidgetBean concept) {
		final SnomedTerminologyBrowser browser = browserProvider.get();
		final ValidationStatusReporter reporter = new DefaultValidationStatusReporter();
		
		final Collection<ModeledWidgetBeanValidator> validators = newHashSet();
		validators.add(new DescriptionWidgetBeanValidator(browser));
		validators.add(new RelationshipWidgetBeanValidator(browser));
		
		for (ModeledWidgetBeanValidator validator : validators) {
			validator.validate(branchPath, concept, reporter);
		}
		
		return reporter.getReport();
	}

}