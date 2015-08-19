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

import java.util.Collections;
import java.util.Set;

import org.apache.lucene.document.Document;

/**
 * @since 4.3
 */
public class ComponentTypeField extends IntIndexField {
	
	public static final String COMPONENT_TYPE = "component_type";
	public static final Set<String> FIELDS_TO_LOAD = Collections.singleton(COMPONENT_TYPE);
	
	public ComponentTypeField(short value) {
		super(COMPONENT_TYPE, value);
	}
	
	public ComponentTypeField(int value) {
		super(COMPONENT_TYPE, value);
	}

	public static int getInt(Document doc) {
		return IntIndexField.getInt(doc, COMPONENT_TYPE);
	}

	public static short getShort(Document doc) {
		return IntIndexField.getShort(doc, COMPONENT_TYPE);
	}
	
}
