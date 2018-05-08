/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.snor;

import static com.b2international.index.query.Expressions.*;
import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOIDUtil;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.Doc;
import com.b2international.index.query.Expression;
import com.b2international.snowowl.core.api.ITerminologyComponentIdProvider;
import com.b2international.snowowl.core.exceptions.NotImplementedException;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;

/**
 * Index document of a MRCM rule.
 * 
 * @see PredicateType
 */
@Doc
@JsonDeserialize(builder = SnomedConstraintDocument.Builder.class)
public final class SnomedConstraintDocument extends RevisionDocument implements ITerminologyComponentIdProvider {

	private static final long serialVersionUID = -3084452506109842527L;

	/**
	 * SNOMED&nbsp;CT concept attribute predicate type.
	 * 
	 * @see SnomedConstraintDocument
	 */
	public static enum PredicateType {
		/** Relationship type predicate. */
		RELATIONSHIP,
		/** Description type predicate. */
		DESCRIPTION,
		/** Data type predicate. */
		DATATYPE;

		/**
		 * Returns with the {@link PredicateType predicate type} enumeration identified by the unique ordinal value.
		 * 
		 * @param ordinal
		 *            the unique ordinal value of the predicate type.
		 * @return the predicate type.
		 */
		public static PredicateType getByOrdinal(final int ordinal) {
			return PredicateType.values()[ordinal];
		}
	}

	public static Builder builder(AttributeConstraint constraint) {
		final ConceptSetDefinition domain = constraint.getDomain();
		final String domainExpression = PredicateUtils.toEclExpression(domain);

		// collect and index domain identifier based on their domain type
		final Set<String> selfIds = newHashSet();
		final Set<String> descendantIds = newHashSet();
		final Set<String> refSetIds = newHashSet();
		PredicateUtils.collectDomainIds(domain, selfIds, descendantIds, refSetIds);
		
		// TODO index relationship refinements as type#value
		GroupRule groupRule = GroupRule.ALL_GROUPS;
		int minCardinality = -1;
		int maxCardinality = 0;
		
		ConceptModelPredicate predicate = constraint.getPredicate();
		
		if (predicate instanceof CardinalityPredicate) {
			final CardinalityPredicate cardinalityPredicate = (CardinalityPredicate) predicate;
			predicate = cardinalityPredicate.getPredicate();
			minCardinality = cardinalityPredicate.getMinCardinality();
			maxCardinality = cardinalityPredicate.getMaxCardinality();
			
			if (cardinalityPredicate.getGroupRule() != null) {
				groupRule = cardinalityPredicate.getGroupRule();
			} else {
				// TODO LOG???
			}
		}

		final SnomedConstraintDocument.Builder doc;
		
		if (predicate instanceof DescriptionPredicate) {
			doc = SnomedConstraintDocument.descriptionBuilder().descriptionTypeId(((DescriptionPredicate) predicate).getTypeId());
		} else if (predicate instanceof ConcreteDomainElementPredicate) {
			final ConcreteDomainElementPredicate dataTypePredicate = (ConcreteDomainElementPredicate) predicate;
			doc = SnomedConstraintDocument.dataTypeBuilder()
					.dataTypeLabel(dataTypePredicate.getLabel())
					.dataTypeName(dataTypePredicate.getName())
					.dataType(dataTypePredicate.getType());
		} else if (predicate instanceof RelationshipPredicate) {
			final RelationshipPredicate relationshipPredicate = (RelationshipPredicate) predicate;
			final String characteristicTypeConceptId = relationshipPredicate.getCharacteristicTypeConceptId();
			final String type = PredicateUtils.toEclExpression(relationshipPredicate.getAttribute());
			final String valueType = PredicateUtils.toEclExpression(relationshipPredicate.getRange());
			final String characteristicType = Strings.isNullOrEmpty(characteristicTypeConceptId) ? "<" + Concepts.CHARACTERISTIC_TYPE : "<<" + characteristicTypeConceptId;

			doc = SnomedConstraintDocument.relationshipBuilder()
				.relationshipTypeExpression(type)
				.relationshipValueExpression(valueType)
				.characteristicTypeExpression(characteristicType)
				.groupRule(groupRule);
		} else {
			throw new IllegalArgumentException("Cannot index constraint " + constraint);
		}
		
		return doc.id(CDOIDUtil.getLong(constraint.cdoID()))
			.domain(domainExpression)
			.selfIds(selfIds)
			.descendantIds(descendantIds)
			.refSetIds(refSetIds)
			.minCardinality(minCardinality)
			.maxCardinality(maxCardinality);
	}
	
