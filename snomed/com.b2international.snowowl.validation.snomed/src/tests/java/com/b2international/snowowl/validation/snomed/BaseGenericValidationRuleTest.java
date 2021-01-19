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
package com.b2international.snowowl.validation.snomed;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.index.Index;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.internal.validation.ValidationConfiguration;
import com.b2international.snowowl.core.internal.validation.ValidationRepository;
import com.b2international.snowowl.core.internal.validation.ValidationThreadPool;
import com.b2international.snowowl.core.validation.ValidateRequestBuilder;
import com.b2international.snowowl.core.validation.ValidationRequests;
import com.b2international.snowowl.core.validation.issue.ValidationIssue;
import com.b2international.snowowl.core.validation.issue.ValidationIssues;
import com.b2international.snowowl.core.validation.rule.ValidationRule;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteList;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.datastore.server.snomed.SnomedDatastoreServerActivator;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclParser;
import com.b2international.snowowl.snomed.core.ecl.DefaultEclSerializer;
import com.b2international.snowowl.snomed.core.ecl.EclParser;
import com.b2international.snowowl.snomed.core.ecl.EclSerializer;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConstraintForm;
import com.b2international.snowowl.snomed.mrcm.HierarchyConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.HierarchyInclusionType;
import com.b2international.snowowl.snomed.mrcm.MrcmFactory;
import com.b2international.snowowl.snomed.mrcm.RelationshipPredicate;
import com.b2international.snowowl.test.commons.snomed.DocumentBuilders;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;

/**
 * 
 * @since 6.4
 */
public abstract class BaseGenericValidationRuleTest extends BaseRevisionIndexTest {

	private static final String ATTRIBUTE = "246061005";
	private static final Long ATTRIBUTEL = Long.parseLong(ATTRIBUTE);
	private static final Long ROOT_CONCEPTL = Long.parseLong(Concepts.ROOT_CONCEPT);
	private static final long CASE_SIGNIFICANCEL = Long.parseLong(Concepts.CASE_SIGNIFICANCE_ROOT_CONCEPT);
	private static final long DESCRIPTION_TYPEL = Long.parseLong(Concepts.DESCRIPTION_TYPE_ROOT_CONCEPT);
	private static final String HISTORICAL_ASSOCIATION = "900000000000522004";
	private static final String EPRESCRIBING_ROUTE_SIMPLE_REFSET = "999000051000001100";
	private static final Long CONCEPT_MODEL_ATTRIBUTEL = Long.parseLong(Concepts.CONCEPT_MODEL_ATTRIBUTE);
	private static final Long REFSET_ROOTL = Long.parseLong(Concepts.REFSET_ROOT_CONCEPT);

	private BranchContext context;

	@Parameter
	public long effectiveTime;
	
	@Parameters(name = "EffectiveTime: {0}")
	public static Iterable<? extends Object> data() {
	    return Arrays.asList(-1, 1);
	}
	
