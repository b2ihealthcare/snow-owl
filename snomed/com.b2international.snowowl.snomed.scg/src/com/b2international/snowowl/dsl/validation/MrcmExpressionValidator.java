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
package com.b2international.snowowl.dsl.validation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.markers.IDiagnostic;
import com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity;
import com.b2international.snowowl.dsl.expressionextractor.ExtractedSCGAttributeGroup;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.datastore.ConceptModelProvider;
import com.b2international.snowowl.snomed.datastore.IConceptModelProvider;
import com.b2international.snowowl.snomed.datastore.MrcmEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedClientRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ConceptSetProcessorFactory;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConstraintBase;
import com.b2international.snowowl.snomed.mrcm.ConstraintStrength;
import com.b2international.snowowl.snomed.mrcm.DependencyPredicate;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.b2international.snowowl.snomed.mrcm.core.renderer.ConceptModelComponentRenderer;
import com.b2international.snowowl.snomed.mrcm.core.validator.MrcmConstraintDiagnostic;
import com.b2international.snowowl.snomed.mrcm.core.validator.MrcmPredicateDiagnostic;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Class is for validation SCG expressions based on MRCM rules.
 * 
 */
public class MrcmExpressionValidator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MrcmExpressionValidator.class);
	private static final String CONSTRAINT_VIOLATION_MESSAGE_PREFIX = "MRCM constraint violated: ";

	
	public List<IDiagnostic> validate(final List<String> focusConceptIdList, final List<ExtractedSCGAttributeGroup> attributeGroups) {
		MrcmEditingContext context = null;
		ConceptModel conceptModel = null;
		try {

			final Set<ConstraintBase> constraints = Sets.newHashSet();
			final SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getServiceChecked(SnomedClientTerminologyBrowser.class);
			final SnomedClientRefSetBrowser refSetBrowser = ApplicationContext.getInstance().getServiceChecked(SnomedClientRefSetBrowser.class);

			context = new MrcmEditingContext();
			conceptModel = context.getOrCreateConceptModel();

			for (final String focusConceptId : focusConceptIdList) {
				constraints.addAll(getConceptModelProvider().getConstraintsForValidation(conceptModel, focusConceptId, terminologyBrowser, refSetBrowser));
			}

			final List<IDiagnostic> result = Lists.newArrayList();

			for (final ConstraintBase constraintBase : constraints) {
				final IDiagnostic constraintDiagnostic = new MrcmConstraintDiagnostic(constraintBase, CONSTRAINT_VIOLATION_MESSAGE_PREFIX
						+ ConceptModelComponentRenderer.getHumanReadableRendering(constraintBase));
				checkConstraint(focusConceptIdList, attributeGroups, constraintBase, constraintDiagnostic);
				result.add(constraintDiagnostic);
			}

			return result;
		} finally {
			if (null != context) {
				context.close();
			}
		}
	}
	
	/**
	 * @param attributeConceptGroups
	 * @param constraint
	 * @return the number of times this constraint has been satisfied
	 */
	private void checkConstraint(final List<String> focusConceptIdList, final List<ExtractedSCGAttributeGroup> attributeConceptGroups, final ConstraintBase constraint, final IDiagnostic parentDiagnostic) {
		if (constraint instanceof AttributeConstraint) {
			final AttributeConstraint attributeConstraint = (AttributeConstraint) constraint;
			if (!attributeConstraint.getDomain().isActive()) {
				//TODO: lots of domains are inactive in the official MRCM XML, bug or feature?
				LOGGER.warn("No active domain for constraint: " + constraint);
//				return;
			}
			final ConceptModelPredicate predicate = attributeConstraint.getPredicate();
			if (!predicate.isActive()) {
				LOGGER.warn("No active predicate for constraint: " + constraint);
				return;
			}
			parentDiagnostic.getChildren().addAll(checkPredicate(predicate, focusConceptIdList, attributeConceptGroups, constraint.getStrength()));
		}
	}
	
	private List<IDiagnostic> checkPredicate(final ConceptModelPredicate predicate, final List<String> focusConceptIdList, final List<ExtractedSCGAttributeGroup> attributeConceptGroups, final ConstraintStrength constraintStrength) {
		final List<IDiagnostic> newDiagnostics = Lists.newArrayList();
		final SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getServiceChecked(SnomedClientTerminologyBrowser.class);
		final SnomedClientRefSetBrowser refSetBrowser = ApplicationContext.getInstance().getServiceChecked(SnomedClientRefSetBrowser.class);
		final SnomedClientIndexService indexService = ApplicationContext.getInstance().getServiceChecked(SnomedClientIndexService.class);
		
		if (predicate instanceof RelationshipPredicate) {
			final RelationshipPredicate relationshipPredicate = (RelationshipPredicate) predicate;
			final ConceptSetDefinition attributeSetDefinition = relationshipPredicate.getAttribute();
			final Set<SnomedConceptIndexEntry> attributeConcepts = ImmutableSet.copyOf(ConceptSetProcessorFactory.createProcessor(attributeSetDefinition, 
					terminologyBrowser, refSetBrowser, indexService).getConcepts());
			final ConceptSetDefinition rangeSetDefinition = relationshipPredicate.getRange();
			final Set<SnomedConceptIndexEntry> rangeConcepts = ImmutableSet.copyOf(ConceptSetProcessorFactory.createProcessor(rangeSetDefinition, 
					terminologyBrowser, refSetBrowser, indexService).getConcepts());

			boolean foundAtLeastOneMatch = false;
			
			final SnomedConceptIndexEntry isaConcept = terminologyBrowser.getConcept(SnomedConstants.Concepts.IS_A);
			for (final String focusConceptConceptId : focusConceptIdList) {
				final SnomedConceptIndexEntry relationshipConcept = terminologyBrowser.getConcept(focusConceptConceptId);
				if (attributeConcepts.contains(isaConcept) && rangeConcepts.contains(relationshipConcept)) {
					// add as many success markers as there are relationships satisfying the constraint
					newDiagnostics.add(new MrcmPredicateDiagnostic(predicate.getUuid(), CONSTRAINT_VIOLATION_MESSAGE_PREFIX 
							+ ConceptModelComponentRenderer.getHumanReadableRendering(predicate), DiagnosticSeverity.OK));
					foundAtLeastOneMatch = true;
				}
			}
			
			for (final ExtractedSCGAttributeGroup attributeConceptGroup : attributeConceptGroups) {
				for(final Entry<String, String> conceptPair : attributeConceptGroup.getAttributeConceptIdMap().entrySet()) {
					final SnomedConceptIndexEntry relationshipTypeMini = terminologyBrowser.getConcept(conceptPair.getKey());
					final SnomedConceptIndexEntry relationshipDestinationMini = terminologyBrowser.getConcept(conceptPair.getValue());
					if (attributeConcepts.contains(relationshipTypeMini) && rangeConcepts.contains(relationshipDestinationMini)) {
						// add as many success markers as there are relationships satisfying the constraint
						newDiagnostics.add(new MrcmPredicateDiagnostic(predicate.getUuid(), CONSTRAINT_VIOLATION_MESSAGE_PREFIX 
								+ ConceptModelComponentRenderer.getHumanReadableRendering(predicate), DiagnosticSeverity.OK));
						foundAtLeastOneMatch = true;
					}
				}
			}
			// add a single failure marker, if there were no matching relationships
			if (!foundAtLeastOneMatch)
				newDiagnostics.add(new MrcmPredicateDiagnostic(predicate.getUuid(),  CONSTRAINT_VIOLATION_MESSAGE_PREFIX
						+ ConceptModelComponentRenderer.getHumanReadableRendering(predicate), mapStrengthToSeverity(constraintStrength)));
		} else if (predicate instanceof DependencyPredicate) {
			throw new UnsupportedOperationException("Dependency predicate validation is not implemented yet.");
		} else if (predicate instanceof CardinalityPredicate) {
			final CardinalityPredicate cardinalityPredicate = (CardinalityPredicate) predicate;
			final ConceptModelPredicate childPredicate = cardinalityPredicate.getPredicate();
			if (!childPredicate.isActive()) {
				LOGGER.warn("No active child predicate for cardinality predicate: " + cardinalityPredicate);
				// TODO: kind of ugly to report this as a success, but it's not a failure either
				newDiagnostics.add(new MrcmPredicateDiagnostic(predicate.getUuid(), CONSTRAINT_VIOLATION_MESSAGE_PREFIX
						+ ConceptModelComponentRenderer.getHumanReadableRendering(predicate), DiagnosticSeverity.OK));
				return newDiagnostics;
			}
			
			final int minCardinality = cardinalityPredicate.getMinCardinality();
			final int maxCardinality = cardinalityPredicate.getMaxCardinality();
			
			GroupRule groupRule = cardinalityPredicate.getGroupRule();
			if (groupRule == null) {
				groupRule = GroupRule.ALL_GROUPS;
				LOGGER.warn("No groupRule attribute found on CardinalityPredicate, defaulting to ALL_GROUPS.");
			}	
			
			switch (groupRule) {
			case UNGROUPED:
				// group == 0 means ungrouped
				newDiagnostics.add(
						checkCardinalityChildPredicate(
								cardinalityPredicate, 
								childPredicate, 
								constraintStrength, 
								CompareUtils.isEmpty(attributeConceptGroups) ? null : attributeConceptGroups.get(0), 
								minCardinality, 
								maxCardinality));
				break;
			case SINGLE_GROUP:
				for (final ExtractedSCGAttributeGroup relationship : attributeConceptGroups) {
					// group == 0 means ungrouped
					if (relationship.getGroupId() == 0)
						continue;
					newDiagnostics.add(checkCardinalityChildPredicate(cardinalityPredicate, childPredicate, constraintStrength, relationship, minCardinality, maxCardinality));
				}
				break;
			case ALL_GROUPS:
				for(final ExtractedSCGAttributeGroup relationship : attributeConceptGroups) {
					newDiagnostics.add(checkCardinalityChildPredicate(cardinalityPredicate, childPredicate, constraintStrength, relationship, minCardinality, maxCardinality));
				}
				break;
			case MULTIPLE_GROUPS:
				throw new UnsupportedOperationException("MULTIPLE_GROUPS policy is not implemented yet.");
			default:
				break;
			}
			
		}

		return newDiagnostics;
	}
	
	private MrcmPredicateDiagnostic checkCardinalityChildPredicate(final CardinalityPredicate cardinalityPredicate, final ConceptModelPredicate childPredicate, final ConstraintStrength constraintStrength, final ExtractedSCGAttributeGroup attributeRelationships, final int min, final int max) {
		final List<ExtractedSCGAttributeGroup> attributeGroups;
		if (null == attributeRelationships) {
			attributeGroups  = Collections.emptyList();
		} else {
			attributeGroups  = Collections.singletonList(attributeRelationships);
		}
		final List<IDiagnostic> childDiagnostics = checkPredicate(childPredicate, Collections.<String> emptyList(), attributeGroups, constraintStrength);
		final int okDiagnosticCount = getOkDiagnosticCount(childDiagnostics);
		if (min <= okDiagnosticCount && convertMaxCardinality(max) >= okDiagnosticCount) {
			return new MrcmPredicateDiagnostic(cardinalityPredicate.getUuid(), CONSTRAINT_VIOLATION_MESSAGE_PREFIX 
					+ ConceptModelComponentRenderer.getHumanReadableRendering(childPredicate), DiagnosticSeverity.OK);
		} else {
			return new MrcmPredicateDiagnostic(cardinalityPredicate.getUuid(), CONSTRAINT_VIOLATION_MESSAGE_PREFIX 
					+ ConceptModelComponentRenderer.getHumanReadableRendering(childPredicate), 
					mapStrengthToSeverity(constraintStrength));
		}
	}
	
	private int getOkDiagnosticCount(final Collection<IDiagnostic> diagnostics) {
		int counter = 0;
		for (final IDiagnostic diagnostic : diagnostics) {
			if (diagnostic.isOk())
				counter++;
		}
		return counter;
	}
	
	private int convertMaxCardinality(final int maxCardinality) {
		return maxCardinality == -1 ? Integer.MAX_VALUE : maxCardinality;
	}
	
	private DiagnosticSeverity mapStrengthToSeverity(final ConstraintStrength strength) {
		switch (strength) {
		case MANDATORY_CM:
			return DiagnosticSeverity.ERROR;
		case ADVISORY_CM:
			return DiagnosticSeverity.WARNING;
		case RECOMMENDED_CM:
			return DiagnosticSeverity.INFO;
		case MANDATORY_PC:
			return DiagnosticSeverity.INFO;
		case INFORMATION_MODEL_PC:
			return DiagnosticSeverity.INFO;
		case IMPLEMENTATION_SPECIFIC_PC:
			return DiagnosticSeverity.INFO;
		case USE_CASE_SPECIFIC_PC:
			return DiagnosticSeverity.INFO;
		default:
			throw new IllegalArgumentException("Unexpected constraint strength: " + strength);
		}
	}
	
	protected IConceptModelProvider getConceptModelProvider() {
		return ConceptModelProvider.INSTANCE;
	}
	
}