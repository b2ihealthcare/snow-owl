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

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * {@link ThreadLocal} {@link SimpleDateFormat} with specified timezone and datePattern settings.
 * 
 * @since 3.9
 */
public final class TimezonedSharedSimpleDateFormat extends ThreadLocal<SimpleDateFormat> {

	private final String datePattern;
	private final TimeZone timeZone;

	/* package */TimezonedSharedSimpleDateFormat(final String datePattern, final TimeZone timeZone) {
		this.datePattern = datePattern;
		this.timeZone = timeZone;
	}

	@Override
	protected SimpleDateFormat initialValue() {
		final SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);

		/*
		 * XXX: It is assumed that the format string does not have a time zone part, otherwise calling parse() may overwrite the Timezone value set
		 * here.
		 */
		if (timeZone != null) {
			dateFormat.setTimeZone(timeZone);
		}
		return dateFormat;
	}

	@Override
	public void set(final SimpleDateFormat value) {
		// Do not allow changing the stored value.
		return;
	}

}