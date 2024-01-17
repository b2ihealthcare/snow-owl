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
package com.b2international.snowowl.core.internal.date;

import static com.google.common.base.Preconditions.checkNotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.b2international.snowowl.core.date.DateFormatter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @since 3.9
 */
class TimezonedDateFormatter implements DateFormatter {

	private LoadingCache<String, TimezonedSharedSimpleDateFormat> CACHE = CacheBuilder.newBuilder().build(new CacheLoader<String, TimezonedSharedSimpleDateFormat>() {
		@Override
		public TimezonedSharedSimpleDateFormat load(final String datePattern) throws Exception {
			return new TimezonedSharedSimpleDateFormat(datePattern, timeZone);
		}
	});

	private TimeZone timeZone;
	
	TimezonedDateFormatter(TimeZone timeZone) {
		this.timeZone = checkNotNull(timeZone, "timeZone");
	}
	
	@Override
	public String format(Object date, String datePattern) {
		checkNotNull(date, "Date object cannot not be null");
		checkNotNull(datePattern, "Pattern cannot not be null");
		return getDateFormat(datePattern).format(date);
	}

	@Override
	public Date parse(String formattedDate, String datePattern) throws ParseException {
		checkNotNull(formattedDate, "FormattedDate cannot not be null");
		checkNotNull(datePattern, "Pattern cannot not be null");
		return getDateFormat(datePattern).parse(formattedDate);
	}
	
	private SimpleDateFormat getDateFormat(String datePattern) {
		return CACHE.getUnchecked(datePattern).get();
	}

}