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

import java.util.List;

import org.eclipse.core.runtime.IStatus;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.datastore.FullySpecifiedNameUniquenessValidator;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.LanguageConfiguration;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DescriptionWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ModeledWidgetBean;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * @since 4.3
 */
public class DescriptionWidgetBeanValidator implements ModeledWidgetBeanValidator {

	private SnomedTerminologyBrowser browser;
	
	public DescriptionWidgetBeanValidator(SnomedTerminologyBrowser browser) {

		this.browser = browser;
	}
	
	@Override
	public void validate(IBranchPath branch, ConceptWidgetBean concept, ValidationStatusReporter reporter) {
		
		LanguageConfiguration languageConfiguration = ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class).getLanguageConfiguration();
		String languagePreferenceRefsetId = languageConfiguration.getLanguageRefSetId(branch);
		
		int numberOfPreferredTerms = 0;
		
		final List<ModeledWidgetBean> descriptions = concept.getDescriptions().getElements();
		for (final DescriptionWidgetBean description : Iterables.filter(descriptions, DescriptionWidgetBean.class)) {
			if (description.isFsn()) {
				if (Strings.isNullOrEmpty(description.getTerm())) {
					reporter.error(description, "Fully specified name should be specified.");
				} else {
					//check fsn uniqueness for unpersisted concept.
					if (null == browser.getConcept(branch, concept.getConceptId())) {
						final IStatus status = new FullySpecifiedNameUniquenessValidator().validate(description.getTerm());
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
		
		if (numberOfPreferredTerms == 0) {
			reporter.warning(concept, "Concept does not have active preferred synonym for the configured " 
					+ languageConfiguration.getLanguageCode() + " language.");
		}
		
		//load all the preferred synonyms as well
		IEventBus eventBus = ApplicationContext.getServiceForClass(IEventBus.class);
		
		List<Long> languagePreferenceList = Lists.newArrayList(Long.valueOf(languagePreferenceRefsetId), 
				Long.valueOf(Concepts.REFSET_LANGUAGE_TYPE_UK),
				Long.valueOf(Concepts.REFSET_LANGUAGE_TYPE_US));

		SnomedDescriptions preferredSynonyms = SnomedRequests.prepareSearchDescription()
			.filterByConceptId(concept.getConceptId())
			.filterByActive(true)
			.filterByType("<<" + Concepts.SYNONYM)
			.all()
			.filterByLanguageRefSetIds(languagePreferenceList)
			.filterByAcceptability(Acceptability.PREFERRED)
			.build(branch.getPath()).executeSync(eventBus);
		
		numberOfPreferredTerms = numberOfPreferredTerms + preferredSynonyms.getTotal();
		
		if (numberOfPreferredTerms == 0) {
			reporter.error(concept, "Concept should have at least one active preferred synonym.");
		}
	}
	
}
