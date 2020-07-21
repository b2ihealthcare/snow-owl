/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Objects;

import com.b2international.snowowl.core.domain.SetMapping;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
/**
 * @since 7.8
 */
public interface CompareSets {

	default ConceptMapCompareResult compareDifferents(List<SetMapping> baseSet, List<SetMapping> compareSet) {
		ListMultimap<SetMapping, SetMapping> changes = ArrayListMultimap.create();
		List<SetMapping> remove = Lists.newArrayList();
		List<SetMapping> add = Lists.newArrayList();

		remove.addAll(baseSet);
		add.addAll(compareSet);

		for (SetMapping memberA : baseSet) {
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
		return new ConceptMapCompareResult (add, remove, changes);
	}

	default boolean isSame(SetMapping memberA, SetMapping memberB) {
		return isSourceEqual(memberA, memberB) && isTargetEqual(memberA, memberB);
	}

	default boolean isChanged(SetMapping memberA, SetMapping memberB) {
		return isSourceEqual(memberA, memberB) && !isTargetEqual(memberA, memberB);
	}

	default boolean isTargetEqual(SetMapping memberA, SetMapping memberB) {
		return  Objects.equals(memberA.getTargetComponentURI(),memberB.getTargetComponentURI());
	}

	default boolean isSourceEqual(SetMapping memberA, SetMapping memberB){
		return Objects.equals(memberA.getSourceComponentURI(), memberB.getSourceComponentURI());
	}

}
