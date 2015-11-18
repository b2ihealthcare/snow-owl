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
package com.b2international.snowowl.snomed.datastore;

import java.util.Iterator;
import java.util.Set;

import com.b2international.commons.concurrent.ConcurrentCollectionUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorClientService;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ConceptSetProcessorFactory;
import com.b2international.snowowl.snomed.datastore.snor.ConstraintFormIsApplicableForValidationPredicate;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConstraintBase;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Singleton class to provide access to the Machine Readable Concept Model.
 * 
 */
public enum ConceptModelProvider implements IConceptModelProvider {
	
	/**
	 * The singleton instance.
	 */
	INSTANCE;
	
	@Override
	public Set<ConstraintBase> getAllConstraints(final ConceptModel conceptModel, final String conceptId, final SnomedClientTerminologyBrowser terminologyBrowser, final SnomedClientRefSetBrowser refSetBrowser) {
		final SnomedConceptIndexEntry concept = terminologyBrowser.getConcept(conceptId);
		final IEscgQueryEvaluatorClientService evaluatorService = ApplicationContext.getInstance().getService(IEscgQueryEvaluatorClientService.class);
		final SnomedClientIndexService indexService = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
		return getMatchingConstraints(concept, conceptModel.getConstraints(), evaluatorService, terminologyBrowser, refSetBrowser, indexService);
	}

	@Override
	public Set<ConstraintBase> getConstraintsForValidation(final ConceptModel conceptModel, final String conceptId, final SnomedClientTerminologyBrowser terminologyBrowser, final SnomedClientRefSetBrowser refSetBrowser) {
		final SnomedConceptIndexEntry concept = terminologyBrowser.getConcept(conceptId);
		final Predicate<ConstraintBase> filterPredicate = Predicates.and(
				new ActiveConceptModelComponentPredicate(), 
				new ConstraintFormIsApplicableForValidationPredicate());
		final Set<ConstraintBase> filteredConstraints = Sets.newHashSet(Iterables.filter(conceptModel.getConstraints(), filterPredicate));
		final IEscgQueryEvaluatorClientService evaluatorService = ApplicationContext.getInstance().getService(IEscgQueryEvaluatorClientService.class);
		final SnomedClientIndexService indexService = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
		return getMatchingConstraints(concept, filteredConstraints, evaluatorService, terminologyBrowser, refSetBrowser, indexService);
	}

	private Set<ConstraintBase> getMatchingConstraints(final SnomedConceptIndexEntry concept, final Iterable<ConstraintBase> constraints, IEscgQueryEvaluatorClientService evaluatorService, 
			final SnomedClientTerminologyBrowser terminologyBrowser, 
			final SnomedClientRefSetBrowser refSetBrowser, 
			final SnomedClientIndexService indexService) {
		
		final Iterator<ConstraintBase> matchingConstraints = ConcurrentCollectionUtils.filter(constraints.iterator(), new Predicate<ConstraintBase>() {
			@Override public boolean apply(final ConstraintBase constraint) {
				if (constraint instanceof AttributeConstraint) {
					final AttributeConstraint attributeConstraint = (AttributeConstraint) constraint;
					final ConceptSetDefinition domainConceptSetDefinition = attributeConstraint.getDomain();
					
					//can happen if user forgot to specify the domain for the MRCM rule (attribute constraint)
					if (null == domainConceptSetDefinition) {
						return false;
					}
					boolean contains = ConceptSetProcessorFactory.createProcessor(domainConceptSetDefinition, terminologyBrowser, refSetBrowser, indexService).contains(concept);
					if (contains) {
						return true;
					}
				}
				return false;
			}
		});
		if (!matchingConstraints.hasNext()) { //if there is not matching constraint.
			return Sets.newHashSet();
		}
		return ImmutableSet.copyOf(matchingConstraints);
	}
}