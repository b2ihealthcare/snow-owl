/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.ecl;

import static com.b2international.snowowl.datastore.index.RevisionDocument.Expressions.ids;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedAncestors;
import static com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Expressions.statedParents;
import static com.b2international.snowowl.test.commons.snomed.DocumentBuilders.concept;
import static com.b2international.snowowl.test.commons.snomed.DocumentBuilders.relationship;
import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.Before;
import org.junit.Test;

import com.b2international.collections.PrimitiveCollectionModule;
import com.b2international.collections.PrimitiveSets;
import com.b2international.index.Index;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.revision.BaseRevisionIndexTest;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.request.RevisionIndexReadRequest;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.tree.Trees;
import com.b2international.snowowl.snomed.datastore.id.RandomSnomedIdentiferGenerator;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * @since 5.15.1
 */
public class SnomedStatedEclEvaluationTest extends BaseRevisionIndexTest {

	private static final Injector INJECTOR = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();
	
	private static final String ROOT_CONCEPT = RandomSnomedIdentiferGenerator.generateConceptId();
	private static final String HAS_ACTIVE_INGREDIENT = Concepts.HAS_ACTIVE_INGREDIENT;
	private static final String SUBSTANCE = Concepts.SUBSTANCE;
	private static final String STATED_CONCEPT = RandomSnomedIdentiferGenerator.generateConceptId();

	private BranchContext context;
	
	@Before
	public void setup() {
		super.setup();
		context = TestBranchContext.on(MAIN)
				.with(EclParser.class, new DefaultEclParser(INJECTOR.getInstance(IParser.class), INJECTOR.getInstance(IResourceValidator.class)))
				.with(EclSerializer.class, new DefaultEclSerializer(INJECTOR.getInstance(ISerializer.class)))
				.with(Index.class, rawIndex())
				.with(RevisionIndex.class, index())
				.build();
	}
	
	@Override
	protected void configureMapper(ObjectMapper mapper) {
		super.configureMapper(mapper);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.registerModule(new PrimitiveCollectionModule());
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableSet.of(SnomedDescriptionIndexEntry.class, SnomedConceptDocument.class, SnomedRelationshipIndexEntry.class, SnomedRefSetMemberIndexEntry.class);
	}
	
	@Test
	public void statedRefinementWithZeroToOneCardinalityInAttributeConjuction() throws Exception {
		generateTestHierarchy();
		final Expression actual = eval(String.format("<<%s:[1..*]{[0..1]%s=<<%s}", ROOT_CONCEPT, HAS_ACTIVE_INGREDIENT, SUBSTANCE));
		
		final Expression expected = and(
				descendantsOrSelfOf(ROOT_CONCEPT),
				ids(ImmutableSet.of(STATED_CONCEPT))
				);
		assertEquals(expected, actual);
	}
	
	private Expression eval(String expression) {
		return new RevisionIndexReadRequest<>(SnomedRequests.prepareEclEvaluation(expression).setExpressionForm(Trees.STATED_FORM).build())
				.execute(context)
				.getSync();		
	}
	
	private void generateTestHierarchy() {
		indexRevision(MAIN, nextStorageKey(), concept(STATED_CONCEPT).statedParents(PrimitiveSets.newLongOpenHashSet(Long.parseLong(ROOT_CONCEPT))).build());
		indexRevision(MAIN, nextStorageKey(), relationship(STATED_CONCEPT, HAS_ACTIVE_INGREDIENT, SUBSTANCE, Concepts.STATED_RELATIONSHIP).group(1).build());
	}
	
	private Expression descendantsOrSelfOf(String...conceptIds) {
		return Expressions.builder()
				.should(ids(ImmutableSet.copyOf(conceptIds)))
				.should(statedParents(ImmutableSet.copyOf(conceptIds)))
				.should(statedAncestors(ImmutableSet.copyOf(conceptIds))).build();
	}
	
	private static Expression and(Expression left, Expression right) {
		return Expressions.builder().filter(left).filter(right).build();
	}

}
