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
package com.b2international.commons.collect;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;

import com.b2international.collections.PrimitiveLists;
import com.b2international.collections.longs.LongIterator;
import com.b2international.collections.longs.LongList;
import com.b2international.collections.longs.LongListIterator;
import com.b2international.collections.longs.ResettableLongListIterator;

/**
 * Converts an {@link LongIterator iterator} into a {@link LongListIterator list iterator} by caching the returned entries.
 *
 */
public class LongListIteratorWrapper implements ResettableLongListIterator {

    private final LongIterator delegate;
    private final LongList list = PrimitiveLists.newLongArrayList();

    private int currentIndex = 0;
    private int wrappedIteratorIndex = 0;

    public LongListIteratorWrapper(final LongIterator iterator) {
        this.delegate = checkNotNull(iterator, "iterator");
    }

    @Override
	public boolean hasNext() {
        return currentIndex == wrappedIteratorIndex ? delegate.hasNext() : true;
    }

    @Override
	public boolean hasPrevious() {
        return !(0 == currentIndex);
    }

    @Override
	public long next() throws NoSuchElementException {
        if (currentIndex < wrappedIteratorIndex) {
            ++currentIndex;
            return list.get(currentIndex - 1);
        }

        final long retval = delegate.next();
        list.add(retval);
        ++currentIndex;
        ++wrappedIteratorIndex;
        return retval;
    }

    @Override
	public int nextIndex() {
        return currentIndex;
    }

    @Override
	public long previous() throws NoSuchElementException {
        if (0 == currentIndex) {
            throw new NoSuchElementException();
        }
        --currentIndex;
        return list.get(currentIndex);    
    }

    @Override
	public int previousIndex() {
        return currentIndex - 1;
    }

    /**
     * Not supported. Always throws {@link UnsupportedOperationException}.
     */
    @Override
	public void add(final long obj) throws UnsupportedOperationException {
	    throw new UnsupportedOperationException();
	}

    /**
     * Not supported. Always throws {@link UnsupportedOperationException}.
     */
	@Override
	public void remove() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

	/**
     * Not supported. Always throws {@link UnsupportedOperationException}.
     */
    @Override
	public void set(final long obj) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
	public void reset()  {
        currentIndex = 0;
    }

}