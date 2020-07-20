/*
 * Copyright 2012 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.compare;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;
/**
 * @since 7.8
 */
public interface CompareSets <T,R> {

	default ConceptCompareSetResult<T> compareDifferents(List<T> baseSet, List<T> compareSet) {
		ListMultimap<T, T> changes = ArrayListMultimap.create();
		Set<T> remove = Sets.newHashSet();
		Set<T> add = Sets.newHashSet();

		remove.addAll(baseSet);
		add.addAll(compareSet);

		for (T memberA : baseSet) {
			compareSet.forEach(memberB -> {
				if (isSame(memberA, memberB)) {
					remove.remove(memberA);
					add.remove(memberB);
				} else if (isChanged(memberA, memberB)) {
					remove.remove(memberA);
					add.remove(memberB);
					changes.put(memberA, memberB);
				}
			});
		}
		return new ConceptCompareSetResult<T>(add, remove, changes);
	}
	
	default boolean isSame(T memberA, T memberB) {
		if (isSourceEqual(memberA, memberB) && isTargetEqual(memberA, memberB)) {
			return true;
		}
		return false;
	}
	
	default boolean isChanged(T memberA, T memberB) {
		if (isSourceEqual(memberA, memberB) && !isTargetEqual(memberA, memberB)) {
			return true;
		}
		return false;
	}
	
	ConceptCompareSetResult<T> doCompare(R baseSet, R compareSet);
	boolean isTargetEqual(T memberA, T memberB);
	boolean isSourceEqual(T memberA, T memberB);

}
