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

import java.util.Collection;

import com.b2international.snowowl.fhir.core.model.Designation;
import com.b2international.snowowl.fhir.core.model.LookupResult;
import com.b2international.snowowl.fhir.core.model.Property;
import com.b2international.snowowl.fhir.core.model.serialization.SerializableParameter;
import com.google.common.collect.Lists;

/**
 * Parameter-based converter for the {@link LookupResult} object.
 * @since 6.3
 *
 */
public class LookupResultConverter extends SerializableParametersConverter {
	
	@Override
	protected Collection<SerializableParameter> getCollectionParameters(Object value) throws Exception {
		
		Collection<SerializableParameter> collectionParameters = Lists.newArrayList();

		@SuppressWarnings("rawtypes")
		Collection values = (Collection) value;
		
		for (Object object : values) {
			if (object instanceof Designation) {
				Collection<SerializableParameter> designationParams = toParameters((Designation) object);
				SerializableParameter fhirParam = new SerializableParameter("designation", "part", designationParams);
				collectionParameters.add(fhirParam);
			} else if (object instanceof Property) {
				Collection<SerializableParameter> propertyParams = toParameters((Property) object);
				SerializableParameter fhirParam = new SerializableParameter("property", "part", propertyParams);
				collectionParameters.add(fhirParam);
			}
		}
		return collectionParameters;
	}

}
