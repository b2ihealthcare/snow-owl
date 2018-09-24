/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;

/**
 * Useful methods when working with {@link Date} instances, especially useful in formatting and parsing to/from {@link String} values.
 * 
 * @since 3.9
 */
public abstract class Dates {

	private Dates() {
	}

	/**
	 * The long timestamp representing one day after the epoch in GMT.
	 */
	public static final long MIN_DATE_LONG = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);

	/**
	 * Formats the specified object representing a date with the default {@value DateFormats#DEFAULT} format in the default {@link TimeZone}, and
	 * returns the result. The formatter will use the locally available {@link TimeZone} to represent the date.
	 * 
	 * @param date
	 *            - the date to format
	 * @return - the formatted date as string
	 * @throws NullPointerException
	 *             - if the given object is <code>null</code>
	 * @see TimeZone#getDefault()
	 * @see #formatByHostTimeZone(Object, String)
	 * @see #format(Object, TimeZone, String)
	 */
	public static final String formatByHostTimeZone(Object date) {
		return formatByHostTimeZone(date, DateFormats.DEFAULT);
	}

	/**
	 * Formats the specified object representing a date with the given datePattern in the default {@link TimeZone}, and returns the result.
	 * 
	 * @param date
	 *            - the date to format
	 * @param datePattern
	 *            - the pattern to use
	 * @return - the formatted date as string
	 * @throws NullPointerException
	 *             - if the given object is <code>null</code>
	 * @see TimeZone#getDefault()
	 * @see #format(Object, TimeZone, String)
	 */
	public static final String formatByHostTimeZone(Object date, String datePattern) {
		return format(date, TimeZone.getDefault(), datePattern);
	}

	/**
	 * Formats the specified object representing a date with the given datePattern in the <b>GMT</b> {@link TimeZone}, and returns the result.
	 * 
	 * @param date
	 *            - the date to format
	 * @return - the formatted date as string
	 * @throws NullPointerException
	 *             - if the given object is <code>null</code>
	 * @see #getGmtTimeZone()
	 * @see #formatByGmt(Object, String)
	 * @see #format(Object, TimeZone, String)
	 */
	public static final String formatByGmt(Object date) {
		return formatByGmt(date, DateFormats.DEFAULT);
	}

	/**
	 * Formats the specified object representing a date with the given datePattern in the <b>GMT</b> {@link TimeZone}, and returns the result.
	 * 
	 * @param date
	 *            - the date to format
	 * @param datePattern
	 *            - the pattern to use
	 * @return - the formatted date as string
	 * @throws NullPointerException
	 *             - if the given object is <code>null</code>
	 * @see #getGmtTimeZone()
	 * @see #format(Object, TimeZone, String)
	 */
	public static final String formatByGmt(Object date, String datePattern) {
		return format(date, getGmtTimeZone(), datePattern);
	}

	/**
	 * Formats the specified object representing a date with the given datePattern in the given {@link TimeZone}, and return the result. It will try
	 * to convert the given object to a {@link Date} object if possible, if not it will use it as is.
	 * 
	 * @param date
	 *            - the date to format
	 * @param timeZone
	 *            - the {@link TimeZone} to use
	 * @param datePattern
	 *            - the pattern to use
	 * @throws NullPointerException
	 *             - if the given object is <code>null</code>
	 */
	public static final String format(Object date, TimeZone timeZone, String datePattern) {
		return getDateFormatter(timeZone).format(convert(date), datePattern);
	}

	/**
	 * Parses the specified date given as string with the {@value DateFormats#DEFAULT} pattern.
	 * 
	 * @param date
	 *            - the date string, cannot be <code>null</code>
	 * @return - the parse date object, never <code>null</code>
	 * @throws SnowowlRuntimeException
	 *             - if a {@link ParseException} is thrown during execution
	 * @throws NullPointerException
	 *             - if date is <code>null</code>
	 * @see #parse(String, String)
	 */
	public static Date parse(final String date) {
		return parse(date, DateFormats.DEFAULT);
	}

	/**
	 * Parses the specified date given as string with a given pattern.
	 * 
	 * @param date
	 *            - the date string, cannot be <code>null</code>
	 * @param datePattern
	 *            - the date pattern for the parsing, cannot be <code>null</code>
	 * @return the parsed date object, never <code>null</code>
	 * @throws SnowowlRuntimeException
	 *             - if a {@link ParseException} is thrown during execution
	 * @throws NullPointerException
	 *             - if one of the arguments was <code>null</code>
	 */
	public static Date parse(final String date, final String datePattern) {
		checkNotNull(date, "Date argument cannot be null.");
		checkNotNull(datePattern, "Date pattern argument cannot be null.");
		try {
			return getDateFormatter(getGmtTimeZone()).parse(date, datePattern);
		} catch (final ParseException e) {
			throw new IllegalArgumentException("Error while parsing date '" + date + "' with pattern: '" + datePattern + "'.", e);
		}
	}

	/**
	 * Returns the number of milliseconds since January&nbsp;1,&nbsp;1970,&nbsp;00:00:00&nbsp;GMT represented by the specified date object.
	 * 
	 * @param date
	 *            - the date to extract time from
	 * @return - the number of milliseconds
	 * @throws NullPointerException
	 *             - if the given date object is <code>null</code>
	 * @throws IllegalArgumentException
	 *             - if it is impossible to extract the time from the object
	 */
	public static long getTime(final Object date) {
		checkNotNull(date, "date");
		if (date instanceof Date) {
			return ((Date) date).getTime();
		} else if (date instanceof Timestamp) {
			return ((Timestamp) date).getTime();
		} else if (date instanceof java.sql.Date) {
			return ((java.sql.Date) date).getTime();
		}
		throw new IllegalArgumentException("Unknown date type: " + date.getClass());
	}

	/**
	 * @return the {@link Date} instance for the beginning of the current day in the GMT timezone.
	 */
	public static Date todayGmt() {
		/*
		 * We're getting the values in local time, as retrieving these in GMT may roll back a day.
		 * 
		 * Be extra careful when looking at the return value using toString() without formatting them in GMT: For October 6th, seeing
		 * "Mon Oct 06 02:00:00 CEST 2014" in Hungary or "10/5/14 7:00 PM" in New York is *OK*.
		 */
		final Calendar todayInLocalTime = Calendar.getInstance();

		final int year = todayInLocalTime.get(Calendar.YEAR);
		final int month = todayInLocalTime.get(Calendar.MONTH);
		final int date = todayInLocalTime.get(Calendar.DATE);

		final Calendar todayInGmt = Calendar.getInstance(getGmtTimeZone());
		todayInGmt.clear();
		todayInGmt.set(year, month, date);

		return todayInGmt.getTime();
	}

	/**
	 * @return the GMT timezone instance.
	 */
	public static TimeZone getGmtTimeZone() {
		return TimeZone.getTimeZone("GMT");
	}

	private static Object convert(Object date) {
		if (date instanceof Long) {
			return new Date(((Long) date).longValue());
		}
		return date;
	}

	private static DateFormatter getDateFormatter(TimeZone timeZone) {
		return DateFormatter.REGISTRY.getDateFormatter(timeZone);
	}

	/**
	 * Returns the current time using {@link DateFormats#FULL} format.
	 * @return a String representation of the current time
	 */
	public static String now() {
		return now(DateFormats.FULL);
	}
	
	/**
	 * Returns the current time using the given format.
	 * @param format - format to use
	 * @return a String representation of the current time
	 */
	public static String now(String format) {
		return formatByHostTimeZone(new Date(), format);
	}

}