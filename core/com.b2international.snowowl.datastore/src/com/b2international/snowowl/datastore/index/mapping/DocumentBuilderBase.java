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
package com.b2international.snowowl.datastore.index.mapping;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.datastore.index.DocumentUpdater;
import com.b2international.snowowl.datastore.index.SortKeyMode;

/**
 * @since 4.3
 */
public abstract class DocumentBuilderBase<T extends DocumentBuilderBase<T>> {

	/**
	 * @since 4.3
	 */
	public static final class DocumentBuilder extends DocumentBuilderBase<DocumentBuilder> {
		
		public DocumentBuilder() {
			super();
		}
		
		public DocumentBuilder(Document doc) {
			super(doc);
		}
		
		@Override
		protected DocumentBuilder getSelf() {
			return this;
		}
	}
	
	private Document doc;
	
	protected DocumentBuilderBase() {
		this(new Document());
	}
	
	protected DocumentBuilderBase(Document doc) {
		this.doc = checkNotNull(doc, "doc");
	}
	
	public final T clear() {
		this.doc = new Document();
		return getSelf();
	}
	
	public T id(String value) {
		return addToDoc(Mappings.id(), value);
	}
	
	public T type(Short value) {
		return type(value.intValue());
	}
	
	public T type(Integer value) {
		return addToDoc(Mappings.type(), value);
	}
	
	public T storageKey(Long value) {
		return addToDoc(Mappings.storageKey(), value);
	}
	
	public T compareUniqueKey(Long value) {
		return addToDoc(Mappings.compareUniqueKey(), value);
	}
	
	public T compareIgnoreUniqueKey(Long value) {
		return addToDoc(Mappings.compareIgnoreUniqueKey(), value);
	}
	
	public T parent(String value) {
		return addToDoc(Mappings.parent(), value);
	}
	
	public T ancestor(String value) {
		return addToDoc(Mappings.ancestor(), value);
	}
	
	public T iconId(String value) {
		return addToDoc(Mappings.iconId(), value);
	}
	
	public T label(String value) {
		return update(Mappings.label(), value);
	}
	
	public T field(String fieldName, String value) {
		return addToDoc(Mappings.stringField(fieldName), value);
	}
	
	public T field(String fieldName, Integer value) {
		return addToDoc(Mappings.intField(fieldName), value);
	}
	
	public T field(String fieldName, Long value) {
		return addToDoc(Mappings.longField(fieldName), value);
	}
	
	public T field(String fieldName, Boolean value) {
		return addToDoc(Mappings.boolField(fieldName), value);
	}
	
	public T update(String fieldName, Long value) {
		return update(Mappings.longField(fieldName), value);
	}
	
	public T update(String fieldName, String value) {
		return update(Mappings.stringField(fieldName), value);
	}
	
	public T update(String fieldName, Integer value) {
		return update(Mappings.intField(fieldName), value);
	}
	
	public T update(String fieldName, Boolean value) {
		return update(Mappings.boolField(fieldName), value);
	}
	
	public T tokenizedField(String fieldName, String value) {
		return addToDoc(Mappings.textField(fieldName), value);
	}
	
	public T docValuesField(String fieldName, Long value) {
		return addToDoc(Mappings.longDocValuesField(fieldName), value);
	}
	
	public T docValuesField(String fieldName, String value) {
		return addToDoc(Mappings.stringDocValuesField(fieldName), value);
	}
	
	public T docValuesField(String fieldName, Float value) {
		return addToDoc(Mappings.floatDocValuesField(fieldName), value);
	}
	
	public T textDocValuesField(String fieldName, String value) {
		return addToDoc(new DocValuesTextIndexField(fieldName), value);
	}
	
	public T tokenizedArray(String fieldName, Collection<String> terms) {
		for (String term : terms) {
			tokenizedField(fieldName, term);
		}
		return getSelf();
	}
	
	public final <F> T addToDoc(IndexField<F> field, F value) {
		field.addTo(doc, value);
		return getSelf();
	}
	
	public <F> T update(IndexField<F> field, F value) {
		return removeAll(field).addToDoc(field, value);
	}
	
	public final <F> T removeAll(IndexField<F> field) {
		field.removeAll(doc);
		return getSelf();
	}
	
	public T labelWithSort(String value) {
		label(value);
		SortKeyMode.INSTANCE.update(this, value);
		return getSelf();
	}
	
	public <F> T array(IndexField<F> field, List<F> terms) {
		for (F term : terms) {
			addToDoc(field, term);
		}
		return getSelf();
	}
	
	public T storedOnly(String fieldName, int value) {
		return addToDoc(Mappings.storedOnlyIntField(fieldName), value);
	}
	
	public T storedOnly(String fieldName, String value) {
		return addToDoc(Mappings.storedOnlyStringField(fieldName), value);
	}
	
	public T storedOnly(String fieldName, Long value) {
		return addToDoc(Mappings.storedOnlyLongField(fieldName), value);
	}
	
	public T storedOnly(String fieldName, Float value) {
		return addToDoc(Mappings.storedOnlyFloatField(fieldName), value);
	}
	
	public T storedOnlyWithDocValues(String fieldName, Integer value) {
		return addToDoc(Mappings.storedOnlyIntFieldWithDocValues(fieldName), value);
	}
	
	public T storedOnlyWithDocValues(String fieldName, Long value) {
		return addToDoc(Mappings.storedOnlyLongFieldWithDocValues(fieldName), value);
	}
	
	public final Document build() {
		// TODO build the doc here, and register field - value entries while building
		return doc;
	}
	
	public T searchOnlyField(String fieldName, int value) {
		return addToDoc(Mappings.searchOnlyIntField(fieldName), value);
	}

	public T searchOnlyField(String fieldName, long value) {
		return addToDoc(Mappings.searchOnlyLongField(fieldName), value);
	}

	public T searchOnlyField(String fieldName, String value) {
		return addToDoc(Mappings.searchOnlyStringField(fieldName), value);
	}
	
	protected abstract T getSelf();
	
	public final T with(DocumentUpdater<T> updater) {
		updater.update(getSelf());
		return getSelf();
	}
	
	protected final int toIntValue(boolean value) {
		return value ? 1 : 0;
	}
}
