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
package com.b2international.snowowl.core.monitoring;

import static com.b2international.commons.StringUtils.valueOfOrEmptyString;
import static com.b2international.commons.time.TimeUtil.nanoToString;

import org.eclipse.core.runtime.NullProgressMonitor;

import com.google.common.base.Stopwatch;

public class ConsoleProgressMonitor extends NullProgressMonitor {
	
	/** max number of times to echo to console, to avoid slowing down progress for lots of steps */ 
	private final int maxEchoes;
	private int echoCounter;
	
	String name;
	int work = 0;
	int percent;
	int totalWork;
	
	long time;
	private Stopwatch overallProcessTime;
	
	public ConsoleProgressMonitor() {
		this(0);
	}
	
	/**
	 * @param maxEchoes the max number of echoes, to avoid slowing down progress for lots of steps
	 */
	public ConsoleProgressMonitor(final int maxEchoes) {
		this.maxEchoes = maxEchoes;
		echoCounter = 0;
	}

	@Override
	public void beginTask(final String name, final int totalWork) {
		overallProcessTime = Stopwatch.createStarted();
		this.name = name;
		work = 0;
		percent = 0;
		this.totalWork = totalWork;
		time = System.nanoTime();
		logWork(0, false);
	}

	@Override
	public void worked(final int work) {
		this.work += work;
		if(maxEchoes > 0) {
			echoCounter += work;
		}
		final int newPercent = 0 == totalWork ? percent : this.work * 100 / totalWork;
		if(newPercent != percent) {
			percent = newPercent;
			logWork(percent, false);
		}
	}

	@Override
	public void done() {
		echoCounter = 0;
		logWork(100, true);
	}
	
	protected void logWork(final int percent, boolean logOverallTime) {
		if (percent > 0 && (maxEchoes <= 0 || echoCounter <= 0 || echoCounter >= totalWork / maxEchoes)) {
			final long now = System.nanoTime();
			if (logOverallTime) {
				System.out.format("%s: Done [%s]\n", valueOfOrEmptyString(name), valueOfOrEmptyString(overallProcessTime));
			} else {
				System.out.format("%s: %3d%% [%s]\n", name, percent, nanoToString(now - time));
			}
			echoCounter = 0;
			time = now;
		}
	}
	
	@Override
	public void subTask(final String name) {
		System.out.println("Starting subtask " + name);
		super.subTask(name);
	}
	
	@Override
	public void setTaskName(final String name) {
		this.name = name;
	}

}