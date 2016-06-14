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
package com.b2international.snowowl.snomed.datastore.index.change;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.index.revision.RevisionSearcher;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.datastore.index.ChangeSetProcessorBase;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.b2international.snowowl.snomed.core.mrcm.ConceptModelUtils;
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
	public void process(ICDOCommitChangeSet commitChangeSet, RevisionSearcher searcher) {
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
		final String domainExpression = PredicateUtils.getEscgExpression(domain);
		GroupRule groupRule = GroupRule.ALL_GROUPS;
		int minCardinality = -1;
		int maxCardinality = 0;
		
		ConceptModelPredicate predicate = constraint.getPredicate();
		
		if (predicate instanceof CardinalityPredicate) {
			final CardinalityPredicate cardinalityPredicate = (CardinalityPredicate) predicate;
			if (0 == cardinalityPredicate.getMaxCardinality()) {
				// TODO LOG???
				return;
			}
			predicate = cardinalityPredicate.getPredicate();
			minCardinality = cardinalityPredicate.getMinCardinality();
			maxCardinality = cardinalityPredicate.getMaxCardinality();
			
			if (cardinalityPredicate.getGroupRule() != null) {
				groupRule = cardinalityPredicate.getGroupRule();
			} else {
				// TODO LOG???
			}
		}

		final PredicateIndexEntry.Builder doc;
		
		if (predicate instanceof DescriptionPredicate) {
			doc = PredicateIndexEntry.descriptionBuilder().descriptionType(((DescriptionPredicate) predicate).getTypeId());
		} else if (predicate instanceof ConcreteDomainElementPredicate) {
			final ConcreteDomainElementPredicate dataTypePredicate = (ConcreteDomainElementPredicate) predicate;
			doc = PredicateIndexEntry.dataTypeBuilder()
					.dataTypeLabel(dataTypePredicate.getLabel())
					.dataTypeName(dataTypePredicate.getName())
					.dataType(dataTypePredicate.getType());
		} else if (predicate instanceof RelationshipPredicate) {
			final RelationshipPredicate relationshipPredicate = (RelationshipPredicate) predicate;
			final String characteristicTypeConceptId = relationshipPredicate.getCharacteristicTypeConceptId();
			final String type = PredicateUtils.getEscgExpression(relationshipPredicate.getAttribute());
			final String valueType = PredicateUtils.getEscgExpression(relationshipPredicate.getRange());
			final String characteristicType = Strings.isNullOrEmpty(characteristicTypeConceptId) ? "<" + Concepts.CHARACTERISTIC_TYPE : "<<" + characteristicTypeConceptId;

			doc = PredicateIndexEntry.relationshipBuilder()
				.relationshipTypeExpression(type)
				.relationshipValueExpression(valueType)
				.characteristicTypeExpression(characteristicType)
				.groupRule(groupRule);
		} else {
			throw new IllegalArgumentException("Cannot index constraint " + constraint);
		}
		
		doc.id(CDOIDUtil.getLong(constraint.cdoID()))
			.domain(domainExpression)
			.minCardinality(minCardinality)
			.maxCardinality(maxCardinality);
		
		indexRevision(constraint.cdoID(), doc.build());
	}

}
