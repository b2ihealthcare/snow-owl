/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.FhirException;

/**
 * FHIR date related utilities
 * @since 6.4
 */
public class FhirDates {
	
	/**
	 * A date, date-time or partial date (e.g. just year or year + month) as used in human communication. 
	 * If hours and minutes are specified, a time zone SHALL be populated. Seconds must be provided due to schema type constraints but may be zero-filled and may be ignored. 
	 * Dates SHALL be valid dates. The time "24:00" is not allowed
	 */
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	/**
	 * A date, or partial date (e.g. just year or year + month) as used in human communication. 
	 * There is no time zone. Dates SHALL be valid dates.
	 */
	public static final String DATE_SHORT_FORMAT = "yyyy-MM-dd";
	
	
	/**
	 * https://www.hl7.org/fhir/datatypes.html#dateTime
	 */
	public static String[] DATETIME_PATTERNS = new String[] {
			"yyyy", 
			"yyyy-MM", 
			"yyyy-MM-dd",
			"yyyy-MM-dd'T'HH:mm:ss",
			"yyyy-MM-dd'T'HH:mm:ssZ",
			"yyyy-MM-dd'T'HH:mm:ss.SSSZ",
			"yyyy-MM-dd'T'HH:mm:ssXXX", 
			"yyyy-MM-dd'T'HH:mm:ss.SSSSXXX",
			"yyyy-MM-dd'T'HH:mm:ss.SSSX"};
	
	/**
	 * Returns a date object for the given string representation supported by FHIR
	 * @param dateString
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(final String dateString) {
		try {
			return DateUtils.parseDate(dateString, FhirDates.DATETIME_PATTERNS);
		} catch (NullPointerException | ParseException e) {
			throw FhirException.createFhirError(String.format("Invalid date string '%s' valid date formats are %s.", dateString, Arrays.toString(DATETIME_PATTERNS)), 
					OperationOutcomeCode.MSG_PARAM_INVALID);
		}
	}
	
	
}
