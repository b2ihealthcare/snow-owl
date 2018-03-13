/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Maps.newHashMapWithExpectedSize;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.search.SearchHit;

import com.b2international.commons.CompareUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Primitives;

/**
 * @since 6.4
 */
public interface HitConverter<T> {

	T convert(SearchHit hit) throws IOException;
	
	final class FieldValueHitConverter<T> implements HitConverter<T> {

		private final Class<T> select;

		private FieldValueHitConverter(Class<T> select) {
			this.select = select;
		}
		
		@Override
		public T convert(SearchHit hit) {
			final Map<String, DocumentField> source = hit.getFields();
			if (CompareUtils.isEmpty(source)) {
				return select.cast(hit.getId());
			} else {
				Object fieldValue = source.get(Iterables.getOnlyElement(source.keySet())).<T>getValue();
				if (Integer.class == select && fieldValue instanceof Long) {
					return select.cast(Integer.valueOf(Ints.checkedCast(((Long) fieldValue).longValue())));
				} else {
					return select.cast(fieldValue);
				}
			}
		}
	}
	
	final class SourceAsMapHitConverter<T> implements HitConverter<T> {

		private final Class<T> select;

		private SourceAsMapHitConverter(Class<T> select) {
			this.select = select;
		}
		
		@Override
		public T convert(SearchHit hit) {
			return select.cast(hit.getSourceAsMap());
		}
		
	}
	
	final class FieldsAsMapHitConverter<T> implements HitConverter<T> {
		
		private final Class<T> select;

		private FieldsAsMapHitConverter(Class<T> select) {
			this.select = select;
		}
		
		@Override
		public T convert(SearchHit hit) {
			final Map<String, Object> val = newHashMapWithExpectedSize(hit.getFields().size());
			hit.getFields().forEach((field, docField) -> {
				val.put(field, docField.getValue());
			});
			return select.cast(val);
		}
		
	}
	
	final class SourceAsStringArrayHitConverter<T> implements HitConverter<T> {
		
		private final Class<T> select;
		private final List<String> fields;

		private SourceAsStringArrayHitConverter(Class<T> select, List<String> fields) {
			this.select = select;
			this.fields = fields;
		}
		
		@Override
		public T convert(SearchHit hit) {
			final Map<String, Object> source = hit.getSourceAsMap();
			final String[] val = new String[fields.size()];
			for (int i = 0; i < fields.size(); i++) {
				String field = fields.get(i);
				val[i] = String.valueOf(source.get(field));
			}
			return select.cast(val);
		}
		
	}
	
	final class FieldsAsStringArrayHitConverter<T> implements HitConverter<T> {
		
		private final Class<T> select;
		private final List<String> fields;

		private FieldsAsStringArrayHitConverter(Class<T> select, List<String> fields) {
			this.select = select;
			this.fields = fields;
		}
		
		@Override
		public T convert(SearchHit hit) {
			final String[] val = new String[fields.size()];
			for (int i = 0; i < fields.size(); i++) {
				String field = fields.get(i);
				DocumentField docField = hit.getFields().get(field);
				if (docField != null) {
					Object fieldValue = docField.getValue();
					val[i] = String.valueOf(fieldValue);
				} else {
					val[i] = null;							
				}
			}
			return select.cast(val);
		}
		
	}
	
	final class SourceAsObjectHitConverter<T> implements HitConverter<T> {
		
		private final ObjectReader reader;

		private SourceAsObjectHitConverter(ObjectReader reader) {
			this.reader = reader;
		}
		
		@Override
		public T convert(SearchHit hit) throws IOException {
			final byte[] bytes = BytesReference.toBytes(hit.getSourceRef());
			return reader.readValue(bytes, 0, bytes.length);
		}
		
	}
	
	final class FieldsAsObjectHitConverter<T> implements HitConverter<T> {

		private final ObjectMapper mapper;
		private final Class<T> select;

		private FieldsAsObjectHitConverter(ObjectMapper mapper, Class<T> select) {
			this.mapper = mapper;
			this.select = select;
		}
		
		@Override
		public T convert(SearchHit hit) {
			final Map<String, Object> val = newHashMapWithExpectedSize(hit.getFields().size());
			hit.getFields().forEach((field, docField) -> {
				val.put(field, docField.getValue());
			});
			return mapper.convertValue(val, select);
		}
		
	}
	
	static <T> HitConverter<T> getConverter(ObjectMapper mapper, Class<T> select, Class<?> from, boolean fetchSource, List<String> fields) {
		if (Primitives.isWrapperType(select) || String.class.isAssignableFrom(select)) {
			checkState(!fetchSource, "Single field fetching is not supported when it requires to load the source of the document.");
			return new FieldValueHitConverter<>(select);
		} else if (Map.class.isAssignableFrom(select)) {
			if (fetchSource) {
				return new SourceAsMapHitConverter<>(select);
			} else {
				return new FieldsAsMapHitConverter<>(select);
			}
		} else if (String[].class.isAssignableFrom(select)) {
			if (fetchSource) {
				return new SourceAsStringArrayHitConverter<>(select, fields);
			} else {
				return new FieldsAsStringArrayHitConverter<>(select, fields);
			}
		} else {
			if (fetchSource) {
				return new SourceAsObjectHitConverter<>(getResultObjectReader(mapper, select, from));
			} else {
				return new FieldsAsObjectHitConverter<>(mapper, select);
			}
		}
	}
	
	static <T> ObjectReader getResultObjectReader(ObjectMapper mapper, Class<T> select, Class<?> from) {
		return select != from 
				? mapper.readerFor(select).without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) 
				: mapper.readerFor(select);
	}
	
}
