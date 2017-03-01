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
package com.b2international.snowowl.snomed.mrcm.core.server.validator;

import java.util.Collection;
import java.util.List;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ModeledWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipGroupWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipWidgetBean;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * @since 4.3
 */
public class RelationshipWidgetBeanValidator implements ModeledWidgetBeanValidator {

	@Override
	public void validate(final IBranchPath branch, ConceptWidgetBean concept, ValidationStatusReporter reporter) {
		final List<ModeledWidgetBean> groups = concept.getProperties().getElements();

		boolean hasActiveIsA = false;
		final Multimap<String, String> isaTypeToParentIdMap = HashMultimap.create();

		final LoadingCache<String, String> conceptToLabelMap = CacheBuilder.newBuilder().build(new CacheLoader<String, String>() {
			@Override
			public String load(String key) throws Exception {
				return getLabel(key, branch);
			}
		});
		
		for (final RelationshipGroupWidgetBean group : Iterables.filter(groups, RelationshipGroupWidgetBean.class)) {

			final List<ModeledWidgetBean> relationships = group.getElements();

			for (final RelationshipWidgetBean relationshipWidgetBean : Iterables.filter(relationships, RelationshipWidgetBean.class)) {
				if (!relationshipWidgetBean.isValid()) {
					continue;
				}

				if (relationshipWidgetBean.isUnsanctioned()) {
					reporter.warning(relationshipWidgetBean, "No concept model rule found for this property.");
				}

				// The rest of the loop only deals with IS A relationships
				final String destinationId = relationshipWidgetBean.getSelectedValue().getId();
				if (relationshipWidgetBean.isIsA()) {
					final String characteristicTypeId = relationshipWidgetBean.getSelectedCharacteristicType().getId();
					final String characteristicTypeLabel = conceptToLabelMap.getUnchecked(characteristicTypeId);
					final Collection<String> parents = isaTypeToParentIdMap.get(characteristicTypeId);
					if (parents.contains(destinationId)) {
						reporter.error(relationshipWidgetBean, "The concept has duplicate (%s) Is-a relationships.", characteristicTypeLabel);
					} else {
						isaTypeToParentIdMap.put(characteristicTypeId, destinationId);
					}

					// check for active IS_A in ungrouped properties
					if (!hasActiveIsA && 0 == group.getGroupNumber()) {
						hasActiveIsA = true;
					}

					final String snomedConceptId = concept.getConceptId();
					if (destinationId.equals(snomedConceptId)) {
						reporter.error(relationshipWidgetBean, "For Is-a relationships, the destination should not be the same as the edited concept.");
					} else {
						if (!DescriptionWidgetBeanValidator.exists(branch, snomedConceptId)) {
							continue; // new concept. it does not exist in store. cannot cause cycle.
						}
						
						final ApplicationContext context = ApplicationContext.getInstance();
						final List<SnomedConcept> snomedConcepts = SnomedRequests.prepareSearchConcept()
								.all()
								.filterByActive(true)
								.filterByAncestor(snomedConceptId)
								.filterById(destinationId)
								.setLocales(context.getService(LanguageSetting.class).getLanguagePreference())
								.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch.getPath())
								.execute(context.getService(IEventBus.class))
								.getSync()
								.getItems();
						
						if (snomedConcepts.size() != 0) {
							reporter.error(relationshipWidgetBean,
									"For Is-a relationships, the destination should not be the descendant of the edited concept.");
						}
					}
				}
			}
		}

		// root concept and inactive concepts do not need active IS_A relationship
		if (!hasActiveIsA && !Concepts.ROOT_CONCEPT.equals(concept.getConceptId()) && concept.isActive()) {
			reporter.error(concept, "Concept must have at least one active ungrouped 'Is a' relationship.");
		}
	}
	
	private String getLabel(final String id, final IBranchPath branchPath) {
		return SnomedRequests.prepareGetConcept(id)
				.setLocales(getLocales())
				.setExpand("pt()")
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branchPath.getPath())
				.execute(getEventBus())
				.getSync().getPt().getTerm();
	}

	private List<ExtendedLocale> getLocales() {
		return ApplicationContext.getInstance().getService(LanguageSetting.class).getLanguagePreference();
	}

	private IEventBus getEventBus() {
		return ApplicationContext.getInstance().getService(IEventBus.class);
	}

}
