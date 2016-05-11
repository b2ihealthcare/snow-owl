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
package com.b2international.index.revision;

import static com.b2international.index.revision.RevisionFixtures.STORAGE_KEY1;
import static com.b2international.index.revision.RevisionFixtures.STORAGE_KEY2;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Test;

import com.b2international.index.Fixtures.NestedData;
import com.b2international.index.Fixtures.ParentData;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionFixtures.DeeplyNestedData;
import com.google.common.collect.ImmutableList;

/**
 * @since 4.7
 */
public class ComplexDocumentRevisionIndexTest extends BaseRevisionIndexTest {

	private final String branchPath = RevisionBranch.MAIN_PATH;
	
	@Override
	protected final Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(DeeplyNestedData.class);
	}
	
	@Test
	public void searchDeeplyNestedDocument() throws Exception {
		final DeeplyNestedData data = new DeeplyNestedData(new ParentData("field1", new NestedData("field2")));
		final DeeplyNestedData data2 = new DeeplyNestedData(new ParentData("field12", new NestedData("field22")));
		
		indexRevision(branchPath, STORAGE_KEY1, data);
		indexRevision(branchPath, STORAGE_KEY2, data2);
		
		final Query<DeeplyNestedData> deeplyNestedQuery = Query.builder(DeeplyNestedData.class).selectAll().where(Expressions.nestedMatch("parentData.nestedData", Expressions.exactMatch("field2", "field2"))).build();
		final Iterable<DeeplyNestedData> matches = search(branchPath, deeplyNestedQuery);
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(data);
	}
	
	@Test
	public void searchAndReturnDeeplyNestedDocument() throws Exception {
		final NestedData nestedData = new NestedData("field2");
		final DeeplyNestedData data = new DeeplyNestedData(new ParentData("field1", nestedData));
		final DeeplyNestedData data2 = new DeeplyNestedData(new ParentData("field12", new NestedData("field22")));
		
		indexRevision(branchPath, STORAGE_KEY1, data);
		indexRevision(branchPath, STORAGE_KEY2, data2);
		
		final Query<NestedData> query = Query.builder(NestedData.class, DeeplyNestedData.class).selectAll().where(Expressions.exactMatch("field2", "field2")).build();
		final Iterable<NestedData> matches = search(branchPath, query);
		assertThat(matches).hasSize(1);
		assertThat(matches).containsOnly(nestedData);
	}
	
}
