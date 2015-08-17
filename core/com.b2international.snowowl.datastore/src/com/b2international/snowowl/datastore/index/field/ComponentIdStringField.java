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
package com.b2international.snowowl.datastore.index;

import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.util.BytesRef;

import com.google.common.base.Function;

public class ComponentIdStringField extends ComponentIdField {

	private final String componentId;

	public ComponentIdStringField(String componentId) {
		this.componentId = componentId;
	}
	
	@Override
	public StringField toField() {
		return new StringField(COMPONENT_ID, componentId, Store.YES);
	}
	
	public static Sort createSort() {
		return ComponentIdField.createSort(Type.STRING);
	}

	public static Filter createFilter(String... componentIds) {
		return ComponentIdField.createFilter(new Function<String, BytesRef>() {	@Override public BytesRef apply(String input) {
			return toBytesRef(input);
		}}, componentIds);
	}

	@Override
	protected BytesRef toBytesRef() {
		return toBytesRef(componentId);
	}

	private static BytesRef toBytesRef(String componentId) {
		return new BytesRef(componentId);
	}

}
