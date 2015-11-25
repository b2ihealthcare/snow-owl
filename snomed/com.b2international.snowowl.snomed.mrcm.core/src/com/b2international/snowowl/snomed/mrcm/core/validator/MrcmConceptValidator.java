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
package com.b2international.snowowl.snomed.mrcm.core.validator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.markers.IDiagnostic;
import com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity;
import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.datastore.ConceptModelProvider;
import com.b2international.snowowl.snomed.datastore.IConceptModelProvider;
import com.b2international.snowowl.snomed.datastore.MrcmEditingContext;
import com.b2international.snowowl.snomed.datastore.SnomedClientPredicateBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientRefSetBrowser;
import com.b2international.snowowl.snomed.datastore.SnomedClientTerminologyBrowser;
import com.b2international.snowowl.snomed.datastore.index.SnomedClientIndexService;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.services.ConceptSetProcessorFactory;
import com.b2international.snowowl.snomed.datastore.services.SnomedRefSetMembershipLookupService;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.ConstraintBase;
import com.b2international.snowowl.snomed.mrcm.ConstraintStrength;
import com.b2international.snowowl.snomed.mrcm.DataType;
import com.b2international.snowowl.snomed.mrcm.DependencyPredicate;
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.b2international.snowowl.snomed.mrcm.core.renderer.ConceptModelComponentRenderer;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Validates concepts against the MRCM constraints.
 * 
 */
