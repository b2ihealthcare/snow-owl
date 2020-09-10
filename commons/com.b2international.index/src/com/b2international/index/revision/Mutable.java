/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.revision;

import java.util.Objects;

import com.b2international.index.IndexException;
import com.b2international.index.mapping.DocumentMapping;
import com.b2international.index.revision.StagingArea.RevisionPropertyDiff;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.10
 */
public interface Mutable {

	default Builder<?, ? extends Mutable> toBuilder() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Applies a collection of updates on this {@link Mutable} object and returns the modified new instance.
	 * 
	 * @param <M> - the expected type to return
	 * @param mapper - the mapper to use for reading specific JSON serialized values from {@link RevisionPropertyDiff}s
	 * @param mapping - the mapping of the mutable object
	 * @param propertyDiffs - the diffs to apply
	 * @return the new mutated instance with all changes applied from the propertyDiffs variable, never <code>null</code>
	 * @throws IndexException - if one of the property diffs cannot be applied
	 */
	default <M extends Mutable> M withUpdates(ObjectMapper mapper, DocumentMapping mapping, Iterable<RevisionPropertyDiff> propertyDiffs) throws IndexException {
		Builder<?, ? extends Mutable> builder = toBuilder();
		for (RevisionPropertyDiff diff : propertyDiffs) {
			try {
				builder = builder._setProperty(mapper, mapping, diff.getProperty(), diff.getNewValue());
			} catch (Exception e) {
				throw new IndexException("Couldn't apply property change update: " + diff, e);
			} 
		}
		return (M) builder.build();
	}
	
	/**
	 * @param <B>
	 * @param <T>
	 */
	interface Builder<B extends Builder<B, T>, T> {
		
		/**
		 * @param mapper
		 * @param mapping
		 * @param property
		 * @param newValue
		 * @return
		 * @throws Exception
		 */
		default B _setProperty(ObjectMapper mapper, DocumentMapping mapping, String property, String newValue) throws Exception {
			Objects.requireNonNull(property);
			if (property.contains("/")) {
				final String nestedPath = property.substring(0, property.indexOf("/"));
				final String nestedPathProperty = property.substring(property.indexOf("/") + 1);
				
				// TODO add recursion for infinite depth
				final Object nestedObjectValue = Mutables.get(getSelf(), nestedPath);
				final Class<?> fieldType = mapping.getNestedMapping(nestedObjectValue.getClass()).getFieldType(property);
				final Object convertedValue = Mutables.readValue(mapper, fieldType, nestedPathProperty, newValue);
				return Mutables.set(getSelf(), property, Mutables.set(nestedObjectValue, property, convertedValue));
			} else {
				final Class<?> fieldType = mapping.getFieldType(property);
				final Object value = Mutables.readValue(mapper, fieldType, property, newValue);
				return Mutables.set(getSelf(), property, value);
			}
		}

		B getSelf();
		
		T build();

	}
	
}
