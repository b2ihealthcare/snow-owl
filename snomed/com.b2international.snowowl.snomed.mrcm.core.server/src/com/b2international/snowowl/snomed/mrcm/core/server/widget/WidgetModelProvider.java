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
package com.b2international.snowowl.snomed.mrcm.core.server.widget;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.snor.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.mrcm.core.widget.ConceptModelConstraintToWidgetModelConverter;
import com.b2international.snowowl.snomed.mrcm.core.widget.IWidgetModelProvider;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;

/**
 * @deprecated - use {@link SnomedRequests#prepareGetApplicablePredicates(String, Set, Set, Set)} with proper parameters instead
 */
public enum WidgetModelProvider implements IWidgetModelProvider {
	
	INSTANCE;

	@Override
	public ConceptWidgetModel createConceptWidgetModel(final IBranchPath branchPath, final Iterable<String> ruleParentIds, final String ruleRefSetId) {
		final Collection<SnomedConstraintDocument> applicablePredicates = SnomedRequests.prepareGetApplicablePredicates(branchPath.getPath(), Collections.<String>emptySet(), newHashSet(ruleParentIds), Collections.singleton(ruleRefSetId)).getSync();
		return ConceptModelConstraintToWidgetModelConverter.processConstraints(branchPath, applicablePredicates);
	}
	
	@Override
	public ConceptWidgetModel createConceptWidgetModel(final IBranchPath branchPath, final String conceptId, final String ruleRefSetId) {
		final Collection<SnomedConstraintDocument> applicablePredicates = SnomedRequests.prepareGetApplicablePredicates(branchPath.getPath(), Collections.singleton(conceptId), getAncestors(branchPath.getPath(), conceptId), Collections.singleton(ruleRefSetId)).getSync();
		return ConceptModelConstraintToWidgetModelConverter.processConstraints(branchPath, applicablePredicates);
	}

	private Set<String> getAncestors(String branch, String conceptId) {
		return SnomedRequests.prepareGetConcept()
				.setComponentId(conceptId)
				.build(branch)
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(ISnomedConcept.GET_ANCESTORS)
				.getSync();
	}
}
 