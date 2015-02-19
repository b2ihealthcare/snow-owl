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
package com.b2international.snowowl.snomed.mrcm.core.widget;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.mrcm.core.configuration.SnomedSimpleTypeRefSetAttributeConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ContainerWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DescriptionContainerWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.LeafWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipGroupWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipGroupWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipGroupWidgetModel.GroupFlag;
import com.b2international.snowowl.snomed.mrcm.mini.SectionType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

/**
 * Client side widget bean provider implementation, which uses CDO and RPC calls. 
 * This implementation is only recommended in cases when the CDO transaction is dirty, i.e. in case of a newly created concept. In case of an already
 * existing concept, use {@link ClientWidgetBeanProvider} instead.
 * 
 */
public class CDOClientWidgetBeanProvider implements IClientWidgetBeanProvider {

	private final WidgetBeanProviderStrategy strategy;
	private final Concept concept;

	public CDOClientWidgetBeanProvider(final ConceptWidgetModel conceptWidgetModel, final Concept concept, final boolean includeUnsanctioned) {
		this.concept = concept;
		this.strategy = new CDOWidgetBeanProviderStrategy(conceptWidgetModel, concept, includeUnsanctioned);
	}
	
	@SuppressWarnings("unchecked")
	public ConceptWidgetBean createConceptWidgetBean(final String conceptId, final ConceptWidgetModel conceptWidgetModel, final SnomedSimpleTypeRefSetAttributeConfiguration configuration, final boolean includeUnsanctioned, IProgressMonitor monitor) {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		cleanDanglingComponents(concept);
		
		final ConceptWidgetBean cwb = new ConceptWidgetBean(conceptWidgetModel, concept.getId(), concept.isActive());
		
		final AtomicReference<List<LeafWidgetBean>> descriptionBeansRef = 
				new AtomicReference<List<LeafWidgetBean>>(Lists.<LeafWidgetBean>newArrayList());
		
		final AtomicReference<ListMultimap<Integer, LeafWidgetBean>> relationshipGroupsRef = 
				new AtomicReference<ListMultimap<Integer, LeafWidgetBean>>(ArrayListMultimap.<Integer, LeafWidgetBean>create());
		
		final AtomicReference<List<LeafWidgetBean>> dataTypeBeansRef = 
				new AtomicReference<List<LeafWidgetBean>>(Lists.<LeafWidgetBean>newArrayList());
		
		final AtomicReference<List<LeafWidgetBean>> mappingBeansRef = 
				new AtomicReference<List<LeafWidgetBean>>(Lists.<LeafWidgetBean>newArrayList());
		
		final ILanguageConfigurationProvider languageConfigurationProvider = ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class);
		final String configuredLanguageRefSetId = languageConfigurationProvider.getLanguageConfiguration().getLanguageRefSetId(BranchPathUtils.createPath(concept));
		final AtomicReference<String> languageRefSetIdRef = new AtomicReference<String>(configuredLanguageRefSetId);
		
		ForkJoinUtils.runInParallel(
				new Runnable() { @Override public void run() { strategy.createDescriptionWidgetBeans(cwb, descriptionBeansRef, languageRefSetIdRef); }},
				new Runnable() { @Override public void run() { relationshipGroupsRef.set(strategy.createRelationshipGroupWidgetBeans(cwb)); }},
				new Runnable() { @Override public void run() { dataTypeBeansRef.set(strategy.createDataTypeWidgetBeans(cwb)); }}
		);
		
		final DescriptionContainerWidgetBean descriptionContainerWidgetBean = new DescriptionContainerWidgetBean(conceptWidgetModel.getDescriptionContainerModel(), cwb, languageRefSetIdRef.get());
		for (final LeafWidgetBean widgetBean : LeafWidgetBeanSorter.mergeAndSortElements(SectionType.DESCRIPTION_SECTION, configuration, true, descriptionBeansRef.get(), dataTypeBeansRef.get())) {
			descriptionContainerWidgetBean.add(widgetBean);
		}
		
		cwb.setDescriptions(descriptionContainerWidgetBean);
		
