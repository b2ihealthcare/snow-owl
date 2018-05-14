/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.constraint;

import static com.b2international.index.query.Expressions.matchAny;
import static com.b2international.index.query.Expressions.matchAnyEnum;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.Doc;
import com.b2international.index.Keyword;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.api.ITerminologyComponentIdProvider;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.CompositeConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConstraintForm;
import com.b2international.snowowl.snomed.mrcm.ConstraintStrength;
import com.b2international.snowowl.snomed.mrcm.EnumeratedConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.mrcm.ReferenceSetConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.RelationshipConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.util.MrcmSwitch;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Strings;

/**
 * The indexed document representation of an MRCM constraint.
 * 
 * @since 2.0
 */
@Doc
@JsonDeserialize(builder = SnomedConstraintDocument.Builder.class)
public final class SnomedConstraintDocument extends RevisionDocument implements ITerminologyComponentIdProvider {
	private static final long serialVersionUID = -3084452506109842527L;

	public static Builder builder(final AttributeConstraint constraint) {

		// Examine the inner predicate in case it is wrapped in cardinality restrictions
		ConceptModelPredicate predicate = constraint.getPredicate();
		if (predicate instanceof CardinalityPredicate) {
			predicate = ((CardinalityPredicate) predicate).getPredicate();
		}

		final SnomedConstraintPredicateType predicateType = SnomedConstraintPredicateType.typeOf(predicate);
		final ConceptSetDefinitionFragment domainFragment = ConceptSetDefinitionFragment.from(constraint.getDomain());
		final PredicateFragment predicateFragment = PredicateFragment.from(constraint.getPredicate());

		/* 
		 * Collect SCT identifiers and keys seen in the constraint's domain part, which will be used when we
		 * are looking for applicable constraints.
		 */
		final Set<String> selfIds = newHashSet();
		final Set<String> descendantIds = newHashSet();
		final Set<String> refSetIds = newHashSet();
		final Set<String> relationshipKeys = newHashSet(); // "typeId=destinationId" format
		collectIds(constraint.getDomain(), selfIds, descendantIds, refSetIds, relationshipKeys);

		return new Builder()
				.id(constraint.getUuid())
				.storageKey(CDOIDUtils.asLong(constraint.cdoID()))
				.active(constraint.isActive())
				.effectiveTime(EffectiveTimes.getEffectiveTime(constraint.getEffectiveTime()))
				.author(constraint.getAuthor())
				.strength(constraint.getStrength())
				.validationMessage(constraint.getValidationMessage())
				.description(constraint.getDescription())
				.form(constraint.getForm())
				.domain(domainFragment)
				.predicate(predicateFragment)
				.predicateType(predicateType)
				.selfIds(selfIds)
				.descendantIds(descendantIds)
				.refSetIds(refSetIds)
				.relationshipKeys(relationshipKeys);
	}
	
	/**
	 * Collects key concept IDs from the specified concept set definition. Results are aggregated in the
	 * given sets.
	 * 
	 * @param definition
	 *            the definition to parse
	 * @param selfIds
	 *            the definition applies to these concepts
	 * @param descendantIds
	 *            the definition applies to the descendants of these concepts
	 * @param refSetIds
	 *            the definition applies to members of these reference sets
	 * @param relationshipKeys
	 *            the definition applies to concepts that include a relationship with these
	 *            type-destination values
	 */
	private static void collectIds(final ConceptSetDefinition definition, 
			final Set<String> selfIds, 
			final Set<String> descendantIds, 
			final Set<String> refSetIds, 
			final Set<String> relationshipKeys) {

		// XXX: The methods need to return a non-null value so that the switch does not fall "upwards", to the more generic cases.
		new MrcmSwitch<ConceptSetDefinition>() {
			
			@Override
			public ConceptSetDefinition caseHierarchyConceptSetDefinition(final HierarchyConceptSetDefinition definition) {
				final String focusConceptId = definition.getFocusConceptId();
				final HierarchyInclusionType inclusionType = definition.getInclusionType();

				switch (inclusionType) {
					case SELF:
						selfIds.add(focusConceptId);
						break;
					case DESCENDANT:
						descendantIds.add(focusConceptId);
						break;
					case SELF_OR_DESCENDANT:
						selfIds.add(focusConceptId);
						descendantIds.add(focusConceptId);
						break;
					default: 
						throw new IllegalStateException("Unexpected hierarchy inclusion type '" + inclusionType + "'.");
				}

				return definition;
			}

			@Override
			public ConceptSetDefinition caseEnumeratedConceptSetDefinition(final EnumeratedConceptSetDefinition definition) {
				final List<String> conceptIds = definition.getConceptIds();
				selfIds.addAll(conceptIds);
				return definition;
			}

			@Override
			public ConceptSetDefinition caseReferenceSetConceptSetDefinition(final ReferenceSetConceptSetDefinition definition) {
				final String refSetId = definition.getRefSetIdentifierConceptId();
				refSetIds.add(refSetId);
				return definition;
			}

			@Override
			public ConceptSetDefinition caseCompositeConceptSetDefinition(final CompositeConceptSetDefinition definition) {
				for (final ConceptSetDefinition childdefinition : definition.getChildren()) {
					collectIds(childdefinition, selfIds, descendantIds, refSetIds, relationshipKeys);
				}
				return definition;
			}

			@Override
			public ConceptSetDefinition caseRelationshipConceptSetDefinition(final RelationshipConceptSetDefinition definition) {
				final String typeId = definition.getTypeConceptId();
				final String destinationId = definition.getDestinationConceptId();
				relationshipKeys.add(String.format("%s=%s", typeId, destinationId));
				return definition;
			}

		}.doSwitch(definition);
	}


