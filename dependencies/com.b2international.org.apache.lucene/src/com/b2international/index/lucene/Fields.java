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

import com.b2international.index.lucene.FieldsToLoadBuilderBase.FieldsToLoadBuilder;
import com.b2international.index.lucene.QueryBuilderBase.QueryBuilder;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * @since 4.3
 */
public class Fields {
	
	public static final String ROOT_ID_STRING = "ROOT";

	private static final String COMPONENT_ID_FIELD_NAME = "component_id";
	private static final String COMPONENT_TYPE_FIELD_NAME = "component_type";
	private static final String COMPONENT_STORAGEKEY_FIELD_NAME = "component_storage_key";
	private static final String COMPONENT_PARENT_FIELD_NAME = "concept_parent_id";
	private static final String COMPONENT_ANCESTOR_FIELD_NAME = "concept_ancestor_id";
	private static final String COMPONENT_LABEL_FIELD_NAME = "component_label";
	private static final String COMPONENT_ICON_ID_FIELD_NAME = "component_icon_id";
	private static final String COMPONENT_COMPARE_UNIQUE_KEY_FIELD_NAME = "component_compare_unique_key";
	private static final String COMPONENT_IGNORE_COMPARE_UNIQUE_KEY_FIELD_NAME = "component_ignore_compare_unique_key";
	
	private static final IndexField<String> COMPONENT_ID = stringField(COMPONENT_ID_FIELD_NAME);
	private static final IntIndexField COMPONENT_TYPE = new IntIndexField(COMPONENT_TYPE_FIELD_NAME);
	private static final NumericDocValuesIndexField<Long> COMPONENT_STORAGE_KEY = longDocValuesField(COMPONENT_STORAGEKEY_FIELD_NAME);
	private static final IndexField<String> COMPONENT_PARENT = filteredField(stringField(COMPONENT_PARENT_FIELD_NAME), Predicates.not(Predicates.equalTo(ROOT_ID_STRING)));
	private static final IndexField<String> UNFILTERED_COMPONENT_PARENT = stringField(COMPONENT_PARENT_FIELD_NAME); // TODO move to proper bundle
	private static final IndexField<String> COMPONENT_ANCESTOR = filteredField(stringField(COMPONENT_ANCESTOR_FIELD_NAME), Predicates.not(Predicates.equalTo(ROOT_ID_STRING)));
	private static final BinaryDocValuesIndexField COMPONENT_LABEL = new DocValuesTextIndexField(COMPONENT_LABEL_FIELD_NAME);
	private static final IndexField<String> COMPONENT_ICON_ID = stringField(COMPONENT_ICON_ID_FIELD_NAME);
	private static final NumericDocValuesIndexField<Long> COMPONENT_COMPARE_UNIQUE_KEY = longDocValuesField(COMPONENT_COMPARE_UNIQUE_KEY_FIELD_NAME);
	private static final NumericDocValuesIndexField<Long> COMPONENT_IGNORE_COMPARE_UNIQUE_KEY = longDocValuesField(COMPONENT_IGNORE_COMPARE_UNIQUE_KEY_FIELD_NAME);
	
	public static LongCollectionIndexField filteredLongField(LongIndexField field, Predicate<? super Long> predicate) {
		return new FilteredLongIndexField(field, predicate);
	}
	
	public static <T> IndexField<T> filteredField(IndexField<T> field, Predicate<? super T> predicate) {
		return new FilteredIndexField<>(field, predicate);
	}
	
	public static IntIndexField type() {
		return COMPONENT_TYPE;
	}
	
	public static IndexField<String> id() {
		return COMPONENT_ID;
	}
	
	public static IndexField<String> parent() {
		return COMPONENT_PARENT;
	}
	
	public static IndexField<String> unfilteredParent() {
		return UNFILTERED_COMPONENT_PARENT;
	}
	
	public static IndexField<String> ancestor() {
		return COMPONENT_ANCESTOR;
	}
	
	public static NumericDocValuesIndexField<Long> storageKey() {
		return COMPONENT_STORAGE_KEY;
	}
	
