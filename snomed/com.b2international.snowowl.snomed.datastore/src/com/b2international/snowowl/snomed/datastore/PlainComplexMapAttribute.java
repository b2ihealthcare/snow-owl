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
package com.b2international.snowowl.snomed.datastore;

import javax.annotation.Nullable;

/**
 * Represents a plain text SNOMED CT complex map type attribute.
 */
public class PlainComplexMapAttribute implements IComplexMapAttribute {

	private final String label;

	/**
	 * Creates a new instance.
	 * @param label the label of the element can be {@code null}.
	 */
	public PlainComplexMapAttribute(@Nullable final String label) {
		this.label = label;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.snomed.datastore.IComplexMapAttribute#setAttribute(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setAttribute(final Object key, final Object value) {
		//intentionally does nothing
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.valueOf(label);
	}
	
}