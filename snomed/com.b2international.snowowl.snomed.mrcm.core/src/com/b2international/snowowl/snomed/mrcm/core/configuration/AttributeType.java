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
package com.b2international.snowowl.snomed.mrcm.core.configuration;

import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("attributeType")
public enum AttributeType {
	
	DESCRIPTION((short) 101, "Description"),
	RELATIONSHIP((short) 102, "Relationship"),
	CONCRETE_DATA_TYPE((short) 104, "Concrete data type");
	@XStreamAlias("terminologyComponentIdValu")
	private final short terminologyComponentIdValu;
	@XStreamAlias("name")
	private final String name;
	
	private AttributeType(final short terminologyComponentIdValu, final String name) {
		this.terminologyComponentIdValu = terminologyComponentIdValu;
		this.name = name;
	}
	
	public short getTerminologyComponentIdValu() {
		return terminologyComponentIdValu;
	}
	
	public String getTerminologyComponentId() {
		return CoreTerminologyBroker.getInstance().getTerminologyComponentId(terminologyComponentIdValu);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name;
	}
	
	public static AttributeType getByTerminologyComponentIdValue(final short terminologyComponentIdValu) {
		for (final AttributeType type : values()) {
			if (terminologyComponentIdValu == type.getTerminologyComponentIdValu()) {
				return type;
			}
		}
		throw new IllegalArgumentException("Cannot find attribute type for terminology component ID value: " + terminologyComponentIdValu);
	}
	
}