	@Override
	public void setup() {
		super.setup();
		final Injector injector = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
		context = TestBranchContext.on(MAIN)
				.with(EclParser.class, new DefaultEclParser(injector.getInstance(IParser.class), injector.getInstance(IResourceValidator.class)))
				.with(EclSerializer.class, new DefaultEclSerializer(injector.getInstance(ISerializer.class))).with(Index.class, rawIndex())
				.with(RevisionIndex.class, index()).with(ObjectMapper.class, getMapper())
				.with(ValidationRepository.class, new ValidationRepository(rawIndex()))
				.with(ClassLoader.class, SnomedDatastoreServerActivator.class.getClassLoader())
				.with(ValidationThreadPool.class, new ValidationThreadPool(1, 1, 1)).build();
		
		// index common required SNOMED CT Concepts
		index().write(MAIN, 1L, writer -> {
			writer.put(nextStorageKey(), concept(Concepts.ROOT_CONCEPT).build());
			// Attributes
			writer.put(nextStorageKey(), concept(ATTRIBUTE).parents(ROOT_CONCEPTL).build());
			writer.put(nextStorageKey(), concept(Concepts.IS_A).parents(ATTRIBUTEL).build());
			writer.put(nextStorageKey(), concept(Concepts.CONCEPT_MODEL_ATTRIBUTE).parents(ATTRIBUTEL).build());
			writer.put(nextStorageKey(), concept(Concepts.HAS_ACTIVE_INGREDIENT).parents(CONCEPT_MODEL_ATTRIBUTEL).build());
			writer.put(nextStorageKey(), concept(Concepts.FINDING_SITE).parents(CONCEPT_MODEL_ATTRIBUTEL).build());
			// Char Types
			writer.put(nextStorageKey(), concept(Concepts.CHARACTERISTIC_TYPE).parents(ROOT_CONCEPTL).build());
			writer.put(nextStorageKey(), concept(Concepts.ADDITIONAL_RELATIONSHIP).parents(Long.parseLong(Concepts.CHARACTERISTIC_TYPE)).build());
			writer.put(nextStorageKey(), concept(Concepts.DEFINING_RELATIONSHIP).parents(Long.parseLong(Concepts.CHARACTERISTIC_TYPE)).build());
			writer.put(nextStorageKey(), concept(Concepts.QUALIFYING_RELATIONSHIP).parents(Long.parseLong(Concepts.CHARACTERISTIC_TYPE)).build());
			writer.put(nextStorageKey(), concept(Concepts.INFERRED_RELATIONSHIP).parents(Long.parseLong(Concepts.CHARACTERISTIC_TYPE)).build());
			writer.put(nextStorageKey(), concept(Concepts.STATED_RELATIONSHIP).parents(Long.parseLong(Concepts.CHARACTERISTIC_TYPE)).build());
			// Description types
			writer.put(nextStorageKey(), concept(Concepts.DESCRIPTION_TYPE_ROOT_CONCEPT).parents(ROOT_CONCEPTL).build());
			writer.put(nextStorageKey(), concept(Concepts.SYNONYM).parents(DESCRIPTION_TYPEL).build());
			writer.put(nextStorageKey(), concept(Concepts.TEXT_DEFINITION).parents(DESCRIPTION_TYPEL).build());
			// Case significance
			writer.put(nextStorageKey(), concept(Concepts.CASE_SIGNIFICANCE_ROOT_CONCEPT).parents(ROOT_CONCEPTL).build());
			writer.put(nextStorageKey(), concept(Concepts.ENTIRE_TERM_CASE_INSENSITIVE).parents(CASE_SIGNIFICANCEL).build());
			writer.put(nextStorageKey(), concept(Concepts.ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE).parents(CASE_SIGNIFICANCEL).build());
			writer.put(nextStorageKey(), concept(Concepts.ENTIRE_TERM_CASE_SENSITIVE).parents(CASE_SIGNIFICANCEL).build());
			// Modules
			writer.put(nextStorageKey(), concept(Concepts.UK_DRUG_EXTENSION_MODULE).parents(ROOT_CONCEPTL).build());
			writer.put(nextStorageKey(), concept(Concepts.PHYSICAL_OBJECT).parents(ROOT_CONCEPTL).build());
			writer.put(nextStorageKey(), concept(HISTORICAL_ASSOCIATION).parents(ROOT_CONCEPTL).build()); // Historical association
			// Refsets
			writer.put(nextStorageKey(), concept(EPRESCRIBING_ROUTE_SIMPLE_REFSET).parents(ROOT_CONCEPTL).build());
			writer.put(nextStorageKey(), concept(Concepts.REFSET_ROOT_CONCEPT).parents(ROOT_CONCEPTL).build());
			writer.put(nextStorageKey(), concept(Concepts.REFSET_LANGUAGE_TYPE).parents(REFSET_ROOTL).build());
			writer.put(nextStorageKey(), concept(Concepts.REFSET_DESCRIPTION_TYPE).parents(REFSET_ROOTL).build());
			// Members
			writer.put(nextStorageKey(), member(Concepts.FULLY_SPECIFIED_NAME, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_DESCRIPTION_TYPE)
					.field(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 255)
					.build());
			writer.put(nextStorageKey(), member(Concepts.SYNONYM, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_DESCRIPTION_TYPE)
					.field(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 255)
					.build());
			writer.put(nextStorageKey(), member(Concepts.TEXT_DEFINITION, SnomedTerminologyComponentConstants.CONCEPT_NUMBER, Concepts.REFSET_DESCRIPTION_TYPE)
					.field(SnomedRf2Headers.FIELD_DESCRIPTION_LENGTH, 4096)
					.build());
			
			writer.commit();
			return null;
		});

	}
	
	protected final SnomedConceptDocument.Builder concept(final String id) {
		return DocumentBuilders.concept(id).effectiveTime(generateRandomEffectiveTime());
	}
	
	protected final SnomedConstraintDocument.Builder constraint(AttributeConstraint constraint) {
		return DocumentBuilders.constraint(constraint).effectiveTime(generateRandomEffectiveTime());
	}
	
	protected final SnomedDescriptionIndexEntry.Builder description(final String id, final String type, final String term) {
		return DocumentBuilders.description(id, type, term).effectiveTime(generateRandomEffectiveTime());
	}
	
	protected final SnomedRelationshipIndexEntry.Builder relationship(final String source, final String type, final String destination) {
		return DocumentBuilders.relationship(source, type, destination).effectiveTime(generateRandomEffectiveTime());
	}
	
	protected final SnomedRelationshipIndexEntry.Builder relationship(final String source, final String type, final String destination, String characteristicTypeId) {
		return DocumentBuilders.relationship(source, type, destination, characteristicTypeId).effectiveTime(generateRandomEffectiveTime());
	}
	
	protected final SnomedRefSetMemberIndexEntry.Builder member(String referencedComponentId, short referencedComponentType, String referenceSetId) {
		return member(UUID.randomUUID().toString(), referencedComponentId, referencedComponentType, referenceSetId);
	}
	
