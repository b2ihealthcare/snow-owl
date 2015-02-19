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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.core.api.IdAndTerminologyComponentIdProviderImpl.create;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Maps.toMap;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IdAndTerminologyComponentIdProvider;
import com.b2international.snowowl.core.validation.GlobalConstraintStatus;
import com.b2international.snowowl.core.validation.IGlobalConstraint;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.google.common.base.Function;
import com.google.common.collect.Multimap;

/**
 * Global constraint, which checks if there is more than concept with the same active fully specified name.
 * 
 */
public class UniqueFSNGlobalConstraint implements IGlobalConstraint {

	private static final Logger LOG = LoggerFactory.getLogger(UniqueFSNGlobalConstraint.class);
	private static final String ID = "com.b2international.snowowl.snomed.validation.examples.uniqueFSNGlobalConstraint";

	@Override
	public GlobalConstraintStatus validate(IBranchPath branchPath, IProgressMonitor monitor) {
		LOG.info("Started global FSN validation constraint validation rule");
		final SubMonitor subMonitor = SubMonitor.convert(monitor);
		try {
			
			final String languageRefSetId = getServiceForClass(ILanguageConfigurationProvider.class).getLanguageConfiguration().getLanguageRefSetId();
			final Multimap<String, String> fsnToIdsMapping = getServiceForClass(ISnomedComponentService.class).getFullySpecifiedNameToIdsMapping(branchPath, languageRefSetId);
			final Map<String, IdAndTerminologyComponentIdProvider> allConcepts = getAllConcepts(fsnToIdsMapping);
			final Collection<IdAndTerminologyComponentIdProvider> violatingConcepts = newHashSet();
			
			for (final Entry<String, Collection<String>> entry : fsnToIdsMapping.asMap().entrySet()) {
				if (entry.getValue().size() > 1) {
					for (final String conceptId : entry.getValue()) {
						violatingConcepts.add(createConcept(conceptId));
						allConcepts.remove(conceptId);
					}
				}
			}

			return new GlobalConstraintStatus(ID, violatingConcepts, allConcepts.values());
		} catch (Exception e) {
			LOG.error("Exception happened during FSN uniqueness validation.", e);
		} finally {
			LOG.info("Completed global FSN validation constraint validation rule.");
			subMonitor.done();
		}
		// FIXME return error status, as the calculation failed
		return createEmptyStatus();
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
			public IdAndTerminologyComponentIdProvider apply(String conceptId) {
				return create(conceptId, CONCEPT_NUMBER);
			}
		}));
	}
}