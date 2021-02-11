/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.annotation.Nullable;

import com.b2international.commons.exceptions.BadRequestException;

/**
 * Effective Times should be always in GMT time. Use this class when working with effective times in any ontology, this will ensure proper parsing and formatting of them.
 * 
 * @since 3.9
 */
public abstract class EffectiveTimes {

	private EffectiveTimes() {
	}

	/**
	 * The long timestamp representing unset effective time. Value: {@value}
	 */
	public static final long UNSET_EFFECTIVE_TIME = -1L;

	/**
	 * The string label returned for unset effective time values. Value: {@value}
	 */
	public static final String UNSET_EFFECTIVE_TIME_LABEL = "Unpublished";

	/**
	 * Formats the given object representing an effective time with the {@value DateFormats#DEFAULT} format and returns the result.
	 * 
	 * @param effectiveTime
	 *            - the date to format, may be <code>null</code>
	 * @return - the formatted date as string
	 */
	public static final String format(@Nullable Object effectiveTime) {
		return format(effectiveTime, DateFormats.DEFAULT);
	}

	/**
	 * Formats the given object representing an effective time with the given datePattern and returns the result.
	 * <p>
	 * Unset effective times will be represented with the {@link #UNSET_EFFECTIVE_TIME_LABEL} constant.
	 * 
	 * @param effectiveTime
	 *            - the date to format, may be <code>null</code>
	 * @param datePattern
	 *            - the pattern to use
	 * @return - the formatted date as string
	 */
	public static String format(@Nullable Object effectiveTime, String datePattern) {
		return format(effectiveTime, datePattern, UNSET_EFFECTIVE_TIME_LABEL);
	}

	/**
	 * Formats the given object representing an effective time with the given datePattern and returns the result.
	 * <p>
	 * Unset effective time values will be represented with the third {@code String} argument.
	 * 
	 * @param effectiveTime
	 *            - the date to format, may be <code>null</code>
	 * @param datePattern
	 *            - the pattern to use
	 * @param unsetEffectiveTimeLabel
	 *            - the label to use when the effective time is not set
	 * @return - the formatted date as string
	 */
	public static String format(@Nullable Object effectiveTime, String datePattern, String unsetEffectiveTimeLabel) {
		if (effectiveTime == null) {
			return unsetEffectiveTimeLabel;
		} else if (effectiveTime instanceof Long) {
			if (((long) effectiveTime) == UNSET_EFFECTIVE_TIME) {
				return unsetEffectiveTimeLabel;
			}
			return DateTimeFormatter.ofPattern(datePattern).format(toDate((long) effectiveTime));
		} else if (effectiveTime instanceof LocalDate) {
			return DateTimeFormatter.ofPattern(datePattern).format((LocalDate) effectiveTime);
		} else {
			throw new IllegalArgumentException(String.format("Unsupported effectiveTime representation to format to String. Got: %s. Acceptable types are: java.lang.Long and java.time.LocalDate.", effectiveTime.getClass()));
		}
	}

	/**
	 * Parses the given effectiveTime with the {@value DateFormats#DEFAULT} format and returns the parsed {@link LocalDate}.
	 * 
	 * @param effectiveTime
	 *            - the effectiveTime to parse, cannot be <code>null</code>
	 * @return - the parsed date
	 * @throws IllegalArgumentException
	 *             - if a {@link ParseException} is throw during execution
	 */
	public static LocalDate parse(String effectiveTime) {
		return parse(effectiveTime, DateFormats.DEFAULT);
	}

	/**
	 * Parses the given effectiveTime with the given datePattern and returns the parsed {@link LocalDate}.
	 * 
	 * @param effectiveTime
	 *            - the effectiveTime to parse, cannot be <code>null</code>
	 * @param datePattern
	 *            - the pattern to use, cannot be <code>null</code>
	 * @return - the parsed date or <code>null</code> if the given effectiveTime value equals with the value {@link #UNSET_EFFECTIVE_TIME_LABEL}.
	 * @throws IllegalArgumentException
	 *             - if a {@link ParseException} is throw during execution
	 * @throws NullPointerException
	 *             - if one of the arguments was <code>null</code>
	 */
	public static LocalDate parse(String effectiveTime, String datePattern) {
		if (UNSET_EFFECTIVE_TIME_LABEL.equals(effectiveTime)) {
			return null;
		}
		try {
			return LocalDate.parse(effectiveTime, DateTimeFormatter.ofPattern(datePattern));
		} catch (DateTimeParseException e) {
			throw new BadRequestException(String.format("'%s' cannot be parsed into date with format '%s'", effectiveTime, datePattern));
		}
	}

	/**
	 * Returns the milliseconds representation of an effective time specified in String format. If the effectiveTime is <code>null</code> it returns
	 * the {@value #UNSET_EFFECTIVE_TIME} value.
	 * 
	 * @param effectiveTime
	 *            - to extract time from
	 * @param datePattern
	 *            - a valid {@link SimpleDateFormat} date pattern
	 * @return the number of milliseconds from the effective time String
	 * @see DateFormats
	 * @see #getEffectiveTime(LocalDate)
	 */
	public static final long getEffectiveTime(@Nullable String effectiveTime, String datePattern) {
		return getEffectiveTime(parse(effectiveTime, datePattern));
	}

	/**
	 * Returns the milliseconds representation of an effective time {@link LocalDate} in UTC. 
	 * If the effectiveTime is <code>null</code> it returns the {@value #UNSET_EFFECTIVE_TIME} value.
	 * 
	 * @param effectiveTime
	 *            - to extract time from
	 * @return the number of milliseconds from the {@link LocalDate} object at UTC
	 */
	public static final long getEffectiveTime(@Nullable LocalDate effectiveTime) {
		if (null == effectiveTime) {
			return UNSET_EFFECTIVE_TIME;
		}
		return effectiveTime
					.atStartOfDay(ZoneOffset.UTC)
					.toInstant()
					.toEpochMilli();
	}

	/**
	 * Converts the given effectiveTime timestamp to a {@link LocalDate} if it is not equals to {@link #UNSET_EFFECTIVE_TIME}, in this case return
	 * <code>null</code>.
	 * 
	 * @param effectiveTime
	 *            - the effectiveTime to convert
	 * @return - the converted {@link LocalDate}, can be <code>null</code>
	 */
	@Nullable
	public static LocalDate toDate(long effectiveTime) {
		return UNSET_EFFECTIVE_TIME == effectiveTime ? null : Instant.ofEpochMilli(effectiveTime).atZone(ZoneOffset.UTC).toLocalDate();
	}

	/**
	 * Returns <code>true</code> if the given Object is an unset representation of an effective time value, <code>false</code> otherwise.
	 * 
	 * @param effectiveTimeValue
	 * @return
	 */
	public static boolean isUnset(Object effectiveTimeValue) {
		if (effectiveTimeValue == null) {
			return true;
		} else if (effectiveTimeValue instanceof Long && UNSET_EFFECTIVE_TIME == (long) effectiveTimeValue) {
			return true;
		} else if (effectiveTimeValue instanceof String) {
			if (UNSET_EFFECTIVE_TIME_LABEL.compareToIgnoreCase((String) effectiveTimeValue) == 0) {
				return true;
			}
			try {
				if (UNSET_EFFECTIVE_TIME == Long.parseLong((String) effectiveTimeValue)) {
					return true;
				}
			} catch (NumberFormatException e) {
				// ignore, return false, since the given value cannot be a valid unset effective time value
			}
		}
		return false;
	}

}
