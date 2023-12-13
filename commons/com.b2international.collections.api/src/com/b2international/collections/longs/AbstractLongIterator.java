/*
 * Copyright 2011-2016 B2i Healthcare, https://b2ihealthcare.com
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

import static com.google.common.base.Preconditions.checkState;

import java.util.NoSuchElementException;

/**
 * Skeleton unmodifiable implementation of the {@link LongIterator}.
 */
public abstract class AbstractLongIterator extends UnmodifiableLongIterator {

	private State state = State.NOT_READY;

	private enum State {
		/** We have computed the next element and haven't returned it yet. */
		READY,

		/** We haven't yet computed or have already returned the element. */
		NOT_READY,

		/** We have reached the end of the data and are finished. */
		DONE,

		/** We've suffered an exception and are kaput. */
		FAILED,
	}

	private long next;

	protected abstract long computeNext();

	/**
	 * Implementations of {@code computeNext} <b>must</b> invoke this method when
	 * there are no elements left in the iteration.
	 *
	 * @return {@link Long#MIN_VALUE}; a convenience so your {@link #computeNext} implementation 
	 * can use the simple statement {@code return endOfData();}
	 */
	protected final long endOfData() {
		state = State.DONE;
		return Long.MIN_VALUE;
	}

	/*
	 * (non-Javadoc)
	 * @see bak.pcj.LongIterator#hasNext()
	 */
	@Override
	public final boolean hasNext() {
		checkState(state != State.FAILED);
		switch (state) {
			case DONE:
				return false;
			case READY:
				return true;
			default:
		}
		return tryToComputeNext();
	}

	/*
	 * (non-Javadoc)
	 * @see bak.pcj.LongIterator#next()
	 */
	@Override
	public final long next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		state = State.NOT_READY;
		return next;
	}

	private boolean tryToComputeNext() {
		state = State.FAILED; // temporary pessimism
		next = computeNext();
		if (state != State.DONE) {
			state = State.READY;
			return true;
		}
		return false;
	}

}