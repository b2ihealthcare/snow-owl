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
package com.b2international.commons;

import java.lang.reflect.Array;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import com.b2international.collections.PrimitiveCollection;
import com.google.common.collect.Multimap;
import com.google.common.primitives.Primitives;

public class CompareUtils {

	public static boolean isEmpty(final Object object) {
		
		if (null == object) {
			return true;
		}

		if (Primitives.isWrapperType(object.getClass())) {
			return false;
		}
		
		if (object instanceof Enum<?>) {
			return false;
		}

		if (object instanceof String) {
			return StringUtils.isEmpty((String) object);
		}
		
		if (object instanceof Collection) {
			return ((Collection<?>) object).size() == 0;
		}
		
		if (object instanceof Iterable) {
			return !((Iterable<?>) object).iterator().hasNext();
		}

		if (object instanceof Iterator<?>) {
			return !((Iterator<?>) object).hasNext();
		}
		
		if (object instanceof Map) {
			return ((Map<?, ?>) object).size() == 0;
		}
		
		if (object instanceof Multimap) {
			return ((Multimap<?, ?>) object).size() == 0;
		}
		
		if (object.getClass().isArray()) {
			return Array.getLength(object) == 0;
		}
		
		if (object instanceof BitSet) {
			return ((BitSet) object).isEmpty();
		}
		
		if (object instanceof Pair<?, ?>) {
			return null == ((Pair<?, ?>) object).getA() || null == ((Pair<?, ?>) object).getB();
		}
		
		if (object instanceof Stack) {
			return ((Stack<?>) object).isEmpty();
		}
		
		if (object instanceof PrimitiveCollection) {
			return ((PrimitiveCollection) object).isEmpty();
		}
		
		throw new IllegalArgumentException("Don't know how to check emptiness of " + object.getClass());
	}
}