/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import static com.google.common.collect.Sets.newHashSetWithExpectedSize;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.elasticsearch.common.UUIDs;
import org.junit.Test;

import com.b2international.index.Fixtures.Data;
import com.b2international.index.es.query.EsQueryBuilder;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.query.SetPredicate;

/**
 * This test should fail without the partitioning code for {@link SetPredicate}s in {@link EsQueryBuilder}, 
 * Elasticsearch prevents running queries with more than the configured <i>index.max_terms_count</i>. 
 * 
 * @since 7.4
 */
public class MaxTermsCountTest extends BaseIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return Collections.singleton(Data.class);
	}
	
	@Test
	public void queryWithMoreThanMaxTermsCount() throws Exception {
		final int numberOfTerms = 2 * IndexClientFactory.DEFAULT_MAX_TERMS_COUNT;
		final Set<String> moreThanMaxTermsCount = newHashSetWithExpectedSize(numberOfTerms);
		for (int i = 0; i < numberOfTerms; i++) {
			moreThanMaxTermsCount.add(""+i);
		}
		
		indexDocuments(List.of(
			createData("1"),
			createData("2"),
			createData(""+(numberOfTerms + 1))
		));
		
		// two matches
		assertThat(
			search(Query.select(Data.class).where(Expressions.matchAny("field1", moreThanMaxTermsCount)).build()).getTotal()
		).isEqualTo(2);
	}

	private Data createData(String field1Value) {
		final Data data = new Data(UUIDs.randomBase64UUID());
		data.setField1(field1Value);
		return data;
	}

}
