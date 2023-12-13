/*
 * Copyright 2011-2015 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.core.date;

import java.text.ParseException;
import java.util.Date;

import com.b2international.snowowl.core.internal.date.DateFormatterRegistry;

/**
 * Interface used to format Objects representing {@link Date}s, like the {@link Date} itself, or timestamps in {@link Long} value.
 * 
 * @since 3.9
 */
public interface DateFormatter {

	/**
	 * The registry containing cached {@link DateFormatter} instances for later use.
	 */
	DateFormatterRegistry REGISTRY = DateFormatterRegistry.INSTANCE;

	/**
	 * Formats the specified object representing a date with the given datePattern, and return the result.
	 * 
	 * @param date
	 *            - the date to format
	 * @param datePattern
	 *            - the pattern to use
	 * @return the formatted date as string
	 */
	String format(Object date, String datePattern);

	/**
	 * Parses the given formattedDate with the given datePattern and return the result.
	 * 
	 * @param formattedDate
	 *            - the date to parse
	 * @param datePattern
	 *            - the pattern to use
	 * @return - the parsed date object
	 * @throws ParseException
	 *             - if the date object cannot be parsed from the given string
	 */
	Date parse(String formattedDate, String datePattern) throws ParseException;

}