		final List<LeafWidgetBean> groupZeroBeans = relationshipGroupsRef.get().containsKey(0) ? relationshipGroupsRef.get().get(0) : ImmutableList.<LeafWidgetBean>of();
		final ContainerWidgetBean propertiesContainerWidgetBean = new ContainerWidgetBean(conceptWidgetModel.getRelationshipGroupContainerModel(), cwb);
		final RelationshipGroupWidgetModel ungroupedModel = conceptWidgetModel.getRelationshipGroupContainerModel().getFirstMatching(GroupFlag.UNGROUPED);
		final RelationshipGroupWidgetBean groupZeroContainerBean = new RelationshipGroupWidgetBean(ungroupedModel, 0, cwb);
		for (final LeafWidgetBean widgetBean : LeafWidgetBeanSorter.mergeAndSortElements(SectionType.PROPERTY_SECTION, configuration, true, groupZeroBeans, dataTypeBeansRef.get())) {
			groupZeroContainerBean.add(widgetBean);
		}
		
		propertiesContainerWidgetBean.add(groupZeroContainerBean);
		
		final RelationshipGroupWidgetModel groupedModel = conceptWidgetModel.getRelationshipGroupContainerModel().getFirstMatching(GroupFlag.GROUPED);
		
		for (final Integer groupNumber : ImmutableSortedSet.copyOf(relationshipGroupsRef.get().keySet())) {
			
			if (0 == groupNumber) {
				continue;
			}
			
			final RelationshipGroupWidgetBean groupNonZeroContainerBean = new RelationshipGroupWidgetBean(groupedModel, groupNumber, cwb);
			
			for (final LeafWidgetBean widgetBean : LeafWidgetBeanSorter.mergeAndSortElements(SectionType.PROPERTY_SECTION, configuration, true, relationshipGroupsRef.get().get(groupNumber))) {
				groupNonZeroContainerBean.add(widgetBean);
			}
			
			propertiesContainerWidgetBean.add(groupNonZeroContainerBean);
		}
		
		cwb.setProperties(propertiesContainerWidgetBean);
		
		if (conceptWidgetModel.getMappingContainerWidgetModel() != null) {
			final ContainerWidgetBean mappingContainerWidgetBean = new ContainerWidgetBean(conceptWidgetModel.getMappingContainerWidgetModel(), cwb);
			for (final LeafWidgetBean bean : LeafWidgetBeanSorter.mergeAndSortElements(SectionType.PROPERTY_SECTION, configuration, true, mappingBeansRef.get())) {
				mappingContainerWidgetBean.add(bean);	
			}
			cwb.setMappings(mappingContainerWidgetBean);			
		}
		
		return cwb;
	}

	/*cleans the dangling relationships and descriptions*/
	private void cleanDanglingComponents(final Concept concept) {
		//first we have to clean up dangling references due to remotely changes made on the SNOMED CT concept
		//descriptions first
		for (final Iterator<Description> itr = concept.getDescriptions().iterator(); itr.hasNext(); /* */) {
			
			final Description description = itr.next();
			
			for (final Iterator<SnomedLanguageRefSetMember> itr2 = description.getLanguageRefSetMembers().iterator(); itr2.hasNext(); /* */) {
				final SnomedLanguageRefSetMember member = itr2.next();
				
				if (null != member.cdoID() && !member.cdoID().isTemporary()) { //persisted language refset member, cannot cause problems at all 
					continue;
				}
				
				if (!CDOUtils.checkObjectRefernces(member)) {
					itr2.remove();
				}	
			}
			
			if (null != description.cdoID() && !description.cdoID().isTemporary()) { //persisted description, cannot cause problems 
				continue;
			}
			
			if (!CDOUtils.checkObjectRefernces(description)) {
				itr.remove();
				description.setConcept(null);
			}
		}
		
		//source/outbound relationships 
		for (final Iterator<Relationship> itr =concept.getOutboundRelationships().iterator(); itr.hasNext(); /* */) {
			
			final Relationship relationship = itr.next();
			
			if (null != relationship.cdoID() && !relationship.cdoID().isTemporary()) { //persisted source relationship, cannot cause problems 
				continue;
			}
			
			if (!CDOUtils.checkObjectRefernces(relationship)) {
				itr.remove();
				relationship.setSource(null);
				relationship.setDestination(null);
			}
		}
	}
}