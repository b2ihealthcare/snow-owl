/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.typedproperty;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.b2international.snowowl.fhir.core.FhirConstants;

/**
 * FHIR Date-time typed property, designated as propertyName[x] in the specification.
 * 
 * @see <a href=" https://www.hl7.org/fhir/formats.html">Choice</a> for further information about how to use [x].
 * @since 7.1
 */
public class DateTimeProperty extends TypedProperty<Date> {
	
	public DateTimeProperty(Date value) {
		super(value);
	}
	
	@Override
	public String getValueString() {
		Date date = getValue();
		final SimpleDateFormat df = new SimpleDateFormat(FhirConstants.DATE_TIME_FORMAT);
		return df.format(date);
	}
	
}
