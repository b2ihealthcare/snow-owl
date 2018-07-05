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

import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.google.common.collect.Lists;

/**
 * Jackson property filter to subset the response.
 * 
 * @since 6.4
 */
public class FhirBeanPropertyFilter implements PropertyFilter {
	
	public static final String FILTER_NAME = "FhirResponseFilter";

	private List<String> requestedFields = Lists.newArrayList();
	
	public static FhirBeanPropertyFilter createFilter(List<String> requestedFields) {
		return new FhirBeanPropertyFilter(requestedFields);
	}
	
	public static FhirBeanPropertyFilter createFilter(SummaryParameter summaryParameter) {
		
		switch (summaryParameter) {
		case FALSE:
			//return everything
			return new IncludeAllFhirBeanPropertyFilter();
		case TRUE:
			//return summary elements
			return new SummaryFhirBeanPropertyFilter();
		case COUNT:
			//return count only. How is this different from the _count?
			return new CountFhirBeanPropertyFilter();
		case DATA:
			//remove the text element
			return new DataFhirBeanPropertyFilter();
		case TEXT:
			//Return only the "text" element, the 'id' element, the 'meta' element, 
			//and only top-level mandatory elements
			return new TextFhirBeanPropertyFilter();
		default:
			throw new UnsupportedOperationException("No property filter is registered for " + summaryParameter);
		}
	}

	/**
	 * @param requestedFields
	 */
	protected FhirBeanPropertyFilter(List<String> requestedFields) {
		if (requestedFields != null) {
			this.requestedFields = requestedFields;
		}
	}
	
	protected FhirBeanPropertyFilter() {
	}

	@Override
	public void serializeAsField(Object pojo, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
		if (include(writer)) {
			writer.serializeAsField(pojo, jgen, provider);
		} else if (!jgen.canOmitFields()) { // since 2.3
			writer.serializeAsOmittedField(pojo, jgen, provider);
		}
	}

	@Override
	public void serializeAsElement(Object elementValue, JsonGenerator jgen, SerializerProvider provider, PropertyWriter writer) throws Exception {
		if (includeElement(elementValue)) {
			writer.serializeAsElement(elementValue, jgen, provider);
		}
	}

	@Deprecated
	@Override
	public void depositSchemaProperty(PropertyWriter writer, ObjectNode propertiesNode, SerializerProvider provider) throws JsonMappingException {
		if (include(writer)) {
			writer.depositSchemaProperty(propertiesNode, provider);
		}
	}

	@Override
	public void depositSchemaProperty(PropertyWriter writer, JsonObjectFormatVisitor objectVisitor, SerializerProvider provider)
			throws JsonMappingException {
		if (include(writer)) {
			writer.depositSchemaProperty(objectVisitor, provider);
		}
	}
	
	/**
	 * Method called to determine whether property will be included (if 'true'
	 * returned) or filtered out (if 'false' returned)
	 */
	protected boolean include(BeanPropertyWriter writer) {
		Mandatory mandatoryAnnotation = writer.findAnnotation(Mandatory.class);
		
		if (mandatoryAnnotation!=null) {
			return true;
		}
		return requestedFields.contains(writer.getName());
	}

	/**
	 * Method called to determine whether property will be included (if 'true'
	 * returned) or filtered out (if 'false' returned)
	 *
	 * @since 2.3
	 */
	protected boolean include(PropertyWriter writer) {
		Mandatory mandatoryAnnotation = writer.findAnnotation(Mandatory.class);
		
		if (mandatoryAnnotation!=null) {
			return true;
		}
		return requestedFields.contains(writer.getName());
	}

	/**
	 * Method that defines what to do with container elements (values contained in
	 * an array or {@link java.util.Collection}: default implementation simply
	 * writes them out.
	 * 
	 * @since 2.3
	 */
	protected boolean includeElement(Object elementValue) {
		return true;
	}

}
