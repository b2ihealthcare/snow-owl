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
package com.b2international.snowowl.snomed.datastore.request.rf2.importer;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.IS_A;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.MODULE_B2I_EXTENSION;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.MODULE_ROOT;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.REFSET_DEFINING_TYPE;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.REFSET_MEASUREMENT_TYPE;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.REFSET_ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.common.SnomedConstants.Concepts.SYNONYM;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.config.SnowOwlConfiguration;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.RequestBuilder;
import com.b2international.snowowl.core.events.bulk.BulkRequest;
import com.b2international.snowowl.core.events.bulk.BulkRequestBuilder;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.common.SnomedConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.ISnomedImportPostProcessor;
import com.b2international.snowowl.snomed.datastore.ISnomedPostProcessorContext;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRefSetCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.6
 */
public class SnomedConcreteDomainImportPostProcessor implements ISnomedImportPostProcessor {

	private static final String B2I_NAMESPACE = "1000154";
	
	private static final String B2I_MODULE_PT = "SNOMED CT B2i extension module";
	private static final String B2I_MODULE_FSN = "SNOMED CT B2i extension module (core metadata concept)";
	
	private static final String DEFINING_TYPE_REFSET_FSN = "Defining type reference set (foundation metadata concept)";
	private static final String DEFINING_TYPE_REFSET_PT = "Defining type reference set";
	private static final String CONCRETE_DOMAIN_TYPE_REFSET_FSN = "Concrete domain type reference set (foundation metadata concept)";
	private static final String CONCRETE_DOMAIN_TYPE_REFSET_PT = "Concrete domain type reference set";
	private static final String MEASUREMENT_TYPE_REFSET_FSN = "Measurement type reference set (foundation metadata concept)";
	private static final String MEASUREMENT_TYPE_REFSET_PT = "Measurement type reference set";

	private static final String BOOLEAN_DATATYPE_REFSET_FSN = "Boolean datatype reference set (foundation metadata concept)";
	private static final String BOOLEAN_DATATYPE_REFSET_PT = "Boolean datatype reference set";
	private static final String DATETIME_DATATYPE_REFSET_FSN = "Datetime datatype reference set (foundation metadata concept)";
	private static final String DATETIME_DATATYPE_REFSET_PT = "Datetime datatype reference set";
	private static final String STRING_DATATYPE_REFSET_FSN = "String datatype reference set (foundation metadata concept)";
	private static final String STRING_DATATYPE_REFSET_PT = "String datatype reference set";
	private static final String FLOAT_DATATYPE_REFSET_FSN = "Float datatype reference set (foundation metadata concept)";
	private static final String FLOAT_DATATYPE_REFSET_PT = "Float datatype reference set";
	private static final String INTEGER_DATATYPE_REFSET_FSN = "Integer datatype reference set (foundation metadata concept)";
	private static final String INTEGER_DATATYPE_REFSET_PT = "Integer datatype reference set";
	
	@Override
	public void postProcess(final ISnomedPostProcessorContext context) {
		
		final SnomedCoreConfiguration config = ApplicationContext.getServiceForClass(SnowOwlConfiguration.class)
				.getModuleConfig(SnomedCoreConfiguration.class);
		
		final String branch = context.branch();
		
		if (!config.isConcreteDomainSupported()) {
			return;
		}
		
		List<RequestBuilder<TransactionContext, ?>> requests = newArrayList();
		
		if (!conceptExists(config.getConcreteDomainTypeRefsetIdentifier(), branch)) {

			// create module
			createConcept(MODULE_B2I_EXTENSION, B2I_MODULE_FSN, B2I_MODULE_PT, MODULE_ROOT, context, requests);
			
			// create defining type refset concept
			createConcept(REFSET_DEFINING_TYPE, DEFINING_TYPE_REFSET_FSN, DEFINING_TYPE_REFSET_PT, REFSET_ROOT_CONCEPT,
					context, requests);
			
			// create concrete domain type refset concept
			createConcept(config.getConcreteDomainTypeRefsetIdentifier(), CONCRETE_DOMAIN_TYPE_REFSET_FSN,
					CONCRETE_DOMAIN_TYPE_REFSET_PT, REFSET_DEFINING_TYPE, context, requests);
			
			// create measurement type concrete domain refset concept
			createConcept(REFSET_MEASUREMENT_TYPE, MEASUREMENT_TYPE_REFSET_FSN, MEASUREMENT_TYPE_REFSET_PT,
					config.getConcreteDomainTypeRefsetIdentifier(), context, requests);

		}

		// create boolean concrete domain refset identifier concept and refset
		createRefsetAndConcept(config.getBooleanDatatypeRefsetIdentifier(), BOOLEAN_DATATYPE_REFSET_FSN,
				BOOLEAN_DATATYPE_REFSET_PT, config.getConcreteDomainTypeRefsetIdentifier(), context, requests);
		
		// create string concrete domain refset identifier concept and refset
		createRefsetAndConcept(config.getStringDatatypeRefsetIdentifier(), STRING_DATATYPE_REFSET_FSN,
				STRING_DATATYPE_REFSET_PT, config.getConcreteDomainTypeRefsetIdentifier(), context, requests);
		
		// create date-time concrete domain refset identifier concept and refset
		createRefsetAndConcept(config.getDatetimeDatatypeRefsetIdentifier(), DATETIME_DATATYPE_REFSET_FSN,
				DATETIME_DATATYPE_REFSET_PT, config.getConcreteDomainTypeRefsetIdentifier(), context, requests);

		// create integer concrete domain refset identifier concept and refset
		createRefsetAndConcept(config.getIntegerDatatypeRefsetIdentifier(), INTEGER_DATATYPE_REFSET_FSN,
				INTEGER_DATATYPE_REFSET_PT, REFSET_MEASUREMENT_TYPE, context, requests);
		
		// create float concrete domain refset identifier concept and refset
		createRefsetAndConcept(config.getFloatDatatypeRefsetIdentifier(), FLOAT_DATATYPE_REFSET_FSN,
				FLOAT_DATATYPE_REFSET_PT, REFSET_MEASUREMENT_TYPE, context, requests);
		
		if (!requests.isEmpty()) {
			
			try {
				
				final BulkRequestBuilder<TransactionContext> bulkRequest = BulkRequest.create();
				
				requests.forEach(bulkRequest::add);
				
				SnomedRequests.prepareCommit()
					.setBody(bulkRequest)
					.setUserId(context.getUserId())
					.setCommitComment("Import post processor created concrete domain reference sets")
					.setParentContextDescription(DatastoreLockContextDescriptions.IMPORT)
					.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
					.execute(getServiceForClass(IEventBus.class))
					.getSync();
				
			} catch (final Exception e) {
				context.getLogger().error("Caught exception while creating concrete domain reference sets in {}", getClass().getSimpleName(), e);
			}
			
		}
		
	}

