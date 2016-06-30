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

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.b2international.index.revision.RevisionFixtures.Data;
import com.google.common.collect.ImmutableList;

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
		
		final RevisionIndex index = index();
		index.write(MAIN, 1L, new RevisionIndexWrite<Void>() {
			@Override
			public Void execute(RevisionWriter writer) throws IOException {
				writer.put(STORAGE_KEY1, new Data("field1ChangedOnMAIN", "field2"));
				// simulate another transaction when the first transaction is open, but still not committed
				index.write("MAIN/a", 2L, new RevisionIndexWrite<Void>() {
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
		assertDocEquals(new Data("field1", "field2ChangedOnChild"), childRevision);
		
		final Data mainRevision = getRevision(MAIN, Data.class, STORAGE_KEY1);
		assertDocEquals(new Data("field1ChangedOnMAIN", "field2"), mainRevision);
	}
	
}
