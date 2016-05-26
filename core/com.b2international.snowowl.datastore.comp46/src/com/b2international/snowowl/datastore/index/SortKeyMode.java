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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.util.BytesRef;

import com.b2international.index.lucene.Fields;
import com.b2international.snowowl.core.api.index.CommonIndexConstants;

/**
 * Enumerates different sort key modes.  
 */
public enum SortKeyMode {
	
	INSTANCE;

	/**
	 * Registers sort key field(s) on the specified document. The source label is transformed to lower case, and all characters with diacritical marks
	 * will be replaced by their "plain" counterparts.
	 * <p>
	 * Depending on the actual sort key chosen, this method may add an indexed {@link StringField}, a {@link SortedDocValuesField}, or both.
	 * 
	 * @param doc the document to add the sort key field(s) to
	 * @param label the source label to use 
	 */
	public void update(final Document doc, final String label) {
		final String value = IndexUtils.getSortKey(label);
		Fields.stringField(CommonIndexConstants.COMPONENT_LABEL_SORT_KEY).addTo(doc, value);
		doc.add(new SortedDocValuesField(CommonIndexConstants.COMPONENT_LABEL_SORT_KEY, new BytesRef(value)));
	}

}
