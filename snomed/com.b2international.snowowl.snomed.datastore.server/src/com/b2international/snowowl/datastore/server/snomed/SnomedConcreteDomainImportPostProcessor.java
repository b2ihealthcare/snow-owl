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
package com.b2international.snowowl.datastore.server.snomed;

import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_SPECIFIED_NAME;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_B2I_EXTENSION;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_ROOT;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.MODULE_SCT_CORE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_BOOLEAN_DATATYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_CONCRETE_DOMAIN_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_DATETIME_DATATYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_DEFINING_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_FLOAT_DATATYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_INTEGER_DATATYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_MEASUREMENT_TYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_ROOT_CONCEPT;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.REFSET_STRING_DATATYPE;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.SYNONYM;

import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.exceptions.NotFoundException;
import com.b2international.snowowl.datastore.oplock.impl.DatastoreLockContextDescriptions;
import com.b2international.snowowl.datastore.server.CDOServerCommitBuilder;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.store.SnomedComponents;
import com.b2international.snowowl.snomed.datastore.ISnomedImportPostProcessor;
import com.b2international.snowowl.snomed.datastore.ISnomedPostProcessorContext;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;

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
	public void postProcess(final ISnomedPostProcessorContext postProcessorContext) {
		
		final ImportOnlySnomedTransactionContext context = new ImportOnlySnomedTransactionContext(postProcessorContext.getEditingContext());
		final SnomedCoreConfiguration snomedCoreConfiguration = context.getSnomedCoreConfig();
		
		if (snomedCoreConfiguration.isConcreteDomainSupported()) {
			
			if (!isConcreteDomainRefsetIdentifierConceptsExist(context)) {

				if (isDefaultConcreteDomainConfiguration(snomedCoreConfiguration)) {
					
					if (!exists(MODULE_B2I_EXTENSION, Concept.class, context)) {
						
						createConcept(MODULE_B2I_EXTENSION, B2I_MODULE_FSN, B2I_MODULE_PT, MODULE_ROOT, context);
						
						createConcept(REFSET_DEFINING_TYPE, DEFINING_TYPE_REFSET_FSN, DEFINING_TYPE_REFSET_PT, REFSET_ROOT_CONCEPT, context);
						createConcept(REFSET_CONCRETE_DOMAIN_TYPE, CONCRETE_DOMAIN_TYPE_REFSET_FSN, CONCRETE_DOMAIN_TYPE_REFSET_PT, REFSET_DEFINING_TYPE, context);
						
						createConcept(REFSET_BOOLEAN_DATATYPE, BOOLEAN_DATATYPE_REFSET_FSN, BOOLEAN_DATATYPE_REFSET_PT, REFSET_CONCRETE_DOMAIN_TYPE, context);
						createConcept(REFSET_DATETIME_DATATYPE, DATETIME_DATATYPE_REFSET_FSN, DATETIME_DATATYPE_REFSET_PT, REFSET_CONCRETE_DOMAIN_TYPE, context);
						createConcept(REFSET_STRING_DATATYPE, STRING_DATATYPE_REFSET_FSN, STRING_DATATYPE_REFSET_PT, REFSET_CONCRETE_DOMAIN_TYPE, context);
						
						createConcept(REFSET_MEASUREMENT_TYPE, MEASUREMENT_TYPE_REFSET_FSN, MEASUREMENT_TYPE_REFSET_PT, REFSET_CONCRETE_DOMAIN_TYPE, context);
						createConcept(REFSET_FLOAT_DATATYPE, FLOAT_DATATYPE_REFSET_FSN, FLOAT_DATATYPE_REFSET_PT, REFSET_MEASUREMENT_TYPE, context);
						createConcept(REFSET_INTEGER_DATATYPE, INTEGER_DATATYPE_REFSET_FSN, INTEGER_DATATYPE_REFSET_PT, REFSET_MEASUREMENT_TYPE, context);
						
					}
					
				} else {
					postProcessorContext.getLogger().error("Concrete domain refset identifier concepts are missing from the dataset");
					return;
				}
				
			} 
			
			if (!isConcreteDomainRefsetExists(snomedCoreConfiguration.getBooleanDatatypeRefsetIdentifier(), context)) {
				createRefSet(REFSET_BOOLEAN_DATATYPE, context);
			}
			
			if (!isConcreteDomainRefsetExists(snomedCoreConfiguration.getDatetimeDatatypeRefsetIdentifier(), context)) {
				createRefSet(REFSET_DATETIME_DATATYPE, context);
			}
			
			if (!isConcreteDomainRefsetExists(snomedCoreConfiguration.getFloatDatatypeRefsetIdentifier(), context)) {
				createRefSet(REFSET_FLOAT_DATATYPE, context);
			}
			
			if (!isConcreteDomainRefsetExists(snomedCoreConfiguration.getIntegerDatatypeRefsetIdentifier(), context)) {
				createRefSet(REFSET_INTEGER_DATATYPE, context);
			}
			
			if (!isConcreteDomainRefsetExists(snomedCoreConfiguration.getStringDatatypeRefsetIdentifier(), context)) {
				createRefSet(REFSET_STRING_DATATYPE, context);
			}
			
			if (context.getEditingContext().isDirty()) {
				try {
					new CDOServerCommitBuilder(postProcessorContext.getUserId(), "Import post processor created concrete domain reference sets.", context.getEditingContext().getTransaction())
						.parentContextDescription(DatastoreLockContextDescriptions.IMPORT)
						.commit();
				} catch (final CommitException e) {
					postProcessorContext.getLogger().error(String.format("Caught exception while creating concrete domain reference sets in %s", getClass().getSimpleName()), e);
				}
			}
		}
	}

	private boolean isConcreteDomainRefsetExists(final String refsetId, final ImportOnlySnomedTransactionContext context) {
		return exists(refsetId, SnomedRefSet.class, context);
	}

	private void createRefSet(final String referenceSetId, final ImportOnlySnomedTransactionContext context) {
		
		final SnomedConcreteDataTypeRefSet refSet = SnomedComponents.newConcreteDomainReferenceSet()
			.withIdentifierConceptId(referenceSetId)
			.withDataType(SnomedRefSetUtil.getConcreteDomainRefSetMap().inverse().get(referenceSetId))
			.build(context);
		
		context.getEditingContext().getRefSetEditingContext().add(refSet);
		
	}

	private void createConcept(final String identifierConceptId, final String fsnTerm, final String ptTerm, final String parent, final ImportOnlySnomedTransactionContext context) {
		
		final Concept concept = SnomedComponents.newConcept()
				.withId(identifierConceptId)
				.withActive(true)
				.withModule(MODULE_B2I_EXTENSION.equals(identifierConceptId) ? MODULE_SCT_CORE : MODULE_B2I_EXTENSION) // workaround to be able to set the module for the B2i module concept
				.build(context);
		
		context.add(concept);

		if (MODULE_B2I_EXTENSION.equals(identifierConceptId)) {
			concept.setModule(concept); // workaround to be able to set the module for the B2i module concept
		}
		
		final String defaultLanguageCode = context.getDefaultLanguageCode();
		final String defaultLanguageRefsetId = context.getDefaultLanguageRefsetId();
		
		final Description fsn = createDescription(concept.getId(), fsnTerm, FULLY_SPECIFIED_NAME, Acceptability.PREFERRED, defaultLanguageCode, defaultLanguageRefsetId, context);
		final Description pt = createDescription(concept.getId(), ptTerm, SYNONYM, Acceptability.PREFERRED, defaultLanguageCode, defaultLanguageRefsetId, context);
		
		concept.getDescriptions().add(fsn);
		concept.getDescriptions().add(pt);
		
		final Relationship statedIsa = createIsaRelationship(identifierConceptId, parent, CharacteristicType.STATED_RELATIONSHIP, context);
		final Relationship inferredIsa = createIsaRelationship(identifierConceptId, parent, CharacteristicType.INFERRED_RELATIONSHIP, context);
		
		concept.getOutboundRelationships().add(statedIsa);
		concept.getOutboundRelationships().add(inferredIsa);
	}

	private Relationship createIsaRelationship(final String source, final String destination, final CharacteristicType characteristicType, final ImportOnlySnomedTransactionContext context) {

		Relationship relationship = SnomedComponents.newRelationship()
			.withIdFromNamespace(B2I_NAMESPACE)
			.withActive(true)
			.withModule(MODULE_B2I_EXTENSION)
			.withSource(source)
			.withDestination(destination)
			.isa()
			.withCharacteristicType(characteristicType)
			.withModifier(RelationshipModifier.EXISTENTIAL)
			.build(context);
		
		SnomedIdentifiers identifiers = context.service(SnomedIdentifiers.class);
		identifiers.register(relationship.getId());
		
		return relationship;
	}

	private Description createDescription(final String conceptId, final String term, final String type, final Acceptability acceptability, final String languageCode, final String languageRefsetId, final ImportOnlySnomedTransactionContext context) {
		
		final Description description = SnomedComponents.newDescription()
			.withIdFromNamespace(B2I_NAMESPACE)
			.withActive(true)
			.withModule(MODULE_B2I_EXTENSION)
			.withConcept(conceptId)
			.withLanguageCode(languageCode)
			.withType(type)
			.withTerm(term)
			.withCaseSignificance(CaseSignificance.CASE_INSENSITIVE)
			.build(context);
		
		SnomedIdentifiers identifiers = context.service(SnomedIdentifiers.class);
		identifiers.register(description.getId());
		
		final SnomedLanguageRefSetMember languageRefSetMember = SnomedComponents.newLanguageMember()
			.withActive(true)
			.withModule(MODULE_B2I_EXTENSION)
			.withRefSet(languageRefsetId)
			.withReferencedComponent(description.getId())
			.withAcceptability(acceptability)
			.build(context);
		
		description.getLanguageRefSetMembers().add(languageRefSetMember);
		
		return description;
	}
	
	private boolean isDefaultConcreteDomainConfiguration(final SnomedCoreConfiguration config) {
		return REFSET_CONCRETE_DOMAIN_TYPE.equals(config.getConcreteDomainTypeRefsetIdentifier())
				&& REFSET_BOOLEAN_DATATYPE.equals(config.getBooleanDatatypeRefsetIdentifier())
				&& REFSET_DATETIME_DATATYPE.equals(config.getDatetimeDatatypeRefsetIdentifier())
				&& REFSET_FLOAT_DATATYPE.equals(config.getFloatDatatypeRefsetIdentifier())
				&& REFSET_INTEGER_DATATYPE.equals(config.getIntegerDatatypeRefsetIdentifier())
				&& REFSET_STRING_DATATYPE.equals(config.getStringDatatypeRefsetIdentifier());
	}

	private boolean isConcreteDomainRefsetIdentifierConceptsExist(final ImportOnlySnomedTransactionContext context) {
		return exists(context.getSnomedCoreConfig().getBooleanDatatypeRefsetIdentifier(), Concept.class, context) &&
				exists(context.getSnomedCoreConfig().getDatetimeDatatypeRefsetIdentifier(), Concept.class, context) &&
				exists(context.getSnomedCoreConfig().getFloatDatatypeRefsetIdentifier(), Concept.class, context) &&
				exists(context.getSnomedCoreConfig().getIntegerDatatypeRefsetIdentifier(), Concept.class, context) &&
				exists(context.getSnomedCoreConfig().getStringDatatypeRefsetIdentifier(), Concept.class, context);
	}
	
	private <T extends EObject> boolean exists(final String componentId, final Class<T> componentClass, final ImportOnlySnomedTransactionContext context) {
		try {
			context.getEditingContext().lookup(componentId, componentClass);
		} catch (final NotFoundException e) {
			return false;
		}
		return true;
	}
	
}
