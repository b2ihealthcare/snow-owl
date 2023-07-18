/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.request;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.eclipse.xtext.parser.IParser;
import org.eclipse.xtext.serializer.ISerializer;
import org.eclipse.xtext.validation.IResourceValidator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snomed.ecl.EclStandaloneSetup;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.QueryExpression;
import com.b2international.snowowl.core.domain.QueryExpressionDiff;
import com.b2international.snowowl.core.domain.QueryExpressionDiffs;
import com.b2international.snowowl.core.ecl.DefaultEclParser;
import com.b2international.snowowl.core.ecl.DefaultEclSerializer;
import com.b2international.snowowl.core.ecl.EclParser;
import com.b2international.snowowl.core.ecl.EclSerializer;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.request.QueryOptimizer;
import com.b2international.snowowl.core.request.ecl.EclRewriter;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.request.SnomedHierarchyStats.ConceptDescendantCountById;
import com.b2international.snowowl.snomed.core.request.SnomedHierarchyStats.ConceptSearchById;
import com.b2international.snowowl.snomed.core.request.SnomedHierarchyStats.EdgeSearchBySourceId;
import com.b2international.snowowl.snomed.core.request.SnomedQueryOptimizer.ConceptSetEvaluator;
import com.b2international.snowowl.snomed.core.request.SnomedQueryOptimizer.EclEvaluator;
import com.b2international.snowowl.snomed.core.request.SnomedRelationshipStats.RelationshipSearchBySource;
import com.b2international.snowowl.snomed.core.request.SnomedRelationshipStats.RelationshipSearchByTypeAndDestination;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.snomed.RandomSnomedIdentiferGenerator;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;

