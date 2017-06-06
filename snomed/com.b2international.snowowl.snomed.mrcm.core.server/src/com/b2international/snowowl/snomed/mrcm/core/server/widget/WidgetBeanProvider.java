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
package com.b2international.snowowl.snomed.mrcm.core.server.widget;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.commons.functions.UncheckedCastFunction;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.ExtendedComponentImpl;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.IComponentNameProvider;
import com.b2international.snowowl.core.api.browser.IClientTerminologyBrowser;
import com.b2international.snowowl.core.api.component.IconIdProviderUtil;
import com.b2international.snowowl.snomed.datastore.ILanguageConfigurationProvider;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.SnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.mrcm.core.configuration.SnomedSimpleTypeRefSetAttributeConfiguration;
import com.b2international.snowowl.snomed.mrcm.core.widget.LeafWidgetBeanSorter;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ContainerWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DescriptionContainerWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.LeafWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.MappingWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipGroupWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.MappingWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipGroupWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipGroupWidgetModel.GroupFlag;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.WidgetModel;
import com.b2international.snowowl.snomed.mrcm.mini.SectionType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

/**
 * Server side widget bean provider.
 * 
 */
public class WidgetBeanProvider {

	private final ServerSideWidgetBeanProviderStrategy strategy;
	private final ConceptWidgetModel conceptWidgetModel;
	private final IBranchPath branchPath;
	private final SnomedSimpleTypeRefSetAttributeConfiguration configuration;
	private final String conceptId;
	private final boolean doSort;

	public WidgetBeanProvider(final String conceptId, final ConceptWidgetModel conceptWidgetModel, final IBranchPath branchPath, final SnomedSimpleTypeRefSetAttributeConfiguration configuration, final boolean includeUnsanctioned, final boolean doSort) {
		this.conceptId = conceptId;
		this.conceptWidgetModel = conceptWidgetModel;
		this.branchPath = branchPath;
		this.configuration = configuration;
		this.doSort = doSort;
		this.strategy = new ServerSideWidgetBeanProviderStrategy(conceptId, conceptWidgetModel, branchPath, includeUnsanctioned);
	}
	
	@SuppressWarnings("unchecked")
	public ConceptWidgetBean createConceptWidgetBean(IProgressMonitor monitor) {
		
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		final SnomedConceptIndexEntry concept = strategy.getConcept(conceptId);
		final ConceptWidgetBean cwb = new ConceptWidgetBean(conceptWidgetModel, conceptId, concept.isActive());
		
		final AtomicReference<List<LeafWidgetBean>> descriptionBeansRef = new AtomicReference<List<LeafWidgetBean>>();
		final AtomicReference<ListMultimap<Integer, LeafWidgetBean>> relationshipGroupsRef = new AtomicReference<ListMultimap<Integer, LeafWidgetBean>>();
		final AtomicReference<List<LeafWidgetBean>> dataTypeBeansRef = new AtomicReference<List<LeafWidgetBean>>();
		final AtomicReference<List<LeafWidgetBean>> mappingBeansRef = new AtomicReference<List<LeafWidgetBean>>();
		
		final ILanguageConfigurationProvider languageConfigurationProvider = ApplicationContext.getInstance().getService(ILanguageConfigurationProvider.class);
		final String configuredLanguageRefSetId = languageConfigurationProvider.getLanguageConfiguration().getLanguageRefSetId(branchPath);
		final AtomicReference<String> languageRefSetIdRef = new AtomicReference<String>(configuredLanguageRefSetId);

		ForkJoinUtils.runInParallel(
				new Runnable() { @Override public void run() { strategy.createDescriptionWidgetBeans(cwb, descriptionBeansRef, languageRefSetIdRef); }},
				new Runnable() { @Override public void run() { relationshipGroupsRef.set(strategy.createRelationshipGroupWidgetBeans(cwb)); }},
				new Runnable() { @Override public void run() { dataTypeBeansRef.set(strategy.createDataTypeWidgetBeans(cwb)); }},
				new Runnable() { @Override public void run() { mappingBeansRef.set(createMappingWidgetBeans(cwb, conceptId)); }}
		);
		
		final DescriptionContainerWidgetBean descriptionContainerWidgetBean = new DescriptionContainerWidgetBean(conceptWidgetModel.getDescriptionContainerModel(), cwb, languageRefSetIdRef.get());
		for (final LeafWidgetBean widgetBean : LeafWidgetBeanSorter.mergeAndSortElements(SectionType.DESCRIPTION_SECTION, configuration, doSort, descriptionBeansRef.get(), dataTypeBeansRef.get())) {
			descriptionContainerWidgetBean.add(widgetBean);
		}
		
		cwb.setDescriptions(descriptionContainerWidgetBean);
		
		final List<LeafWidgetBean> groupZeroBeans = relationshipGroupsRef.get().containsKey(0) ? relationshipGroupsRef.get().get(0) : ImmutableList.<LeafWidgetBean>of();
		final ContainerWidgetBean propertiesContainerWidgetBean = new ContainerWidgetBean(conceptWidgetModel.getRelationshipGroupContainerModel(), cwb);
		final RelationshipGroupWidgetModel ungroupedModel = conceptWidgetModel.getRelationshipGroupContainerModel().getFirstMatching(GroupFlag.UNGROUPED);
		final RelationshipGroupWidgetBean groupZeroContainerBean = new RelationshipGroupWidgetBean(ungroupedModel, 0, cwb);
		for (final LeafWidgetBean widgetBean : LeafWidgetBeanSorter.mergeAndSortElements(SectionType.PROPERTY_SECTION, configuration, doSort, groupZeroBeans, dataTypeBeansRef.get())) {
			groupZeroContainerBean.add(widgetBean);
		}
		
		propertiesContainerWidgetBean.add(groupZeroContainerBean);
		
		final RelationshipGroupWidgetModel groupedModel = conceptWidgetModel.getRelationshipGroupContainerModel().getFirstMatching(GroupFlag.GROUPED);
		
		for (final Integer groupNumber : ImmutableSortedSet.copyOf(relationshipGroupsRef.get().keySet())) {
			
			if (0 == groupNumber) {
				continue;
			}
			
			final RelationshipGroupWidgetBean groupNonZeroContainerBean = new RelationshipGroupWidgetBean(groupedModel, groupNumber, cwb);
			
			for (final LeafWidgetBean widgetBean : LeafWidgetBeanSorter.mergeAndSortElements(SectionType.PROPERTY_SECTION, configuration, doSort, relationshipGroupsRef.get().get(groupNumber))) {
				groupNonZeroContainerBean.add(widgetBean);
			}
			
			propertiesContainerWidgetBean.add(groupNonZeroContainerBean);
		}
		
		cwb.setProperties(propertiesContainerWidgetBean);
		
		if (conceptWidgetModel.getMappingContainerWidgetModel() != null) {
			final ContainerWidgetBean mappingContainerWidgetBean = new ContainerWidgetBean(conceptWidgetModel.getMappingContainerWidgetModel(), cwb);
			for (final LeafWidgetBean bean : LeafWidgetBeanSorter.mergeAndSortElements(SectionType.PROPERTY_SECTION, configuration, doSort, mappingBeansRef.get())) {
				mappingContainerWidgetBean.add(bean);	
			}
			cwb.setMappings(mappingContainerWidgetBean);			
		}
		
		//clean up underlying cache, if any.
		strategy.clear();
		
		return cwb;
	}

