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
package com.b2international.index.lucene;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * @since 4.3
 */
public class FieldsToLoadBuilderBase<T extends FieldsToLoadBuilderBase<T>> {

	private final ImmutableSet.Builder<String> fieldsToLoad = ImmutableSet.builder();

	public static final class FieldsToLoadBuilder extends FieldsToLoadBuilderBase<FieldsToLoadBuilder> {}
	
	protected FieldsToLoadBuilderBase() {
	}
	
	public T id() {
		return field(Fields.id());
	}
	
	public T type() {
		return field(Fields.type());
	}
	
	public T parent() {
		return field(Fields.parent());
	}
	
	public T ancestor() {
		return field(Fields.ancestor());
	}

	public T storageKey() {
		return field(Fields.storageKey());
	}
	
	public T label() {
		return field(Fields.label());
	}
	
	public T iconId() {
		return field(Fields.iconId());
	}
	
	public final T field(IndexField<?> field) {
		return field(field.fieldName());
	}
	
	public final T field(String fieldName) {
		fieldsToLoad.add(fieldName);
		return (T) this;
	}
	
	public final T fields(Set<String> fieldNames) {
		fieldsToLoad.addAll(fieldNames);
		return (T) this;
	}
	
	public final Set<String> build() {
		return fieldsToLoad.build();
	}

}
