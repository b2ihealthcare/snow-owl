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
package com.b2international.snowowl.datastore.server.snomed.index.init;

import static com.b2international.commons.StringUtils.isEmpty;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.Term;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.concurrent.equinox.ForkJoinUtils;
import com.b2international.snowowl.core.SimpleFamilyJob;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;
import com.b2international.snowowl.datastore.server.DatastoreServerActivator;
import com.b2international.snowowl.datastore.server.snomed.index.SnomedIndexServerService;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.datastore.MrcmEditingContext;
import com.b2international.snowowl.snomed.datastore.PredicateUtils;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.ConstraintDomain;
import com.b2international.snowowl.snomed.datastore.PredicateUtils.DefinitionType;
import com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants;
import com.b2international.snowowl.snomed.datastore.snor.ConstraintFormIsApplicableForValidationPredicate;
import com.b2international.snowowl.snomed.datastore.snor.PredicateIndexEntry.PredicateType;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModel;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.ConstraintBase;
import com.b2international.snowowl.snomed.mrcm.DataType;
import com.b2international.snowowl.snomed.mrcm.DescriptionPredicate;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Index initializer for MRCM.
 * 
 */
public class MrcmIndexInitializer extends SimpleFamilyJob {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MrcmIndexInitializer.class);
	
	private static final int PREDICATE_TYPE_ID = 999; // dummy terminology component type ID for predicates
	
	// value format: [inclusion_type]#[predicate_uuid]
	private final Multimap<Long, String> conceptIdToPredicateMap = ArrayListMultimap.create();
	private final Multimap<Long, String> refSetIdToPredicateMap = ArrayListMultimap.create();
	private final SnomedIndexServerService indexService;
	private final IBranchPath branchPath;

	/**
	 * @param indexService
	 * @param branchPath
	 */
	public MrcmIndexInitializer(final SnomedIndexServerService indexService, final IBranchPath branchPath) {
		super("Processing MRCM", new Object());
		this.indexService = indexService;
		this.branchPath = branchPath;
	}

	@Override
	public IStatus run(final IProgressMonitor monitor) {
		final SubMonitor subMonitor = SubMonitor.convert(monitor);
		subMonitor.subTask(getName());
		MrcmEditingContext context = null;
		ConceptModel conceptModel = null;
		try {
			context = new MrcmEditingContext();
			conceptModel = context.getConceptModel();
			
			final Iterable<ConstraintBase> filter = Iterables.filter(conceptModel.getConstraints(), new ConstraintFormIsApplicableForValidationPredicate());
			
			for (final AttributeConstraint constraint : Iterables.filter(filter, AttributeConstraint.class)) {
				importAttributeConstraint(constraint);
			}
	
		} catch (final IOException e) {
			return new Status(IStatus.ERROR, DatastoreServerActivator.PLUGIN_ID, "Error in index initializer.", e);
		} finally {
			if (null != context) {
				context.close();
			}
		}
		return Status.OK_STATUS;
	}

	private void importAttributeConstraint(final AttributeConstraint attributeConstraint) throws IOException {
		final ConceptSetDefinition domain = attributeConstraint.getDomain();
		final ConceptModelPredicate predicate = attributeConstraint.getPredicate();
		final String queryExpression  = PredicateUtils.getEscgExpression(attributeConstraint.getDomain());
		
		// Predicates are assumed to be of 0..1 cardinality by default
		if (predicate instanceof DescriptionPredicate) {
			importDescriptionPredicate(predicate, queryExpression, false, false);
			processConstraintDomain(predicate.getUuid(), domain);
		} else if (predicate instanceof ConcreteDomainElementPredicate) {
			importConcreteDomainElementPredicate(predicate, queryExpression, false, false);
			processConstraintDomain(predicate.getUuid(), domain);
		} else if (predicate instanceof RelationshipPredicate) {
			importRelationshipPredicate(predicate, queryExpression, GroupRule.ALL_GROUPS, false, false);
			processConstraintDomain(predicate.getUuid(), domain);
		} else if (predicate instanceof CardinalityPredicate) {
			
			final CardinalityPredicate cardinalityPredicate = (CardinalityPredicate) predicate;
			final ConceptModelPredicate childPredicate = cardinalityPredicate.getPredicate();
			final boolean required = PredicateUtils.isRequired(cardinalityPredicate);
			final boolean multiple = PredicateUtils.isMultiple(cardinalityPredicate);
			
			if (cardinalityPredicate.getMaxCardinality() == 0) {
				return;
			}
			
			if (childPredicate instanceof DescriptionPredicate) {
				importDescriptionPredicate(childPredicate, queryExpression, required, multiple);
				processConstraintDomain(childPredicate.getUuid(), domain);
			} else if (childPredicate instanceof ConcreteDomainElementPredicate) {
				importConcreteDomainElementPredicate(childPredicate, queryExpression, required, multiple);
				processConstraintDomain(childPredicate.getUuid(), domain);
			} else if (childPredicate instanceof RelationshipPredicate) {
				GroupRule groupRule = cardinalityPredicate.getGroupRule();
				
				if (groupRule == null) {
					groupRule = GroupRule.ALL_GROUPS;
					LOGGER.warn(MessageFormat.format(
							"No groupRule attribute found on CardinalityPredicate for constraint {0}, defaulting to ALL_GROUPS.",
							attributeConstraint.getUuid()));
				}
				
				importRelationshipPredicate(childPredicate, queryExpression, groupRule, required, multiple);
				processConstraintDomain(childPredicate.getUuid(), domain);
			}
		}
	}
	
	private void processConstraintDomain(final String predicateUuid, final ConceptSetDefinition domain) {
		
		for (final ConstraintDomain constraintDomain : PredicateUtils.processConstraintDomain(predicateUuid, domain)) {
			
			if (DefinitionType.CONCEPT.equals(constraintDomain.getType())) {
				conceptIdToPredicateMap.put(constraintDomain.getComponentId(), constraintDomain.getPredicateKey());
			} else if (DefinitionType.REFSET.equals(constraintDomain.getType())) {
				refSetIdToPredicateMap.put(constraintDomain.getComponentId(), constraintDomain.getPredicateKey());
			} else {
				throw new IllegalStateException("Unexpected constraint definition type '" + constraintDomain.getType() + "'.");
			}
		}
	}

	private void importConcreteDomainElementPredicate(final ConceptModelPredicate predicate, final String queryExpression, final boolean required,
			final boolean multiple) throws IOException {
		
		final ConcreteDomainElementPredicate dataTypePredicate = (ConcreteDomainElementPredicate) predicate;
		final String dataTypeName = dataTypePredicate.getName();
		final String dataTypeLabel = dataTypePredicate.getLabel();
		final DataType dataType = dataTypePredicate.getType();
		
		final Document document = new Document();
		document.add(new IntField(CommonIndexConstants.COMPONENT_TYPE, PREDICATE_TYPE_ID, Store.YES));
		document.add(new StringField(SnomedIndexBrowserConstants.PREDICATE_UUID, predicate.getUuid(),Store.YES));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_TYPE, PredicateType.DATATYPE.name()));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_DATA_TYPE_LABEL, dataTypeLabel));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_DATA_TYPE_NAME, dataTypeName));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_DATA_TYPE_TYPE, dataType.name()));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_QUERY_EXPRESSION, queryExpression));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_REQUIRED, required ? 1 : 0));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_MULTIPLE, multiple ? 1 : 0));
		indexService.index(branchPath, document, new Term(SnomedIndexBrowserConstants.PREDICATE_UUID, predicate.getUuid()));
	}

	private void importDescriptionPredicate(final ConceptModelPredicate predicate, final String queryExpression, final boolean required,
			final boolean multiple) throws IOException {
		final DescriptionPredicate descriptionPredicate = (DescriptionPredicate) predicate;
		final long descriptionId = Long.valueOf(descriptionPredicate.getTypeId());
		
		final Document document = new Document();
		document.add(new IntField(CommonIndexConstants.COMPONENT_TYPE, PREDICATE_TYPE_ID, Store.YES));
		document.add(new StringField(SnomedIndexBrowserConstants.PREDICATE_UUID, predicate.getUuid(),Store.YES));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_TYPE, PredicateType.DESCRIPTION.name()));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_DESCRIPTION_TYPE_ID, descriptionId));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_QUERY_EXPRESSION, queryExpression));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_REQUIRED, required ? 1 : 0));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_MULTIPLE, multiple ? 1 : 0));
		indexService.index(branchPath, document, new Term(SnomedIndexBrowserConstants.PREDICATE_UUID, predicate.getUuid()));
	}

	private void importRelationshipPredicate(final ConceptModelPredicate predicate, final String queryExpression, final GroupRule groupRule, 
			final boolean required, final boolean multiple) throws IOException {
	
		final RelationshipPredicate relationshipPredicate = (RelationshipPredicate) predicate;
	
		final String characteristicTypeConceptId = relationshipPredicate.getCharacteristicTypeConceptId();
	
		final AtomicReference<String> typeReference = new AtomicReference<String>();
		final AtomicReference<String> valueTypeReference = new AtomicReference<String>();
		final AtomicReference<String> characteristicTypeReference = new AtomicReference<String>();
	
		ForkJoinUtils.runInParallel(
	
		new Runnable() {
			@Override
			public void run() {
				final String attributeExpression = PredicateUtils.getEscgExpression(relationshipPredicate.getAttribute());
				typeReference.set(attributeExpression);
			}
		},
	
		new Runnable() {
			@Override
			public void run() {
				if (isEmpty(characteristicTypeConceptId)) {
					characteristicTypeReference.set("<" + SnomedConstants.Concepts.CHARACTERISTIC_TYPE);
				} else {
					characteristicTypeReference.set("<<" + characteristicTypeConceptId);
				}
			}
		},
	
		new Runnable() {
			@Override
			public void run() {
				final String valueExpression = PredicateUtils.getEscgExpression(relationshipPredicate.getRange());
				valueTypeReference.set(valueExpression);
			}
		});
		
		final Document document = new Document();
		document.add(new IntField(CommonIndexConstants.COMPONENT_TYPE, PREDICATE_TYPE_ID, Store.YES));
		document.add(new StringField(SnomedIndexBrowserConstants.PREDICATE_UUID, predicate.getUuid(),Store.YES));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_TYPE, PredicateType.RELATIONSHIP.name()));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_RELATIONSHIP_TYPE_EXPRESSION, typeReference.get()));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_RELATIONSHIP_VALUE_EXPRESSION, valueTypeReference.get()));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_CHARACTERISTIC_TYPE_EXPRESSION, characteristicTypeReference.get()));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_GROUP_RULE, groupRule.name()));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_QUERY_EXPRESSION, queryExpression));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_REQUIRED, required ? 1 : 0));
		document.add(new StoredField(SnomedIndexBrowserConstants.PREDICATE_MULTIPLE, multiple ? 1 : 0));
		indexService.index(branchPath, document, new Term(SnomedIndexBrowserConstants.PREDICATE_UUID, predicate.getUuid()));
	}

	public Multimap<Long, String> getConceptIdToPredicateMap() {
		return conceptIdToPredicateMap;
	}

	public Multimap<Long, String> getRefSetIdToPredicateMap() {
		return refSetIdToPredicateMap;
	}

}