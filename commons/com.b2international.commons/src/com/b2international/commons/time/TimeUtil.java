/*
 * Copyright (C) 2008 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.commons.time;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

/**
 * @see Stopwatch original source
 */
public class TimeUtil {

	public static String nanoToString(final long nanos) {
		final TimeUnit unit = chooseUnit(nanos);
		final double value = (double) nanos / NANOSECONDS.convert(1, unit);

		// Too bad this functionality is not exposed as a regular method call
		return String.format("%.4g %s", value, abbreviate(unit));
	}

	public static TimeUnit chooseUnit(final long nanos) {
		if (DAYS.convert(nanos, NANOSECONDS) > 0) {
			return DAYS;
		}
		if (HOURS.convert(nanos, NANOSECONDS) > 0) {
			return HOURS;
		}
		if (MINUTES.convert(nanos, NANOSECONDS) > 0) {
			return MINUTES;
		}
		if (SECONDS.convert(nanos, NANOSECONDS) > 0) {
			return SECONDS;
		}
		if (MILLISECONDS.convert(nanos, NANOSECONDS) > 0) {
			return MILLISECONDS;
		}
		if (MICROSECONDS.convert(nanos, NANOSECONDS) > 0) {
			return MICROSECONDS;
		}
		return NANOSECONDS;
	}

	
	public static String abbreviate(final TimeUnit unit) {
		switch (unit) {
		case NANOSECONDS:
			return "ns";
		case MICROSECONDS:
			return "\u03bcs"; // ?s
		case MILLISECONDS:
			return "ms";
		case SECONDS:
			return "s";
		case MINUTES:
			return "min";
		case HOURS:
			return "h";
		case DAYS:
			return "d";
		default:
			throw new AssertionError();
		}
	}

	
	public static String milliToString(final long millis) {
		return nanoToString(NANOSECONDS.convert(millis, MILLISECONDS));
	}
	
	public static String toString(final Stopwatch watch) {
		return nanoToReadableString(watch.elapsed(NANOSECONDS));
	}
	
	public static String milliToReadableString(final long millis) {
		return nanoToReadableString(NANOSECONDS.convert(millis, MILLISECONDS));
	}
	
	public static String nanoToReadableString(final long nanos) {
		final TimeUnit baseUnit = chooseUnit(nanos);
		if (baseUnit == DAYS) {
			final int days = (int) (nanos / getBase(DAYS));
			final long hoursInNanos = nanos - (days * getBase(DAYS));
			final int hours = (int) NANOSECONDS.toHours(hoursInNanos);
			final long minutesInNanos = hoursInNanos - (hours * getBase(HOURS)); 
			final int minutes = (int) NANOSECONDS.toMinutes(minutesInNanos);
			final long secondsInNanos = minutesInNanos - (minutes * getBase(MINUTES));
			final int seconds = (int) NANOSECONDS.toSeconds(secondsInNanos);
			return String.format("%s %s %s %s %s %s %s %s", days, abbreviate(DAYS), hours, abbreviate(HOURS), minutes, abbreviate(MINUTES), seconds, abbreviate(SECONDS));
		} else if (baseUnit == HOURS) {
			final int hours = (int) (nanos / getBase(HOURS));
			final long minutesInNanos = nanos - (hours * getBase(HOURS)); 
			final int minutes = (int) NANOSECONDS.toMinutes(minutesInNanos);
			final long secondsInNanos = minutesInNanos - (minutes * getBase(MINUTES));
			final int seconds = (int) NANOSECONDS.toSeconds(secondsInNanos);
			return String.format("%s %s %s %s %s %s", hours, abbreviate(HOURS), minutes, abbreviate(MINUTES), seconds, abbreviate(SECONDS));
		}  else if (baseUnit == MINUTES) {
			final int minutes = (int) (nanos / getBase(MINUTES));
			final long secondsInNanos = nanos - (minutes * getBase(MINUTES));
			final int seconds = (int) NANOSECONDS.toSeconds(secondsInNanos);
			return String.format("%s %s %s %s", minutes, abbreviate(MINUTES), seconds, abbreviate(SECONDS));
		}		
		return nanoToString(nanos);
	}
	
	private static long getBase(final TimeUnit unit) {
		return NANOSECONDS.convert(1, unit);
	}
}
