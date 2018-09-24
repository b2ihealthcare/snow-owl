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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.b2international.index.revision.RevisionFixtures.RevisionData;
import com.google.common.collect.ImmutableList;

/**
 * @since 7.0
 */
public class HookTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return ImmutableList.<Class<?>>of(RevisionData.class);
	}
	
	@Test
	public void preCommitHook() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		Hooks.PreCommitHook preCommit = staging -> {
			assertEquals(1, staging.getNewObjects().size());
			latch.countDown();
		};
		index().hooks().addHook(preCommit);
		commit(MAIN, Collections.singleton(new RevisionData(STORAGE_KEY1, "field1", "field2")));
		assertTrue(latch.await(1, TimeUnit.SECONDS));
	}
	
	@Test
	public void postCommitHook() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		Hooks.PostCommitHook postCommit = commit -> {
			assertEquals(1, commit.getDetailsByObject(STORAGE_KEY1).size());
			latch.countDown();
		};
		index().hooks().addHook(postCommit);
		commit(MAIN, Collections.singleton(new RevisionData(STORAGE_KEY1, "field1", "field2")));
		assertTrue(latch.await(1, TimeUnit.SECONDS));
	}
	
}
