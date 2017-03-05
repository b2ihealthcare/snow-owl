/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.List;

import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.datastore.FullySpecifiedNameUniquenessValidator;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DescriptionWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ModeledWidgetBean;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * @since 4.3
 */
public class DescriptionWidgetBeanValidator implements ModeledWidgetBeanValidator {

	@Override
	public void validate(IBranchPath branch, ConceptWidgetBean concept, ValidationStatusReporter reporter) {
		int numberOfFsns = 0;
		int numberOfPreferredTerms = 0;
		final List<ModeledWidgetBean> descriptions = concept.getDescriptions().getElements();
		for (final DescriptionWidgetBean description : Iterables.filter(descriptions, DescriptionWidgetBean.class)) {
			if (description.isFsn()) {
				numberOfFsns++;
				if (Strings.isNullOrEmpty(description.getTerm())) {
					reporter.error(description, "Fully specified name should be specified.");
				} else {
					//check fsn uniqueness for unpersisted concept.
					if (!exists(branch, concept.getConceptId())) {
						final IStatus status = new FullySpecifiedNameUniquenessValidator(branch).validate(description.getTerm());
						if (!status.isOK()) {
							reporter.error(description, status.getMessage());	
						}
					}
				}
				
			} else if (description.isPreferred()) {
				numberOfPreferredTerms++;
				if (Strings.isNullOrEmpty(description.getTerm())) {
					reporter.error(description, "Preferred term should be specified.");
				}
			}
		}
		if (numberOfFsns > 1) {
			reporter.error(concept, "Concept should have exactly one active fully specified name.");
		}
		if (numberOfPreferredTerms != 1) {
			reporter.error(concept, "Concept should have one active preferred synonym.");
		}
	}

	static boolean exists(IBranchPath branchPath, String conceptId) {
		return SnomedRequests.prepareSearchConcept()
				.setLimit(0)
				.filterById(conceptId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync().getTotal() > 0;
	}
	
}
