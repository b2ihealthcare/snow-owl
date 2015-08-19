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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.util.BytesRef;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;

/**
 * @since 4.3
 */
public class ComponentParentStringField extends ComponentParentField {

	public static final String ROOT_ID = "ROOT";
	public static final ComponentParentStringField ROOT_PARENT = new ComponentParentStringField(ROOT_ID);
	
	private String parentId;
	
	public ComponentParentStringField(String parentId) {
		checkArgument(!Strings.isNullOrEmpty(parentId), "ParentId cannot be null or empty");
		this.parentId = parentId;
	}
	
	@Override
	protected IndexableField toField() {
		checkState(parentId != null, "Parent ID should not be null at this point");
		return new StringField(getFieldName(), parentId, Store.YES);
	}

	@Override
	protected BytesRef toBytesRef() {
		return new BytesRef(parentId);
	}

	public static Collection<String> getValues(Document doc) {
		final IndexableField[] fields = doc.getFields(COMPONENT_PARENT);
		final Builder<String> parents = ImmutableList.builder();
		for (IndexableField field : fields) {
			final String id = field.stringValue();
			if (!isRoot(id)) {
				parents.add(id);
			}
		}
		return parents.build();
	}

	public static String getValue(Document doc) {
		return Iterables.getOnlyElement(getValues(doc));
	}

	public static boolean isRoot(String parent) {
		return ROOT_ID.equals(parent);
	}
}
