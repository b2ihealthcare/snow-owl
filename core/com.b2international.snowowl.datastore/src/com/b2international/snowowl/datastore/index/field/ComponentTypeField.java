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

import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;

import com.b2international.snowowl.datastore.index.IndexUtils;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.3
 */
public class ComponentTypeField extends IndexField {
	
	public static final String COMPONENT_TYPE = "component_type";
	public static final Set<String> FIELDS_TO_LOAD = ImmutableSet.<String>builder().add(COMPONENT_TYPE).build();
	
	private int componentType;

	public ComponentTypeField(short componentType) {
		this((int) componentType);
	}
	
	public ComponentTypeField(int componentType) {
		this.componentType = componentType;
	}

	@Override
	public Query toQuery() {
		return new TermQuery(new Term(COMPONENT_TYPE, IndexUtils.intToPrefixCoded(componentType)));
	}
	
	@Override
	protected BytesRef toBytesRef() {
		return null;
	}
	
	@Override
	protected IndexableField toField() {
		return new IntField(COMPONENT_TYPE, componentType, Store.YES);
	}

	public static int getInt(Document doc) {
		return IndexUtils.getIntValue(doc.getField(COMPONENT_TYPE));
	}

	public static short getShort(Document doc) {
		return IndexUtils.getShortValue(doc.getField(COMPONENT_TYPE));
	}
	
}
