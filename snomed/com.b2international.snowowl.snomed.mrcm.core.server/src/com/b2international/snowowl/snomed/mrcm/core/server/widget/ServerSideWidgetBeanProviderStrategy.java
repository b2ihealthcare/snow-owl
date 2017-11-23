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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.b2international.commons.StringUtils;
import com.b2international.commons.functions.UncheckedCastFunction;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.IIndexQueryAdapter;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.SnomedTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionContainerQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedDescriptionIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.SnomedIndexService;
import com.b2international.snowowl.snomed.datastore.index.SnomedRelationshipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.refset.SnomedRefSetMembershipIndexQueryAdapter.SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.SnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.mrcm.core.widget.SnomedDescription;
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
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Server side widget bean provider strategy implementation.
 * 
 * Calls the underlying service methods (e.g. index service, terminology browser, etc.) directly as opposed to over RPC.
 * 
 */
public class ServerSideWidgetBeanProviderStrategy extends WidgetBeanProviderStrategy {

	private final IBranchPath branchPath;
	private final String conceptId;

	public ServerSideWidgetBeanProviderStrategy(final String conceptId, final ConceptWidgetModel conceptWidgetModel, final IBranchPath branchPath, final boolean includeUnsanctioned) {
		super(conceptWidgetModel, includeUnsanctioned);
		this.conceptId = conceptId;
		this.branchPath = branchPath;
	}

	@Override
	protected Map<String, Multimap<String, String>> getDescriptionPreferabilityMap() {
		return getServiceForClass(ISnomedComponentService.class).getDescriptionPreferabilityMap(branchPath, conceptId);
	}

	@Override
	protected Collection<SnomedDescription> getDescriptions() {
		final SnomedDescriptionIndexQueryAdapter queryAdapter = SnomedDescriptionContainerQueryAdapter.createFindByConceptIds(conceptId);
		return Collections2.transform(getIndexService().searchUnsorted(branchPath, queryAdapter), SnomedDescription.IndexObjectConverterFunctions.INSTANCE);
	}
	
	protected SnomedConceptIndexEntry getConcept(final String conceptId) {
		return ApplicationContext.getInstance().getService(SnomedTerminologyBrowser.class).getConcept(branchPath, conceptId);
	}

	/*returns with the index service for the SNOMED CT terminology*/
	private SnomedIndexServerService getIndexService() {
		return (SnomedIndexServerService) ApplicationContext.getInstance().getService(SnomedIndexService.class);
	}

	@Override
	protected Collection<SnomedRelationship> getRelationships() {
		final int searchStyle = SnomedRelationshipIndexQueryAdapter.SEARCH_SOURCE_ID | SnomedRelationshipIndexQueryAdapter.SEARCH_ACTIVE_RELATIONSHIPS_ONLY;
		final SnomedRelationshipIndexQueryAdapter adapter = new SnomedRelationshipIndexQueryAdapter(conceptId, searchStyle);
		final int limit = getIndexService().getTotalHitCount(branchPath, adapter.createQuery());
		
		if (limit < 1) {
			return Collections.emptySet();
		} else {
			return Collections2.transform(getIndexService().search(branchPath, adapter, limit), SnomedRelationship.IndexObjectConverterFunction.INSTANCE);
		}
	}

	@Override
	public List<LeafWidgetBean> createRelationshipDataTypeWidgetBeans(final ConceptWidgetBean cwb, final Map<String, String> relationshipIdToCharTypeMap) {

		final List<LeafWidgetBean> beans = Lists.newArrayList();

		//ignore inactive ones
		final Collection<SnomedRefSetMemberIndexEntry> indexEntries = new SnomedRefSetMembershipLookupService().getRelationshipDataTypes(relationshipIdToCharTypeMap.keySet());
		
		//occurred 
		final Iterable<SnomedRefSetMemberIndexEntry> dataTypes = Iterables.filter(indexEntries, new Predicate<SnomedRefSetMemberIndexEntry>() {
			@Override public boolean apply(final SnomedRefSetMemberIndexEntry member) {
				return member.isActive();
			}
		});
		
		final RelationshipGroupWidgetModel groupModel = conceptWidgetModel.getRelationshipGroupContainerModel().getFirstMatching(GroupFlag.GROUPED);
		
		for (final SnomedRefSetMemberIndexEntry entry : dataTypes) {
			final DataTypeWidgetModel matchingModel = groupModel.getFirstMatching(entry.getAttributeLabel(), entry.getRefSetPackageDataType());
			final DataTypeWidgetBean widgetBean = new DataTypeWidgetBean(cwb, matchingModel, entry.getReferencedComponentId(), 
					relationshipIdToCharTypeMap.get(entry.getReferencedComponentId()), entry.getId(), entry.isReleased(), true);
			if (entry.getUomComponentId() != null) {
				widgetBean.setSelectedUom(entry.getUomComponentId());
			}
			widgetBean.setSelectedValue(SnomedRefSetUtil.serializeValue(entry.getRefSetPackageDataType(), entry.getValue()));
			widgetBean.setSelectedLabel(entry.getAttributeLabel());
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
		
		for (final SnomedRefSetMemberIndexEntry entry : getConcreteDataTypes(conceptId)) {
			final DataTypeWidgetModel matchingModel = dataTypeModel.getFirstMatching(entry.getAttributeLabel(), entry.getRefSetPackageDataType());
			final DataTypeWidgetBean widgetBean = new DataTypeWidgetBean(cwb, matchingModel, entry.getReferencedComponentId(), entry.getId(), entry.isReleased(), false);
			widgetBean.setSelectedValue(SnomedRefSetUtil.serializeValue(entry.getRefSetPackageDataType(), entry.getValue()));
			widgetBean.setSelectedLabel(entry.getAttributeLabel());
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

	private Iterable<SnomedRefSetMemberIndexEntry> getConcreteDataTypes(final String id) {
		final IIndexQueryAdapter<SnomedRefSetMemberIndexEntry> createFindByRefSetTypeQuery = SnomedConcreteDataTypeRefSetMembershipIndexQueryAdapter
				.createFindActivesByReferencedComponentIdQuery(CONCEPT, id);
		// XXX The number of allowed concrete domain datatypes are maximized in 100 for a concept
		return ApplicationContext.getInstance().getService(SnomedIndexService.class).search(branchPath, createFindByRefSetTypeQuery, 100);
	}

}