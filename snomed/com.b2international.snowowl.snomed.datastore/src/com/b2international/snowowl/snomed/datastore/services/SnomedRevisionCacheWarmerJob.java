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
package com.b2international.snowowl.snomed.datastore.services;

import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.cdo.view.CDOView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.ILookupService;
import com.b2international.snowowl.datastore.cdo.CDORevisionCacheWarmer;
import com.b2international.snowowl.datastore.cdo.CDORevisionCacheWarmerJob;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.LanguageConfiguration;
import com.b2international.snowowl.snomed.datastore.SnomedConceptLookupService;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetEditingContext;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * Warms the SNOMED&nbsp;CT terminology specific CDO client side revision cache for better performance. 
 * @see CDORevisionCacheWarmerJob
 * @see CDORevisionCacheWarmer
 */
public class SnomedRevisionCacheWarmerJob extends CDORevisionCacheWarmerJob {

	private static final Logger LOGGER = LoggerFactory.getLogger(SnomedRevisionCacheWarmerJob.class);
	
	private static final Set<String> SUPPORTED_LANGUAGE_TYPE_REFSET_IDS = ImmutableSet.<String>of(
			Concepts.AUSTRALIAN_LANGUAGE_REFERENCE_SET,
			Concepts.REFSET_LANGUAGE_TYPE_SG,
			Concepts.REFSET_LANGUAGE_TYPE_UK,
			Concepts.REFSET_LANGUAGE_TYPE_US,
			Concepts.REFSET_LANGUAGE_TYPE_ES
		);
	
	private static final Set<String> INACTIVATION_INDICATOR_REFSET_IDS = ImmutableSet.<String>of(
			Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR,
			Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR
		);

	private static final Set<String> HISTORICA_ASSOCIATION_REFSET_IDS = ImmutableSet.<String>of(
			Concepts.REFSET_ALTERNATIVE_ASSOCIATION,
			Concepts.REFSET_MOVED_FROM_ASSOCIATION,
			Concepts.REFSET_MOVED_TO_ASSOCIATION,
			Concepts.REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION,
			Concepts.REFSET_REFERS_TO_ASSOCIATION,
			Concepts.REFSET_REPLACED_BY_ASSOCIATION,
			Concepts.REFSET_SAME_AS_ASSOCIATION,
			Concepts.REFSET_SIMILAR_TO_ASSOCIATION,
			Concepts.REFSET_WAS_A_ASSOCIATION
		);
	
	private static final Set<String> LARGE_SIMPLE_MAP_REFSETS_IDS = ImmutableSet.<String>of(
			Concepts.CTV3_SIMPLE_MAP_TYPE_REFERENCE_SET_ID,
			Concepts.SNOMED_RT_SIMPLE_MAP_TYPE_REFERENCE_SET_ID
		); 

	/**
	 * Creates a new instance of this revision cache warmer.
	 */
	public SnomedRevisionCacheWarmerJob() {
		super("SNOMED CT semantic cache warmer");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Warming semantic cache for SNOMED CT terminology...", 23);
		SnomedEditingContext context = null;
		try {
			try {
				context = new SnomedEditingContext();
				subMonitor.worked(1);
				loadRefSet(context.getTransaction(), subMonitor, Iterables.toArray(HISTORICA_ASSOCIATION_REFSET_IDS, String.class));
				loadRefSet(context.getTransaction(), subMonitor, Iterables.toArray(INACTIVATION_INDICATOR_REFSET_IDS, String.class));
				loadRefSet(context.getTransaction(), subMonitor, Iterables.toArray(LARGE_SIMPLE_MAP_REFSETS_IDS, String.class));
				loadLanguageRefSet(context.getRefSetEditingContext(), subMonitor);
				loadConcepts(context, subMonitor);
			} catch (final IllegalStateException e) {
				//intentionally swallow the illegal state exception when closing application while cache warmer is running. 
				LOGGER.warn("User abort when warming semantic cache for SNOMED CT terminology.");
			}
		} finally {
			if (null != context)
				context.close();
		}
		return Status.OK_STATUS;
	}

	/*loads the language reference set and all members' CDO ID into the client side revision cache.*/
	private void loadLanguageRefSet(final SnomedRefSetEditingContext refSetEditingContext, final SubMonitor subMonitor) {
		final String id = getDefaultLanguageRefSetId();
		final ILookupService<String, SnomedRefSet, CDOView> service = getLookupService();
		if (null == id) {
			for (final String languageId : SUPPORTED_LANGUAGE_TYPE_REFSET_IDS) {
				//it forces the CDO to load all members' CDO ID into client side revision cache
				service.getComponent(languageId, refSetEditingContext.getTransaction());
			}
		}
		loadRefSet(refSetEditingContext.getTransaction(), subMonitor, id);
	}

	/*looks up a SNOMED CT reference set via a CDO view. works one the specified progress monitor*/
	private void loadRefSet(final CDOView view, final IProgressMonitor monitor, final String... refSetIds) {
		for (final String refSetId : refSetIds) {
			if (StringUtils.isEmpty(refSetId)) {
				return;
			}
			if (view.isClosed()) {
				return;
			}
			
			//load reference set and the members
			getLookupService().getComponent(refSetId, view);
			
			if (null != monitor) {
				monitor.worked(1);
			}
		}
	}
	
	/*returns with the reference set lookup service*/
	private ILookupService<String, SnomedRefSet, CDOView> getLookupService() {
		return CoreTerminologyBroker.getInstance().getLookupService(SnomedTerminologyComponentConstants.REFSET);
	}

	/*returns with the default language type reference set ID from store. returns with null if error occurred or service is not available*/
	private String getDefaultLanguageRefSetId() {
		final ILanguageConfigurationProvider languageConfigurationProvider = ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class);
		if (null == languageConfigurationProvider)
			return null;
		
		final LanguageConfiguration configuration = languageConfigurationProvider.getLanguageConfiguration();
		if (null == configuration)
			return null;
		
		return configuration.getLanguageRefSetId();
	}

	/*loads the more frequently used concepts into the client side revision cache*/
	private void loadConcepts(final SnomedEditingContext context, final SubMonitor subMonitor) {
		//load module concept
		context.getDefaultModuleConcept();
		subMonitor.worked(1);
		//load IS A concept
		loadConcept(context, Concepts.IS_A);
		subMonitor.worked(1);
		//load case  sensitive concept
		loadConcept(context, Concepts.ENTIRE_TERM_CASE_SENSITIVE);
		subMonitor.worked(1);
		//load existential modifier
		loadConcept(context, Concepts.EXISTENTIAL_RESTRICTION_MODIFIER);
		subMonitor.worked(1);
		//load refinability concept
		loadConcept(context, Concepts.NOT_REFINABLE);
		subMonitor.worked(1);
		//load FSN concept
		loadConcept(context, Concepts.FULLY_SPECIFIED_NAME);
		subMonitor.worked(1);
		//load synonym
		loadConcept(context, Concepts.SYNONYM);
		subMonitor.worked(1);
		//load stated characteristic type
		loadConcept(context, Concepts.STATED_RELATIONSHIP);
		subMonitor.worked(1);
	}

	/*loads a concept identified by the unique concept identifier argument*/
	private void loadConcept(final SnomedEditingContext context, final String conceptId) {
		new SnomedConceptLookupService().getComponent(conceptId, context.getTransaction());
	}
	
	
}