	private List<LeafWidgetBean> createMappingWidgetBeans(final ConceptWidgetBean cwb, final String conceptId) {
		final List<LeafWidgetBean> result = Lists.newArrayList();
		if (conceptWidgetModel.getMappingContainerWidgetModel() != null) {
			final List<MappingWidgetModel> unusedModels = Lists.newArrayList(
					Lists.transform(conceptWidgetModel.getMappingContainerWidgetModel().getChildren(), new UncheckedCastFunction<WidgetModel, MappingWidgetModel>(MappingWidgetModel.class)));
			final Collection<SnomedRefSetMemberIndexEntry> atcMappings = new SnomedRefSetMembershipLookupService().getAtcMappings(conceptId);
			for (final SnomedRefSetMemberIndexEntry entry : atcMappings) {
				if (!entry.isActive()) {
					continue;
				}
				
				final String mapTargetComponentType = entry.getMapTargetComponentType();
				final short mapTargetComponentTypeShort = entry.getMapTargetComponentTypeAsShort();
				final String mapTargetComponentId = entry.getMapTargetComponentId();
				
				final MappingWidgetModel model = conceptWidgetModel.getMappingContainerWidgetModel().getFirstMatching(mapTargetComponentType);
				final MappingWidgetBean bean = new MappingWidgetBean(cwb, model, entry.isReleased());
				
				// XXX: as WidgetBeanProvider currently being used via the RPC mechanism which serializes class values, we cannot use returned terminologyDepenendentSelectedValue, instead we use ExtendedComponent
				final IClientTerminologyBrowser<IComponent<String>, String> terminologyBrowser = CoreTerminologyBroker.getInstance()
						.getTerminologyBrowserFactory(mapTargetComponentType)
						.getTerminologyBrowser();
				
				final IComponentNameProvider nameProvider = CoreTerminologyBroker.getInstance()
						.getNameProviderFactory(mapTargetComponentType)
						.getNameProvider();
				
				final IComponent<String> localSelectedValue = terminologyBrowser.getConcept(mapTargetComponentId);
				final String iconId = IconIdProviderUtil.getIconId(localSelectedValue);
				
				// FIXME: branch path for external terminology component?
				final String label = nameProvider.getComponentLabel(branchPath, mapTargetComponentId);
				
				final IComponent<String> serializableSelectedValue = new ExtendedComponentImpl(mapTargetComponentId, label, iconId, mapTargetComponentTypeShort);
				
				bean.setSelectedValue(serializableSelectedValue);
				bean.setUuid(entry.getId());
				result.add(bean);
				unusedModels.remove(model);
			}
		}
		
		return result;
	}
}