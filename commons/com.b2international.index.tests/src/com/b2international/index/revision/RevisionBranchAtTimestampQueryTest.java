/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.revision.RevisionFixtures.RevisionData;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.0
 */
public class RevisionBranchAtTimestampQueryTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(RevisionData.class);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void branchAtExpressionNegativeTimestamp() throws Exception {
		getRevision("MAIN@-1", RevisionData.class, STORAGE_KEY1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void branchAtExpressionWithoutPath() throws Exception {
		getRevision("@0", RevisionData.class, STORAGE_KEY1);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void branchAtExpressionWithoutTimestamp() throws Exception {
		getRevision("MAIN@", RevisionData.class, STORAGE_KEY1);
	}
	
	@Test(expected = NotFoundException.class)
	public void branchAtExpressionWithInvalidBranchPath() throws Exception {
		getRevision("non-existent@0", RevisionData.class, STORAGE_KEY1);
	}
	
	@Test
	public void branchAtExpression() throws Exception {
		final RevisionData rev1 = new RevisionData(STORAGE_KEY1, "field1", "field2");
		final RevisionData rev2 = new RevisionData(STORAGE_KEY1, "field1Changed", "field2");
		
		long commit1 = commit(MAIN, Collections.singleton(rev1));
		long commit2 = commit(MAIN, Collections.singleton(rev2));
		
		assertDocEquals(rev2, getRevision(MAIN, RevisionData.class, STORAGE_KEY1));
		assertDocEquals(rev1, getRevision("MAIN@"+commit1, RevisionData.class, STORAGE_KEY1));
		assertDocEquals(rev2, getRevision("MAIN@"+commit2, RevisionData.class, STORAGE_KEY1));
		assertNull(getRevision("MAIN@0", RevisionData.class, STORAGE_KEY1));
	}
	
}
