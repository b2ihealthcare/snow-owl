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

import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

import com.b2international.index.query.Phase;
import com.b2international.index.query.Query;
import com.google.common.base.Stopwatch;

/**
 * @since 5.0
 */
public class QueryProfiler {
	
	private static final String TEMPLATE = "[slowlog]: phase:{}, query:{}, time:{}";
	
	private final Query<?> query;
	private final SlowLogConfig config;
	
	private final Map<Phase, Stopwatch> phaseExecutionTimes = newHashMap();

	public QueryProfiler(Query<?> query, SlowLogConfig config) {
		this.query = query;
		this.config = config;
		this.phaseExecutionTimes.put(Phase.QUERY, Stopwatch.createUnstarted());
		this.phaseExecutionTimes.put(Phase.FETCH, Stopwatch.createUnstarted());
	}

	public void start(Phase phase) {
		phaseExecutionTimes.get(phase).start();
	}
	
	public void end(Phase phase) {
		phaseExecutionTimes.get(phase).stop();
	}
	
	public void log(Logger log) throws IOException {
		final Stopwatch queryWatch = this.phaseExecutionTimes.get(Phase.QUERY);
		final long queryExecTime = queryWatch.elapsed(TimeUnit.MILLISECONDS);
		if (config.getQueryWarnThreshold() <= queryExecTime) {
			log.warn(TEMPLATE, Phase.QUERY, query, queryWatch);
		} else if (config.getQueryInfoThreshold() <= queryExecTime) {
			log.info(TEMPLATE, Phase.QUERY, query, queryWatch);
		} else if (config.getQueryDebugThreshold() <= queryExecTime) {
			log.debug(TEMPLATE, Phase.QUERY, query, queryWatch);
		} else if (config.getQueryTraceThreshold() <= queryExecTime) {
			log.trace(TEMPLATE, Phase.QUERY, query, queryWatch);
		}
		
		final Stopwatch fetchWatch = this.phaseExecutionTimes.get(Phase.FETCH);
		final long fetchExecTime = fetchWatch.elapsed(TimeUnit.MILLISECONDS);
		if (config.getFetchWarnThreshold() <= fetchExecTime) {
			log.warn(TEMPLATE, Phase.FETCH, query, fetchWatch);
		} else if (config.getFetchInfoThreshold() <= fetchExecTime) {
			log.info(TEMPLATE, Phase.FETCH, query, fetchWatch);
		} else if (config.getFetchDebugThreshold() <= fetchExecTime) {
			log.debug(TEMPLATE, Phase.FETCH, query, fetchWatch);
		} else if (config.getFetchTraceThreshold() <= fetchExecTime) {
			log.trace(TEMPLATE, Phase.FETCH, query, fetchWatch);
		}
	}
	
}
