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
package com.b2international.snowowl.snomed.validation.constraints.global;

import static com.b2international.snowowl.core.api.IdAndTerminologyComponentIdProviderImpl.create;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.toMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IdAndTerminologyComponentIdProvider;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.core.validation.GlobalConstraintStatus;
import com.b2international.snowowl.core.validation.IGlobalConstraint;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Global constraint, which checks if there is more than one concept with the same active and preferred fully specified name.
 * 
 */
public class UniqueFSNGlobalConstraint implements IGlobalConstraint {

	private static final Logger LOG = LoggerFactory.getLogger(UniqueFSNGlobalConstraint.class);
	
	private static final String ID = "com.b2international.snowowl.snomed.validation.examples.uniqueFSNGlobalConstraint";

	@Override
	public GlobalConstraintStatus validate(final IBranchPath branchPath, final IProgressMonitor monitor) {
		
		LOG.info("Validating global FSN uniqueness constraint...");
		
		final SubMonitor subMonitor = SubMonitor.convert(monitor, 3);
		
		if (subMonitor.isCanceled()) {
			return createEmptyStatus();
		}
		
		try {

			final IEventBus bus = ApplicationContext.getServiceForClass(IEventBus.class);
			final List<ExtendedLocale> locales = ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference();
			
			final Set<String> activeConceptIds = getActiveConceptIds(branchPath);
			
			subMonitor.worked(1);
			
			final SnomedDescriptions descriptions = SnomedRequests.prepareSearchDescription()
				.filterByActive(true)
				.filterByType(Concepts.FULLY_SPECIFIED_NAME)
				.filterByAcceptability(Acceptability.PREFERRED)
				.filterByExtendedLocales(locales)
				.filterByConceptId(activeConceptIds)
				.all()
				.build(branchPath.getPath())
				.executeSync(bus);
		
			subMonitor.worked(1);
			
			if (subMonitor.isCanceled()) {
				return createEmptyStatus();
			}
			
			final HashMultimap<String, String> fsnToIdsMap = HashMultimap.<String, String>create();
			
			for (final ISnomedDescription description : descriptions.getItems()) {
				fsnToIdsMap.put(description.getTerm(), description.getConceptId());
			}
			
			final Map<String, IdAndTerminologyComponentIdProvider> allConcepts = getAllConcepts(fsnToIdsMap);
			final Collection<IdAndTerminologyComponentIdProvider> violatingConcepts = newHashSet();
			
			for (final Entry<String, Collection<String>> entry : fsnToIdsMap.asMap().entrySet()) {
				if (entry.getValue().size() > 1) {
					for (final String conceptId : entry.getValue()) {
						violatingConcepts.add(createConcept(conceptId));
						allConcepts.remove(conceptId);
					}
				}
			}

			subMonitor.worked(1);
			
			LOG.info("Global FSN uniqueness validation finished.");
			
			return new GlobalConstraintStatus(ID, violatingConcepts, allConcepts.values());
			
		} catch (final Exception e) {
			LOG.info("Exception happened during FSN uniqueness validation.", e);
		}

		// FIXME return error status, as the calculation failed
		return createEmptyStatus();
	}

	private Set<String> getActiveConceptIds(IBranchPath branchPath) {
		return SnomedRequests.prepareSearchConcept()
				.all()
				.filterByActive(true)
				.build(branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.then(new Function<SnomedConcepts, Set<String>>() {
					@Override
					public Set<String> apply(SnomedConcepts input) {
						return FluentIterable.from(input).transform(IComponent.ID_FUNCTION).toSet();
					}
				})
				.getSync();
	}

	private IdAndTerminologyComponentIdProvider createConcept(final String conceptId) {
		return create(conceptId, SnomedTerminologyComponentConstants.CONCEPT_NUMBER);
	}

	private GlobalConstraintStatus createEmptyStatus() {
		return new GlobalConstraintStatus(ID, 
				Collections.<IdAndTerminologyComponentIdProvider>emptySet(), 
				Collections.<IdAndTerminologyComponentIdProvider>emptySet());
	}
	
	private Map<String, IdAndTerminologyComponentIdProvider> getAllConcepts(final Multimap<String, String> fsnToIdsMapping) {
		return newHashMap(toMap(newHashSet(fsnToIdsMapping.values()), new Function<String, IdAndTerminologyComponentIdProvider>() {
			@Override
			public IdAndTerminologyComponentIdProvider apply(final String conceptId) {
				return create(conceptId, CONCEPT_NUMBER);
			}
		}));
	}
}