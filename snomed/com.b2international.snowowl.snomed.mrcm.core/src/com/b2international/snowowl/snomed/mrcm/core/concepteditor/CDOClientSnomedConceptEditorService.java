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
package com.b2international.snowowl.snomed.mrcm.core.concepteditor;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;

import bak.pcj.set.LongOpenHashSet;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.utils.ComponentUtils2;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.SnomedClientPredicateBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.IClientSnomedComponentService;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.datastore.snor.SnomedTerminologyBrowserProvider;
import com.b2international.snowowl.snomed.mrcm.core.configuration.SnomedSimpleTypeRefSetAttributeConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.extensions.IConceptModelExtension;
import com.b2international.snowowl.snomed.mrcm.core.extensions.IConceptModelExtensionProvider;
import com.b2international.snowowl.snomed.mrcm.core.widget.CDOClientWidgetBeanProvider;
import com.b2international.snowowl.snomed.mrcm.core.widget.IClientWidgetModelProvider;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;

/**
 * Client-side SNOMED concept editor service implementation, which uses CDO and RPC calls.
 * <p>
 * This implementation is only recommended in cases when the CDO transaction is dirty, i.e. in case of a newly created
 * concept. In case of an already existing concept, use {@link IndexClientSnomedConceptEditorService} instead.
 * 
 * @see ClientSnomedConceptEditorServiceFactory
 */
public class CDOClientSnomedConceptEditorService implements IClientSnomedConceptEditorService {

	private final Concept concept;

	/**
	 * Creates a new instance for the specified concept.
	 * 
	 * @param concept the concept to edit (may not be {@code null})
	 */
	public CDOClientSnomedConceptEditorService(final Concept concept) {
		this.concept = concept;
	}

	@Override
	public SnomedConceptDetailsBean getConceptDetailsBean(final long conceptId, final boolean includeUnsanctioned) {
		
		final List<String> ruleParentIds = newArrayList();
		String ruleRefSetId = null;
		
		// XXX: no IS A relationships with non-defining characteristic types expected; dirty and detached relationships will not be taken into account
		for (final Relationship newRelationship : ComponentUtils2.getNewObjects(concept.cdoView(), Relationship.class)) {
			if (newRelationship.isActive() 
					&& concept.getId().equals(newRelationship.getSource().getId()) 
					&& Concepts.IS_A.equals(newRelationship.getType().getId())) {

				ruleParentIds.add(newRelationship.getDestination().getId());
			}
		}

		// XXX: at most one new reference set membership will be used
		for (final SnomedRefSetMember newRefSetMember : ComponentUtils2.getNewObjects(concept.cdoView(), SnomedRefSetMember.class)) {
			if (newRefSetMember.isActive() 
					&& SnomedTerminologyComponentConstants.CONCEPT_NUMBER == newRefSetMember.getReferencedComponentType() 
					&& concept.getId().equals(newRefSetMember.getReferencedComponentId())) {
				
				ruleRefSetId = newRefSetMember.getRefSetIdentifierId();
				break;
			}
		}

		// Retrieve initial model
		final String conceptIdString = Long.toString(conceptId);
		final IClientWidgetModelProvider widgetModelProvider = ApplicationContext.getServiceForClass(IClientWidgetModelProvider.class);
		
		final ConceptWidgetModel widgetModel;
		if (ruleParentIds.isEmpty()) {
			widgetModel = widgetModelProvider.createConceptWidgetModel(conceptIdString, ruleRefSetId);
		} else {
			widgetModel = widgetModelProvider.createConceptWidgetModel(ruleParentIds, ruleRefSetId);
		}
		
		// Apply concept model extensions
		final IConceptModelExtensionProvider conceptModelExtensionProvider = ApplicationContext.getServiceForClass(IConceptModelExtensionProvider.class);
		final Collection<IConceptModelExtension> extensions = conceptModelExtensionProvider.getModelExtensions(concept);
		for (final IConceptModelExtension conceptModelExtension : extensions) {
			conceptModelExtension.modifyWidgetModel(widgetModel);
		}
		
		// Create widget bean
		final CDOClientWidgetBeanProvider widgetBeanProvider = new CDOClientWidgetBeanProvider(widgetModel, concept, includeUnsanctioned);
		final SnomedSimpleTypeRefSetAttributeConfiguration configuration = SnomedSimpleTypeRefSetAttributeConfiguration.getConfiguration(concept);
		final ConceptWidgetBean widgetBean = widgetBeanProvider.createConceptWidgetBean(conceptIdString, widgetModel, configuration, includeUnsanctioned, new NullProgressMonitor());
		
		// Retrieve synonym and descendant type IDs
		final Set<String> synonymAndDescendants = ApplicationContext.getServiceForClass(IClientSnomedComponentService.class).getSynonymAndDescendantIds();
		final LongOpenHashSet synonymAndDescendantIds = new LongOpenHashSet();
		for (final String synonymAndDescendant : synonymAndDescendants) {
			synonymAndDescendantIds.add(Long.parseLong(synonymAndDescendant));
		}

		// Retrieve applicable predicates
		final SnomedClientPredicateBrowser predicateBrowser = ApplicationContext.getServiceForClass(SnomedClientPredicateBrowser.class);
		final Collection<PredicateIndexEntry> predicates;
		if (ruleParentIds.isEmpty()) {
			predicates = predicateBrowser.getPredicates(conceptIdString, ruleRefSetId);
		} else {
			predicates = predicateBrowser.getPredicates(ruleParentIds, ruleRefSetId);
		}

		// Use extended terminology browser for creating an index entry
		final SnomedClientTerminologyBrowser terminologyBrowser = SnomedTerminologyBrowserProvider.getTerminologyBrowser(concept);
		final SnomedConceptIndexEntry conceptIndexEntry = terminologyBrowser.getConcept(conceptIdString);
		final SnomedConceptLabelAndIconIdMappings conceptMappings = getConceptMappings(conceptId, conceptIndexEntry.isActive());
		
		final SnomedConceptDetailsBean conceptDetailsBean = new SnomedConceptDetailsBean(conceptIndexEntry.getLabel(), 
				Long.parseLong(conceptIndexEntry.getIconId()), 
				widgetBean, 
				conceptMappings, 
				synonymAndDescendantIds, 
				configuration,
				predicates);

		return conceptDetailsBean;
	}

	@Override
	public SnomedConceptLabelAndIconIdMappings getConceptMappings(final long conceptId, final boolean active) {
		final IClientSnomedConceptEditorService conceptEditorService = ApplicationContext.getServiceForClass(IClientSnomedConceptEditorService.class);
		return conceptEditorService.getConceptMappings(conceptId, active);
	}
}