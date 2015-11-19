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
package com.b2international.snowowl.snomed.datastore.index.update;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry.PredicateType;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.google.common.base.Strings;

/**
 * @since 4.3
 */
public class ConstraintUpdater extends DocumentUpdaterBase<SnomedDocumentBuilder> {

	private AttributeConstraint constraint;

	public ConstraintUpdater(AttributeConstraint constraint) {
		super(constraint.getUuid());
		this.constraint = constraint;
	}

	@Override
	public void doUpdate(SnomedDocumentBuilder doc) {
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
		doc
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
