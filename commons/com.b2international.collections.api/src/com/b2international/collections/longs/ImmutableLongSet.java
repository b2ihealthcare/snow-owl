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
package com.b2international.collections.longs;

import com.b2international.collections.LongCollection;

/**
 * @since 4.7
 */
public final class ImmutableLongSet extends ImmutableLongCollection implements LongSet {

	ImmutableLongSet(LongCollection collection) {
		super(collection);
	}

	@Override
	protected LongCollection wrap(LongCollection collection) {
		return of(collection);
	}

	@Override
	public LongSet dup() {
		return (LongSet) super.dup();
	}

	/**
	 * Returns an {@link ImmutableLongSet} containing the elements from the given collection.
	 * 
	 * @param collection
	 * @return
	 */
	public static ImmutableLongSet of(LongCollection collection) {
		return new ImmutableLongSet(collection);
	}

}
