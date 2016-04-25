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
package com.b2international.collections.bytes;

/**
 * @since 4.7
 */
public abstract class AbstractByteCollection implements ByteCollection {

	@Override
	public boolean addAll(ByteCollection collection) {
		boolean changed = false;
		
		final ByteIterator itr = collection.iterator();
		while (itr.hasNext()) {
			changed |= add(itr.next());
		}
		
		return changed;
	}
	
	@Override
	public boolean contains(byte value) {
		final ByteIterator itr = iterator();
		while (itr.hasNext()) {
			byte current = itr.next();
			if (current == value) {
				return true;
			}
		}
	
		return false;
	}

	@Override
	public boolean containsAll(ByteCollection collection) {
		final ByteIterator itr;
		final ByteCollection other;
		
		if (size() < collection.size()) {
			itr = this.iterator();
			other = collection;
		} else {
			itr = collection.iterator();
			other = this;
		}
		
		while (itr.hasNext()) {
			if (!other.contains(itr.next())) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public boolean remove(byte value) {
		boolean changed = false;
		
		final ByteIterator itr = iterator();
		while (itr.hasNext()) {
			byte current = itr.next();
			if (current == value) {
				itr.remove();
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public boolean removeAll(ByteCollection collection) {
		return retainOrRemoveAll(collection, true);
	}

	@Override
	public boolean retainAll(ByteCollection collection) {
		return retainOrRemoveAll(collection, true);
	}

	private boolean retainOrRemoveAll(ByteCollection collection, boolean retain) {
		boolean changed = false;
		
		final ByteIterator itr = iterator();
		while (itr.hasNext()) {
			byte current = itr.next();
			if (collection.contains(current) ^ retain) {
				itr.remove();
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public byte[] toArray() {
		final byte[] result = new byte[size()];
		final ByteIterator itr = iterator();
		int i = 0;
		
		while (itr.hasNext()) {
			result[i++] = itr.next();
		}
		
		return result;
	}

	@Override
	public void clear() {
		final ByteIterator itr = iterator();
		while (itr.hasNext()) {
			itr.next();
			itr.remove();
		}
	}

	@Override
	public boolean isEmpty() {
		final ByteIterator itr = iterator();
		return !itr.hasNext();
	}

	@Override
	public int size() {
		int size = 0;
		final ByteIterator itr = iterator();
		
		while (itr.hasNext()) {
			itr.next();
			size++;
		}
		
		return size;
	}
	
	@Override
	public String toString() {
		ByteIterator it = iterator();
        if (! it.hasNext())
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (;;) {
            byte e = it.next();
            sb.append(e);
            if (! it.hasNext())
                return sb.append(']').toString();
            sb.append(',').append(' ');
        }
	}

}