	private void createConcept(String conceptId, String fsn, String pt, String parentId,
			ISnomedPostProcessorContext context, List<RequestBuilder<TransactionContext, ?>> requests) {

		if (!conceptExists(conceptId, context.branch())) {
			requests.add(createConcept(conceptId, fsn, pt, parentId, context.branch()));
			context.getLogger().info("Created required concept for data type reference sets: '{}'", conceptId);
		}

	}

	private void createRefsetAndConcept(String conceptId, String fsn, String pt, String parentId,
			ISnomedPostProcessorContext context, List<RequestBuilder<TransactionContext, ?>> requests) {

		if (!conceptExists(conceptId, context.branch())) {
			requests.add(createConcept(conceptId, fsn, pt, parentId, context.branch()));
			context.getLogger().info("Created {} identifier concept with id: '{}'", pt.toLowerCase(), conceptId);
		}

		if (!refsetExists(conceptId, context.branch())) {
			requests.add(createRefSet(conceptId, context.branch()));
			context.getLogger().info("Created {}", pt.toLowerCase());
		}

	}

	private boolean conceptExists(final String conceptId, final String branch) {
		return SnomedRequests.prepareSearchConcept()
				.filterByIds(ImmutableSet.of(conceptId))
				.setLimit(0)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(getServiceForClass(IEventBus.class))
				.getSync()
				.getTotal() > 0;
	}

	private boolean refsetExists(final String refsetId, final String branch) {
		return SnomedRequests.prepareSearchRefSet()
				.filterByIds(ImmutableSet.of(refsetId))
				.setLimit(0)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.execute(getServiceForClass(IEventBus.class))
				.getSync()
				.getTotal() > 0;
	}

	private SnomedRefSetCreateRequestBuilder createRefSet(final String identifierId, final String branch) {
		return SnomedRequests.prepareNewRefSet()
			.setIdentifierId(identifierId)
			.setType(SnomedRefSetType.CONCRETE_DATA_TYPE);
	}

	private SnomedConceptCreateRequestBuilder createConcept(final String identifierConceptId, final String fsnTerm, final String ptTerm, final String parent, final String branch) {
		return SnomedRequests.prepareNewConcept()
				.setId(identifierConceptId)
				.setActive(true)
				.setModuleId(MODULE_B2I_EXTENSION.equals(identifierConceptId) ? MODULE_SCT_CORE : MODULE_B2I_EXTENSION) // workaround to be able to set the module for the B2i module concept
				.addDescription(createDescription(identifierConceptId, fsnTerm, FULLY_SPECIFIED_NAME, Acceptability.PREFERRED, branch))
				.addDescription(createDescription(identifierConceptId, ptTerm, SYNONYM, Acceptability.PREFERRED, branch))
				.addRelationship(createIsaRelationship(identifierConceptId, parent, CharacteristicType.STATED_RELATIONSHIP, branch))
				.addRelationship(createIsaRelationship(identifierConceptId, parent, CharacteristicType.INFERRED_RELATIONSHIP, branch));
	}
	
	private SnomedDescriptionCreateRequestBuilder createDescription(final String conceptId, final String term, final String type, final Acceptability acceptability, final String branch) {
		return SnomedRequests.prepareNewDescription()
				.setIdFromNamespace(B2I_NAMESPACE)
				.setActive(true)
				.setModuleId(MODULE_B2I_EXTENSION)
				.setConceptId(conceptId)
				.setLanguageCode("en")
				.setTypeId(type)
				.setTerm(term)
				.setCaseSignificance(CaseSignificance.CASE_INSENSITIVE)
				.setAcceptability(ImmutableMap.of(SnomedConstants.Concepts.REFSET_LANGUAGE_TYPE_US, acceptability));
	}
	
	private SnomedRelationshipCreateRequestBuilder createIsaRelationship(final String source, final String destination, final CharacteristicType characteristicType, final String branch) {
		return SnomedRequests.prepareNewRelationship() 
			.setIdFromNamespace(B2I_NAMESPACE)
			.setActive(true)
			.setModuleId(MODULE_B2I_EXTENSION)
			.setSourceId(source)
			.setDestinationId(destination)
			.setTypeId(IS_A)
			.setCharacteristicType(characteristicType)
			.setModifier(RelationshipModifier.EXISTENTIAL);
	}
	
}