	public static BinaryDocValuesIndexField label() {
		return COMPONENT_LABEL;
	}
	
	public static IndexField<String> iconId() {
		return COMPONENT_ICON_ID;
	}
	
	public static NumericDocValuesIndexField<Long> compareUniqueKey() {
		return COMPONENT_COMPARE_UNIQUE_KEY;
	}
	
	public static NumericDocValuesIndexField<Long> compareIgnoreUniqueKey() {
		return COMPONENT_IGNORE_COMPARE_UNIQUE_KEY;
	}
	
	public static QueryBuilder newQuery() {
		return new QueryBuilder();
	}
	
	public static FieldsToLoadBuilder fieldsToLoad() {
		return new FieldsToLoadBuilder();
	}
	
	public static boolean isRoot(String parent) {
		return ROOT_ID_STRING.equals(parent);
	}
	
	public static IndexField<String> textField(String fieldName) {
		return new TextIndexField(fieldName);
	}
	
	public static IndexField<String> stringField(String fieldName) {
		return new StringIndexField(fieldName);
	}
	
	public static IntIndexField intField(String fieldName) {
		return new IntIndexField(fieldName);
	}
	
	public static LongIndexField longField(String fieldName) {
		return new LongIndexField(fieldName);
	}
	
	public static IndexField<Boolean> boolField(String fieldName) {
		return new BooleanIndexField(fieldName);
	}
	
	public static IndexField<Float> floatField(String fieldName) {
		return new FloatIndexField(fieldName);
	}
	
	public static NumericDocValuesIndexField<Long> longDocValuesField(String fieldName) {
		return new DocValuesLongIndexField(fieldName);
	}
	
	public static IndexField<Float> floatDocValuesField(String fieldName) {
		return new DocValuesFloatIndexField(fieldName);
	}
	
	public static NumericDocValuesIndexField<Integer> intDocValuesField(String fieldName) {
		return new DocValuesIntIndexField(fieldName);
	}

	public static StoredIndexField<Integer> storedOnlyIntField(String fieldName) {
		return new StoredOnlyIndexField<>(intField(fieldName));
	}
	
	public static StoredIndexField<Long> storedOnlyLongField(String fieldName) {
		return new StoredOnlyIndexField<>(longField(fieldName));
	}
	
	public static StoredIndexField<String> storedOnlyStringField(String fieldName) {
		return new StoredOnlyIndexField<>(stringField(fieldName));
	}
	
	public static StoredIndexField<Float> storedOnlyFloatField(String fieldName) {
		return new StoredOnlyIndexField<>(floatField(fieldName));
	}
	
	public static IndexField<Integer> searchOnlyIntField(String fieldName) {
		return new IntIndexField(fieldName, false);
	}
	
	public static IndexField<Boolean> searchOnlyBoolField(String fieldName) {
		return new BooleanIndexField(fieldName, false);
	}
	
	public static IndexField<Long> searchOnlyLongField(String fieldName) {
		return new LongIndexField(fieldName, false);
	}
	
	public static IndexField<Float> searchOnlyFloatField(String fieldName) {
		return new FloatIndexField(fieldName, false);
	}
	
	public static IndexField<String> searchOnlyStringField(String fieldName) {
		return new StringIndexField(fieldName, false);
	}
	
	public static IndexField<String> searchOnlyTextField(String fieldName) {
		return new TextIndexField(fieldName, false);
	}
	
	public static NumericDocValuesIndexField<Integer> storedOnlyIntFieldWithDocValues(String fieldName) {
		return new StoredOnlyDocValuesLongIndexField<>(Fields.storedOnlyIntField(fieldName));
	}

	public static NumericDocValuesIndexField<Long> storedOnlyLongFieldWithDocValues(String fieldName) {
		return new StoredOnlyDocValuesLongIndexField<>(Fields.storedOnlyLongField(fieldName));
	}
	
	public static BinaryDocValuesIndexField stringDocValuesField(String fieldName) {
		return new DocValuesStringIndexField(fieldName);
	}
	
	public static IndexField<Object> none() {
		return EmptyIndexField.INSTANCE;
	}

}
