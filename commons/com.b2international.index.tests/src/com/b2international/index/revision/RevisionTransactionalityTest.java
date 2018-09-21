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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.b2international.index.DocSearcher;
import com.b2international.index.Hits;
import com.b2international.index.IndexRead;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionFixtures.Data;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

/**
 * @since 5.0
 */
public class RevisionTransactionalityTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(Data.class);
	}
	
	@Test
	public void tx1UpdateAndTx2UpdateOnSameDocumentShouldInvalidateThePreviousRevisionOnBothSegments() throws Exception {
		// store the initial revision on segment 0
		final Data data = new Data("field1", "field2");
		indexRevision(MAIN, STORAGE_KEY1, data);
		
		// create MAIN/a, which will create two new segments, 1 for 'a' and 2 for 'MAIN'
		createBranch(MAIN, "a");
		
		final long mainCommitTime = currentTime();
		final long childCommitTime = currentTime();
		
		final RevisionIndex index = index();
		index.write(MAIN, mainCommitTime, new RevisionIndexWrite<Void>() {
			@Override
			public Void execute(RevisionWriter writer) throws IOException {
				writer.put(STORAGE_KEY1, new Data("field1ChangedOnMAIN", "field2"));
				// simulate another transaction when the first transaction is open, but still not committed
				index.write("MAIN/a", childCommitTime, new RevisionIndexWrite<Void>() {
					@Override
					public Void execute(RevisionWriter writer) throws IOException {
						writer.put(STORAGE_KEY1, new Data("field1", "field2ChangedOnChild"));
						writer.commit();
						return null;
					}
				});
				writer.commit();
				return null;
			}
		});
		
		// after both tx commit query the branches for the latest revision
		final Data childRevision = getRevision("MAIN/a", Data.class, STORAGE_KEY1);
		final Data mainRevision = getRevision(MAIN, Data.class, STORAGE_KEY1);
		
		final Data expectedOnChild = new Data("field1", "field2ChangedOnChild");
		final Data expectedOnMain = new Data("field1ChangedOnMAIN", "field2");
		
		assertDocEquals(expectedOnChild, childRevision);
		assertDocEquals(expectedOnMain, mainRevision);
		
		// assert that mainRevision has segment ID equal to 2, while childRevision has segment ID equal to 1
		assertEquals(1, childRevision.getSegmentId());
		assertEquals(2, mainRevision.getSegmentId());
		
		// assert that the previous revision of the document has replacedIns set for both segment 1 and 2
		final Data replacedRevision = rawIndex().read(new IndexRead<Data>() {
			@Override
			public Data execute(DocSearcher index) throws IOException {
				final Hits<Data> hits = index.search(Query.select(Data.class)
						// only a single revision exists in segment 0
						.where(Expressions.match(Revision.SEGMENT_ID, 0))
						.limit(2) // query two items so getOnlyElement will throw exception in case of invalid query
						.build());
				return Iterables.getOnlyElement(hits);
			}
		});
		assertThat(replacedRevision.getReplacedIns()).containsOnly(1, 2);
	}
	
}
