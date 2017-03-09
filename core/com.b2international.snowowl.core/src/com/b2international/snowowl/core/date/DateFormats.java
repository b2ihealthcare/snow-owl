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

import java.text.SimpleDateFormat;

/**
 * Common {@link SimpleDateFormat} patterns to use when formatting dates.
 * 
 * @since 3.9
 */
public abstract class DateFormats {

	private DateFormats() {
	}

	/**
	 * The default date format with years, months and date of month values separated by a dash. Value: {@value}
	 */
	public static final String DEFAULT = "yyyy-MM-dd";

	/**
	 * Long time format which includes time up to second resolution. Value: {@value}
	 */
	public static final String LONG = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * Compact long date format indicating years, months, days, hours, minutes and seconds. Value: {@value} 
	 */
	public static final String COMPACT_LONG = "yyyyMMdd_HHmmss";
	
	/**
	 * Medium date format, wich includes time up to minutes resolution. Value: {@value}
	 */
	public static final String MEDIUM = "yyyy-MM-dd HH:mm";

	/**
	 * Short date format with years, months and date of month put together without separators. Value: {@value}
	 */
	public static final String SHORT = "yyyyMMdd";

	/**
	 * The most detailed time format which includes time up to millisecond resolution. Value: {@value}
	 */
	public static final String FULL = "yyyyMMddHHmmssSSS";

}