	public static Builder descriptionBuilder() {
		return new Builder().predicateType(PredicateType.DESCRIPTION);
	}
	
	public static Builder relationshipBuilder() {
		return new Builder().predicateType(PredicateType.RELATIONSHIP);
	}
	
	public static Builder dataTypeBuilder() {
		return new Builder().predicateType(PredicateType.DATATYPE);
	}

	/**
	 * @since 4.7
	 */
	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder extends RevisionDocumentBuilder<Builder> {

		private PredicateType predicateType;
		private String domain;
		private int minCardinality;
		private int maxCardinality;
		private String descriptionTypeId;
		private String dataTypeName;
		private String dataTypeLabel;
		private DataType dataType;
		private GroupRule groupRule;
		private String characteristicTypeExpression;
		private String relationshipTypeExpression;
		private String relationshipValueExpression;
		private Collection<String> selfIds;
		private Collection<String> descendantIds;
		private Collection<String> refSetIds;

		/* Required for Jackson deserialization */
		@JsonCreator
		Builder() {
		}

		Builder predicateType(PredicateType predicateType) {
			this.predicateType = predicateType;
			return getSelf();
		}

		public Builder id(long storageKey) {
			storageKey(storageKey);
			return id(Long.toString(storageKey));
		}

		public Builder domain(String expression) {
			this.domain = expression;
			return getSelf();
		}

		public Builder cardinality(int min, int max) {
			return minCardinality(min).maxCardinality(max);
		}
		
		public Builder minCardinality(int minCardinality) {
			this.minCardinality = minCardinality;
			return getSelf();
		}

		public Builder maxCardinality(int maxCardinality) {
			this.maxCardinality = maxCardinality;
			return getSelf();
		}
		
		public Builder descriptionTypeId(String typeId) {
			this.descriptionTypeId = typeId;
			return getSelf();
		}
		
		public Builder dataTypeLabel(String dataTypeLabel) {
			this.dataTypeLabel = dataTypeLabel;
			return getSelf();
		}
		
		public Builder dataTypeName(String dataTypeName) {
			this.dataTypeName = dataTypeName;
			return getSelf();
		}
		
		public Builder dataType(DataType dataType) {
			this.dataType = dataType;
			return getSelf();
		}
		
		public Builder characteristicTypeExpression(String characteristicTypeExpression) {
			this.characteristicTypeExpression = characteristicTypeExpression;
			return getSelf();
		}
		
		public Builder relationshipTypeExpression(String relationshipTypeExpression) {
			this.relationshipTypeExpression = relationshipTypeExpression;
			return getSelf();
		}
		
		public Builder relationshipValueExpression(String relationshipValueExpression) {
			this.relationshipValueExpression = relationshipValueExpression;
			return getSelf();
		}
		
		public Builder groupRule(GroupRule groupRule) {
			this.groupRule = groupRule;
			return getSelf();
		}
		
		Builder selfIds(Collection<String> selfIds) {
			this.selfIds = selfIds;
			return getSelf();
		}
		
		Builder descendantIds(Collection<String> descendantIds) {
			this.descendantIds = descendantIds;
			return getSelf();
		}
		
		Builder refSetIds(Collection<String> refSetIds) {
			this.refSetIds = refSetIds;
			return getSelf();
		}

		public SnomedConstraintDocument build() {
			final SnomedConstraintDocument doc = new SnomedConstraintDocument(id, domain, predicateType, minCardinality, maxCardinality);
			
			doc.selfIds = Collections3.toImmutableSet(selfIds);
			doc.descendantIds = Collections3.toImmutableSet(descendantIds);
			doc.refSetIds = Collections3.toImmutableSet(refSetIds);
			doc.setBranchPath(branchPath);
			doc.setCommitTimestamp(commitTimestamp);
			doc.setStorageKey(storageKey);
			doc.setReplacedIns(replacedIns);
			doc.setSegmentId(segmentId);
			
			switch (predicateType) {
			case DESCRIPTION:
				doc.descriptionTypeId = descriptionTypeId;
				break;
			case DATATYPE:
				doc.dataType = dataType;
				doc.dataTypeLabel = dataTypeLabel;
				doc.dataTypeName = dataTypeName;
				break;
			case RELATIONSHIP:
				doc.groupRule = groupRule;
				doc.characteristicTypeExpression = characteristicTypeExpression;
				doc.relationshipTypeExpression = relationshipTypeExpression;
				doc.relationshipValueExpression = relationshipValueExpression;
				break;
			default: throw new NotImplementedException("Unsupported predicate type '%s'", predicateType);
			}
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
		public static final String SELF_IDS = "selfIds";
		public static final String DESCENDANT_IDS = "descendantIds";
		public static final String REFSET_IDS = "refSetIds";
		public static final String PREDICATE_TYPE = "predicateType";
	}
	
	/**
	 * @since 4.7
	 */
	public static final class Expressions extends RevisionDocument.Expressions {
		
		public static Expression types(Collection<PredicateType> types) {
			return matchAny(Fields.PREDICATE_TYPE, FluentIterable.from(types).transform(new Function<PredicateType, String>() {
				@Override
				public String apply(PredicateType input) {
					return input.name();
				}
			}).toSet());
		}
		
		public static Expression selfIds(Collection<String> selfIds) {
			return matchAny(Fields.SELF_IDS, selfIds);
		}
		
		public static Expression descendantIds(Collection<String> descendantIds) {
			return matchAny(Fields.DESCENDANT_IDS, descendantIds);
		}
		
		public static Expression refSetIds(Collection<String> refSetIds) {
			return matchAny(Fields.REFSET_IDS, refSetIds);
		}
		
	}

	/** Type of the predicate. Cannot be {@code null}. */
	private final PredicateType predicateType;
	/** The unique ID of the description type SNOMED&nbsp;CT concept. Can be {@code null}. */
	private String descriptionTypeId;
	/** The humane readable name of the concrete domain data type. E.g.: {@code Vitamin} or {@code Clinically significant}. */
	private String dataTypeLabel;
	/** The unique came-case name of the concrete domain data type. E.g.: {@code isVitamin}. Can be {@code null}. */
	private String dataTypeName;
	/**
	 * Represents the concrete domain of the predicate. Can be {@code null} if the current predicate type is NOT {@link PredicateType#DATATYPE data
	 * type}.
	 */
	private DataType dataType;
	/** ESCG expression describing the allowed SNOMED&nbsp;CT relationship type concept IDs. Can be {@code null}. */
	private String relationshipTypeExpression;
	/** ESCG expression describing the allowed SNOMED&nbsp;CT relationship value concept IDs. Can be {@code null}. */
	private String relationshipValueExpression;
	/** ESCG expression describing the allowed SNOMED&nbsp;CT relationship characteristic type concept IDs. Can be {@code null}. */
	private String characteristicTypeExpression;
	/**
	 * Enumeration instance for the relationship group role. Can be {@code null} if the current predicate instance is NOT a
	 * {@link PredicateType#RELATIONSHIP relationship type}.
	 */
	private GroupRule groupRule;
	/** The parsed query expression. Represents the domain part of the MRCM attribute constraint. */
	private final String domain;
	private final int minCardinality;
	private final int maxCardinality;
	
	private Set<String> selfIds;
	private Set<String> descendantIds;
	private Set<String> refSetIds;

	/**
	 * Private constructor.
	 * 
	 * @param domain
	 *            query expression describing the domain part of the attribute constraint.
	 * @param type
	 *            the type of the predicate representation.
	 * @param flags
	 *            the flags for describing the {@code isMultiple} and {@code isRequired} boolean properties.
	 */
	private SnomedConstraintDocument(final String id, final String domain, final PredicateType type, final int minCardinality,
			final int maxCardinality) {
		super(id, createLabel(id, type), null);
		this.domain = domain;
		this.predicateType = type;
		this.minCardinality = minCardinality;
		this.maxCardinality = maxCardinality;
	}

	private static String createLabel(String storageKey, PredicateType type) {
		if (Strings.isNullOrEmpty(storageKey))  {
			return null;
		}
		return Objects.toStringHelper(SnomedConstraintDocument.class).add("id", storageKey).add("type", type).toString();
	}

	/**
	 * Returns with the type of the current predicate instance.
	 * 
	 * @return the predicate type.
	 * @see PredicateType.
	 */
	public PredicateType getPredicateType() {
		return predicateType;
	}

	/**
	 * Returns with the query expression wrapper representing the domain part of the MRCM attribute constraint.
	 * 
	 * @return the query expression wrapper.
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Returns with the unique ID of the description type SNOMED&nbsp;CT concept.
	 * 
	 * @return the ID of the description type concept.
	 */
	public String getDescriptionTypeId() {
		return descriptionTypeId;
	}

	/**
	 * Returns with the unique name of the concrete domain data type. This name *SHOULD* uniquely identify the predicate. <br>
	 * The format is given in camel-case. E.g.: {@code isVitamin}.
	 * 
	 * @return the unique camel-case name of the concrete domain data type.
	 */
	public String getDataTypeName() {
		return dataTypeName;
	}

	/**
	 * Returns with the humane readable name of the concrete domain data type. E.g.: {@code Vitamin}.
	 * 
	 * @return the human readable name of the data type.
	 */
	public String getDataTypeLabel() {
		return dataTypeLabel;
	}

	/**
	 * Returns with the type of the concrete domain.
	 * 
	 * @return the concrete domain.
	 */
	public DataType getDataType() {
		return dataType;
	}

	/**
	 * Returns with an ESCG expression describing the SNOMED&nbsp;CT relationship characteristic type concept IDs associated with the current
	 * predicate.
	 * 
	 * @return the relationship characteristic type concept IDs represented as an ESCG expression.
	 */
	public String getCharacteristicTypeExpression() {
		return characteristicTypeExpression;
	}

	/**
	 * Returns with an ESCG expression describing the SNOMED&nbsp;CT relationship type concept IDs associated with the current predicate.
	 * 
	 * @return the relationship type concept IDs represented as an ESCG expression.
	 */
	public String getRelationshipTypeExpression() {
		return relationshipTypeExpression;
	}

	/**
	 * Returns with an ESCG expression describing the SNOMED&nbsp;CT relationship value concept IDs associated with the current predicate.
	 * 
	 * @return the relationship value concept IDs represented as an ESCG expression.
	 */
	public String getRelationshipValueExpression() {
		return relationshipValueExpression;
	}

	/**
	 * Returns with the group role.
	 * 
	 * @return the group role.
	 */
	public GroupRule getGroupRule() {
		return groupRule;
	}

	/**
	 * Returns the minimum cardinality of this MRCM predicate.
	 * 
	 * @return
	 */
	public int getMinCardinality() {
		return minCardinality;
	}

	/**
	 * Returns the maximum cardinality of this MRCM predicate.
	 * 
	 * @return
	 */
	public int getMaxCardinality() {
		return maxCardinality;
	}
	
	/**
	 * Returns all SNOMED CT identifiers where this predicate can be applied directly.
	 *  
	 * @return
	 */
	public Set<String> getSelfIds() {
		return selfIds;
	}
	
	/**
	 * Returns all SNOMED CT identifiers where this predicate can be applied on the sub hierarchy of the given identifier.
	 * @return
	 */
	public Set<String> getDescendantIds() {
		return descendantIds;
	}
	
	/**
	 * Returns all SNOMED CT reference set identifiers where this predicate can be applied.
	 * @return
	 */
	public Set<String> getRefSetIds() {
		return refSetIds;
	}

	/**
	 * Returns {@code true} if the predicate is required according to the associated MRCM rule.
	 * 
	 * @return {@code true} if the predicate is required, otherwise {@code false}.
	 */
	@JsonIgnore
	public boolean isRequired() {
		return minCardinality > 0;
	}

	/**
	 * Returns {@code true} if the predicate is multiple according to the associated MRCM rule.
	 * 
	 * @return {@code true} if the predicate is multiple, otherwise {@code false}.
	 */
	@JsonIgnore
	public boolean isMultiple() {
		return maxCardinality == -1;
	}

	@Override
	@JsonIgnore
	public String getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.PREDICATE_TYPE;
	}

	@Override
	protected ToStringHelper doToString() {
		return super.doToString()
				.add("type", predicateType)
				.add("descriptionTypeId", descriptionTypeId)
				.add("dataTypeLabel", dataTypeLabel)
				.add("dataTypeName", dataTypeName)
				.add("dataType", dataType)
				.add("relationshipTypeExpression", relationshipTypeExpression)
				.add("relationshipValueExpression", relationshipValueExpression)
				.add("groupRule", groupRule)
				.add("domain", domain)
				.add("minCardinality", minCardinality)
				.add("maxCardinality", maxCardinality)
				.add("selfIds", selfIds)
				.add("descendantIds", descendantIds)
				.add("refSetIds", refSetIds);
	}

}