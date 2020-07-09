/*******************************************************************************
 * Copyright (c) 2020 B2i Healthcare. All rights reserved.
 *******************************************************************************/
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

	default CompareSetResult<T> compareDifferents(List<T> baseSet, List<T> compareSet) {
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
		return new CompareSetResult<T>(add, remove, changes);
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
	
	CompareSetResult<T> doCompare(R baseSet, R compareSet);
	boolean isTargetEqual(T memberA, T memberB);
	boolean isSourceEqual(T memberA, T memberB);

}
