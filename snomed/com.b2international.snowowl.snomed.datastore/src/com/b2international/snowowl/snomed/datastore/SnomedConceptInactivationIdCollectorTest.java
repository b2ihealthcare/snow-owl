/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singleton;

import java.util.Collection;
import java.util.Collections;

import com.b2international.snowowl.core.api.IBranchPath;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Test for {@link SnomedConceptInactivationIdCollector} class.
 * TODO move it to a test plug-in. Replace main method with a test method.
 *
 */
public class SnomedConceptInactivationIdCollectorTest {

	
	public static void main(String[] args) {
		
		final SnomedConceptInactivationIdCollector collector = new MockConceptInactivationIdCollector();
		final IBranchPath branchPath = createMainPath();
		checkState(newHashSet(collector.collectSelfAndDescendantConceptIds(branchPath, "SCT")).equals(newHashSet("A", "B", "C", "D", "E", "x", "y", "z", "SCT")));
		checkState(newHashSet(collector.collectSelfAndDescendantConceptIds(branchPath, "y")).equals(newHashSet("y", "A", "B", "D")));
		checkState(newHashSet(collector.collectSelfAndDescendantConceptIds(branchPath, "A")).equals(newHashSet("A", "B", "D")));
		checkState(newHashSet(collector.collectSelfAndDescendantConceptIds(branchPath, "B")).equals(newHashSet("B", "D")));
		checkState(newHashSet(collector.collectSelfAndDescendantConceptIds(branchPath, "C")).equals(newHashSet("C")));
		checkState(newHashSet(collector.collectSelfAndDescendantConceptIds(branchPath, "D")).equals(newHashSet("D")));
		checkState(newHashSet(collector.collectSelfAndDescendantConceptIds(branchPath, "E")).equals(newHashSet("E")));
		checkState(newHashSet(collector.collectSelfAndDescendantConceptIds(branchPath, "x")).equals(newHashSet("x")));
		
		checkState(newHashSet(collector.collectSelfAndDescendantConceptIds(branchPath, "A", "x")).equals(newHashSet("x", "A", "B", "D")));
		checkState(newHashSet(collector.collectSelfAndDescendantConceptIds(branchPath, "A", "x", "z")).equals(newHashSet("x", "z", "A", "B", "C", "D", "E")));
		checkState(newHashSet(collector.collectSelfAndDescendantConceptIds(branchPath, "B", "x")).equals(newHashSet("x", "B", "D")));
		checkState(newHashSet(collector.collectSelfAndDescendantConceptIds(branchPath, "C", "B")).equals(newHashSet("B", "C", "D", "E")));
		
	}
	 
	/**
	 * Mock {@link SnomedConceptInactivationIdCollector} for testing purposes.
	 *
	 */
	static class MockConceptInactivationIdCollector extends SnomedConceptInactivationIdCollector {

		Multimap<String, String> superTypeIds = HashMultimap.create();
		Multimap<String, String> subTypeIds = HashMultimap.create();
		
		{
			
			superTypeIds.putAll("SCT", Collections.<String>emptySet());
			superTypeIds.putAll("x", singleton("SCT"));
			superTypeIds.putAll("y", singleton("SCT"));
			superTypeIds.putAll("z", singleton("SCT"));
			superTypeIds.putAll("A", newHashSet("SCT", "y"));
			superTypeIds.putAll("B", newHashSet("SCT", "y", "A"));
			superTypeIds.putAll("C", newHashSet("SCT", "x", "y", "z", "A"));
			superTypeIds.putAll("D", newHashSet("SCT", "y", "A", "B"));
			superTypeIds.putAll("E", newHashSet("SCT", "x", "y", "z", "A", "B", "C", "D"));
			
			subTypeIds.putAll("E", Collections.<String>emptySet());
			subTypeIds.putAll("D", singleton("E"));
			subTypeIds.putAll("C", singleton("E"));
			subTypeIds.putAll("B", newHashSet("C", "D", "E"));
			subTypeIds.putAll("A", newHashSet("B", "C", "D", "E"));
			subTypeIds.putAll("y", newHashSet("A", "B", "C", "D", "E"));
			subTypeIds.putAll("x", newHashSet("C", "E"));
			subTypeIds.putAll("z", newHashSet("C", "E"));
			subTypeIds.putAll("SCT", newHashSet("x", "y", "z", "A", "B", "C", "D", "E"));
			
		}
		
		@Override
		protected Collection<String> getAllSubTypeIds(final IBranchPath branchPath, final String conceptId) {
			return newHashSet(subTypeIds.get(conceptId));
		}
		
		@Override
		protected Collection<String> getAllSuperTypeIds(final IBranchPath branchPath, final String conceptId) {
			return newHashSet(superTypeIds.get(conceptId));
		}
		
	}
	
}