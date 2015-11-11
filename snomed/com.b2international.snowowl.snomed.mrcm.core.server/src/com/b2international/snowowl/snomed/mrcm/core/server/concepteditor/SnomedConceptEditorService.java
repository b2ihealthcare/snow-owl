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
package com.b2international.snowowl.snomed.mrcm.core.server.concepteditor;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;

import bak.pcj.map.LongKeyLongMap;
import bak.pcj.map.LongKeyLongOpenHashMap;
import bak.pcj.map.LongKeyMap;
import bak.pcj.map.LongKeyOpenHashMap;
import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedPredicateBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.SnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.mrcm.core.concepteditor.ISnomedConceptEditorService;
import com.b2international.snowowl.snomed.mrcm.core.concepteditor.SnomedConceptDetailsBean;
import com.b2international.snowowl.snomed.mrcm.core.concepteditor.SnomedConceptLabelAndIconIdMappings;
import com.b2international.snowowl.snomed.mrcm.core.configuration.SnomedSimpleTypeRefSetAttributeConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.extensions.IConceptModelExtension;
import com.b2international.snowowl.snomed.mrcm.core.extensions.IConceptModelExtensionProvider;
import com.b2international.snowowl.snomed.mrcm.core.server.widget.WidgetBeanProvider;
import com.b2international.snowowl.snomed.mrcm.core.server.widget.WidgetBeanProviderFactory;
import com.b2international.snowowl.snomed.mrcm.core.widget.IWidgetModelProvider;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;

/**
 * Server-side implementation of the SNOMED CT concept editor service.
 * 
 */
public class SnomedConceptEditorService implements ISnomedConceptEditorService {

	@Override
	public SnomedConceptDetailsBean getConceptDetailsBean(final IBranchPath branchPath, final long conceptId, 
			final SnomedSimpleTypeRefSetAttributeConfiguration configuration, 
			final boolean includeUnsanctioned) {

		checkNotNull(branchPath, "branchPath");
		
		// Retrieve initial model
		final String conceptIdString = Long.toString(conceptId);
		final IWidgetModelProvider widgetModelProvider = ApplicationContext.getServiceForClass(IWidgetModelProvider.class);
		final ConceptWidgetModel widgetModel = widgetModelProvider.createConceptWidgetModel(branchPath, conceptIdString, null);
		
		// Apply concept model extensions
		final IConceptModelExtensionProvider conceptModelExtensionProvider = ApplicationContext.getServiceForClass(IConceptModelExtensionProvider.class);
		final Collection<IConceptModelExtension> extensions = conceptModelExtensionProvider.getModelExtensions(branchPath, conceptId);
		for (final IConceptModelExtension conceptModelExtension : extensions) {
			conceptModelExtension.modifyWidgetModel(widgetModel);
		}

		// Create widget bean
		final WidgetBeanProvider widgetBeanProvider = new WidgetBeanProviderFactory().createProvider(branchPath, conceptIdString, widgetModel, configuration, includeUnsanctioned, true);
		final ConceptWidgetBean widgetBean = widgetBeanProvider.createConceptWidgetBean(new NullProgressMonitor());

		// Retrieve synonym and descendant type IDs
		final Set<String> synonymAndDescendants = ApplicationContext.getServiceForClass(ISnomedComponentService.class).getSynonymAndDescendantIds(branchPath);
		final LongSet synonymAndDescendantIds = new LongOpenHashSet();
		for (final String synonymAndDescendantId : synonymAndDescendants) {
			synonymAndDescendantIds.add(Long.parseLong(synonymAndDescendantId));
		}

		// Retrieve applicable predicates
		SnomedPredicateBrowser predicateBrowser = ApplicationContext.getServiceForClass(SnomedPredicateBrowser.class);
		final Collection<PredicateIndexEntry> predicates = predicateBrowser.getPredicates(branchPath, conceptIdString, null);

		// Create regular index entry
		final SnomedTerminologyBrowser terminologyBrowser = ApplicationContext.getServiceForClass(SnomedTerminologyBrowser.class);
		final SnomedConceptIndexEntry conceptIndexEntry = terminologyBrowser.getConcept(branchPath, conceptIdString);
		final SnomedConceptLabelAndIconIdMappings conceptMappings = getConceptMappings(branchPath, conceptId, conceptIndexEntry.isActive());
		
		checkArgument(conceptIndexEntry != null, "Can't find concept '" + conceptId + "'.");

		final SnomedConceptDetailsBean snomedConceptDetailsBean = new SnomedConceptDetailsBean(conceptIndexEntry.getLabel(), 
				Long.parseLong(conceptIndexEntry.getIconId()), 
				widgetBean, 
				conceptMappings, 
				synonymAndDescendantIds, 
				configuration,
				predicates);

		return snomedConceptDetailsBean;
	}

