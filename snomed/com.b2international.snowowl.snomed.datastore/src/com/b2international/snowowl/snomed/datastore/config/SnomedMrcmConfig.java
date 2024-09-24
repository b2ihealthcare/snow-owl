/*
 * Copyright 2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.datastore.config;

import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;

/**
 * @since 9.3.0
 */
public class SnomedMrcmConfig {
	
	private String allowedDataAttributesExpression = "<" + Concepts.CONCEPT_MODEL_DATA_ATTRIBUTE;
	private String allowedObjectAttributesExpression = "<" + Concepts.CONCEPT_MODEL_OBJECT_ATTRIBUTE;
	
	
	public String getAllowedDataAttributesExpression() {
		return allowedDataAttributesExpression;
	}
	
	public void setAllowedDataAttributesExpression(String allowedDataAttributesExpression) {
		this.allowedDataAttributesExpression = allowedDataAttributesExpression;
	}
	
	public String getAllowedObjectAttributesExpression() {
		return allowedObjectAttributesExpression;
	}
	
	public void setAllowedObjectAttributesExpression(String allowedObjectAttributesExpression) {
		this.allowedObjectAttributesExpression = allowedObjectAttributesExpression;
	}
	
}
