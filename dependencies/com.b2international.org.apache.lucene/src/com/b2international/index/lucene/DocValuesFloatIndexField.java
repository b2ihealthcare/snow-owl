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

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FloatDocValuesField;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.NumericDocValues;

/**
 * @since 4.3
 */
public class DocValuesFloatIndexField extends FloatIndexField implements NumericDocValuesIndexField<Float> {

	public DocValuesFloatIndexField(String fieldName) {
		super(fieldName);
	}
	
	@Override
	public void addTo(Document doc, Float value) {
		super.addTo(doc, value);
		doc.add(toDocValuesField(value));
	}

	@Override
	public NumericDocValuesField toDocValuesField(Float value) {
		return new FloatDocValuesField(fieldName(), value);
	}

	@Override
	public NumericDocValues getDocValues(LeafReader reader) throws IOException {
		return reader.getNumericDocValues(fieldName());
	}
	
}
