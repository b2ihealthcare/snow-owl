/*
 * Copyright 2018-2024 B2i Healthcare, https://b2ihealthcare.com
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
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.hl7.fhir.r4.model.codesystems.OperationOutcomeCode;

import com.b2international.snowowl.fhir.core.exceptions.FhirException;
import com.fasterxml.jackson.databind.util.StdDateFormat;

/**
 * FHIR date related utilities
 * 
 * A date, date-time or partial date (e.g. just year or year + month) as used in human communication. 
 * If hours and minutes are specified, a time zone SHALL be populated. Seconds must be provided due to schema type constraints but may be zero-filled and may be ignored. 
 * Dates SHALL be valid dates. The time "24:00" is not allowed
 * 
 * @since 6.4
 */
public class FhirDates {

	/**
	 * https://www.hl7.org/fhir/datatypes.html#dateTime
	 */
	private static String[] DATETIME_PATTERNS = {
		"yyyy", 
		"yyyy-MM", 
		"yyyy-MM-dd",
		"yyyy-MM-dd'T'HH:mm:ssZ",
		"yyyy-MM-dd'T'HH:mm:ss.SZ", // Regexp in specification allows 1+ digits for milliseconds; going up to 4 here
		"yyyy-MM-dd'T'HH:mm:ss.SSZ",
		"yyyy-MM-dd'T'HH:mm:ss.SSSZ",
		"yyyy-MM-dd'T'HH:mm:ss.SSSSZ",
		"yyyy-MM-dd'T'HH:mm:ssXXX", 
		"yyyy-MM-dd'T'HH:mm:ss.SXXX",
		"yyyy-MM-dd'T'HH:mm:ss.SSXXX",
		"yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
		"yyyy-MM-dd'T'HH:mm:ss.SSSSXXX",
	};
	
	/**
	 * Returns a date object for the given string representation supported by FHIR
	 * 
	 * @param dateString
	 * @return
	 * @throws FhirException
	 */
	public static Date parse(final String dateString) {
		try {
			return DateUtils.parseDate(dateString, DATETIME_PATTERNS);
		} catch (NullPointerException | ParseException e) {
			throw new FhirException(String.format("Invalid date string '%s'. Reason: %s", dateString, e.getMessage()), OperationOutcomeCode.MSGPARAMINVALID);
		}
	}

	public static String format(Date date) {
		return new StdDateFormat().format(date);
	}
	
	
}
