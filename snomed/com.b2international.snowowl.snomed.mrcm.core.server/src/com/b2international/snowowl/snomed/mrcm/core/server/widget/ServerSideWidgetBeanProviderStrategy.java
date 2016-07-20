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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.commons.StringUtils;
import com.b2international.commons.functions.UncheckedCastFunction;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.mrcm.core.widget.SnomedRelationship;
import com.b2international.snowowl.snomed.mrcm.core.widget.WidgetBeanProviderStrategy;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DataTypeWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.LeafWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.DataTypeContainerWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.DataTypeWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipGroupWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipGroupWidgetModel.GroupFlag;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.WidgetModel;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * Server side widget bean provider strategy implementation.
 * 
 * Calls the underlying service methods (e.g. index service, terminology browser, etc.) directly as opposed to over RPC.
 * 
 */
public class ServerSideWidgetBeanProviderStrategy extends WidgetBeanProviderStrategy {

	private final IBranchPath branchPath;
	private final String conceptId;
	
	private Collection<ISnomedDescription> descriptions;

	public ServerSideWidgetBeanProviderStrategy(final String conceptId, final ConceptWidgetModel conceptWidgetModel, final IBranchPath branchPath, final boolean includeUnsanctioned) {
		super(conceptWidgetModel, includeUnsanctioned);
		this.conceptId = conceptId;
		this.branchPath = branchPath;
	}

	@Override
	protected Collection<ISnomedDescription> getDescriptions() {
		if (descriptions == null) {
			this.descriptions = SnomedRequests.prepareSearchDescription()
					.all()
					.filterByActive(true)
					.filterByConceptId(conceptId)
					.build(branchPath.getPath())
					.execute(ApplicationContext.getServiceForClass(IEventBus.class))
					.getSync().getItems();
		}
		return descriptions;
	}
	
	@Override
	protected Collection<SnomedRelationship> getRelationships() {
		return SnomedRequests.prepareSearchRelationship()
			.all()
			.filterByActive(true)
			.filterBySource(conceptId)
			.build(branchPath.getPath())
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.then(new Function<SnomedRelationships, Collection<SnomedRelationship>>() {
				@Override
				public Collection<SnomedRelationship> apply(SnomedRelationships input) {
					return Collections2.transform(SnomedRelationshipIndexEntry.fromRelationships(input), SnomedRelationship.IndexObjectConverterFunction.INSTANCE);
				}
			}).getSync();
	}

	@Override
	public List<LeafWidgetBean> createRelationshipDataTypeWidgetBeans(final ConceptWidgetBean cwb, final String... relationshipIds) {
		final List<LeafWidgetBean> beans = Lists.newArrayList();

		final RelationshipGroupWidgetModel groupModel = conceptWidgetModel.getRelationshipGroupContainerModel().getFirstMatching(GroupFlag.GROUPED);
		for (final SnomedRefSetMemberIndexEntry entry : getConcreteDataTypes(ImmutableSet.copyOf(relationshipIds))) {
			final DataTypeWidgetModel matchingModel = groupModel.getFirstMatching(entry.getAttributeName(), entry.getDataType());
			final DataTypeWidgetBean widgetBean = new DataTypeWidgetBean(cwb, matchingModel, entry.getReferencedComponentId(), entry.getId(), entry.isReleased());
			if (entry.getUnitId() != null) {
				widgetBean.setSelectedUom(entry.getUnitId());
			}
			widgetBean.setSelectedValue(entry.getValue());
			widgetBean.setSelectedLabel(entry.getAttributeName());
			widgetBean.setCharacteristicTypeId(entry.getCharacteristicTypeId());
			beans.add(widgetBean);
		}
		
		return beans;
	}

	@Override
	public List<LeafWidgetBean> createDataTypeWidgetBeans(final ConceptWidgetBean cwb) {

		final List<LeafWidgetBean> beans = Lists.newArrayList();

		final DataTypeContainerWidgetModel dataTypeModel = conceptWidgetModel.getDataTypeContainerWidgetModel();
		
		final List<DataTypeWidgetModel> unusedModels = Lists.newArrayList(
				Lists.transform(dataTypeModel.getChildren(), new UncheckedCastFunction<WidgetModel, DataTypeWidgetModel>(DataTypeWidgetModel.class)));
		
		for (final SnomedRefSetMemberIndexEntry entry : getConcreteDataTypes(Collections.singleton(conceptId))) {
			final DataTypeWidgetModel matchingModel = dataTypeModel.getFirstMatching(entry.getAttributeName(), entry.getDataType());
			final DataTypeWidgetBean widgetBean = new DataTypeWidgetBean(cwb, matchingModel, entry.getReferencedComponentId(), entry.getId(), entry.isReleased());
			widgetBean.setSelectedValue(entry.getValue());
			widgetBean.setSelectedLabel(entry.getAttributeName());
			widgetBean.setCharacteristicTypeId(entry.getCharacteristicTypeId());
			beans.add(widgetBean);
			unusedModels.remove(matchingModel);
		}
		
		for (final DataTypeWidgetModel unusedModel : unusedModels) {
			if (!unusedModel.isUnsanctioned()) {
				final DataTypeWidgetBean widgetBean = new DataTypeWidgetBean(cwb, unusedModel, conceptId);
				if (includeUnsanctioned) {
					beans.add(widgetBean);
				} else {
					if (!StringUtils.isEmpty(widgetBean.getSelectedLabel()) && !StringUtils.isEmpty(widgetBean.getSelectedValue())) {
						beans.add(widgetBean);
					}
				}
			}
		}
		
		return beans;
	}

	private Iterable<SnomedRefSetMemberIndexEntry> getConcreteDataTypes(final Collection<String> id) {
		return SnomedRefSetMemberIndexEntry.from(SnomedRequests.prepareSearchMember()
				.all()
				.filterByActive(true)
				.filterByReferencedComponent(id)
				.build(branchPath.getPath())
				.execute(ApplicationContext.getServiceForClass(IEventBus.class))
				.getSync());
	}

}