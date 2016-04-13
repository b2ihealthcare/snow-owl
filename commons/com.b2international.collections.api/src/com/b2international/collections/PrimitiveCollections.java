/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.collections;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;

import com.b2international.collections.list.PrimitiveLists;
import com.b2international.collections.map.PrimitiveMaps;
import com.b2international.collections.set.PrimitiveSets;

/**
 * @since 4.6
 */
public abstract class PrimitiveCollections {

	private final PrimitiveLists lists;
	private final PrimitiveSets sets;
	private final PrimitiveMaps maps;

	public PrimitiveCollections(final PrimitiveLists lists, final PrimitiveSets sets, final PrimitiveMaps maps) {
		this.lists = checkNotNull(lists, "lists");
		this.sets = checkNotNull(sets, "sets");
		this.maps = checkNotNull(maps, "maps");
	}
	
	public final PrimitiveLists lists() {
		return lists;
	}
	
	public final PrimitiveSets sets() {
		return sets;
	}
	
	public final PrimitiveMaps maps() {
		return maps;
	}

	public final Collection<Long> newLongCollectionToCollectionAdapter(LongCollection source) {
		throw new UnsupportedOperationException();
	}

}
