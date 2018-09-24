/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.index.query.slowlog;

import java.util.Map;

/**
 * @since 5.0
 */
public class SlowLogConfig {

	public static final String QUERY_WARN_THRESHOLD = "query.warn";
	public static final String QUERY_INFO_THRESHOLD = "query.info";
	public static final String QUERY_DEBUG_THRESHOLD = "query.debug";
	public static final String QUERY_TRACE_THRESHOLD = "query.trace";
	
	public static final long QUERY_WARN_THRESHOLD_DEFAULT = 400;
	public static final long QUERY_INFO_THRESHOLD_DEFAULT = 300;
	public static final long QUERY_DEBUG_THRESHOLD_DEFAULT = 100;
	public static final long QUERY_TRACE_THRESHOLD_DEFAULT = 50;
	
	public static final String FETCH_WARN_THRESHOLD = "fetch.warn";
	public static final String FETCH_INFO_THRESHOLD = "fetch.info";
	public static final String FETCH_DEBUG_THRESHOLD = "fetch.debug";
	public static final String FETCH_TRACE_THRESHOLD = "fetch.trace";
	
	public static final long FETCH_WARN_THRESHOLD_DEFAULT = 200;
	public static final long FETCH_INFO_THRESHOLD_DEFAULT = 100;
	public static final long FETCH_DEBUG_THRESHOLD_DEFAULT = 50;
	public static final long FETCH_TRACE_THRESHOLD_DEFAULT = 10;
	
	private final Map<String, Object> settings;
	
	public SlowLogConfig(Map<String, Object> settings) {
		this.settings = settings;
		if (!this.settings.containsKey(QUERY_WARN_THRESHOLD)) {
			this.settings.put(QUERY_WARN_THRESHOLD, QUERY_WARN_THRESHOLD_DEFAULT);
		}
		if (!this.settings.containsKey(QUERY_INFO_THRESHOLD)) {
			this.settings.put(QUERY_INFO_THRESHOLD, QUERY_INFO_THRESHOLD_DEFAULT);
		}
		if (!this.settings.containsKey(QUERY_DEBUG_THRESHOLD)) {
			this.settings.put(QUERY_DEBUG_THRESHOLD, QUERY_DEBUG_THRESHOLD_DEFAULT);
		}
		if (!this.settings.containsKey(QUERY_TRACE_THRESHOLD)) {
			this.settings.put(QUERY_TRACE_THRESHOLD, QUERY_TRACE_THRESHOLD_DEFAULT);
		}
		if (!this.settings.containsKey(FETCH_WARN_THRESHOLD)) {
			this.settings.put(FETCH_WARN_THRESHOLD, FETCH_WARN_THRESHOLD_DEFAULT);
		}
		if (!this.settings.containsKey(FETCH_INFO_THRESHOLD)) {
			this.settings.put(FETCH_INFO_THRESHOLD, FETCH_INFO_THRESHOLD_DEFAULT);
		}
		if (!this.settings.containsKey(FETCH_DEBUG_THRESHOLD)) {
			this.settings.put(FETCH_DEBUG_THRESHOLD, FETCH_DEBUG_THRESHOLD_DEFAULT);
		}
		if (!this.settings.containsKey(FETCH_TRACE_THRESHOLD)) {
			this.settings.put(FETCH_TRACE_THRESHOLD, FETCH_TRACE_THRESHOLD_DEFAULT);
		}
	}

	public long getQueryWarnThreshold() {
		return (long) settings.get(QUERY_WARN_THRESHOLD);
	}
	
	public long getQueryInfoThreshold() {
		return (long) settings.get(QUERY_INFO_THRESHOLD);
	}
	
	public long getQueryDebugThreshold() {
		return (long) settings.get(QUERY_DEBUG_THRESHOLD);
	}
	
	public long getQueryTraceThreshold() {
		return (long) settings.get(QUERY_TRACE_THRESHOLD);
	}
	
	public long getFetchWarnThreshold() {
		return (long) settings.get(QUERY_WARN_THRESHOLD);
	}
	
	public long getFetchInfoThreshold() {
		return (long) settings.get(QUERY_INFO_THRESHOLD);
	}
	
	public long getFetchDebugThreshold() {
		return (long) settings.get(QUERY_DEBUG_THRESHOLD);
	}
	
	public long getFetchTraceThreshold() {
		return (long) settings.get(QUERY_TRACE_THRESHOLD);
	}
	
}
