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
package com.b2international.snowowl.snomed.mrcm.core.widget;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.StringUtils;
import com.b2international.commons.functions.UncheckedCastFunction;
import com.b2international.snowowl.core.api.NullComponent;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.CaseSignificance;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.ConceptWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.DescriptionWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.LeafWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.bean.RelationshipWidgetBean;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.DescriptionWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipGroupWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipGroupWidgetModel.GroupFlag;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.WidgetModel;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * Base class for widget bean provider strategies.
 * 
 */
public abstract class WidgetBeanProviderStrategy {

	protected final ConceptWidgetModel conceptWidgetModel;
	
	/**
	 * Map for caching concepts based on their unique SNOMED CT concept ID.
	 * <br>This cache could be cleared with {@link #clear()} after populating the widget bean.
	 */
	protected Map<String, SnomedConceptIndexEntry> componentCache;
	protected final boolean includeUnsanctioned;
	
	public WidgetBeanProviderStrategy(final ConceptWidgetModel conceptWidgetModel, final boolean includeUnsanctioned) {
		this.includeUnsanctioned = includeUnsanctioned;
		this.conceptWidgetModel = checkNotNull(conceptWidgetModel, "conceptWidgetModel");
		componentCache = Maps.newHashMap();
	}

	/**
	 * Creates and returns {@link LeafWidgetBean widget beans} for the specified relationships.
	 * 
	 * @param groupModel the relationship group widget model
	 * @param relationships the relationships
	 * @return the relationship widget beans
	 */
	private List<LeafWidgetBean> createRelationshipBeans(final ConceptWidgetBean cwb, final RelationshipGroupWidgetModel groupModel, final Collection<SnomedRelationship> relationships) {
		checkNotNull(groupModel, "groupModel");
		checkNotNull(relationships, "relationships");

		final List<RelationshipWidgetModel> unusedModels = Lists.newArrayList(Iterables.filter(groupModel.getChildren(), RelationshipWidgetModel.class));
		final List<LeafWidgetBean> relationshipBeans = Lists.newArrayList();
		//final List<String> relationshipIds = Lists.newArrayList();
		final Map<String, String> relationshipCharTypeMap = new HashMap<>();
		
		// Matching models are populated with a matched instance
		for (final SnomedRelationship relationship : relationships) {
			
			final RelationshipWidgetModel matchedModel = groupModel.getFirstMatching(
					relationship.getTypeId(), 
					relationship.getDestinationId(),
					relationship.getCharacteristicTypeId());
			
			final String id = relationship.getId();
			final long sctId = Long.valueOf(id);
			final String selectedType = relationship.getTypeId();
			final String selectedValue = relationship.getDestinationId();
			final String selectedCharacteristicType = relationship.getCharacteristicTypeId();
			 
			final RelationshipWidgetBean rwb = new RelationshipWidgetBean(cwb, matchedModel, sctId, relationship.isReleased(), relationship.isUniversalRestriction());
			rwb.setSelectedType(selectedType);
			rwb.setSelectedValue(selectedValue);
			rwb.setSelectedCharacteristicType(selectedCharacteristicType);
			relationshipBeans.add(rwb);
			relationshipCharTypeMap.put(id, selectedCharacteristicType);
			unusedModels.remove(matchedModel);
		}

		// Add related concrete domain widget beans for groups only
		if (!groupModel.isUngrouped()) {
			relationshipBeans.addAll(createRelationshipDataTypeWidgetBeans(cwb, relationshipCharTypeMap));
		}
		
		// The rest is left unpopulated
		for (final RelationshipWidgetModel unusedModel : unusedModels) {
			if (!unusedModel.isUnsanctioned() && !shouldSkipRelationshipModel(groupModel, unusedModel)) {
				final RelationshipWidgetBean rwb = new RelationshipWidgetBean(cwb, unusedModel, RelationshipWidgetBean.UNINITIALIZED, false, false);
				
				if (unusedModel.getAllowedTypeIds().size() > 0) {
					rwb.setSelectedType(Iterables.getFirst(unusedModel.getAllowedTypeIds(), null));
				}
				
				// Provide a reasonable default since there is no user-facing control to change this
				final String characteristicTypeId;
				final Set<String> allowedCharacteristicTypeIds = unusedModel.getAllowedCharacteristicTypeIds();

				if (allowedCharacteristicTypeIds.contains(Concepts.STATED_RELATIONSHIP)) {
					characteristicTypeId = Concepts.STATED_RELATIONSHIP;
				} else if (allowedCharacteristicTypeIds.contains(Concepts.DEFINING_RELATIONSHIP)) {
					characteristicTypeId = Concepts.DEFINING_RELATIONSHIP;
				} else {
					characteristicTypeId = Iterables.getFirst(allowedCharacteristicTypeIds, null);
				}

				rwb.setSelectedCharacteristicType(characteristicTypeId);

				if (includeUnsanctioned) {
					relationshipBeans.add(rwb);
				} else {
					if ((!NullComponent.isNullComponent(rwb.getSelectedType()) && !NullComponent.isNullComponent(rwb.getSelectedValue()))) {
						relationshipBeans.add(rwb);
					}
				}
			}
		}
		
		return relationshipBeans;
	}
	
