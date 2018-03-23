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
package com.b2international.snowowl.fhir.core.model.conversion;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.b2international.snowowl.fhir.core.FhirConstants;
import com.b2international.snowowl.fhir.core.model.ParametersModel;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.serialization.SerializableParameter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.google.common.collect.Lists;

/**
 * @since 6.3
 */
public class SerializableParametersConverter extends StdConverter<ParametersModel, ParametersModel> {
	
	@Override
	public ParametersModel convert(ParametersModel fhirModel) {
		try {
			fhirModel.setParameters(toParameters(fhirModel));
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not convert.");
		}
		return fhirModel;
	}
	
	/**
	 * Method to build a the collection of serializable parameters
	 * @throws Exception
	 */
	public List<SerializableParameter> toParameters(ParametersModel fhirModel) throws Exception {
		
		List<SerializableParameter> parameters = Lists.newArrayList();
		
		Field[] fields = fhirModel.getClass().getDeclaredFields();
		Arrays.sort(fields, new FieldOrderComparator());
		
		for (Field field : fields) {
			field.setAccessible(true);
			Object value = field.get(fhirModel);
			
			//embedded collections
			if (field.getType().equals(Collection.class)) {
				parameters.addAll(getCollectionParameters(value));
			
			//simple fields first
			} else {
				if (value!=null) { //TODO: Should we serialize null values?
					SerializableParameter parameter = createSerializableParameter(field, value);
					parameters.add(parameter);
				}
			}
		}
		return parameters;
	}

	/*
	 * Creates a parameter based on the field (type, name and value)
	 * To avoid the type-based switch, SerializableParameter could be made typed.
	 */
	@JsonIgnore
	private SerializableParameter createSerializableParameter(Field field, Object value) {
		
		SerializableParameter parameter = null;
		String type = null;
		
		if (value instanceof Boolean) {
			type = "valueBoolean";
		} else if (value instanceof Integer) {
			type = "valueInteger";
		} else if (value instanceof Date) {
			SimpleDateFormat formatter = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
			String formattedDate = formatter.format((Date) value);
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			value = formattedDate;
			type ="valueDateTime";
		} else if (value instanceof BigDecimal || 
				value instanceof Float || 
				value instanceof Double || 
				value instanceof Long) {
			type = "valueDecimal";
		} else if (value instanceof Code) {
			type = "valueCode";
			value = ((Code) value).getCodeValue();
		} else if (value instanceof Coding) {
				type = "valueCoding";
		} else {
			type = "valueString";
		}
		parameter = new SerializableParameter(field.getName(), type, value);
		return parameter;
	}

	protected Collection<SerializableParameter> getCollectionParameters(Object value) throws Exception {
		return Collections.emptySet();
	}
	
}
