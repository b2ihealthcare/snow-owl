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
package com.b2international.snowowl.datastore.server.remotejobs;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.collections.Procedure;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;

/**
 */
public class ListenableProgressMonitor implements IProgressMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ListenableProgressMonitor.class);
	
	private final Set<IProgressMonitor> listeners = Collections.synchronizedSet(Sets.<IProgressMonitor>newHashSet());
	
	private final AtomicReference<String> name = new AtomicReference<String>("");
	private final AtomicReference<String> subtaskName = new AtomicReference<String>("");
	private final AtomicInteger totalWork = new AtomicInteger(); 
	private final AtomicDouble worked = new AtomicDouble();
	private final AtomicBoolean canceled = new AtomicBoolean();
	private final AtomicBoolean done = new AtomicBoolean();
	
	public void addListener(final IProgressMonitor listener) {
		if (listeners.add(listener)) {
			try {
				listener.beginTask(name.get(), totalWork.get());
				listener.subTask(subtaskName.get());
				listener.worked((int) worked.get());
				listener.setCanceled(canceled.get());
				
				if (done.get()) {
					listener.done();
				}
			} catch (final RuntimeException e) {
				LOGGER.warn("Caught exception while adding progress monitor, removing it.", e);
				listeners.remove(listener);
			}
		}
	}
	
	public void removeListener(final IProgressMonitor listener) {
		listeners.remove(listener);
	}
	
	public void clearListeners() {
		listeners.clear();
	}
	
	@Override
	public void beginTask(final String name, final int totalWork) {
		this.name.set(name);
		this.totalWork.set(totalWork);
		
		subtaskName.set("");
		worked.set(0.0);
		canceled.set(false);
		done.set(false);
		
		signalListeners(new Procedure<IProgressMonitor>() { @Override protected void doApply(final IProgressMonitor listener) {
			listener.beginTask(name, totalWork);
		}});
	}

	@Override
	public void done() {
		if (done.compareAndSet(false, true)) {
			signalListeners(new Procedure<IProgressMonitor>() { @Override protected void doApply(final IProgressMonitor listener) {
				listener.done();
			}});
		}
	}

	@Override
	public void internalWorked(final double work) {
		recordWork(work);
		
		signalListeners(new Procedure<IProgressMonitor>() { @Override protected void doApply(final IProgressMonitor listener) {
			listener.internalWorked(work);
		}});
	}

	@Override
	public void setCanceled(final boolean canceled) {
		this.canceled.set(canceled);
		
		signalListeners(new Procedure<IProgressMonitor>() { @Override protected void doApply(final IProgressMonitor listener) {
			listener.setCanceled(canceled);
		}});
	}

	@Override
	public boolean isCanceled() {
		// XXX: listening monitors are not consulted
		return canceled.get();
	}

	@Override
	public void setTaskName(final String name) {
		this.name.set(name);
		
		signalListeners(new Procedure<IProgressMonitor>() { @Override protected void doApply(IProgressMonitor listener) {
			listener.setTaskName(name);
		}});
	}

	@Override
	public void subTask(final String name) {
		this.subtaskName.set(name);
		
		signalListeners(new Procedure<IProgressMonitor>() { @Override protected void doApply(IProgressMonitor listener) {
			listener.subTask(name);
		}});
	}

	@Override
	public void worked(final int work) {
		recordWork(work);
		
		signalListeners(new Procedure<IProgressMonitor>() { @Override protected void doApply(IProgressMonitor listener) {
			listener.worked(work);
		}});
	}

	private void recordWork(final double work) {
		if (totalWork.get() > IProgressMonitor.UNKNOWN) {
		    while (true) {
		        final double currentWorked = worked.get();
		        final double nextWorked = Math.min(totalWork.get(), currentWorked + work);
		        if (worked.compareAndSet(currentWorked, nextWorked)) {
		          break;
		        }
		    }
		}
	}
	
	private void signalListeners(final Procedure<IProgressMonitor> callback) {
		synchronized (listeners) {
			for (final Iterator<IProgressMonitor> itr = listeners.iterator(); itr.hasNext(); /* empty */) {
				try {
					final IProgressMonitor listener = itr.next();
					callback.apply(listener);
				} catch (final RuntimeException e) {
					LOGGER.warn("Caught exception while calling method on listening progress monitor, removing it.", e);
					itr.remove();
				}
			}
		}
	}
}