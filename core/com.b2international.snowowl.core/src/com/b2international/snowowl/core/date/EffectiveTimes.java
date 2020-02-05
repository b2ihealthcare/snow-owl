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

import java.text.ParseException;
import java.util.Date;

import javax.annotation.Nullable;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;

/**
 * Effective Times should be always in GMT time. Use this class when working with effective times in any ontology, this will ensure the proper parsing
 * and formatting of them.
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
			return Dates.formatByGmt(toDate((Long) effectiveTime), datePattern);
		}
		return Dates.formatByGmt(effectiveTime, datePattern);
	}

	/**
	 * Parses the given effectiveTime with the {@value DateFormats#DEFAULT} format and returns the parsed {@link Date}.
	 * 
	 * @param effectiveTime
	 *            - the effectiveTime to parse, cannot be <code>null</code>
	 * @return - the parsed date
	 * @throws SnowowlRuntimeException
	 *             - if a {@link ParseException} is throw during execution
	 */
	public static Date parse(String effectiveTime) {
		return parse(effectiveTime, DateFormats.DEFAULT);
	}

	/**
	 * Parses the given effectiveTime with the given datePattern and returns the parsed {@link Date}.
	 * 
	 * @param effectiveTime
	 *            - the effectiveTime to parse, cannot be <code>null</code>
	 * @param datePattern
	 *            - the pattern to use, cannot be <code>null</code>
	 * @return - the parsed date or <code>null</code> if the given effectiveTime value equals with the value {@link #UNSET_EFFECTIVE_TIME_LABEL}.
	 * @throws SnowowlRuntimeException
	 *             - if a {@link ParseException} is throw during execution
	 * @throws NullPointerException
	 *             - if one of the arguments was <code>null</code>
	 */
	public static Date parse(String effectiveTime, String datePattern) {
		if (UNSET_EFFECTIVE_TIME_LABEL.equals(effectiveTime)) {
			return null;
		}
		return Dates.parse(effectiveTime, datePattern);
	}

	/**
	 * Ensures that for effective times the {@link Dates#getTime(Object)} will always return a valid long, for <code>null</code> input it will return
	 * {@link #UNSET_EFFECTIVE_TIME}.
	 * 
	 * @param effectiveTime
	 *            - to extract time from
	 * @return the number of milliseconds from the date object
	 */
	public static final long getEffectiveTime(@Nullable Date effectiveTime) {
		if (null == effectiveTime) {
			return UNSET_EFFECTIVE_TIME;
		}
		return effectiveTime.getTime();
	}

	/**
	 * Converts the given effectiveTime timestamp to a {@link Date} if it is not equals to {@link #UNSET_EFFECTIVE_TIME}, in this case return
	 * <code>null</code>.
	 * 
	 * @param effectiveTime
	 *            - the effectiveTime to convert
	 * @return - the converted date, can be <code>null</code>
	 */
	@Nullable
	public static Date toDate(long effectiveTime) {
		return UNSET_EFFECTIVE_TIME == effectiveTime ? null : new Date(effectiveTime);
	}

	/**
	 * Returns <code>true</code> if the given Object is an unset representation of an effective time value.
	 * @param effectiveTimeValue
	 * @return
	 */
	public static boolean isUnset(Object effectiveTimeValue) {
		if (effectiveTimeValue == null) {
			return true;
		} else if (UNSET_EFFECTIVE_TIME_LABEL.equals(effectiveTimeValue)) {
			return true;
		} else if (effectiveTimeValue instanceof Long && UNSET_EFFECTIVE_TIME == (long) effectiveTimeValue) {
			return true;
		} else if (effectiveTimeValue instanceof String && UNSET_EFFECTIVE_TIME == Long.parseLong((String) effectiveTimeValue)) {
			return true;
		}
		return false;
	}

}