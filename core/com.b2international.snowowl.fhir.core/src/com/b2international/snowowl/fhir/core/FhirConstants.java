/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

/**
 * FHIR constants
 * @since 6.4
 */
public class FhirConstants {
	
	/**
	 * A date, date-time or partial date (e.g. just year or year + month) as used in human communication. 
	 * If hours and minutes are specified, a time zone SHALL be populated. Seconds must be provided due to schema type constraints but may be zero-filled and may be ignored. 
	 * Dates SHALL be valid dates. The time "24:00" is not allowed
	 */
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

}
