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

import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;

import com.google.common.collect.Lists;

/**
 * Progress monitor wrapper that updates the wrapped monitor's subtask every
 * second with the elapsed time (since {@link #beginTask(String, int)} was
 * called).
 * <p>
 * For proper operation, make sure to call {@link #done()} on this monitor even
 * if an exception is thrown (preferably in a finally block).
 */
public final class TimedProgressMonitorWrapper extends ProgressMonitorWrapper {

	private static final class TimedProgressMonitorThread extends Thread {
		
		private final List<WeakReference<TimedProgressMonitorWrapper>> monitorsToUpdate = Collections.synchronizedList(
				Lists.<WeakReference<TimedProgressMonitorWrapper>>newLinkedList());

		public TimedProgressMonitorThread() {
			super(TimedProgressMonitorThread.class.getSimpleName());
			setDaemon(true);
		}

		public void addMonitor(final TimedProgressMonitorWrapper monitor) {
			monitorsToUpdate.add(new WeakReference<TimedProgressMonitorWrapper>(monitor));
		}

		public void removeMonitor(final TimedProgressMonitorWrapper monitor) {
			
			synchronized (monitorsToUpdate) {
				for (final Iterator<WeakReference<TimedProgressMonitorWrapper>> iterator = monitorsToUpdate.iterator(); iterator.hasNext();) {
					final WeakReference<TimedProgressMonitorWrapper> monitorRef = iterator.next();
					
					final TimedProgressMonitorWrapper monitor2 = monitorRef.get();
					
					if (monitor2 == null || monitor2 == monitor) {
						iterator.remove();
					}
				}				
			}
		}

		@Override
		public void run() {

			while (true) {

				synchronized (monitorsToUpdate) {
					for (final Iterator<WeakReference<TimedProgressMonitorWrapper>> iterator = monitorsToUpdate.iterator(); iterator.hasNext();) {
						final WeakReference<TimedProgressMonitorWrapper> monitorRef = iterator.next();

						try {
							final TimedProgressMonitorWrapper monitor = monitorRef.get();
							
							if (monitor != null) {
								monitor.update();
							} else {
								iterator.remove();
							}
						} catch (final Exception e) {
							iterator.remove();
						}
					}
				}
				
				try {
					Thread.sleep(1000);
				} catch (final InterruptedException e) {
					// Nothing to do here
				}
			}
		}
	}

	private static TimedProgressMonitorThread timeUpdateThread;

	private synchronized static TimedProgressMonitorThread getTimeUpdateThread() {
		if (timeUpdateThread == null) {
			timeUpdateThread = new TimedProgressMonitorThread();
			timeUpdateThread.start();
		}
		
		return timeUpdateThread;
	}
	
	private static final String SUBTASK_TEMPLATE = "Elapsed time: {0}.";
	
	private long startTime; 
	private long stopTime;
	private final AtomicBoolean stopped = new AtomicBoolean(true);
	
	private String subTask = "";

	public TimedProgressMonitorWrapper(final IProgressMonitor delegate) {
		super(delegate);
	}
	
	public String getElapsedTime() {
		final long runningFinishTime = (!stopped.get()) ? System.currentTimeMillis() : stopTime;
		final long elapsedTimeMillis = (0 == startTime) ? 0 : Math.max(runningFinishTime - startTime, 0); // Check if the timer was ever started

		final long elapsedMinutes = elapsedTimeMillis / (60L * 1000L);
		final long remainderMillis = elapsedTimeMillis - (elapsedMinutes * 60L * 1000L);
		final long elapsedSeconds = remainderMillis / 1000L;

		return MessageFormat.format("{0} minute{1} {2} second{3}", elapsedMinutes, pluralSuffix(elapsedMinutes), elapsedSeconds, pluralSuffix(elapsedSeconds));
	}

	private String pluralSuffix(long number) {
		return number > 1 ? "s" : ""; 
	}
	
	@Override
	public void beginTask(final String name, final int totalWork) {
		super.beginTask(name, totalWork);
		// Start the wall clock
		resume();
	}

	@Override
	public void done() {
		// Stop the wall clock
		pause();
		super.done();
	}

	@Override
	public void setCanceled(final boolean value) {
		// Only stop the wall clock if the progress monitor has been canceled
		if (value) {
			pause();
		}
		super.setCanceled(value);
	}

	@Override
	public void subTask(final String name) {
		subTask = (name == null) ? "" : name;
		update();
	}

	private void update() {
		final StringBuilder builder = new StringBuilder();
		builder.append(MessageFormat.format(SUBTASK_TEMPLATE, getElapsedTime()));
		
		if (!subTask.isEmpty()) {
			builder.append(" ");
			builder.append(subTask);
		}
		
		super.subTask(builder.toString());
	}

	public synchronized void pause() {
		if (stopped.compareAndSet(false, true)) {
			stopTime = System.currentTimeMillis();
			getTimeUpdateThread().removeMonitor(this);
		}
	}

	public synchronized void resume() {
		if (stopped.compareAndSet(true, false)) {
			startTime = System.currentTimeMillis() - Math.max((stopTime - startTime), 0);
			stopTime = 0L;
			getTimeUpdateThread().addMonitor(this);
		}
	}
}