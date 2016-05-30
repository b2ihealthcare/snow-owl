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
package com.b2international.snowowl.datastore.server.snomed.index.change;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry.PredicateType;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.b2international.snowowl.snomed.mrcm.core.ConceptModelUtils;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

/**
 * @since 4.3
 */
public class ConstraintChangeProcessor extends ChangeSetProcessorBase {

	public ConstraintChangeProcessor() {
		super("predicate changes");
	}

	@Override
	public void doProcess(ICDOCommitChangeSet commitChangeSet, RevisionSearcher searcher) {
		final Collection<AttributeConstraint> newAndDirtyConstraints = newHashSet();

		for (ConceptModelPredicate predicate : Iterables.concat(commitChangeSet.getNewComponents(ConceptModelPredicate.class),
				commitChangeSet.getDirtyComponents(ConceptModelPredicate.class))) {
			newAndDirtyConstraints.add(ConceptModelUtils.getContainerConstraint(predicate));
		}

		for (ConceptSetDefinition definition : Iterables.concat(commitChangeSet.getNewComponents(ConceptSetDefinition.class),
				commitChangeSet.getDirtyComponents(ConceptSetDefinition.class))) {
			newAndDirtyConstraints.add(ConceptModelUtils.getContainerConstraint(definition));
		}

		// (re)index new/changed constraints
		for (AttributeConstraint constraint : newAndDirtyConstraints) {
			index(constraint);
		}

		deleteRevisions(PredicateIndexEntry.class, commitChangeSet.getDetachedComponents(MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT));
	}
	
	private void index(AttributeConstraint constraint) {
		final ConceptSetDefinition domain = constraint.getDomain();
		final String queryExpression = PredicateUtils.getEscgExpression(domain);
		GroupRule groupRule = GroupRule.ALL_GROUPS;
		boolean required = false;
		boolean multiple = false;
		
		ConceptModelPredicate predicate = constraint.getPredicate();
		
		if (predicate instanceof CardinalityPredicate) {
			final CardinalityPredicate cardinalityPredicate = (CardinalityPredicate) predicate;
			if (0 == cardinalityPredicate.getMaxCardinality()) {
				// TODO LOG???
				return;
			}
			predicate = cardinalityPredicate.getPredicate();
			required = PredicateUtils.isRequired(cardinalityPredicate);
			multiple = PredicateUtils.isMultiple(cardinalityPredicate);
			if (cardinalityPredicate.getGroupRule() != null) {
				groupRule = cardinalityPredicate.getGroupRule();
			} else {
				// TODO LOG???
			}
		}

		// reindex entire attribute constraint
		PredicateIndexEntry
			.clear()
			.storageKey(CDOIDUtil.getLong(constraint.cdoID()))
			.type(SnomedTerminologyComponentConstants.PREDICATE_TYPE_ID)
			.predicateQueryExpression(queryExpression)
			.predicateRequired(required)
			.predicateMultiple(multiple);
		
		if (predicate instanceof DescriptionPredicate) {
			createDescriptionPredicateDocument(doc, (DescriptionPredicate) predicate);
		} else if (predicate instanceof ConcreteDomainElementPredicate) {
			createConcreteDomainPredicateDocument(doc, (ConcreteDomainElementPredicate) predicate);
		} else if (predicate instanceof RelationshipPredicate) {
			createRelationshipPredicateDocument(doc, (RelationshipPredicate) predicate, groupRule);
		} else {
			throw new IllegalArgumentException("Cannot index constraint " + constraint);
		}
	}

	public static void createConcreteDomainPredicateDocument(SnomedDocumentBuilder doc, ConcreteDomainElementPredicate predicate) {
		doc
			.predicateType(PredicateType.DATATYPE)
			.predicateDataTypeLabel(predicate.getLabel())
			.predicateDataTypeName(predicate.getName())
			.predicateDataType(predicate.getType());
	}

	public static void createDescriptionPredicateDocument(SnomedDocumentBuilder doc, DescriptionPredicate predicate) {
		doc
			.predicateType(PredicateType.DESCRIPTION)
			.predicateDescriptionTypeId(Long.valueOf(predicate.getTypeId()));
	}

	public static void createRelationshipPredicateDocument(SnomedDocumentBuilder doc, RelationshipPredicate predicate, GroupRule groupRule) {
		final String characteristicTypeConceptId = predicate.getCharacteristicTypeConceptId();
		final String type = PredicateUtils.getEscgExpression(predicate.getAttribute());
		final String valueType = PredicateUtils.getEscgExpression(predicate.getRange());
		final String characteristicType = Strings.isNullOrEmpty(characteristicTypeConceptId) ? "<" + Concepts.CHARACTERISTIC_TYPE : "<<" + characteristicTypeConceptId;

		doc
			.predicateType(PredicateType.RELATIONSHIP)
			.predicateRelationshipTypeExpression(type)
			.predicateRelationshipValueExpression(valueType)
			.predicateCharacteristicTypeExpression(characteristicType)
			.predicateGroupRule(groupRule.name());
	}
	
}