	public static Builder descriptionBuilder() {
		return new Builder().predicateType(SnomedConstraintPredicateType.DESCRIPTION);
	}

	public static Builder relationshipBuilder() {
		return new Builder().predicateType(SnomedConstraintPredicateType.RELATIONSHIP);
	}

	public static Builder dataTypeBuilder() {
		return new Builder().predicateType(SnomedConstraintPredicateType.DATATYPE);
	}

	public static Builder dependencyBuilder() {
		return new Builder().predicateType(SnomedConstraintPredicateType.DEPENDENCY);
	}

	/**
	 * @since 4.7
	 */
	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder extends RevisionDocumentBuilder<Builder> {

		private boolean active;
		private long effectiveTime;
		private String author;

		private ConstraintStrength strength;
		private String validationMessage;
		private String description;
		private ConstraintForm form;
		private ConceptSetDefinitionFragment domain;
		private PredicateFragment predicate;

		private SnomedConstraintPredicateType predicateType;
		private Collection<String> selfIds;
		private Collection<String> descendantIds;
		private Collection<String> refSetIds;
		private Collection<String> relationshipKeys;

		/* Required for Jackson deserialization */
		@JsonCreator
		Builder() { }

		public Builder active(boolean active) {
			this.active = active;
			return getSelf();
		}

		public Builder effectiveTime(long effectiveTime) {
			this.effectiveTime = effectiveTime;
			return getSelf();
		}

		public Builder author(String author) {
			this.author = author;
			return getSelf();
		}

		public Builder strength(ConstraintStrength strength) {
			this.strength = strength;
			return getSelf();
		}

		public Builder validationMessage(String validationMessage) {
			this.validationMessage = validationMessage;
			return getSelf();
		}

		public Builder description(String description) {
			this.description = description;
			return getSelf();
		}

		public Builder form(ConstraintForm form) {
			this.form = form;
			return getSelf();
		}

		public Builder domain(ConceptSetDefinitionFragment domain) {
			this.domain = domain;
			return getSelf();
		}

		public Builder predicate(PredicateFragment predicate) {
			this.predicate = predicate;
			return getSelf();
		}

		public Builder predicateType(SnomedConstraintPredicateType predicateType) {
			this.predicateType = predicateType;
			return getSelf();
		}

		public Builder selfIds(final Collection<String> selfIds) {
			this.selfIds = selfIds;
			return getSelf();
		}

		public Builder descendantIds(final Collection<String> descendantIds) {
			this.descendantIds = descendantIds;
			return getSelf();
		}

		public Builder refSetIds(final Collection<String> refSetIds) {
			this.refSetIds = refSetIds;
			return getSelf();
		}
		
		public Builder relationshipKeys(final Collection<String> relationshipKeys) {
			this.relationshipKeys = relationshipKeys;
			return getSelf();
		}

		public SnomedConstraintDocument build() {
			final SnomedConstraintDocument doc = new SnomedConstraintDocument(id, 
					active, 
					effectiveTime, 
					author, 
					strength, 
					validationMessage, 
					description, 
					form, 
					domain, 
					predicate, 
					predicateType, 
					selfIds, 
					descendantIds, 
					refSetIds,
					relationshipKeys);

			doc.setBranchPath(branchPath);
			doc.setCommitTimestamp(commitTimestamp);
			doc.setStorageKey(storageKey);
			doc.setReplacedIns(replacedIns);
			doc.setSegmentId(segmentId);

			return doc;
		}

		@Override
		protected Builder getSelf() {
			return this;
		}

	}

