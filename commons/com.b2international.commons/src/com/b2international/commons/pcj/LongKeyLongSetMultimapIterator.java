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
package com.b2international.commons.pcj;

import static com.google.common.base.Preconditions.checkNotNull;
import bak.pcj.map.LongKeyMapIterator;
import bak.pcj.set.LongSet;

/**
 * Iterator for {@link LongKeyLongSetMultimap}s.
 *
 */
public final class LongKeyLongSetMultimapIterator implements LongKeyMapIterator {

	private LongKeyMapIterator delegate;

	/*default*/ LongKeyLongSetMultimapIterator(final LongKeyMapIterator delegate) {
		this.delegate = checkNotNull(delegate, "delegate");
	}
	
	@Override
	public boolean hasNext() {
		return this.delegate.hasNext();
	}

	@Override
	public void next() {
		delegate.next();
	}

	@Override
	public void remove() {
		delegate.remove();
	}

	@Override
	public long getKey() {
		return delegate.getKey();
	}

	@Override
	public LongSet getValue() {
		return (LongSet) delegate.getValue();
	}

}