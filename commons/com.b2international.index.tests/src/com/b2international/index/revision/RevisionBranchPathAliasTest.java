/*
 * Copyright 2023 B2i Healthcare, https://b2ihealthcare.com
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
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;

import org.junit.Test;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.index.revision.RevisionFixtures.RevisionData;
import com.google.common.collect.ImmutableSortedSet;

/**
 * @since 9.0.0
 */
public class RevisionBranchPathAliasTest extends BaseRevisionIndexTest {

	@Override
	protected Collection<Class<?>> getTypes() {
		return List.of(RevisionData.class);
	}
	
	@Test
	public void createBranchWithAlias() throws Exception {
		String branchA = createBranch(MAIN, "a");
		
		assertTrue(branching().updateNameAliases(branchA, ImmutableSortedSet.of("b")));
		
		// using the path alias the system should be able to get the branch
		RevisionBranch doc = getBranch("MAIN/b");
		assertThat(doc.getName()).isEqualTo("a");
		assertThat(doc.getPath()).isEqualTo("MAIN/a");
		assertThat(doc.getNameAliases()).isEqualTo(ImmutableSortedSet.of("b"));
		assertThat(doc.getPathAliases()).isEqualTo(ImmutableSortedSet.of("MAIN/b"));
	}
	
	@Test
	public void conflictsWithPath() throws Exception {
		String branchA = createBranch(MAIN, "a");
		String branchB = createBranch(MAIN, "b");
	
		assertThatExceptionOfType(BadRequestException.class)
			.isThrownBy(() -> branching().updateNameAliases(branchA, ImmutableSortedSet.of("b")))
			.withMessage("Conflicting path aliases when trying to update nameAliases of 'MAIN/a' to '[b]'");
	}
	
	@Test
	public void conflictsWithPathAlias() throws Exception {
		String branchA = createBranch(MAIN, "a");
		String branchB = createBranch(MAIN, "b");
		
		assertTrue(branching().updateNameAliases(branchA, ImmutableSortedSet.of("c")));
	
		assertThatExceptionOfType(BadRequestException.class)
			.isThrownBy(() -> branching().updateNameAliases(branchB, ImmutableSortedSet.of("c")))
			.withMessage("Conflicting path aliases when trying to update nameAliases of 'MAIN/b' to '[c]'");
	}
	
	@Test
	public void nullValue() throws Exception {
		String branchA = createBranch(MAIN, "a");
		assertFalse(branching().updateNameAliases(branchA, null));
	}
	
	@Test
	public void clearNameAliases() throws Exception {
		String branchA = createBranch(MAIN, "a");
		
		// set two alias
		assertTrue(branching().updateNameAliases(branchA, ImmutableSortedSet.of("b", "c")));
		
		// clear alias array
		assertTrue(branching().updateNameAliases(branchA, ImmutableSortedSet.of()));
		
		// b alias should not return the branch
		assertThatExceptionOfType(NotFoundException.class)
			.isThrownBy(() -> getBranch("MAIN/b"));
		assertThatExceptionOfType(NotFoundException.class)
			.isThrownBy(() -> getBranch("MAIN/c"));
	}
	
	@Test
	public void removeNameAlias() throws Exception {
		String branchA = createBranch(MAIN, "a");
		
		assertTrue(branching().updateNameAliases(branchA, ImmutableSortedSet.of("b", "c")));
		
		assertTrue(branching().updateNameAliases(branchA, ImmutableSortedSet.of("c")));
		
		// b alias should not return the branch
		assertThatExceptionOfType(NotFoundException.class)
			.isThrownBy(() -> getBranch("MAIN/b"));

		// c alias is still functional
		getBranch("MAIN/c");
	}

}
