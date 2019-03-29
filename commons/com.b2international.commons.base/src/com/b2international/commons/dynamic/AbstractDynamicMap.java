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
package com.b2international.commons.dynamic;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public abstract class AbstractDynamicMap extends AbstractDynamicValue implements DynamicMap {

	protected static final Function<Object, DynamicValue> valueConverter = new Function<Object, DynamicValue>() {
		
		@Override
		public DynamicValue apply(Object input) {

			if (input instanceof DynamicValue) {
				return (DynamicValue) input;
			} else if (input instanceof List) {
				return new DynamicListImpl((List<?>) input);
			} else if (input instanceof Map) {
				return new DynamicMapImpl((Map<?, ?>) input);
			} else {			
				return DynamicValueImpl.create(input);
			}
		}
	};
	
	public static final class EntryImpl implements DynamicMap.Entry {

		private final String propertyKey;
		private final DynamicValue value;
		
		public EntryImpl(String key, DynamicValue value) {
			this.propertyKey = key;
			this.value = value;
		}
		
		@Override
		public String getPropertyKey() {
			return propertyKey;
		}

		@Override
		public DynamicValue getValue() {
			return value;
		}
		
		@Override
		public boolean equals(Object o) {

			if (!(o instanceof EntryImpl)) {
				return false;
			}
			
			EntryImpl otherEntry = (EntryImpl) o;
			
			return Objects.equal(this.getPropertyKey(), otherEntry.getPropertyKey()) &&
					Objects.equal(this.getValue(), otherEntry.getValue());
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(propertyKey, value);
		}
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this).add("propertyKey", propertyKey).add("value", value).toString();
		}
	}
	
	@Override
	public Object get() {
		return this;
	}

	@Override
	public DynamicValue get(String key) {
		return containsKey(key) ? get(key, null) : DynamicValue.MISSING;
	}

	@Override
	public DynamicValue getFirstPropertyValue(String property) {
		Entry firstEntry = Iterables.getFirst(getProperty(property), null);
		return (firstEntry == null) ? DynamicValue.MISSING : firstEntry.getValue();
	}
	
	@Override
	public DynamicValue getFirstPropertyValue(String property, Object defaultValue) {
		Entry firstEntry = Iterables.getFirst(getProperty(property), null);
		return (firstEntry == null) ? DynamicValueImpl.create(defaultValue) : firstEntry.getValue();
	}
	
	protected Collection<Entry> getPrefixedFollowers(DynamicMap map, final String prefix, Iterable<String> propertyParts) {
		return getPrefixedFollowers(map, prefix, Joiner.on('.').join(propertyParts));
	}
	
	protected Collection<Entry> getPrefixedFollowers(DynamicMap map, final String prefix, String property) {
		Collection<Entry> followingValues = map.getProperty(property);
		return ImmutableList.copyOf(Collections2.transform(followingValues, new Function<Entry, Entry>() {
			@Override
			public Entry apply(Entry input) {
				return new EntryImpl(prefix + "." + input.getPropertyKey(), input.getValue());
			}
		}));
	}
}