	@Override
	public SnomedConceptLabelAndIconIdMappings getConceptMappings(final IBranchPath branchPath, final long conceptId, final boolean active) {
		
		final LongKeyMap conceptIdToLabelMap = new LongKeyOpenHashMap();
		final LongKeyLongMap conceptIdToIconIdMap = new LongKeyLongOpenHashMap();
		final SnomedTerminologyBrowser terminologyBrowser = ApplicationContext.getServiceForClass(SnomedTerminologyBrowser.class);
		
		// Self 
		final SnomedConceptIndexEntry self = terminologyBrowser.getConcept(branchPath, Long.toString(conceptId));
		if (null != self) {
			addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, self);
		}
		
		// Language reference set root
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.REFSET_LANGUAGE_TYPE));
		
		// Inactivation reasons
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.LIMITED));
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.DUPLICATE));
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.OUTDATED));
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.AMBIGUOUS));
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.ERRONEOUS));
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.MOVED_ELSEWHERE));
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.INAPPROPRIATE));
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.PENDING_MOVE));
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.CONCEPT_NON_CURRENT));
		
		// Relationship refinability
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.NOT_REFINABLE));
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.OPTIONAL_REFINABLE));
		addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, Concepts.MANDATORY_REFINABLE));
		
		// Hierarchies
		addAllSubTypesToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser, Concepts.DEFINITION_STATUS_ROOT);
		addAllSubTypesToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser, Concepts.CHARACTERISTIC_TYPE);
		addAllSubTypesToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser, Concepts.DESCRIPTION_TYPE_ROOT_CONCEPT);
		addAllSubTypesToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser, Concepts.CASE_SIGNIFICANCE_ROOT_CONCEPT);
		addAllSubTypesToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser, Concepts.MODIFIER_ROOT);
		addAllSubTypesToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser, Concepts.MODULE_ROOT);
		
		// Association reference set targets
		if (!active) {
			
			final String _conceptId = Long.toString(conceptId);
			final Collection<String> refSetIds = SnomedRefSetUtil.ASSOCIATION_REFSETS.keySet();
			final Collection<SnomedRefSetMemberIndexEntry> members = new SnomedRefSetMembershipLookupService().getMembers(SnomedTerminologyComponentConstants.CONCEPT, 
					refSetIds, 
					_conceptId);

			if (null != members) {
				for (final SnomedRefSetMemberIndexEntry entry : members) {
					addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, terminologyBrowser.getConcept(branchPath, entry.getTargetComponentId()));
				}
			}
		}
		
		return new SnomedConceptLabelAndIconIdMappings(conceptIdToLabelMap, conceptIdToIconIdMap);
	}

	private void addAllSubTypesToMaps(final IBranchPath branchPath, final LongKeyMap conceptIdToLabelMap, final LongKeyLongMap conceptIdToIconIdMap, 
			final SnomedTerminologyBrowser terminologyBrowser, 
			final String conceptId) {
		
		final SnomedConceptIndexEntry concept = terminologyBrowser.getConcept(branchPath, conceptId);
		if (null != concept) {
			final Collection<SnomedConceptIndexEntry> subTypes = terminologyBrowser.getAllSubTypes(branchPath, concept);
			for (final SnomedConceptIndexEntry conceptIndexEntry : subTypes) {
				addToMaps(branchPath, conceptIdToLabelMap, conceptIdToIconIdMap, conceptIndexEntry);
			}
		}
	}

	private void addToMaps(final IBranchPath branchPath, final LongKeyMap conceptIdToLabelMap, final LongKeyLongMap conceptIdToIconIdMap, final SnomedConceptIndexEntry conceptIndexEntry) {
		if (conceptIndexEntry != null) {
			final String conceptId = conceptIndexEntry.getId();
			conceptIdToIconIdMap.put(Long.valueOf(conceptId), Long.valueOf(conceptIndexEntry.getIconId()));
			conceptIdToLabelMap.put(Long.valueOf(conceptId), conceptIndexEntry.getLabel());
		}
	}
}