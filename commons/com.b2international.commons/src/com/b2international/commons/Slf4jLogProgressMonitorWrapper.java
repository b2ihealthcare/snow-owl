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
package com.b2international.commons;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.slf4j.Logger;

/**
 * An {@link IProgressMonitor} implementation that displays progress using a SLF4j {@link Logger}.  
 *
 */
public class Slf4jLogProgressMonitorWrapper extends ProgressMonitorWrapper {
	
	private static IProgressMonitor nullToNullMonitor(IProgressMonitor wrappedMonitor) {
		return (null == wrappedMonitor) ? new NullProgressMonitor() : wrappedMonitor;
	}

	private final Logger logger;
	
	/** max number of times to echo to console, to avoid slowing down progress for lots of steps */ 
	private final int maxEchoes;
	
	private int numEchoes;
	
	private String name;
	
	private int work = 0;
	
	private int totalWork;
	
	private int percent;
	
	public Slf4jLogProgressMonitorWrapper(final IProgressMonitor wrappedMonitor, final Logger logger) {
		this(wrappedMonitor, logger, 0);
	}
	
	public Slf4jLogProgressMonitorWrapper(final IProgressMonitor wrappedMonitor, final Logger logger,  final int maxEchoes) {
		super(nullToNullMonitor(wrappedMonitor));
		this.logger = checkNotNull(logger, "logger");
		this.maxEchoes = maxEchoes;
	}

	@Override
	public void beginTask(final String name, final int totalWork) {
		try {
			super.beginTask(name, totalWork);
		} finally {
			this.name = name;
			this.work = 0;
			this.percent = 0;
			this.totalWork = totalWork;
			
			logWork(0);
		}
	}

	@Override
	public void worked(final int work) {
		try {
			super.worked(work);
		} finally {
			this.work += work;
			
			if (maxEchoes > 0) {
				numEchoes += work;
			}
			
			final int newPercent = (0 == totalWork) ? percent : this.work * 100 / totalWork;
			
			if (newPercent != percent) {
				percent = newPercent;
				logWork(percent);
			}
		}
	}

	@Override
	public void done() {
		try {
			super.done();
		} finally {
			numEchoes = 0;
			logWork(100);
		}
	}
	
	protected void logWork(final int percent) {
		if (maxEchoes <= 0 || numEchoes <= 0 || numEchoes >= totalWork / maxEchoes) {
			logger.trace(String.format("%s: %d%%", name, percent));
			numEchoes = 0;
		}
	}
	
	@Override
	public void subTask(final String subTask) {
		try {
			super.subTask(subTask);
		} finally {
			logger.trace(String.format("%s: starting subtask %s", name, subTask));
		}
	}
	
	@Override
	public void setTaskName(final String name) {
		try {
			super.setTaskName(name);
		} finally {
			this.name = name;
		}
	}
}