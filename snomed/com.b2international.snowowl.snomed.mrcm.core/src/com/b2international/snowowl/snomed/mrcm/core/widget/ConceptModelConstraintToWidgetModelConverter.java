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

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.b2international.commons.CompareUtils;
import com.b2international.commons.collections.LongArrayTransformedToSet;
import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.commons.pcj.LongSets;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.escg.IEscgQueryEvaluatorService;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry.PredicateType;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.ConceptWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.DataTypeContainerWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.DataTypeWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.DescriptionContainerWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.DescriptionWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipGroupContainerWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipGroupWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.RelationshipWidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.WidgetModel;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.WidgetModel.LowerBound;
import com.b2international.snowowl.snomed.mrcm.core.widget.model.WidgetModel.UpperBound;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import bak.pcj.LongCollection;

/**
 */
public class ConceptModelConstraintToWidgetModelConverter {

	/**
	 * Creates the concept widget bean based on the specified predicates based on the SNOMED CT concept attribute constraints.
	 * @param predicateMinis the predicates stored in store representing the SNOMED CT concept attribute constraints.
	 * @return concept widget bean for the form editor.
	 */
	public static ConceptWidgetModel processConstraints(IBranchPath path, final Collection<PredicateIndexEntry> predicateMinis) {
		return internalProcessConstraints(path, predicateMinis);
	}
	
	private static ConceptWidgetModel internalProcessConstraints(final IBranchPath branchPath, final Collection<PredicateIndexEntry> predicateMinis) {
		
		final List<DescriptionWidgetModel> descriptionWidgetModels = newSynchronizedList();
		final List<DataTypeWidgetModel> dataTypeWidgetModels = newSynchronizedList();
		final List<WidgetModel> singleGroupRelationshipWidgetModels = newSynchronizedList();
		final List<RelationshipWidgetModel> ungroupedRelationshipWidgetModels = newSynchronizedList();
		final List<RelationshipGroupWidgetModel> relationshipGroupWidgetModels = newSynchronizedList();
		
		final List<Runnable> runnables = Lists.newArrayList();
		for (final PredicateIndexEntry predicateIndexEntry : predicateMinis) {
			runnables.add(new Runnable() { @Override public void run() {
				processAttributeConstraint(branchPath, descriptionWidgetModels, dataTypeWidgetModels, singleGroupRelationshipWidgetModels, ungroupedRelationshipWidgetModels, predicateIndexEntry);
			}});
		}
		
		ForkJoinUtils.runInParallel(runnables);
		
		//should be added in the very end of the process
		//otherwise we can get the wildcard when calling e.g.: com.b2international.snowowl.snomed.mrcm.core.widget.model.DescriptionContainerWidgetModel.getFirstMatching(String)
		descriptionWidgetModels.add(DescriptionWidgetModel.createUnsanctionedModel());
		
		for (DataType type : DataType.values()) {
			dataTypeWidgetModels.add(DataTypeWidgetModel.createUnsanctionedModel(branchPath, type));
		}
		
		ForkJoinUtils.runInParallel(
				new Runnable() { @Override public void run() { createSingleGroupModels(singleGroupRelationshipWidgetModels, branchPath); }},
				new Runnable() { @Override public void run() { ungroupedRelationshipWidgetModels.add(RelationshipWidgetModel.createUnsanctionedModel(branchPath)); }}
		);
		
		final DescriptionContainerWidgetModel descriptionContainerWidgetModel = new DescriptionContainerWidgetModel(descriptionWidgetModels);
		final DataTypeContainerWidgetModel dataTypeContainerWidgetModel = new DataTypeContainerWidgetModel(dataTypeWidgetModels);
		
		final RelationshipGroupWidgetModel ungroupedModel = RelationshipGroupWidgetModel.createUngroupedModel(ungroupedRelationshipWidgetModels);
		ungroupedModel.setConcreteDomainSupported(ApplicationContext.getInstance().getServiceChecked(SnomedCoreConfiguration.class).isConcreteDomainSupported());
		relationshipGroupWidgetModels.add(ungroupedModel);
		relationshipGroupWidgetModels.add(RelationshipGroupWidgetModel.createGroupedModel(singleGroupRelationshipWidgetModels));
		final RelationshipGroupContainerWidgetModel relationshipGroupContainerWidgetModel = new RelationshipGroupContainerWidgetModel(relationshipGroupWidgetModels);
		
		final ConceptWidgetModel conceptWidgetModel = new ConceptWidgetModel(descriptionContainerWidgetModel, relationshipGroupContainerWidgetModel, dataTypeContainerWidgetModel);
		return conceptWidgetModel;
	}

	private static void createSingleGroupModels(final List<WidgetModel> singleGroupRelationshipWidgetModels, final IBranchPath branchPath) {
		
		singleGroupRelationshipWidgetModels.add(RelationshipWidgetModel.createUnsanctionedModel(branchPath));
		
		for (final DataType dataType : DataType.values()) {
			singleGroupRelationshipWidgetModels.add(DataTypeWidgetModel.createInfrastructureModel(dataType));
		}
	}
	
