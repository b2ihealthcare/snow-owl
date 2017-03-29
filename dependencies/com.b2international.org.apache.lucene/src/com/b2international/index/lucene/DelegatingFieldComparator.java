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
package com.b2international.index.lucene;

import java.io.IOException;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.LeafFieldComparator;

/**
 * @since 5.8
 */
public class DelegatingFieldComparator<T> extends FieldComparator<T> {

	private final FieldComparator<T> delegate;
	
	public DelegatingFieldComparator(FieldComparator<T> delegate) {
		this.delegate = delegate;
	}

	@Override
	public int compare(int slot1, int slot2) {
		return delegate.compare(slot1, slot2);
	}

	@Override
	public void setTopValue(T value) {
		delegate.setTopValue(value);
	}

	@Override
	public T value(int slot) {
		return delegate.value(slot);
	}

	@Override
	public LeafFieldComparator getLeafComparator(LeafReaderContext context) throws IOException {
		return delegate.getLeafComparator(context);
	}
}
