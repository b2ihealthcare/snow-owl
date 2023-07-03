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
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
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

import com.b2international.commons.options.Options;
import com.b2international.snomed.ecl.EclStandaloneSetup;
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
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.request.SnomedQueryOptimizer.EclEvaluator;
import com.b2international.snowowl.snomed.core.request.SnomedRelationshipStats.RelationshipSearchBySource;
import com.b2international.snowowl.snomed.core.request.SnomedRelationshipStats.RelationshipSearchByTypeAndDestination;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.google.common.collect.Iterables;
import com.google.inject.Injector;

public class SnomedQueryOptimizerTest {

	private static final Logger LOG = LoggerFactory.getLogger(SnomedQueryOptimizerTest.class);

	private static final Injector ECL_INJECTOR = new EclStandaloneSetup().createInjectorAndDoEMFRegistration();

	@Rule
	public TestWatcher watcher = new TestWatcher() {
		@Override
		protected void starting(final Description description) {
			System.out.println("============== " + description + " ================");
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

	@Before
	public void setupOptimizer() {
		optimizer = new SnomedQueryOptimizer();

		optimizer.setClock(Clock.fixed(Instant.now(), ZoneOffset.UTC));
		optimizer.setResourceUri(SnomedContentRule.SNOMEDCT);
		optimizer.setLog(LOG);

		optimizer.setEvaluator((context, eclExpression, pageSize) -> Stream.empty());
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
	public void testPinnedInclusions() throws Exception {

		final QueryExpression pinned1 = new QueryExpression(IDs.base62UUID(), "< 80631005", true); // Clinical stage finding
		final QueryExpression pinned2 = new QueryExpression(IDs.base62UUID(), "13104003 OR 60333009 OR 50283003 OR 2640006", true); // Children of "Clinical stage finding"

		final EclEvaluator evaluator = mock(EclEvaluator.class, i -> Stream.empty());
		when(evaluator.evaluateEcl(any(), eq("< 80631005"), anyInt())).thenAnswer(i -> Stream.of("13104003", "60333009", "50283003", "2640006"));
		when(evaluator.evaluateEcl(any(), eq("13104003 OR 60333009 OR 50283003 OR 2640006"), anyInt())).thenAnswer(i -> Stream.of("13104003", "60333009", "50283003", "2640006"));

		optimizer.setEvaluator(evaluator);

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
	public void testRelationshipRefinement() throws Exception {

		final Set<String> calculusFindings = Set.of(
			"833292002", // Calcium oxalate calculus of bladder (disorder)
			"833291009", // Calcium oxalate calculus of kidney (disorder)
			"444717006", // Calcium oxalate urolithiasis (disorder)
			"427649000", // Calcium renal calculus (disorder)
			"23754003",  // Calculous pyelonephritis (disorder)
			"313413008", // Calculus finding (finding)
			"266474003", // Calculus in biliary tract (disorder)
			"236709004", // Calculus in calyceal diverticulum (disorder)
			"18109005",  // Calculus in diverticulum of bladder (disorder)
			"236711008"  // Calculus in pelviureteric junction (disorder)
		);

		final Set<String> nonMembers = Set.of(
			"396717008", // Crow
			"74353003"   // Egret
		);

		final List<QueryExpression> inclusions = calculusFindings.stream()
			.map(id -> new QueryExpression(IDs.base62UUID(), id, false))
			.toList();

		final EclEvaluator evaluator = mock(EclEvaluator.class, i -> Stream.of(i.getArgument(1, String.class)));
		when(evaluator.evaluateEcl(any(), eq("* : 116676008 = 56381008"), anyInt())).thenAnswer(i -> calculusFindings.stream());

		// All concepts will have an "Associated morphology = Calculus" relationship
		final RelationshipSearchBySource relationshipSearchBySource = mock(RelationshipSearchBySource.class, i -> Stream.of());
		when(relationshipSearchBySource.findRelationshipsBySource(any(), eq(calculusFindings)))
			.thenAnswer(i -> {
				final Set<String> sourceIds = i.getArgument(1); 
				final List<SnomedRelationship> relationships = newArrayList();
	
				int idx = 0;
				for (final String id : sourceIds) {
					final SnomedRelationship r1 = new SnomedRelationship();
					r1.setSourceId(id);
					r1.setTypeId("116676008"); // Associated morphology
					r1.setDestinationId("56381008"); // Calculus
					r1.setRelationshipGroup(0);
	
					relationships.add(r1);
	
					if (idx % 2 == 0) {
						final SnomedRelationship r2 = new SnomedRelationship();
						r2.setSourceId(id);
						r2.setTypeId("363698007"); // Finding site
						r2.setDestinationId("123037004"); // Body structure
						r2.setRelationshipGroup(0);
	
						relationships.add(r2);
					}
	
					idx++;
				}
	
				return relationships.stream();
			});

		final RelationshipSearchByTypeAndDestination relationshipSearchByTypeAndDestination = mock(RelationshipSearchByTypeAndDestination.class, i -> Stream.of());
		when(relationshipSearchByTypeAndDestination.findRelationshipsByTypeAndDestination(any(), eq(Set.of("116676008", "363698007")), eq(Set.of("56381008", "123037004"))))
			.thenAnswer(i -> {
				final Set<String> sourceIds = newHashSet(calculusFindings);
				final List<SnomedRelationship> relationships = newArrayList();
	
				int idx = 0;
				for (final String id : sourceIds) {
	
					// Achieve 100% precision on this type-destination pair by using all calculus finding concepts
					final SnomedRelationship r1 = new SnomedRelationship();
					
					r1.setSourceId(id);
					r1.setTypeId("116676008"); // Associated morphology
					r1.setDestinationId("56381008"); // Calculus
					r1.setRelationshipGroup(0);
	
					relationships.add(r1);
	
					if (idx % 2 == 0) {
						// Achieve 71% precision (5 good, 7 total) on this type-destination pair by including two non-member concepts
						final SnomedRelationship r2 = new SnomedRelationship();
						
						r2.setSourceId(id);
						r2.setTypeId("363698007"); // Finding site
						r2.setDestinationId("123037004"); // Body structure
						r2.setRelationshipGroup(0);
	
						relationships.add(r2);
					}
	
					idx++;
				}
	
				for (final String id : nonMembers) {
					final SnomedRelationship r2 = new SnomedRelationship();
					
					r2.setSourceId(id);
					r2.setTypeId("363698007"); // Finding site
					r2.setDestinationId("123037004"); // Body structure
					r2.setRelationshipGroup(0);
	
					relationships.add(r2);
				}
	
				return relationships.stream();
			});

		// We will not test the "less than 10 true positives" filter as 19 concepts are needed to pass the 95% bar (19 / (19 + 1) = 0.95))
		// We will not test the "more than 2 non-members" filter as 38 concepts are needed to pass the 95% bar (38 / (38 + 2) = 0.95))

		optimizer.setEvaluator(evaluator);
		optimizer.setRelationshipSearchBySource(relationshipSearchBySource);
		optimizer.setRelationshipSearchByTypeAndDestination(relationshipSearchByTypeAndDestination);

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

		assertThat(diff.getRemove())
			.containsAll(inclusions);
	}
}