public class SnomedQueryOptimizerTest {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedQueryOptimizerTest.class);
	private static final Injector ECL_INJECTOR = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();

	private static class EclConceptSetEvaluator implements ConceptSetEvaluator {

		private final EclEvaluator evaluator;
		
		private EclConceptSetEvaluator(final EclEvaluator evaluator) {
			this.evaluator = evaluator;
		}

		@Override
		public Set<String> evaluateConceptSet(
			final BranchContext context, 
			final List<ExtendedLocale> locales,
			final ResourceURI resourceUri, 
			final Collection<QueryExpression> inclusions, 
			final Collection<QueryExpression> exclusions,
			final int pageSize
		) {
			final Set<String> includedIds = inclusions.stream()
				.flatMap(clause -> evaluator.evaluateEcl(context, clause.getQuery(), pageSize))
				.collect(Collectors.toSet());

			exclusions.stream()
				.flatMap(clause -> evaluator.evaluateEcl(context, clause.getQuery(), pageSize))
				.forEachOrdered(excludedId -> includedIds.remove(excludedId));

			return ImmutableSet.copyOf(includedIds);
		}
	}
	
	@Rule
	public TestWatcher watcher = new TestWatcher() {
		@Override
		protected void starting(final Description description) {
			System.out.println("============== " + description + " ================");
		}
		
		@Override
		protected void finished(final Description description) {
			System.out.println();
		}
	};

	private static BranchContext context;

	@BeforeClass
	public static void setupContext() {
		context = mock(BranchContext.class);

		when(context.service(EclParser.class)).thenReturn(new DefaultEclParser(
			ECL_INJECTOR.getInstance(IParser.class), 
			ECL_INJECTOR.getInstance(IResourceValidator.class)));

		when(context.service(EclSerializer.class)).thenReturn(new DefaultEclSerializer(
			ECL_INJECTOR.getInstance(ISerializer.class)));

		when(context.service(EclRewriter.class)).thenReturn(new EclRewriter());		
	}

	private SnomedQueryOptimizer optimizer;

	private void setEclAndConceptSetEvaluator(final EclEvaluator evaluator) {
		optimizer.setEvaluator(evaluator);
		optimizer.setConceptSetEvaluator(new EclConceptSetEvaluator(evaluator));
	}

	@Before
	public void setupOptimizer() {
		optimizer = new SnomedQueryOptimizer();

		optimizer.setClock(Clock.fixed(Instant.now(), ZoneOffset.UTC));
		optimizer.setResourceUri(SnomedContentRule.SNOMEDCT);
		optimizer.setLog(LOG);

		optimizer.setEvaluator((context, eclExpression, pageSize) -> Stream.empty());
		optimizer.setConceptSetEvaluator((context, locales, resourceUri, inclusions, exclusions, pageSize) -> Set.of());
		
		optimizer.setLabeler((context, locales, codeSystemUri, unlabeledExpressions) -> unlabeledExpressions);

		optimizer.setConceptSearchById((context, conceptIds) -> Stream.empty());
		optimizer.setConceptDescendantCountById((context, conceptIds, direct) -> Stream.empty());
		optimizer.setEdgeSearchBySourceId((context, sourceIds) -> Stream.empty());

		optimizer.setRelationshipSearchBySource((context, sourceIds) -> Stream.empty());
		optimizer.setRelationshipSearchByTypeAndDestination((context, typeIds, destinationIds) -> Stream.empty());
	}

	@Test
	public void testEmpty() throws Exception {

		final Options optimizeOptions = Options.builder()
			.put(QueryOptimizer.OptionKey.INCLUSIONS, List.<QueryExpression>of())
			.put(QueryOptimizer.OptionKey.EXCLUSIONS, List.<QueryExpression>of())
			.put(QueryOptimizer.OptionKey.LOCALES, null)
			.put(QueryOptimizer.OptionKey.LIMIT, 100)
			.build();

		final QueryExpressionDiffs diffs = optimizer.optimize(context, optimizeOptions);
		assertThat(diffs).isEmpty();
		assertThat(diffs.isHasMoreOptimizations()).isFalse();
	}

	@Test
	public void testSingleInclusion() throws Exception {
		
		setEclAndConceptSetEvaluator((context, expression, pageSize) -> Stream.of(expression));

		final QueryExpression inclusion = new QueryExpression(IDs.base62UUID(), "131148009", false); // Bleeding
		
		final Options optimizeOptions = Options.builder()
			.put(QueryOptimizer.OptionKey.INCLUSIONS, List.<QueryExpression>of(inclusion))
			.put(QueryOptimizer.OptionKey.EXCLUSIONS, List.<QueryExpression>of())
			.put(QueryOptimizer.OptionKey.LOCALES, null)
			.put(QueryOptimizer.OptionKey.LIMIT, 100)
			.build();

		final QueryExpressionDiffs diffs = optimizer.optimize(context, optimizeOptions);
		assertThat(diffs).isEmpty();
		assertThat(diffs.isHasMoreOptimizations()).isFalse();
	}

	@Test
	public void testRedundantPinnedInclusions() throws Exception {
		
		final EclEvaluator evaluator = mock(EclEvaluator.class, i -> Stream.empty());
		when(evaluator.evaluateEcl(any(), eq("< 80631005"), anyInt())).thenAnswer(i -> Stream.of("13104003", "60333009", "50283003", "2640006"));
		when(evaluator.evaluateEcl(any(), eq("13104003 OR 60333009 OR 50283003 OR 2640006"), anyInt())).thenAnswer(i -> Stream.of("13104003", "60333009", "50283003", "2640006"));
		setEclAndConceptSetEvaluator(evaluator);

		final QueryExpression pinned1 = new QueryExpression(IDs.base62UUID(), "< 80631005", true); // Clinical stage finding
		final QueryExpression pinned2 = new QueryExpression(IDs.base62UUID(), "13104003 OR 60333009 OR 50283003 OR 2640006", true); // Children of "Clinical stage finding"

		final Options optimizeOptions = Options.builder()
			.put(QueryOptimizer.OptionKey.INCLUSIONS, List.<QueryExpression>of(pinned1, pinned2))
			.put(QueryOptimizer.OptionKey.EXCLUSIONS, List.<QueryExpression>of())
			.put(QueryOptimizer.OptionKey.LOCALES, null)
			.put(QueryOptimizer.OptionKey.LIMIT, 100)
			.build();

		final QueryExpressionDiffs diffs = optimizer.optimize(context, optimizeOptions);
		assertThat(diffs.isHasMoreOptimizations()).isFalse();

		final QueryExpressionDiff diff = Iterables.getOnlyElement(diffs);
		assertThat(diff.getRemove()).containsExactly(pinned2);
	}

	@Test
	public void testOptimizeToRefinement() throws Exception {

		// 57 members + 3 non-members = 60 total (0.95 precision)
		final Set<String> members = IntStream.range(0, 57)
			.mapToObj(i -> RandomSnomedIdentiferGenerator.generateConceptId())
			.collect(Collectors.toSet());

		final Set<String> nonMembers = Set.of(
			"396717008", // Crow
			"74353003",  // Egret
			"33964005"   // Wild bird
		);

		// The ECL evaluator should respond to the refinement query, but otherwise assume that all other ECL expressions are single-concept ones
		final EclEvaluator evaluator = mock(EclEvaluator.class, i -> Stream.of(i.getArgument(1, String.class)));
		when(evaluator.evaluateEcl(any(), eq("* : 116676008 = 56381008"), anyInt())).thenAnswer(i -> members.stream());

		final RelationshipSearchBySource relationshipSearchBySource = mock(RelationshipSearchBySource.class, i -> Stream.of());
		when(relationshipSearchBySource.findRelationshipsBySource(any(), eq(members)))
			.thenAnswer(i -> {
				final Set<String> sourceIds = i.getArgument(1); 
				final List<SnomedRelationship> relationships = newArrayList();
	
				int idx = 0;
				for (final String id : sourceIds) {
					
					// All members will have an "Associated morphology = Calculus" relationship
					final SnomedRelationship r1 = new SnomedRelationship();
					r1.setSourceId(id);
					r1.setTypeId("116676008"); // Associated morphology
					r1.setDestinationId("56381008"); // Calculus
					r1.setRelationshipGroup(0);
					relationships.add(r1);
	
					// Same for "Severity = Severe"
					final SnomedRelationship r2 = new SnomedRelationship();
					r2.setSourceId(id);
					r2.setTypeId("246112005"); // Severity
					r2.setDestinationId("24484000"); // Severe
					r2.setRelationshipGroup(0);
					relationships.add(r2);
					
					if (idx % 2 == 0) {
						
						// Only half of them will have a "Finding site = Body structure" relationship however
						final SnomedRelationship r3 = new SnomedRelationship();
						r3.setSourceId(id);
						r3.setTypeId("363698007"); // Finding site
						r3.setDestinationId("123037004"); // Body structure
						r3.setRelationshipGroup(0);
						relationships.add(r3);
					}
	
					idx++;
				}
	
				return relationships.stream();
			});

		final RelationshipSearchByTypeAndDestination relationshipSearchByTypeAndDestination = mock(RelationshipSearchByTypeAndDestination.class, i -> Stream.of());
		final Set<String> typeIds = Set.of("116676008", "246112005", "363698007");
		final Set<String> destinationIds = Set.of("56381008", "24484000", "123037004");
		when(relationshipSearchByTypeAndDestination.findRelationshipsByTypeAndDestination(any(), eq(typeIds), eq(destinationIds)))
			.thenAnswer(i -> {
				final List<SnomedRelationship> relationships = newArrayList();
	
				int idx = 0;
				for (final String id : members) {
	
					// Achieve 100% precision on this type-destination pair by using all calculus finding concepts
					final SnomedRelationship r1 = new SnomedRelationship();
					r1.setSourceId(id);
					r1.setTypeId("116676008"); // Associated morphology
					r1.setDestinationId("56381008"); // Calculus
					r1.setRelationshipGroup(0);
					relationships.add(r1);
	
					final SnomedRelationship r2 = new SnomedRelationship();
					r2.setSourceId(id);
					r2.setTypeId("246112005"); // Severity
					r2.setDestinationId("24484000"); // Severe
					r2.setRelationshipGroup(0);
					relationships.add(r2);

					if (idx % 2 == 0) {
						final SnomedRelationship r3 = new SnomedRelationship();
						r3.setSourceId(id);
						r3.setTypeId("363698007"); // Finding site
						r3.setDestinationId("123037004"); // Body structure
						r3.setRelationshipGroup(0);
						relationships.add(r3);
					}
	
					idx++;
				}
	
				for (final String id : nonMembers) {
					
					// With three non-member concepts, the 95% stage is passed but the "less than 3 false positives" isn't
					final SnomedRelationship r2 = new SnomedRelationship();
					r2.setSourceId(id);
					r2.setTypeId("246112005"); // Severity
					r2.setDestinationId("24484000"); // Severe
					r2.setRelationshipGroup(0);
					relationships.add(r2);

					// Non-member concepts brings precision down to 90% (27/30)
					final SnomedRelationship r3 = new SnomedRelationship();
					r3.setSourceId(id);
					r3.setTypeId("363698007"); // Finding site
					r3.setDestinationId("123037004"); // Body structure
					r3.setRelationshipGroup(0);
					relationships.add(r3);
				}
	
				return relationships.stream();
			});

		setEclAndConceptSetEvaluator(evaluator);
		optimizer.setRelationshipSearchBySource(relationshipSearchBySource);
		optimizer.setRelationshipSearchByTypeAndDestination(relationshipSearchByTypeAndDestination);

		final List<QueryExpression> inclusions = members.stream()
			.map(id -> new QueryExpression(IDs.base62UUID(), id, false))
			.toList();

		final Options optimizeOptions = Options.builder()
			.put(QueryOptimizer.OptionKey.INCLUSIONS, inclusions)
			.put(QueryOptimizer.OptionKey.EXCLUSIONS, List.<QueryExpression>of())
			.put(QueryOptimizer.OptionKey.LOCALES, null)
			.put(QueryOptimizer.OptionKey.LIMIT, 100)
			.build();

		final QueryExpressionDiffs diffs = optimizer.optimize(context, optimizeOptions);
		assertThat(diffs.getItems()).hasSize(1);
		assertThat(diffs.isHasMoreOptimizations()).isFalse();

		final QueryExpressionDiff diff = Iterables.getOnlyElement(diffs);
		assertThat(diff.getAddToInclusion())
			.extracting(QueryExpression::getQuery)
			.containsExactly("* : 116676008 = 56381008");

		assertThat(diff.getAddToExclusion())
			.isEmpty();
		
		assertThat(diff.getRemove())
			.containsAll(inclusions);
	}
	
	@Test
	public void testOptimizeToDescendantOrSelf() throws Exception {

		final EclEvaluator evaluator = mock(EclEvaluator.class, i -> Stream.empty());
		when(evaluator.evaluateEcl(any(), eq("<< 80631005"), anyInt())).thenAnswer(i -> Stream.of("80631005", "13104003", "60333009", "50283003", "2640006"));
		when(evaluator.evaluateEcl(any(), eq("80631005 OR 13104003 OR 60333009 OR 50283003 OR 2640006"), anyInt())).thenAnswer(i -> Stream.of("80631005", "13104003", "60333009", "50283003", "2640006"));
		setEclAndConceptSetEvaluator(evaluator);

		final ConceptSearchById conceptSearchById = (context, conceptIds) -> conceptIds.stream()
			.map(id -> {
				final SnomedConcept c = new SnomedConcept(id);
				c.setAncestorIds(List.of(SnomedConcept.ROOT_ID));
				
				if ("80631005".equals(id)) {
					c.setParentIds(List.of(SnomedConcept.ROOT_ID));
				} else {
					c.setParentIds(List.of("80631005"));
				}
				
				return c;
			});
		
		final ConceptDescendantCountById conceptDescendantCountById = (context, conceptIds, direct) -> conceptIds.stream()
			.map(id -> {
				final SnomedConcept c = new SnomedConcept(id);

				if ("80631005".equals(id)) {
					// 4 children and descendants
					c.setDescendants(new SnomedConcepts(0, 4));
				} else {
					// All other concepts are leaves
					c.setDescendants(new SnomedConcepts(0, 0));
				}
				
				return c;
			});
		
		final EdgeSearchBySourceId edgeSearchBySourceId = (context, sourceIds) -> sourceIds.stream()
			.filter(id -> !"80631005".equals(id))
			.map(id -> {
				// Relationships point from children to the parent
				final SnomedRelationship r = new SnomedRelationship();
				r.setSourceId(id);
				r.setDestinationId("80631005");
				return r;
			});
				
		optimizer.setConceptSearchById(conceptSearchById);
		optimizer.setConceptDescendantCountById(conceptDescendantCountById);
		optimizer.setEdgeSearchBySourceId(edgeSearchBySourceId);
		
		final QueryExpression include = new QueryExpression(IDs.base62UUID(), "80631005 OR 13104003 OR 60333009 OR 50283003 OR 2640006", false); // "Clinical stage finding" and children

		final Options optimizeOptions = Options.builder()
			.put(QueryOptimizer.OptionKey.INCLUSIONS, List.<QueryExpression>of(include))
			.put(QueryOptimizer.OptionKey.EXCLUSIONS, List.<QueryExpression>of())
			.put(QueryOptimizer.OptionKey.LOCALES, null)
			.put(QueryOptimizer.OptionKey.LIMIT, 100)
			.build();

		final QueryExpressionDiffs diffs = optimizer.optimize(context, optimizeOptions);
		assertThat(diffs.getItems()).hasSize(1);
		assertThat(diffs.isHasMoreOptimizations()).isFalse();

		final QueryExpressionDiff diff = Iterables.getOnlyElement(diffs);
		assertThat(diff.getAddToInclusion())
			.extracting(QueryExpression::getQuery)
			.containsExactly("<< 80631005");

		assertThat(diff.getAddToExclusion())
			.isEmpty();
		
		assertThat(diff.getRemove())
			.containsExactly(include);
	}

	@Test
	public void testSingleInclusionWithValue() throws Exception {
		
		final RelationshipSearchBySource relationshipSearchBySource = (context, sourceIds) -> sourceIds.stream()
			.map(id -> {
				final SnomedRelationship r = new SnomedRelationship();
				r.setSourceId(id);
				r.setTypeId("1142139005"); // Count of base of active ingredient
				r.setValue("#37");
				return r;
			});
		
		final RelationshipSearchByTypeAndDestination relationshipSearchByTypeAndDestination = (context, typeIds, destinationIds) -> {
			final SnomedRelationship r = new SnomedRelationship();
			r.setSourceId("131148009");
			r.setTypeId("1142139005"); // Count of base of active ingredient
			r.setValue("#37");
			return Stream.of(r);
		};
		
		setEclAndConceptSetEvaluator((context, expression, pageSize) -> Stream.of(expression));
		optimizer.setRelationshipSearchBySource(relationshipSearchBySource);
		optimizer.setRelationshipSearchByTypeAndDestination(relationshipSearchByTypeAndDestination);
		
		final QueryExpression inclusion1 = new QueryExpression(IDs.base62UUID(), "131148009", false); // Bleeding
		final QueryExpression inclusion2 = new QueryExpression(IDs.base62UUID(), "125667009 ", false); // Bruising

		final Options optimizeOptions = Options.builder()
			.put(QueryOptimizer.OptionKey.INCLUSIONS, List.<QueryExpression>of(inclusion1, inclusion2))
			.put(QueryOptimizer.OptionKey.EXCLUSIONS, List.<QueryExpression>of())
			.put(QueryOptimizer.OptionKey.LOCALES, null)
			.put(QueryOptimizer.OptionKey.LIMIT, 100)
			.build();

		final QueryExpressionDiffs diffs = optimizer.optimize(context, optimizeOptions);
		assertThat(diffs).isEmpty();
		assertThat(diffs.isHasMoreOptimizations()).isFalse();
	}	
}
