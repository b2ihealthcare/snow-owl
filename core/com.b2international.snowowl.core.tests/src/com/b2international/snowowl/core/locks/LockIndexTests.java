/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.locks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

import com.b2international.index.Index;
import com.b2international.index.Indexes;
import com.b2international.index.WithScore;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.mapping.Mappings;
import com.b2international.index.revision.Revision;
import com.b2international.index.util.Reflections;
import com.b2international.snowowl.core.internal.locks.DatastoreLockContextDescriptions;
import com.b2international.snowowl.core.locks.DatastoreLockIndexEntry;
import com.b2international.snowowl.core.repository.JsonSupport;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.1.0
 */
public class LockIndexTests {

	private static final String USER = "test@b2i.sg";
	private Index index;
	private ObjectMapper mapper;

	@Before
	public void setup() {
		mapper = JsonSupport.getDefaultObjectMapper();
		index = Indexes.createIndex("locks", mapper, new Mappings(DatastoreLockIndexEntry.class));
		index.admin().create();
	}
	
	@Test
	public void indexLockEntry() {
		final String lockId = "1";
		final DatastoreLockIndexEntry lock = DatastoreLockIndexEntry.builder()
			.id(lockId)
			.userId(USER)
			.description(DatastoreLockContextDescriptions.CLASSIFY)
			.repositoryId("repositoryUuid")
			.branchPath("branchPath")
			.build();
		
		indexDocument(lockId, lock);
		final DatastoreLockIndexEntry actual = get(lockId);
		assertDocEquals(lock, actual);
	}
	
	private void indexDocument(final String id, DatastoreLockIndexEntry doc) {
		index.write(writer -> {
			writer.put(id, doc);
			writer.commit();
			
			return null;
		});
	}
	
	private DatastoreLockIndexEntry get(final String lockId) {
		return index.read(searcher -> searcher.get(DatastoreLockIndexEntry.class, lockId));
	}
	
	private void assertDocEquals(DatastoreLockIndexEntry expected, DatastoreLockIndexEntry actual) {
		assertNotNull("Actual document is missing from index", actual);
		for (Field f : index.admin().mappings().getMapping(expected.getClass()).getFields()) {
			if (Revision.Fields.CREATED.equals(f.getName()) 
					|| Revision.Fields.REVISED.equals(f.getName())
					|| DocumentMapping._ID.equals(f.getName())
					|| WithScore.SCORE.equals(f.getName())
					) {
				// skip revision fields from equality check
				continue;
			}
			assertEquals(String.format("Field '%s' should be equal", f.getName()), Reflections.getValue(expected, f), Reflections.getValue(actual, f));
		}
	}
	
}
