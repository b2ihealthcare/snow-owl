/*
 * Copyright 2017 SNOMED International, http://snomed.org
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
package com.b2international.snowowl.snomed.reasoner.normalform;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.collect.LongKeyLongSetMultimap;

public class NodeGraph {

	private LongKeyLongSetMultimap parentMap = new LongKeyLongSetMultimap();

	public void addParent(long conceptId, long parentId) {
		if (conceptId == parentId) return;
		parentMap.put(conceptId, parentId);
	}

	public LongSet getAncestors(long conceptId) {
		if (!parentMap.keySet().contains(conceptId)) {
			return LongCollections.emptySet();
		}
		
		LongSet ancestorIds = PrimitiveSets.newLongOpenHashSet();
		addAncestorIds(conceptId, ancestorIds);
		ancestorIds.remove(conceptId);
		return ancestorIds;
	}

	private void addAncestorIds(long conceptId, LongSet ids) {
		ids.add(conceptId);
		for (LongIterator itr = parentMap.get(conceptId).iterator(); itr.hasNext(); /* empty */) {
			addAncestorIds(itr.next(), ids);
		}
	}
}
