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
package com.b2international.snowowl.datastore.index.field;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField.Type;
import org.apache.lucene.util.BytesRef;

import com.b2international.snowowl.datastore.index.IndexUtils;
import com.google.common.base.Function;

public class ComponentIdLongField extends ComponentIdField {

	private final long componentId;

	public ComponentIdLongField(String componentId) {
		this(Long.parseLong(componentId));
	}
	
	public ComponentIdLongField(long componentId) {
		this.componentId = componentId;
	}
	
	@Override
	public void addTo(Document doc) {
		super.addTo(doc);
		doc.add(toDocValuesField());
	}
	
	private NumericDocValuesField toDocValuesField() {
		return new NumericDocValuesField(COMPONENT_ID, componentId);
	}
	
	@Override
	public LongField toField() {
		return new LongField(COMPONENT_ID, componentId, Store.YES);
	}
	
	public static Sort createSort() {
		return ComponentIdField.createSort(Type.LONG);
	}

	public static Filter createFilter(String... componentIds) {
		return ComponentIdField.createFilter(new Function<String, BytesRef>() {	@Override public BytesRef apply(String input) {
			return toBytesRef(Long.parseLong(input));
		}}, componentIds);
	}

	@Override
	protected BytesRef toBytesRef() {
		return toBytesRef(componentId);
	}

	private static BytesRef toBytesRef(long componentId) {
		return IndexUtils.longToPrefixCoded(componentId);
	}

	public static NumericDocValues getNumericDocValues(AtomicReader leafReader) throws IOException {
		return leafReader.getNumericDocValues(COMPONENT_ID);
	}

	public static long getLong(Document doc) {
		return IndexUtils.getLongValue(doc.getField(COMPONENT_ID));
	}
}