	/**
	 * Creates and returns a {@link Multimap multimap} of relationship group indexes and the relationship widget beans belonging to 
	 * the relationship groups.
	 * 
	 * @return a multimap of relationship group indexes and relationship widget beans
	 */
	public ListMultimap<Integer, LeafWidgetBean> createRelationshipGroupWidgetBeans(final ConceptWidgetBean cwb) {
		final ListMultimap<Integer, LeafWidgetBean> result = ArrayListMultimap.create();
		
		final List<RelationshipGroupWidgetModel> unusedModels = Lists.newArrayList(
				Lists.transform(conceptWidgetModel.getRelationshipGroupContainerModel().getChildren(), new UncheckedCastFunction<WidgetModel, RelationshipGroupWidgetModel>(RelationshipGroupWidgetModel.class)));	
		
		// Collect group numbers
		final Multimap<Integer, SnomedRelationship> relationshipsByGroup = HashMultimap.create();
		
		for (final SnomedRelationship relationship : getRelationships()) {
			relationshipsByGroup.put(relationship.getGroup(), relationship);
		}
		
		// Go through them in natural order
		for (final Integer groupNumber : relationshipsByGroup.keySet()) {
			
			final GroupFlag groupFlag = (groupNumber == 0) ? GroupFlag.UNGROUPED : GroupFlag.GROUPED;
			
			final RelationshipGroupWidgetModel matchedModel = conceptWidgetModel.getRelationshipGroupContainerModel().getFirstMatching(groupFlag);
			final Collection<SnomedRelationship> groupRelationships = relationshipsByGroup.get(groupNumber);
			final List<LeafWidgetBean> relationshipBeans = createRelationshipBeans(cwb, matchedModel, groupRelationships);
			
			result.putAll(groupNumber, relationshipBeans);
			unusedModels.remove(matchedModel);
		}
		
		if (!relationshipsByGroup.isEmpty()) {
			int maxGroupNum = Ordering.natural().max(relationshipsByGroup.keySet());
			
			for (RelationshipGroupWidgetModel model : unusedModels) {
				if (!model.isUnsanctioned()) {
					result.putAll(++maxGroupNum, createRelationshipBeans(cwb, model, Collections.<SnomedRelationship>emptySet()));
				}
			}
		}
	
		return result;
	}

