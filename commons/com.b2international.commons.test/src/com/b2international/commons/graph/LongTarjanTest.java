/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.commons.graph;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.b2international.collections.PrimitiveMaps;
import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongCollections;
import com.b2international.collections.longs.LongKeyLongMap;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.graph.LongTarjan;
import com.google.common.collect.ImmutableList;

/**
 * @since 6.0.0
 */
public class LongTarjanTest {

	@Test
	public void smallBatchSize() throws Exception {
		final LongKeyLongMap followerMap = PrimitiveMaps.newLongKeyLongOpenHashMap();

		followerMap.put(1, 2);
		followerMap.put(2, 3);
		followerMap.put(3, 4);
		followerMap.put(4, 1);
		followerMap.put(5, 3);
		followerMap.put(6, 2);
		followerMap.put(7, 8);
		followerMap.put(8, 6);

		final LongTarjan tarjan = new LongTarjan(3, currentId -> LongCollections.singletonSet(followerMap.get(currentId)));

		final List<LongSet> actual = tarjan.run(followerMap.keySet());
		
		assertEquals(ImmutableList.of(
			PrimitiveSets.newLongOpenHashSet(1L, 2L, 3L, 4L),
			PrimitiveSets.newLongOpenHashSet(5L, 6L, 8L),
			PrimitiveSets.newLongOpenHashSet(7L)
		), actual);
	}
	
}
