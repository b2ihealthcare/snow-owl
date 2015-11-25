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
package com.b2international.snowowl.snomed.refset.maprefsetderivation;

import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedEditingContext;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMemberIndexQueryAdapter;
import com.b2international.snowowl.snomed.refset.derivation.AbstractSnomedRefSetDerivator;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * This class is for deriving SNOMED CT simple map reference set to simple type reference set.
 * Derivation means either referenced components or map targets are used for creating the simple type reference sets.
 * Subclasses must override <code>processMembers(IProgressMonitor monitor)</code> abstract method which handles
 * the type specific derivation.
 * 
 * @deprecated from Snow&nbsp;Owl 3.0.1 use {@link AbstractSnomedRefSetDerivator} instead.
 */
public abstract class AbstractSnomedSimpleMappingRefSetDerivator {
	
	//	because of performance issues
	protected static final int COMMIT_INTERVAL = 100000;

	protected final String refSetId;

	//	this label will be used for the new simple type reference set
	protected final String newRefSetLabel;
	
	protected final SnomedEditingContext snomedEditingContext;
	protected final SnomedClientIndexService refSetSearcher;
	
	//	true if we need to use the maptargets as referenced component in the newly created simple type reference set
	protected boolean mapTargetToReferencedComponent;
	//default module concept ID
	protected final String moduleId;


	public AbstractSnomedSimpleMappingRefSetDerivator(String refSetId, String newRefSetLabel, boolean mapTargetToReferencedComponent) throws SnowowlServiceException {
		this.refSetId = refSetId;
		this.newRefSetLabel = newRefSetLabel;
		this.mapTargetToReferencedComponent = mapTargetToReferencedComponent;

		this.snomedEditingContext = new SnomedEditingContext();
		this.refSetSearcher = ApplicationContext.getInstance().getService(SnomedClientIndexService.class);
		moduleId = snomedEditingContext.getDefaultModuleConcept().getId();
	}
	
	protected void commit(final String commitMessage) throws SnowowlServiceException {
		snomedEditingContext.getRefSetEditingContext().commit(commitMessage);
	}
	
	protected void dispose() {
		snomedEditingContext.close();
	}
	
	protected Set<String> getActiveConceptIds() {
		final SnomedRefSetMemberIndexQueryAdapter queryAdapter = new SnomedRefSetMemberIndexQueryAdapter(refSetId, "", true);
		final List<SnomedRefSetMemberIndexEntry> results = refSetSearcher.search(queryAdapter);
		final Set<String> conceptIds = Sets.newHashSet(Collections2.transform(results, new Function<SnomedRefSetMemberIndexEntry, String>() {
			@Override
			public String apply(SnomedRefSetMemberIndexEntry entry) {
				return mapTargetToReferencedComponent ? entry.getMapTargetComponentId() : entry.getReferencedComponentId();
			}
		}));
		
		final IClientTerminologyBrowser<SnomedConceptIndexEntry, String> browser = ApplicationContext.getInstance().getService(SnomedClientTerminologyBrowser.class);
		return Sets.newHashSet(Iterables.filter(conceptIds, new Predicate<String>() {
			@Override public boolean apply(String conceptId) {
				final SnomedConceptIndexEntry concept = browser.getConcept(conceptId);
				return null != concept && concept.isActive();
			}
		}));
		
		
	}
	
	protected SnomedRegularRefSet createSimpleTypeConceptRefSet() {
		return snomedEditingContext.getRefSetEditingContext().createSnomedSimpleTypeRefSet(newRefSetLabel, CONCEPT);
	}
	
	public abstract void processMembers(IProgressMonitor monitor, final String commitMessage) throws SnowowlServiceException;
}