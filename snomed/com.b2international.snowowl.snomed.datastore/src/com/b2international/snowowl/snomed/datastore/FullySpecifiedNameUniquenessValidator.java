/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.validation.ComponentValidationStatus;
import com.b2international.snowowl.datastore.request.SearchResourceRequest;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * Data binding {@link IValidator validator} for checking the uniqueness of a fully specified name description.
 * 
 */
public class FullySpecifiedNameUniquenessValidator implements IValidator {

	private final IBranchPath branchPath;

	public FullySpecifiedNameUniquenessValidator(final IBranchPath branchPath) {
		this.branchPath = checkNotNull(branchPath, "branchPath");
	}
	
	@Override
	public IStatus validate(Object value) {

		if (!(value instanceof String)) {
			return new ComponentValidationStatus(IStatus.ERROR, SnomedDatastoreActivator.PLUGIN_ID, "String value expected."); 
		}
		
		final String fsn = ((String) value).trim();
		
		if (fsn.isEmpty()) {
			return new ComponentValidationStatus(IStatus.ERROR, SnomedDatastoreActivator.PLUGIN_ID, "Fully specified name must be specified.");
		}
		
		if (fsn.length() < 2) {
			return new ComponentValidationStatus(IStatus.ERROR, SnomedDatastoreActivator.PLUGIN_ID, "Fully specified name should be at least two characters long.");
		}
		
		final SnomedDescriptions descriptions = SnomedRequests.prepareSearchDescription()
				.setLocales(getLocales())
				.filterByActive(true)
				.filterByTerm(fsn)
				.filterByType(Concepts.FULLY_SPECIFIED_NAME)
				.sortBy(SearchResourceRequest.SCORE)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getEventBus())
				.getSync();
		
		if (descriptions.getItems().size() != 0) {
			for (final SnomedDescription description : descriptions.getItems()) {
				if (fsn.equals(description.getTerm())) {
					return new ComponentValidationStatus(IStatus.ERROR, SnomedDatastoreActivator.PLUGIN_ID,
							"Fully specified name is not unique.");
				}
			}
		}
			
		return ComponentValidationStatus.OK_STATUS;
	}

	private IEventBus getEventBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}

	private List<ExtendedLocale> getLocales() {
		return ApplicationContext.getInstance().getService(LanguageSetting.class).getLanguagePreference();
	}

}