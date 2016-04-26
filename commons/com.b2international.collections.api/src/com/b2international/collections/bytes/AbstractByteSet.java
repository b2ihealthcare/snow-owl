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
public abstract class AbstractByteSet extends AbstractByteCollection implements ByteSet {
	
	@Override
	public boolean equals(Object obj) {
		return equals(this, obj);
	}

	@Override
	public int hashCode() {
		return hashCode(this);
	}

	public static boolean equals(ByteSet obj1, Object obj2) {
		if (obj1 == obj2) { return true; }
		if (!(obj2 instanceof ByteSet)) { return false; }
		
		ByteSet other = (ByteSet) obj2;
		if (obj1.size() != other.size()) { return false; }
		return obj1.containsAll(other);
	}

}