	private static void processAttributeConstraint(final IBranchPath branchPath, final List<DescriptionWidgetModel> descriptionWidgetModels, final List<DataTypeWidgetModel> dataTypeWidgetModels,
			final List<WidgetModel> singleGroupRelationshipWidgetModels, final List<RelationshipWidgetModel> ungroupedRelationshipWidgetModels,
			final PredicateIndexEntry predicate) {
		switch (predicate.getType()) {
			case DATATYPE:
				processConcreteDomainElementPredicate(predicate, dataTypeWidgetModels);
				break;
			case DESCRIPTION:
				processDescriptionPredicate(predicate, descriptionWidgetModels);
				break;
			case RELATIONSHIP:
				processRelationshipPredicate(branchPath, predicate, ungroupedRelationshipWidgetModels, singleGroupRelationshipWidgetModels);
		}
	}

	private static void processDescriptionPredicate(final PredicateIndexEntry predicate, final List<DescriptionWidgetModel> descriptionWidgetModels) {
		
		final LowerBound lowerBound = predicate.isRequired() ? LowerBound.REQUIRED : LowerBound.OPTIONAL;
		final UpperBound upperBound = predicate.isMultiple() ? UpperBound.MULTIPLE : UpperBound.SINGLE;
		
		final DescriptionWidgetModel descriptionWidgetModel = DescriptionWidgetModel.createRegularModel(
				lowerBound, 
				upperBound, 
				getIds(predicate.getDescriptionTypeId()));
		descriptionWidgetModels.add(descriptionWidgetModel);
	}

	private static void processConcreteDomainElementPredicate(final PredicateIndexEntry predicate, final List<DataTypeWidgetModel> dataTypeWidgetModels) {
		final LowerBound lowerBound = predicate.isRequired() ? LowerBound.REQUIRED : LowerBound.OPTIONAL;
		final UpperBound upperBound = predicate.isMultiple()? UpperBound.MULTIPLE : UpperBound.SINGLE;
		final DataTypeWidgetModel dataTypeWidgetModel = DataTypeWidgetModel.createRegularModel(lowerBound, upperBound, 
				predicate.getDataTypeName(), predicate.getDataTypeType());
		dataTypeWidgetModels.add(dataTypeWidgetModel);
	}

	/**
	 * @param predicate
	 * @param relationshipWidgetModels
	 * @param required
	 * @param multiple
	 * @param strength
	 */
	private static void processRelationshipPredicate(final IBranchPath branchPath, 
			final PredicateIndexEntry predicate, 
			final List<RelationshipWidgetModel> ungroupedRelationshipWidgetModels, 
			final List<WidgetModel> singleGroupedRelationshipWidgetModels) {

		Preconditions.checkState(PredicateType.RELATIONSHIP.equals(predicate.getType()), "Predicate type was not a relationship type but " + predicate.getType());
		
		final LowerBound lowerBound = predicate.isRequired() ? LowerBound.REQUIRED : LowerBound.OPTIONAL;
		final UpperBound upperBound = predicate.isMultiple() ? UpperBound.MULTIPLE : UpperBound.SINGLE;
		final LongCollection typeIds = getQueryEvaluatorService().evaluateConceptIds(branchPath, predicate.getRelationshipTypeExpression());
		final LongCollection characteristicTypeIds = getQueryEvaluatorService().evaluateConceptIds(branchPath, predicate.getCharacteristicTypeExpression());
		
		final RelationshipWidgetModel relationshipWidgetModel = RelationshipWidgetModel.createRegularModel(lowerBound, upperBound, branchPath, 
				newHashSet(LongSets.toStringSet(typeIds)),
				predicate.getRelationshipValueExpression(),
				newHashSet(LongSets.toStringSet(characteristicTypeIds)));
		
		switch (predicate.getGroupRule()) {
			case SINGLE_GROUP: 
				singleGroupedRelationshipWidgetModels.add(relationshipWidgetModel);
				break;
			case UNGROUPED:
				ungroupedRelationshipWidgetModels.add(relationshipWidgetModel);
				break;
			case ALL_GROUPS:
				ungroupedRelationshipWidgetModels.add(relationshipWidgetModel);
				singleGroupedRelationshipWidgetModels.add(relationshipWidgetModel);
				break;
			default: 
				throw new IllegalArgumentException("Unknown group role: " + predicate.getGroupRule()); 
		}
	}

	private static Set<String> getIds(final long... ids) {
		if (CompareUtils.isEmpty(ids)) {
			return Collections.emptySet();
		}
		Arrays.sort(ids);
		return new LongArrayTransformedToSet(ids);
	}
	
	private static <T> List<T> newSynchronizedList() {
		return Collections.synchronizedList(Lists.<T>newArrayList());
	}
	
	private static IEscgQueryEvaluatorService getQueryEvaluatorService() {
		return getServiceForClass(IEscgQueryEvaluatorService.class);
	}
}
