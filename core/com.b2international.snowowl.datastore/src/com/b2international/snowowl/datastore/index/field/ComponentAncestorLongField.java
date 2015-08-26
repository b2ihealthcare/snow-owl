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

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.LongField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

import bak.pcj.set.LongOpenHashSet;
import bak.pcj.set.LongSet;

import com.b2international.snowowl.datastore.index.IndexUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * @since 4.3
 */
public class ComponentAncestorLongField extends ComponentAncestorField {

	public static final long ROOT_ID = -1L;
	public static final ComponentAncestorLongField ROOT_PARENT = new ComponentAncestorLongField(ROOT_ID);
	
	private long value;
	
	public ComponentAncestorLongField(String value) {
		this(COMPONENT_ANCESTOR, Long.parseLong(value));
	}
	
	public ComponentAncestorLongField(long value) {
		this(COMPONENT_ANCESTOR, value);
	}
	
	public ComponentAncestorLongField(String fieldName, long value) {
		super(fieldName);
		this.value = value;
	}

	@Override
	protected BytesRef toBytesRef() {
		return IndexUtils.longToPrefixCoded(value);
	}

	@Override
	protected IndexableField toField() {
		return new LongField(getFieldName(), value, Store.YES);
	}

	public static Collection<Long> getValues(Document document) {
		final Builder<Long> parents = ImmutableList.builder();
		for (IndexableField field : document.getFields(COMPONENT_ANCESTOR)) {
			final long value = IndexUtils.getLongValue(field);
			if (!isRoot(value)) {
				parents.add(value);
			}
		}
		return parents.build();
	}

	public static LongSet getLongSet(Document doc) {
		final IndexableField[] fields = doc.getFields(COMPONENT_ANCESTOR);
		final LongSet longIds = new LongOpenHashSet(fields.length + 1);
		for (final IndexableField field : fields) {
			final long value = IndexUtils.getLongValue(field);
			if (!isRoot(value)) {
				longIds.add(value);
			}
		}
		return longIds;
	}
	
	public static boolean isRoot(long parent) {
		return ROOT_ID == parent;
	}
	
}
