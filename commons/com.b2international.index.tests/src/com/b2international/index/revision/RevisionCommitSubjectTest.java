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
package com.b2international.index.revision;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.b2international.index.Doc;

/**
 * @since 9.0.0
 */
public class RevisionCommitSubjectTest extends BaseRevisionIndexTest {

	@Doc
	private static final class SubjectRevision extends Revision implements CommitSubject {

		public SubjectRevision(String id) {
			super(id);
		}

		@Override
		public String extractSubjectId() {
			return getId();
		}
		
	}
	
	private static final class CommitSubjectSupplierImpl implements CommitSubjectSupplier {
		
		@Override
		public Set<String> getSubjectIds() {
			return Set.of(STORAGE_KEY2);
		}
		
	}
	
	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(SubjectRevision.class, RevisionFixtures.RevisionData.class);
	}
	
	@Test
	public void commitSubject() throws Exception {
		Commit commit = indexRevision(MAIN, new SubjectRevision(STORAGE_KEY1));
		assertThat(commit.getSubjects()).containsOnly(STORAGE_KEY1);
	}
	
	@Test
	public void commitSubjectSupplier() throws Exception {
		Commit commit = index().prepareCommit(MAIN)
			.withContext(new CommitSubjectSupplierImpl())
			.stageNew(new RevisionFixtures.RevisionData(STORAGE_KEY1, "field1", "field2"))
			.commit(currentTime(), USER_ID, "Commit");
		
		assertThat(commit.getSubjects()).containsOnly(STORAGE_KEY2);
	}
	
}
