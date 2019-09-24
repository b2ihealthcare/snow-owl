/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.typedproperty;

/**
 * FHIR typed property, designated as propertyName[x] in the specification.
 * A typed property is serialized into:
 * <pre>
 * {
 *    "name": "propertyName",
 *    "value[x]": value
 *  }
 * </pre>
 * 
 * @see <a href=" https://www.hl7.org/fhir/formats.html">Choice</a> for further information about how to use [x].
 * @since 7.1
 */
public abstract class TypedProperty<T> {
	
	protected T value;
	
	TypedProperty(T value) {
		this.value = value;
	}
	
	/**
	 * Returns the value of this property
	 * @return
	 */
	public T getValue() {
		return value;
	}
	
	/**
	 * Returns the human-readable representation
	 * of the value of this property.
	 * @return
	 */
	public String getValueString() {
		return value.toString();
	}
	
	/**
	 * Returns the serialized name of this property
	 * @return
	 */
	public String getTypeName() {
		return value.getClass().getSimpleName();
	}
	
	@Override
	public String toString() {
		return getTypeName() + " : " + value;
	}

}
