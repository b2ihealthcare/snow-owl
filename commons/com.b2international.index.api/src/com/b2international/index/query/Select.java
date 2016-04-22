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
package com.b2international.index.query;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

/**
 * @since 4.7
 */
abstract public class Select {

	public interface EmptyBuilder {
		BuildableBuilder fields(String... fields);
		BuildableBuilder nestedObject(String path);
	}
	
	public interface BuildableBuilder extends EmptyBuilder, Buildable<Select> {}
	
	private static final class BuilderImpl implements BuildableBuilder {

		private final List<Select> items = Lists.newArrayList();
		
		public BuildableBuilder fields(String... fields) {
			items.add(new Fields(fields));
			return this;
		}

		public BuildableBuilder nestedObject(String path) {
			items.add(new NestedObject(path));
			return this;
		}
		
		public Select build() {
			if (items.isEmpty()) {
				throw new IllegalStateException("Select clause must not be empty.");
			} else if (items.size() == 1) {
				return items.get(0);
			} else {
				return new Multiple(items);
			}
		}
	}
	
	public static final class Count extends Select {
		@Override
		public String toString() {
			return "COUNT";
		}
	}
	
	public static final class All extends Select {
		@Override
		public String toString() {
			return "ALL";
		}
	}

	public static final class Fields extends Select {
		private final List<String> fields;

		Fields(String... fields) {
			this.fields = Arrays.asList(checkNotNull(fields, "fields"));
		}

		public List<String> getFields() {
			return fields;
		}
		
		@Override
		public String toString() {
			return Joiner.on(", ").join(fields);
		}
	}
	
	public static final class NestedObject extends Select {
		private final String path;

		NestedObject(String path) {
			this.path = checkNotNull(path, "path");
		}
		
		public String getPath() {
			return path;
		}
		
		@Override
		public String toString() {
			// TODO: better notation?
			return path + ".*";
		}
	}

	public static final class Multiple extends Select {
		private final List<Select> items;

		public Multiple(List<Select> items) {
			this.items = ImmutableList.copyOf(checkNotNull(items, "items"));
		}

		public List<Select> getItems() {
			return items;
		}
		
		@Override
		public String toString() {
			return Joiner.on(", ").join(items);
		}
	}
	
	private static final Select ALL = new All();
	private static final Select COUNT = new Count();
	
	public static Select count() {
		return COUNT;
	}

	public static Select all() {
		return ALL;
	}
	
	public static Select nestedObject(String path) {
		return new NestedObject(path);
	}

	public static Select fields(String... fields) {
		return new Fields(fields);
	}
	
	public static EmptyBuilder builder() {
		return new BuilderImpl();
	}
	
}