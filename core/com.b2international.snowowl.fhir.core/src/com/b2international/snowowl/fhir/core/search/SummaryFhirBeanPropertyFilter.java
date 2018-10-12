/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.search;

import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;

/**
 * Property filter to the summary elements
 * @since 6.4
 *
 */
public class SummaryFhirBeanPropertyFilter extends FhirBeanPropertyFilter {

	@Override
	protected boolean include(BeanPropertyWriter writer) {
		return true;
	}
	
	@Override
	protected boolean include(PropertyWriter writer) {
		
		Mandatory mandatoryAnnotation = writer.findAnnotation(Mandatory.class);
		
		if (mandatoryAnnotation!=null) {
			return true;
		}
		
		Summary summaryAnnotation = writer.findAnnotation(Summary.class);
		
		if (summaryAnnotation!=null) {
			return true;
		}
		return false;
	}

}