public class MrcmConceptValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(MrcmConceptValidator.class);
	private static final String CONSTRAINT_VIOLATION_MESSAGE_PREFIX = "MRCM constraint violated: ";
	private final SnomedClientTerminologyBrowser terminologyBrowser;
	private final SnomedClientRefSetBrowser refSetBrowse;

	/**
	 * @param terminologyBrowser
	 */
	public MrcmConceptValidator(final SnomedClientTerminologyBrowser terminologyBrowser, final SnomedClientRefSetBrowser refSetBrowser) {
		this.terminologyBrowser = terminologyBrowser;
		this.refSetBrowse = refSetBrowser;
	}

	public List<IDiagnostic> validate(final Map<String, AttributeConstraint> predicateStorageKeyConstraintBaseMap, final Concept concept) {
		final List<IDiagnostic> result = Lists.newArrayList();
		final SnomedClientPredicateBrowser predicateBrowser = ApplicationContext.getInstance().getService(SnomedClientPredicateBrowser.class);
		final Collection<PredicateIndexEntry> predicates = predicateBrowser.getPredicates(concept.getId(), null);
		final List<Relationship> outboundRelationships = concept.getOutboundRelationships();
		final Collection<Relationship> activeOutboundRelationships = Collections2.filter(outboundRelationships, new ActiveComponentPredicate());
		
		final Iterable<AttributeConstraint> constraints = Iterables.transform(predicates, new Function<PredicateIndexEntry, AttributeConstraint>() {
			@Override public AttributeConstraint apply(final PredicateIndexEntry predicate) {
				return predicateStorageKeyConstraintBaseMap.get(predicate.getId());
			}
		});
		
		for (final ConstraintBase constraintBase : constraints) {
			final StringBuilder builder = new StringBuilder();
			builder.append(CONSTRAINT_VIOLATION_MESSAGE_PREFIX);
			builder.append(ConceptModelComponentRenderer.getHumanReadableRendering(constraintBase));
			final IDiagnostic constraintDiagnostic = new MrcmConstraintDiagnostic(constraintBase, builder.toString());
			checkConstraint(concept, activeOutboundRelationships, constraintBase, constraintDiagnostic);
			result.add(constraintDiagnostic);
		}

		return result;
	}
	
	public List<IDiagnostic> validate(final Concept concept) {
		MrcmEditingContext context = null;
		ConceptModel conceptModel = null;
		try {
			final List<IDiagnostic> result = Lists.newArrayList();

			context = new MrcmEditingContext();
			conceptModel = context.getOrCreateConceptModel();

			final Set<ConstraintBase> constraints = getConceptModelProvider().getConstraintsForValidation(conceptModel, concept.getId(), terminologyBrowser, refSetBrowse);
			final List<Relationship> outboundRelationships = concept.getOutboundRelationships();
			final Collection<Relationship> activeOutboundRelationships = Collections2.filter(outboundRelationships, new ActiveComponentPredicate());
			for (final ConstraintBase constraintBase : constraints) {
				final IDiagnostic constraintDiagnostic = new MrcmConstraintDiagnostic(constraintBase, CONSTRAINT_VIOLATION_MESSAGE_PREFIX
						+ ConceptModelComponentRenderer.getHumanReadableRendering(constraintBase));
				checkConstraint(concept, activeOutboundRelationships, constraintBase, constraintDiagnostic);
				result.add(constraintDiagnostic);
			}

			return result;
		} finally {
			if (null != context) {
				context.close();
			}
		}

	}
	
	protected IConceptModelProvider getConceptModelProvider() {
		return ConceptModelProvider.INSTANCE;
	}

	/**
	 * @param concept TODO
	 * @param relationships
	 * @param constraint
	 * @return the number of times this constraint has been satisfied
	 */
	private void checkConstraint(final Concept concept, final Collection<Relationship> relationships, final ConstraintBase constraint, final IDiagnostic parentDiagnostic) {
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
			parentDiagnostic.getChildren().addAll(checkPredicate(predicate, concept, relationships, constraint.getStrength()));
		}
	}

	/**
	 * @param predicate
	 * @param concept TODO
	 * @param relationships
	 * @return the number of times the predicate has been satisfied
	 */
	private List<IDiagnostic> checkPredicate(final ConceptModelPredicate predicate, final Concept concept, final Collection<Relationship> relationships, final ConstraintStrength constraintStrength) {
		final List<IDiagnostic> newDiagnostics = Lists.newArrayList();
		final SnomedClientTerminologyBrowser terminologyBrowser = ApplicationContext.getInstance().getServiceChecked(SnomedClientTerminologyBrowser.class);
		final SnomedClientRefSetBrowser refSetBrowser = ApplicationContext.getInstance().getServiceChecked(SnomedClientRefSetBrowser.class);
		final SnomedClientIndexService indexService = ApplicationContext.getInstance().getServiceChecked(SnomedClientIndexService.class);
		
		if (predicate instanceof RelationshipPredicate) {
			final RelationshipPredicate relationshipPredicate = (RelationshipPredicate) predicate;
			final ConceptSetDefinition attributeSetDefinition = relationshipPredicate.getAttribute();
			final ConceptSetDefinition rangeSetDefinition = relationshipPredicate.getRange();

			boolean foundAtLeastOneMatch = false;
			for (final Relationship relationship : relationships) {
				
				// XXX: need to reinitialize these iterators for each relationship, is there a better way? 
				final Iterator<SnomedConceptIndexEntry> attributeConcepts = ConceptSetProcessorFactory.createProcessor(attributeSetDefinition, 
						terminologyBrowser, refSetBrowser, indexService).getConcepts();

				final Iterator<SnomedConceptIndexEntry> rangeConcepts = ConceptSetProcessorFactory.createProcessor(rangeSetDefinition, 
						terminologyBrowser, refSetBrowser, indexService).getConcepts();

				final SnomedConceptIndexEntry relationshipTypeMini = terminologyBrowser.getConcept(relationship.getType().getId());
				final SnomedConceptIndexEntry relationshipDestinationMini = terminologyBrowser.getConcept(relationship.getDestination().getId());
				
				if (Iterators.contains(attributeConcepts, relationshipTypeMini) && Iterators.contains(rangeConcepts, relationshipDestinationMini)) {
					// add as many success markers as there are relationships satisfying the constraint
					newDiagnostics.add(new MrcmPredicateDiagnostic(predicate.getUuid(), CONSTRAINT_VIOLATION_MESSAGE_PREFIX 
							+ ConceptModelComponentRenderer.getHumanReadableRendering(predicate), DiagnosticSeverity.OK));
					foundAtLeastOneMatch = true;
				}
			}
			// add a single failure marker, if there were no matching relationships
			if (!foundAtLeastOneMatch)
				newDiagnostics.add(new MrcmPredicateDiagnostic(predicate.getUuid(),  CONSTRAINT_VIOLATION_MESSAGE_PREFIX
						+ ConceptModelComponentRenderer.getHumanReadableRendering(predicate), mapStrengthToSeverity(constraintStrength)));
		} else if (predicate instanceof DependencyPredicate) {
			throw new UnsupportedOperationException("Dependency predicate validation is not implemented yet.");
		} else if (predicate instanceof DescriptionPredicate) {
			final DescriptionPredicate descriptionPredicate = (DescriptionPredicate) predicate;
			final String descriptionTypeId = descriptionPredicate.getTypeId();
			boolean foundAtLeastOneMatch = false;
			final Collection<Description> activeDescriptions = Collections2.filter(concept.getDescriptions(), new ActiveComponentPredicate());
			for (final Description description : activeDescriptions) {
				if (description.getType().getId().equals(descriptionTypeId)) {
					// add as many success markers as there are descriptions satisfying the constraint
					newDiagnostics.add(new MrcmPredicateDiagnostic(predicate.getUuid(), CONSTRAINT_VIOLATION_MESSAGE_PREFIX 
							+ ConceptModelComponentRenderer.getHumanReadableRendering(predicate), DiagnosticSeverity.OK));
					foundAtLeastOneMatch = true;					
				}
			}
			// add a single failure marker, if there were no matching descriptions
			if (!foundAtLeastOneMatch)
				newDiagnostics.add(new MrcmPredicateDiagnostic(predicate.getUuid(),  CONSTRAINT_VIOLATION_MESSAGE_PREFIX
						+ ConceptModelComponentRenderer.getHumanReadableRendering(predicate), mapStrengthToSeverity(constraintStrength)));
		} else if (predicate instanceof ConcreteDomainElementPredicate) {
			final ConcreteDomainElementPredicate concreteDomainElementPredicate = (ConcreteDomainElementPredicate) predicate;
			final SnomedRefSetMembershipLookupService service = new SnomedRefSetMembershipLookupService();
			final Collection<SnomedRefSetMemberIndexEntry> dataTypes = service.getConceptDataTypes(concept.getId());
			System.out.println(concreteDomainElementPredicate);
			boolean foundAtLeastOneMatch = false;
			for (final SnomedRefSetMemberIndexEntry dataTypeIndexEntry : dataTypes) {
				System.out.println(dataTypeIndexEntry);
				if (concreteDomainElementPredicate.getType().equals(mapModelDataTypeToMrcmDataType(dataTypeIndexEntry.getRefSetPackageDataType()))
						&& concreteDomainElementPredicate.getName().equals(dataTypeIndexEntry.getAttributeLabel())) {
					// add as many success markers as there are concrete domain elements satisfying the constraint
					newDiagnostics.add(new MrcmPredicateDiagnostic(predicate.getUuid(), CONSTRAINT_VIOLATION_MESSAGE_PREFIX 
							+ ConceptModelComponentRenderer.getHumanReadableRendering(predicate), DiagnosticSeverity.OK));
					foundAtLeastOneMatch = true;	
				}
			}
			// add a single failure marker, if there were no matching concrete domain elements
			if (!foundAtLeastOneMatch)
				newDiagnostics.add(new MrcmPredicateDiagnostic(predicate.getUuid(),  CONSTRAINT_VIOLATION_MESSAGE_PREFIX
						+ ConceptModelComponentRenderer.getHumanReadableRendering(predicate), mapStrengthToSeverity(constraintStrength)));
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
			
			final Multimap<Integer, Relationship> groupMap = groupRelationships(relationships);
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
				newDiagnostics.add(checkCardinalityChildPredicate(cardinalityPredicate, childPredicate, constraintStrength, concept, groupMap.get(0), minCardinality, maxCardinality));
				break;
			case SINGLE_GROUP:
				for (final Integer groupNumber : groupMap.keySet()) {
					// group == 0 means ungrouped
					if (groupNumber == 0)
						continue;
					final Collection<Relationship> relationshipsInGroup = groupMap.get(groupNumber);
					newDiagnostics.add(checkCardinalityChildPredicate(cardinalityPredicate, childPredicate, constraintStrength, concept, relationshipsInGroup, minCardinality, maxCardinality));
				}
				break;
			case ALL_GROUPS:
				newDiagnostics.add(checkCardinalityChildPredicate(cardinalityPredicate, childPredicate, constraintStrength, concept, relationships, minCardinality, maxCardinality));
				break;
			case MULTIPLE_GROUPS:
				throw new UnsupportedOperationException("MULTIPLE_GROUPS policy is not implemented yet.");
			default:
				break;
			}
			
		}

		return newDiagnostics;
	}

	private MrcmPredicateDiagnostic checkCardinalityChildPredicate(final CardinalityPredicate cardinalityPredicate, final ConceptModelPredicate childPredicate, final ConstraintStrength constraintStrength, final Concept concept, final Collection<Relationship> relationships, final int min, final int max) {
		final List<IDiagnostic> childDiagnostics = checkPredicate(childPredicate, concept, relationships, constraintStrength);
		final int okDiagnosticCount = getOkDiagnosticCount(childDiagnostics);
		if (min <= okDiagnosticCount && convertMaxCardinality(max) >= okDiagnosticCount) {
			return new MrcmPredicateDiagnostic(cardinalityPredicate.getUuid(), CONSTRAINT_VIOLATION_MESSAGE_PREFIX 
					+ ConceptModelComponentRenderer.getHumanReadableRendering(cardinalityPredicate), DiagnosticSeverity.OK);
		} else {
			return new MrcmPredicateDiagnostic(cardinalityPredicate.getUuid(), CONSTRAINT_VIOLATION_MESSAGE_PREFIX 
					+ ConceptModelComponentRenderer.getHumanReadableRendering(cardinalityPredicate), 
					mapStrengthToSeverity(constraintStrength));
		}
	}

	private int convertMaxCardinality(final int maxCardinality) {
		return maxCardinality == -1 ? Integer.MAX_VALUE : maxCardinality;
	}

	private int getOkDiagnosticCount(final Collection<IDiagnostic> diagnostics) {
		int counter = 0;
		for (final IDiagnostic diagnostic : diagnostics) {
			if (diagnostic.isOk())
				counter++;
		}
		return counter;
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
	
	private DataType mapModelDataTypeToMrcmDataType(final com.b2international.snowowl.snomed.snomedrefset.DataType modelDataType) {
		switch (modelDataType) {
		case BOOLEAN: return DataType.BOOLEAN;
		case DATE: return DataType.DATE;
		case DECIMAL: return DataType.FLOAT;
		case INTEGER: return DataType.INTEGER;
		case STRING: return DataType.STRING;
		default:
			throw new IllegalArgumentException("Unexpected datatype: " + modelDataType);
		}
	}
	
	/**
	 * @param relationships
	 * @return an immutable multimap, where the keys are the relationship group numbers, and the values the relationships
	 * belonging to that group
	 */
	private Multimap<Integer, Relationship> groupRelationships(final Collection<Relationship> relationships) {
		final Builder<Integer,Relationship> builder = ImmutableMultimap.<Integer, Relationship>builder();
		for (final Relationship relationship : relationships) {
			builder.put(relationship.getGroup(), relationship);
		}
		return builder.build();
	}

	private static final class ActiveComponentPredicate implements Predicate<Component> {
		@Override public boolean apply(final Component input) {
			return input.isActive();
		}
	}
}