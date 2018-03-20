/*******************************************************************************
 * Copyright (c) 2018 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.fhir.core.model;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.model.serialization.SerializableParameter;
import com.google.common.collect.Lists;

/**
 * @author bbanfai
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
