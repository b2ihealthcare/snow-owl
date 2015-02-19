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
package com.b2international.snowowl.snomed.mrcm.core.widget.model;

import java.io.Serializable;


/**
 * Represents a simple map member in the WidgetModel hierarchy.
 * 
 */
public class MappingWidgetModel extends WidgetModel implements Serializable {

	private static final long serialVersionUID = -1183437795711994075L;

	private String allowedTerminologyComponentId;

	private MappingWidgetModel(LowerBound lowerBound, UpperBound upperBound, ModelType modelType,
			String allowedTerminologyComponentId) {
		super(lowerBound, upperBound, modelType);
		this.allowedTerminologyComponentId = allowedTerminologyComponentId;
	}
	
	public String getAllowedTerminologyComponentId() {
		return allowedTerminologyComponentId;
	}
	
	/**
	 * Default constructor for serialization.
	 */
	protected MappingWidgetModel() {
	}	
	
	/**
	 * Creates an unsanctioned mapping model with 0..* cardinality and the terminology type id.
	 * 
	 * @param allowedTerminologyId the allowed mapping target component type id (may not be {@code null}
	 * @return the created mapping model instance
	 */
	public static MappingWidgetModel createUnsanctionedModel(final String allowedTerminologyId) {
		return new MappingWidgetModel(LowerBound.OPTIONAL, UpperBound.MULTIPLE, ModelType.UNSANCTIONED, allowedTerminologyId);
	}
	
	public boolean matches(final String terminologyId) {
		return allowedTerminologyComponentId.equals(terminologyId);
	}
	
	@Override
	public String toString() {
		return String.format(
				"MappingWidgetModel [\n"
				+ "            allowedTerminologyComponentId=%s\n"
				+ "        ]", 
				allowedTerminologyComponentId);
	}

}