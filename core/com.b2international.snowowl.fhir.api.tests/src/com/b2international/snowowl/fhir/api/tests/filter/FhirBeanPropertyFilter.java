/*******************************************************************************
 * Copyright (c) 2018 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.fhir.api.tests.filter;

import java.util.Set;

import com.b2international.snowowl.fhir.core.model.annotations.Mandatory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.google.common.collect.Sets;

/**
 * @author bbanfai
 *
 */
public class FhirBeanPropertyFilter implements PropertyFilter {

	private Set<String> requestedFields = Sets.newHashSet();

	/**
	 * @param requestedFields
	 */
	public FhirBeanPropertyFilter(String[] requestedFields) {
		if (requestedFields != null) {
			this.requestedFields = Sets.newHashSet(requestedFields);
		}
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
