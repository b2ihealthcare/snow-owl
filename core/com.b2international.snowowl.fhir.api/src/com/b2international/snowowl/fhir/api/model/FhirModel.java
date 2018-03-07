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
package com.b2international.snowowl.fhir.api.model;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.fhir.api.model.serialization.SerializableParameter;
import com.google.common.collect.Lists;

public class FhirModel {
	
	/**
	 *  Method to build a the collection of serializable parameters in the format of:
	 * of a :
	 * <pre>{@code
	   
			}
			</pre>
		 *  @return
	 * @throws Exception
	 */
	public Collection<SerializableParameter> toParameters() throws Exception {
		
		List<SerializableParameter> parameters = Lists.newArrayList();
		
		Field[] fields = this.getClass().getDeclaredFields();
		Arrays.sort(fields, new FieldOrderComparator());
		
		for (Field field : fields) {
			field.setAccessible(true);
			Object value = field.get(this);
			
			//simple fields
			if (!field.getType().equals(Collection.class)) {
				String type = getDataType(field);
				SerializableParameter parameter = new SerializableParameter(field.getName(), type, value);
				parameters.add(parameter);
			} else {
				//embedded collections
				parameters.addAll(getCollectionParameters(value));
			}
		}
		return parameters;
	}

	protected String getDataType(Field field) {
		FhirDataType fhirDataType = field.getAnnotation(FhirDataType.class);
		String typePostFix = null;
		if (fhirDataType == null) {
			typePostFix = field.getType().getSimpleName();
		} else {
			typePostFix = fhirDataType.type().getSerializedName();
		}
		return "value" + typePostFix;
	}
	
	protected Collection<SerializableParameter> getCollectionParameters(Object value) throws Exception {
		return Collections.emptySet();
	}
	

}