	protected final SnomedRefSetMemberIndexEntry.Builder member(String id, String referencedComponentId, short referencedComponentType, String referenceSetId) {
		return DocumentBuilders.member(UUID.randomUUID().toString(), referencedComponentId, referencedComponentType, referenceSetId).effectiveTime(generateRandomEffectiveTime());
	}
	
	protected final HierarchyConceptSetDefinition hierarchyConceptSetDefinition(final String focusConceptId, HierarchyInclusionType inclusionType) {
		HierarchyConceptSetDefinition predicateRange = MrcmFactory.eINSTANCE.createHierarchyConceptSetDefinition();
		predicateRange.setActive(true);
		predicateRange.setUuid(UUID.randomUUID().toString());
		predicateRange.setConceptId(focusConceptId);
		predicateRange.setInclusionType(inclusionType);
		return predicateRange;
	}
	
	protected final RelationshipPredicate relationshipPredicate(ConceptSetDefinition predicateType, ConceptSetDefinition predicateRange) {
		RelationshipPredicate conceptModelPredicate = MrcmFactory.eINSTANCE.createRelationshipPredicate();
		conceptModelPredicate.setActive(true);
		conceptModelPredicate.setUuid(UUID.randomUUID().toString());
		conceptModelPredicate.setAttribute(predicateType);
		conceptModelPredicate.setRange(predicateRange);
		return conceptModelPredicate;
	}
	
	protected final AttributeConstraint attributeConstraint(ConceptSetDefinition conceptSetDefinition, ConceptModelPredicate conceptModelPredicate) {
		AttributeConstraint attributeConstraint = MrcmFactory.eINSTANCE.createAttributeConstraint();
		attributeConstraint.setActive(true);
		attributeConstraint.setDomain(conceptSetDefinition);
		attributeConstraint.setUuid(UUID.randomUUID().toString());
		attributeConstraint.setForm(ConstraintForm.ALL_FORMS);
		attributeConstraint.setPredicate(conceptModelPredicate);
		return attributeConstraint;
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new PrimitiveCollectionModule());
	}

	protected final void assertAffectedComponents(ValidationIssues issues, ComponentIdentifier... expectedComponentIdentifiers) {
		assertThat(issues).hasSize(expectedComponentIdentifiers.length);
		assertThat(issues.stream().map(ValidationIssue::getAffectedComponent).collect(Collectors.toSet())).containsOnly(expectedComponentIdentifiers);
	}

	protected final ValidationIssues validate(String ruleId) {
		final ValidateRequestBuilder validateRequestBuilder = ValidationRequests.prepareValidate();
		if (effectiveTime == EffectiveTimes.UNSET_EFFECTIVE_TIME) {
			final Map<String, Object> filterOptions = ImmutableMap.of(ValidationConfiguration.IS_UNPUBLISHED_ONLY, Boolean.TRUE);
			validateRequestBuilder.setRuleParameters(filterOptions);
		}
		
		new RevisionIndexReadRequest<>(validateRequestBuilder.build()).execute(context);
		return ValidationRequests.issues().prepareSearch().all().filterByRule(ruleId).build().execute(context);
	}

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.of(SnomedConceptDocument.class, SnomedConstraintDocument.class, SnomedRelationshipIndexEntry.class, SnomedDescriptionIndexEntry.class,
				SnomedRefSetMemberIndexEntry.class, ValidationRule.class, ValidationIssue.class, ValidationWhiteList.class);
	}

	protected final void indexRule(String ruleId) throws Exception {
		final URL rulesJson = getClass().getClassLoader().getResource("validation-rules-common.json");
		try (InputStream in = rulesJson.openStream()) {
			MappingIterator<ValidationRule> it = context.service(ObjectMapper.class).readerFor(ValidationRule.class).readValues(in);
			while (it.hasNext()) {
				final ValidationRule rule = it.next();
				if (ruleId.equals(rule.getId())) {
					indexDocument(ruleId, rule);
					return;
				}
			}
		}
	}
	
	protected final String generateTermOfLength(int length) {
		final char[] characters = new char[length];
		
		int pos = 0;
		final Random random = new Random();
		while (pos < length) {
			final char charToAdd;
			if (pos == 0 || pos == length - 1) {
				// XXX - Avoid test failures by ensuring that first and last characters won't be spaces
				charToAdd = 'a';
			} else {
				final long randomNumber = random.nextInt(1000) + 1;
				charToAdd = randomNumber > 800 ? ' ' : 'a';
			}
			
			characters[pos] = charToAdd;
			pos++;
		}
		
		return new String(characters);
	}
	
	private final long generateRandomEffectiveTime() {
		if (EffectiveTimes.UNSET_EFFECTIVE_TIME == effectiveTime) {
			return EffectiveTimes.UNSET_EFFECTIVE_TIME;
		}
		
		final Random random = new Random();
		long randomEffectiveTime = random.nextInt(1000) + 1;
		
		return randomEffectiveTime > 500 ? EffectiveTimes.UNSET_EFFECTIVE_TIME : randomEffectiveTime;
	}


}