	/**
	 * Creates and returns {@link LeafWidgetBean widget beans} for all descriptions of the currently processed concept.
	 * @param selectedLanguageRefSetIdRef 
	 * @param descriptionBeansRef 
	 * 
	 * @return the description widget models
	 */
	public void createDescriptionWidgetBeans(final ConceptWidgetBean cwb, final AtomicReference<List<LeafWidgetBean>> descriptionBeansRef, final AtomicReference<String> selectedLanguageRefSetIdRef) {
	
		final List<LeafWidgetBean> result = Lists.newArrayList();
		final List<DescriptionWidgetModel> unusedModels = Lists.newArrayList(
				Lists.transform(conceptWidgetModel.getDescriptionContainerModel().getChildren(), new UncheckedCastFunction<WidgetModel, DescriptionWidgetModel>(DescriptionWidgetModel.class)));
		
		final Map<String, Multimap<String, String>> descriptionPreferabilityMap = getDescriptionPreferabilityMap();
		
		// Create and populate instance beans for matching models
		for (final SnomedDescription description : getDescriptions()) {
			
			if (!description.isActive()) {
				continue;
			}
			
			final String descriptionId = description.getId();
			final String typeId = description.getTypeId();
			final String term = description.getTerm();
			final boolean released = description.isReleased();
			
			boolean preferred;
			
			if (descriptionPreferabilityMap.containsKey(descriptionId)) {
				// the description should show up as preferred on the UI if it is preferred in the currently selected lang refset and it's type is not FSN nor TEXT DEF
				final Multimap<String, String> acceptabilityMap = descriptionPreferabilityMap.get(descriptionId);
				Collection<String> preferredInLanguageRefsetIds = acceptabilityMap.get(Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED);
				preferred = !Concepts.FULLY_SPECIFIED_NAME.equals(typeId) && !Concepts.TEXT_DEFINITION.equals(typeId) && preferredInLanguageRefsetIds.contains(selectedLanguageRefSetIdRef.get());
			} else {
				preferred = false;
			}

			final DescriptionWidgetModel matchingModel = conceptWidgetModel.getDescriptionContainerModel().getFirstMatching(typeId);
			final DescriptionWidgetBean matchingBean = new DescriptionWidgetBean(cwb, matchingModel, Long.parseLong(descriptionId), released, preferred);
			
			matchingBean.setSelectedType(typeId);
			matchingBean.setTerm(term);
			matchingBean.setCaseSensitivity(description.getCaseSensitivity());
			
			result.add(matchingBean);
			
			unusedModels.remove(matchingModel);
		}
		
		// Now create unpopulated beans for the rest of the models, except the unsanctioned and the ones expecting a preferred term
		for (final DescriptionWidgetModel widgetModel : unusedModels) {
			if (!widgetModel.isUnsanctioned()) {
				
				if (shouldSkipDescriptionModel(result, widgetModel)) {
					continue;
				}
				
				final DescriptionWidgetBean dwb = new DescriptionWidgetBean(cwb, widgetModel, DescriptionWidgetBean.UNINITIALIZED, false, false);
				dwb.setCaseSensitivity(CaseSignificance.ENTIRE_TERM_CASE_INSENSITIVE);
				
				if (widgetModel.getAllowedTypeIds().size() == 1) {
					dwb.setSelectedType(Iterables.getOnlyElement(widgetModel.getAllowedTypeIds())); 
				}
				
				if (includeUnsanctioned) {
					result.add(dwb);
 				} else {
 					if ((!StringUtils.isEmpty(dwb.getTerm()) && !NullComponent.isNullComponent(dwb.getSelectedType()))) {
 						result.add(dwb);
 					}
 				}
				
			}
		}
		
		descriptionBeansRef.set(result);
	}

	/**
	 * Creates and returns {@link LeafWidgetBean widget beans} for the data types associated with the currently processed concept.
	 * @param cwb 
	 * 
	 * @return the data type widget beans
	 */
	abstract public List<LeafWidgetBean> createDataTypeWidgetBeans(ConceptWidgetBean cwb);
	
	abstract public List<LeafWidgetBean> createRelationshipDataTypeWidgetBeans(ConceptWidgetBean cwb, final Map<String, String> relationshipCharTypeMap);

	/**
	 * Clears the underlying cached components if any.
	 */
	public void clear() {
		
		if (!CompareUtils.isEmpty(componentCache)) {
			componentCache.clear();
		}
		
	}
	
	protected boolean shouldSkipDescriptionModel(final List<LeafWidgetBean> result, final DescriptionWidgetModel widgetModel) {
		
		for (final DescriptionWidgetBean createdBean : Iterables.filter(result, DescriptionWidgetBean.class)) {
			
			/*
			 * XXX: An existing description bean may satisfy multiple rules (even though only one is selected as its parent model). Skip
			 * those who do, and treat all descriptions as not preferred.
			 */
			if (widgetModel.matches(createdBean.getSelectedTypeId())) {
				return true;
			}
		}
		
		return false;
	}

	protected boolean shouldSkipRelationshipModel(final RelationshipGroupWidgetModel groupModel, final RelationshipWidgetModel unusedModel) {
		// Skip if we are in a group and the type is in the set that should never appear in a group
		return !groupModel.isUngrouped() && !Sets.intersection(unusedModel.getAllowedTypeIds(), WidgetBeanUtils.NEVER_GROUPED_RELATIONSHIP_TYPE_IDS).isEmpty();
	}
	
	/**
	 * Returns a map where the unique keys are the description ids and the values are the acceptability maps. The acceptability map's keys are the
	 * acceptability ids and the values are the language reference set ids. 
	 * E.g.: <description id> -> <acceptabilityId1> -> <language reference set id1> 
	 * 							 					-> <language reference set id2>
	 * 							 <acceptabilityId2> -> <language reference set id3>
	 * @return
	 */
	abstract protected Map<String, Multimap<String, String>> getDescriptionPreferabilityMap();
	
	abstract protected Collection<SnomedDescription> getDescriptions();
	
	abstract protected Collection<SnomedRelationship> getRelationships();

}
