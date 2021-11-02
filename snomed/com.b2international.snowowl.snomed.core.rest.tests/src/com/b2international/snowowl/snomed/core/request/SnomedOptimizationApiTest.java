/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.snowowl.test.commons.SnomedContentRule.SNOMEDCT;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.codesystem.CodeSystemRequests;
import com.b2international.snowowl.core.domain.QueryExpression;
import com.b2international.snowowl.core.domain.QueryExpressionDiff;
import com.b2international.snowowl.core.domain.QueryExpressionDiffs;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.test.commons.Services;

/**
 * @since 8.0
 */
public class SnomedOptimizationApiTest {
	
	/*
	 * Hierarchy of the SNOMED tree segment with the descendants count the optimization was based on: 
	 * 
	 * MORPHOLOGICALLY_ABNORMAL_STRUCTURE (4)
	 * 	|
	 *  |_Lesion
	 * 	|
	 * 	|_MECHANICAL_ABNORMALITY (2)
	 *  	|
	 *  	|_FLUID_DISTURBANCE (1)
	 * 			|
	 * 			|_EDEMA
	 */
	
	private static final String MORPHOLOGICALLY_ABNORMAL_STRUCTURE = "49755003|Morphologically abnormal structure|";
	private static final String LESION = "52988006|Lesion|";
	private static final String MECHANICAL_ABNORMALITY = "107658001|Mechanical abnormality|";
	private static final String FLUID_DISTURBANCE = "107666005|Fluid disturbance|";
	private static final String EDEMA = "79654002|Edema|";
	
	private static final QueryExpression QUERY_LESION = new QueryExpression(IDs.base64UUID(), LESION, false);
	private static final QueryExpression QUERY_MECHANICAL_ABNORMALITY = new QueryExpression(IDs.base64UUID(), MECHANICAL_ABNORMALITY, false);
	private static final QueryExpression QUERY_FLUID_DISTURBANCE = new QueryExpression(IDs.base64UUID(), FLUID_DISTURBANCE, false);
	private static final QueryExpression QUERY_EDEMA = new QueryExpression(IDs.base64UUID(), EDEMA, false);
	
	private static final List<ExtendedLocale> LOCALES = List.of(ExtendedLocale.valueOf("en-gb"), ExtendedLocale.valueOf("en-us"));
	
	@Test
	public void optimizeEmptyInclusionList() {
		final QueryExpressionDiffs diffs = CodeSystemRequests.prepareOptimizeQueries()
				.filterByInclusions(Collections.emptyList())
				.build(SNOMEDCT)
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);
		
		assertThat(diffs.stream()).isEmpty();
	}
	
	@Test
	public void optimizeOneInclusion() {
		final QueryExpressionDiffs diffs = CodeSystemRequests.prepareOptimizeQueries()
				.filterByInclusions(List.of(QUERY_EDEMA))
				.build(SNOMEDCT)
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);
		
		assertThat(diffs.stream()).isEmpty();
	}
	
	@Test
	public void optimizeInvalidQueries() {
		final QueryExpressionDiffs diffs = CodeSystemRequests.prepareOptimizeQueries()
				.setLocales("en-us")
				.filterByInclusions(List.of(
						new QueryExpression("1", "123|query with syntax error", false),
						new QueryExpression("2", "^<321|query with invalid operator|", false)
					))
				.build(SNOMEDCT)
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);
		
		assertThat(diffs.stream()).isEmpty();
	}
	
	@Test
	public void optimizeComplexExpressions() {
		final QueryExpressionDiffs diffs = CodeSystemRequests.prepareOptimizeQueries()
				.filterByInclusions(List.of(
						new QueryExpression("1", "(<<89155008 OR <<402713007) MINUS (<<403734005 OR <<443868006)", false),
						new QueryExpression("2", "<19829001|Disorder of lung|:116676008|Associated morphology| = 79654002|Edema|", true)
					))
				.build(SNOMEDCT)
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES);
		
		assertThat(diffs.stream()).isEmpty();
	}
	
	@Test
	public void optimizeConceptReferences() {
		final QueryExpressionDiff diff = CodeSystemRequests.prepareOptimizeQueries()
				.setLocales(LOCALES)
				.filterByInclusions(List.of(
						QUERY_FLUID_DISTURBANCE,
						QUERY_EDEMA
					))
				.build(SNOMEDCT)
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES)
				.first()
				.get();
		
		assertThat(diff.getAddToExclusion()).isEmpty();
		
		assertThat(diff.getAddToInclusion()).extracting("query")
			.containsOnly(descendantsOf(MECHANICAL_ABNORMALITY));
		
		assertThat(diff.getRemove()).extracting("query")
			.containsOnly(EDEMA, FLUID_DISTURBANCE);
	}
	
	@Test
	public void optimizePinnedConceptReferences() {
		final QueryExpressionDiff diff = CodeSystemRequests.prepareOptimizeQueries()
				.setLocales(LOCALES)
				.filterByInclusions(List.of(
						QUERY_FLUID_DISTURBANCE,
						QUERY_MECHANICAL_ABNORMALITY,
						QUERY_LESION,
						new QueryExpression(IDs.base64UUID(), EDEMA, true)
					))
				.build(SNOMEDCT)
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES)
				.first()
				.get();
		
		assertThat(diff.getAddToExclusion()).isEmpty();
		
		assertThat(diff.getAddToInclusion()).extracting("query")
			.containsOnly(descendantsOf(MORPHOLOGICALLY_ABNORMAL_STRUCTURE));
		
		assertThat(diff.getRemove()).extracting("query")
			.containsOnly(FLUID_DISTURBANCE, MECHANICAL_ABNORMALITY, LESION)
			//Pinned queries are not removed
			.doesNotContain(EDEMA);
	}
	
	@Test
	public void optimizeConceptReferencesWithoutLocales() {
		final QueryExpressionDiff diff = CodeSystemRequests.prepareOptimizeQueries()
				.filterByInclusions(List.of(
						QUERY_FLUID_DISTURBANCE,
						QUERY_EDEMA
					))
				.build(SNOMEDCT)
				.execute(Services.bus())
				.getSync(1, TimeUnit.MINUTES)
				.first()
				.get();
		
		assertThat(diff.getAddToExclusion()).isEmpty();
		
		//Only IDs are returned
		assertThat(diff.getAddToInclusion()).extracting("query")
			.containsOnly(descendantsOf("107658001"));
		
		assertThat(diff.getRemove()).extracting("query")
			.containsOnly(FLUID_DISTURBANCE, EDEMA);
	}
	
	private String descendantsOf(final String query) {
		return String.join("", "<", query);
	}
}