	/**
	 * @since 4.7
	 */
	public static final class Fields extends RevisionDocument.Fields {
		public static final String PREDICATE_TYPE = "predicateType";
		public static final String SELF_IDS = "selfIds";
		public static final String DESCENDANT_IDS = "descendantIds";
		public static final String REFSET_IDS = "refSetIds";
	}

	/**
	 * @since 4.7
	 */
	public static final class Expressions extends RevisionDocument.Expressions {

		public static Expression predicateTypes(final Collection<SnomedConstraintPredicateType> types) {
			return matchAnyEnum(Fields.PREDICATE_TYPE, types);
		}

		public static Expression selfIds(final Collection<String> selfIds) {
			return matchAny(Fields.SELF_IDS, selfIds);
		}

		public static Expression descendantIds(final Collection<String> descendantIds) {
			return matchAny(Fields.DESCENDANT_IDS, descendantIds);
		}

		public static Expression refSetIds(final Collection<String> refSetIds) {
			return matchAny(Fields.REFSET_IDS, refSetIds);
		}
	}

	private final boolean active;
	private final long effectiveTime;
	@Keyword(index=false)
	private final String author;
	private final ConstraintStrength strength;
	@Keyword(index=false) 
	private final String validationMessage;
	@Keyword(index=false) 
	private final String description;
	private final ConstraintForm form;
	private final ConceptSetDefinitionFragment domain;
	private final PredicateFragment predicate;

	// Used when looking for applicable constraints for a concept
	private final SnomedConstraintPredicateType predicateType;
	private final Set<String> selfIds;
	private final Set<String> descendantIds;
	private final Set<String> refSetIds;
	private final Set<String> relationshipKeys;

	private SnomedConstraintDocument(
			String uuid, 
			boolean active,
			long effectiveTime,
			String author,
			ConstraintStrength strength, 
			String validationMessage, 
			String description, 
			ConstraintForm form,
			ConceptSetDefinitionFragment domain, 
			PredicateFragment predicate,
			SnomedConstraintPredicateType predicateType, 
			Collection<String> selfIds, 
			Collection<String> descendantIds,
			Collection<String> refSetIds, 
			Collection<String> relationshipKeys) {

		super(uuid, Strings.nullToEmpty(description), null);

		this.active = active;
		this.effectiveTime = effectiveTime;
		this.author = author;
		this.strength = strength;
		this.validationMessage = validationMessage;
		this.description = description;
		this.form = form;
		this.domain = domain;
		this.predicate = predicate;
		this.predicateType = predicateType;
		this.selfIds = Collections3.toImmutableSet(selfIds);
		this.descendantIds = Collections3.toImmutableSet(descendantIds);
		this.refSetIds = Collections3.toImmutableSet(refSetIds);
		this.relationshipKeys = Collections3.toImmutableSet(relationshipKeys);
	}

	public boolean isActive() {
		return active;
	}

	public long getEffectiveTime() {
		return effectiveTime;
	}

	public String getAuthor() {
		return author;
	}

	public ConstraintStrength getStrength() {
		return strength;
	}

	public String getValidationMessage() {
		return validationMessage;
	}

	public String getDescription() {
		return description;
	}

	public ConstraintForm getForm() {
		return form;
	}

	public ConceptSetDefinitionFragment getDomain() {
		return domain;
	}

	public PredicateFragment getPredicate() {
		return predicate;
	}

	public SnomedConstraintPredicateType getPredicateType() {
		return predicateType;
	}

	/**
	 * Returns all SNOMED CT identifiers where this predicate can be applied directly.
	 */
	public Set<String> getSelfIds() {
		return selfIds;
	}

	/**
	 * Returns all SNOMED CT identifiers where this predicate can be applied on the sub hierarchy of the given identifier.
	 */
	public Set<String> getDescendantIds() {
		return descendantIds;
	}

	/**
	 * Returns all SNOMED CT reference set identifiers where this predicate can be applied.
	 */
	public Set<String> getRefSetIds() {
		return refSetIds;
	}

	/**
	 * Returns all SNOMED CT relationship type-destination pairs where this predicate can be applied.
	 */
	public Set<String> getRelationshipKeys() {
		return relationshipKeys;
	}

	@Override
	@JsonIgnore
	public String getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.CONSTRAINT;
	}

	@Override
	protected ToStringHelper doToString() {
		return super.doToString()
				.add("uuid", getId())
				.add("active", isActive())
				.add("effectiveTime", getEffectiveTime())
				.add("author", getAuthor())
				.add("strength", getStrength())
				.add("validationMessage", getValidationMessage())
				.add("description", getDescription())
				.add("form", getForm())
				.add("domain", getDomain())
				.add("predicate", getPredicate())
				.add("type", getPredicateType());
	}
}
