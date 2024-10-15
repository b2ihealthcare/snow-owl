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
package com.b2international.snowowl.fhir.core;

import java.util.Date;

import org.hl7.fhir.r5.model.CanonicalType;
import org.hl7.fhir.r5.model.DateTimeType;
import org.hl7.fhir.r5.model.InstantType;
import org.hl7.fhir.r5.model.Resource;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ResourceURI;

/**
 * @since 9.4.0
 */
public class FhirModelHelpers {

	public static final String OID_PREFIX = "urn:oid:";
	
	public static ResourceURI resourceUriFrom(Resource resource) {
		return ResourceURI.of(resource.getResourceType().name().toLowerCase() + "s", resource.getId());
	}
	
	public static DateTimeType toDateTimeElement(Long date) {
		return toDateTimeElement(date == null ? null : new Date(date));
	}
	
	public static DateTimeType toDateTimeElement(Date date) {
		if (date == null) {
			return null;
		} else {
			var dateElement = new DateTimeType(date);
			dateElement.setTimeZoneZulu(true);
			return dateElement;
		}
	}
	
	public static InstantType toInstantElement(Long date) {
		return toInstantElement(date == null ? null : new Date(date));
	}
	
	public static InstantType toInstantElement(Date date) {
		if (date == null) {
			return null;
		} else {
			var instantElement = new InstantType(date);
			instantElement.setTimeZoneZulu(true);
			return instantElement;
		}
	}
	
	public static final boolean isOid(CanonicalType system) {
		return system != null & isOid(system.getValue());
	}
	
	public static final boolean isOid(String system) {
		return system != null && system.startsWith(OID_PREFIX);
	}

	public static String getSystemWithoutOidPrefix(CanonicalType system) {
		return getSystemWithoutOidPrefix(system == null ? null : system.getValue());
	}
	
	public static String getSystemWithoutOidPrefix(String system) {
		if (CompareUtils.isEmpty(system)) {
			return "";
		}
		
		if (isOid(system)) {
			return system.substring(OID_PREFIX.length());
		}
		
		return system;
	}
	
}
