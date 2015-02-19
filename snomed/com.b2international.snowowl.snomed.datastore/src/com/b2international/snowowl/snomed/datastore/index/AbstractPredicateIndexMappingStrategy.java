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
package com.b2international.snowowl.snomed.datastore.index;

import static com.b2international.commons.StringUtils.isEmpty;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.CHARACTERISTIC_TYPE;
import static com.b2international.snowowl.snomed.datastore.PredicateUtils.getEscgExpression;

import java.text.MessageFormat;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry.PredicateType;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.google.common.base.Preconditions;

/**
 * Strategy for creating index documents for MRCM based concept {@link AttributeConstraint attribute constraint}s.
 */
public abstract class AbstractPredicateIndexMappingStrategy<P extends ConceptModelPredicate> extends AbstractIndexMappingStrategy {

	/**
	 * Null implementation. This mapping strategy instance should not be indexed at all. 
	 */
	public static AbstractIndexMappingStrategy NULL_PREDICATE_INDEX_MAPPING_STARTEGY = new AbstractIndexMappingStrategy() {
		/**Always returns with {@code -1L}.*/
		@Override protected long getStorageKey() { return -1L; }
		/**Throw UnsupportedOperationException.*/
		@Override public Document createDocument() { throw new UnsupportedOperationException(); }
	};
			
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPredicateIndexMappingStrategy.class);
	
	public static AbstractIndexMappingStrategy createMappingStrategy(final AttributeConstraint attributeConstraint) {
		
		Preconditions.checkNotNull(attributeConstraint, "Attribute constraint argument cannot be null.");
		
		final ConceptSetDefinition domain = attributeConstraint.getDomain();
		final ConceptModelPredicate predicate = attributeConstraint.getPredicate();
		final String domainQueryExpression = PredicateUtils.getEscgExpression(domain);
		
		
		//predicates are assumed to be 0..1 cardinality be default 
		if (predicate instanceof DescriptionPredicate) {
			
			final DescriptionPredicate descriptionPredicate = (DescriptionPredicate) predicate;
			return new DescriptionPredicateIndexMappingStrategy(descriptionPredicate, domainQueryExpression, false, false);
			
		} else if (predicate instanceof ConcreteDomainElementPredicate) {
			
			final ConcreteDomainElementPredicate concreteDomainPredicate = (ConcreteDomainElementPredicate) predicate;
			return new ConcreteDomainPredicateIndexMappingStrategy(concreteDomainPredicate, domainQueryExpression, false, false);
			
		} else if (predicate instanceof RelationshipPredicate) {
			
			final RelationshipPredicate relationshipPredicate = (RelationshipPredicate) predicate;
			return new RelationshipPredicateIndexMappingStrategy(relationshipPredicate, domainQueryExpression, GroupRule.ALL_GROUPS, false, false);
			
		} else if (predicate instanceof CardinalityPredicate) {
			
			final CardinalityPredicate cardinalityPredicate = (CardinalityPredicate) predicate;
			final ConceptModelPredicate childPredicate = cardinalityPredicate.getPredicate();
			final boolean required = PredicateUtils.isRequired(cardinalityPredicate);
			final boolean multiple = PredicateUtils.isMultiple(cardinalityPredicate);
			
			if (0 == cardinalityPredicate.getMaxCardinality()) {
				//TODO support for prohibited relationships
				//Prohibition of a relationship which is permitted for one or more Domains to which the concept belongs (maxOccurs=0).
				return NULL_PREDICATE_INDEX_MAPPING_STARTEGY;
			}
			
			if (childPredicate instanceof DescriptionPredicate) {
				
				final DescriptionPredicate descriptionPredicate = (DescriptionPredicate) childPredicate;
				return new DescriptionPredicateIndexMappingStrategy(descriptionPredicate, domainQueryExpression, required, multiple);
				
			} else if (childPredicate instanceof ConcreteDomainElementPredicate) {
				
				final ConcreteDomainElementPredicate concreteDomainPredicate = (ConcreteDomainElementPredicate) childPredicate;
				return new ConcreteDomainPredicateIndexMappingStrategy(concreteDomainPredicate, domainQueryExpression, required, multiple);
				
			} else if (childPredicate instanceof RelationshipPredicate) {
				
				GroupRule groupRule = cardinalityPredicate.getGroupRule();
				
				if (null == groupRule) {
					groupRule = GroupRule.ALL_GROUPS;
					LOGGER.warn(MessageFormat.format(
							"No groupRule attribute found on CardinalityPredicate for constraint {0}, defaulting to ALL_GROUPS.",
							attributeConstraint.getUuid()));
				}
				
				final RelationshipPredicate relationshipPredicate = (RelationshipPredicate) childPredicate;
				return new RelationshipPredicateIndexMappingStrategy(relationshipPredicate, domainQueryExpression, groupRule, required, multiple);
				
			}
			
		}
		
		throw new IllegalArgumentException("Cannot create index mapping strategy for " + attributeConstraint);
		
	}
	
	protected final P predicate;
	protected final String domainQueryExpression;
	protected final boolean required;
	protected final boolean multiple;
	
	private AbstractPredicateIndexMappingStrategy(final P predicate, final String domainQueryExpression, final boolean required, final boolean multiple) {
		
		this.predicate = Preconditions.checkNotNull(predicate, "Predicate argument cannot be null.");
		this.domainQueryExpression = Preconditions.checkNotNull(domainQueryExpression, "Domain query expression argument cannot be null.");
		this.required = required;
		this.multiple = multiple;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy#getStorageKey()
	 */
	@Override
	protected long getStorageKey() {
		return CDOIDUtils.asLong(predicate.cdoID());
	}
	
	/**
	 * Strategy for creating index document based on the relationship predicate.
	 */
	private static final class RelationshipPredicateIndexMappingStrategy extends AbstractPredicateIndexMappingStrategy<RelationshipPredicate> {

		private final GroupRule groupRule;

		private RelationshipPredicateIndexMappingStrategy(final RelationshipPredicate predicate, final String domainQueryExpression, 
				final GroupRule groupRule, final boolean required, final boolean multiple) {

			super(predicate, domainQueryExpression, required, multiple);
			this.groupRule = Preconditions.checkNotNull(groupRule, "Group role argument cannot be null.");
		}

		/* (non-Javadoc)
		 * @see com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy#createDocument()
		 */
		@Override
		public Document createDocument() {
			
			final String characteristicTypeConceptId = predicate.getCharacteristicTypeConceptId();
			final String type = getEscgExpression(predicate.getAttribute());
			final String valueType = getEscgExpression(predicate.getRange());
			final String characteristicType = isEmpty(characteristicTypeConceptId) 
					? "<" + CHARACTERISTIC_TYPE 
					: "<<" + characteristicTypeConceptId;  
			
			final Document doc = new Document();
			
			doc.add(new IntField(SnomedIndexBrowserConstants.COMPONENT_TYPE, PredicateUtils.PREDICATE_TYPE_ID, Store.YES));
			doc.add(new StringField(SnomedIndexBrowserConstants.PREDICATE_UUID, predicate.getUuid(),Store.YES));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_TYPE, PredicateType.RELATIONSHIP.name()));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_RELATIONSHIP_TYPE_EXPRESSION, type));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_RELATIONSHIP_VALUE_EXPRESSION, valueType));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_CHARACTERISTIC_TYPE_EXPRESSION, characteristicType));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_GROUP_RULE, groupRule.name()));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_QUERY_EXPRESSION, domainQueryExpression));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_REQUIRED, required ? 1 : 0));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_MULTIPLE, multiple ? 1 : 0));
			
			return doc;
		}
		
	}
	
	/**
	 * Strategy for creating index documents from description predicates.
	 */
	private static final class DescriptionPredicateIndexMappingStrategy extends AbstractPredicateIndexMappingStrategy<DescriptionPredicate> {

		private DescriptionPredicateIndexMappingStrategy(final DescriptionPredicate predicate, final String domainQueryExpression, 
				final boolean required, final boolean multiple) {
			
			super(predicate, domainQueryExpression, required, multiple);
		}

		/* (non-Javadoc)
		 * @see com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy#createDocument()
		 */
		@Override
		public Document createDocument() {
			final long descriptionId = Long.valueOf(predicate.getTypeId());
			
			final Document doc = new Document();
			
			doc.add(new IntField(SnomedIndexBrowserConstants.COMPONENT_TYPE, PredicateUtils.PREDICATE_TYPE_ID, Store.YES));
			doc.add(new StringField(SnomedIndexBrowserConstants.PREDICATE_UUID, predicate.getUuid(),Store.YES));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_TYPE, PredicateType.DESCRIPTION.name()));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_DESCRIPTION_TYPE_ID, descriptionId));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_QUERY_EXPRESSION, domainQueryExpression));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_REQUIRED, required ? 1 : 0));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_MULTIPLE, multiple ? 1 : 0));
			
			return doc;
		}
		
	}
	
	private static final class ConcreteDomainPredicateIndexMappingStrategy extends AbstractPredicateIndexMappingStrategy<ConcreteDomainElementPredicate> {

		private ConcreteDomainPredicateIndexMappingStrategy(final ConcreteDomainElementPredicate predicate, final String domainQueryExpression, 
				final boolean required, final boolean multiple) {
			
			super(predicate, domainQueryExpression, required, multiple);
		}

		/* (non-Javadoc)
		 * @see com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy#createDocument()
		 */
		@Override
		public Document createDocument() {
			
			final Document doc = new Document();

			doc.add(new IntField(SnomedIndexBrowserConstants.COMPONENT_TYPE, PredicateUtils.PREDICATE_TYPE_ID, Store.YES));
			doc.add(new StringField(SnomedIndexBrowserConstants.PREDICATE_UUID, predicate.getUuid(),Store.YES));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_TYPE, PredicateType.DATATYPE.name()));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_DATA_TYPE_LABEL, predicate.getLabel()));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_DATA_TYPE_NAME, predicate.getName()));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_DATA_TYPE_TYPE, predicate.getType().name()));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_QUERY_EXPRESSION, domainQueryExpression));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_REQUIRED, required ? 1 : 0));
			doc.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_MULTIPLE, multiple ? 1 : 0));
			
			return doc;
		}
		
	}

}