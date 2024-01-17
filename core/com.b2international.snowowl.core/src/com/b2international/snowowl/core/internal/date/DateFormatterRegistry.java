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

import java.util.TimeZone;

import com.b2international.snowowl.core.date.DateFormatter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * @since 3.9
 */
public enum DateFormatterRegistry {

	INSTANCE;
	
	private LoadingCache<TimeZone, DateFormatter> CACHE = CacheBuilder.newBuilder().build(new CacheLoader<TimeZone, DateFormatter>() {

		@Override
		public DateFormatter load(TimeZone key) throws Exception {
			return new TimezonedDateFormatter((TimeZone) key);
		}
		
	});
	
	public DateFormatter getDateFormatter(TimeZone key) {
		return CACHE.getUnchecked(key);
	}
	
}