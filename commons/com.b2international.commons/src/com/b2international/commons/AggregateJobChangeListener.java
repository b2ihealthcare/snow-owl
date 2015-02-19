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

import java.util.Iterator;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Aggregates a bunch of {@link IJobChangeListener}. The order of the listeners is unpredictable.
 */
public class AggregateJobChangeListener implements IJobChangeListener {

	private final Iterable<? extends IJobChangeListener> listeners;

	public static AggregateJobChangeListener create(final IJobChangeListener... listeners) {
		return create(Lists.newArrayList(listeners));
	}
	
	public static AggregateJobChangeListener create(final Iterable<? extends IJobChangeListener> listeners) {
		return new AggregateJobChangeListener(listeners);
	}
	
	private AggregateJobChangeListener(final Iterable<? extends IJobChangeListener> listeners) {
		this.listeners = Preconditions.checkNotNull(listeners, "Listeners argument cannot be null.");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#aboutToRun(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void aboutToRun(final IJobChangeEvent event) {
		for (final Iterator<? extends IJobChangeListener> itr = listeners.iterator(); itr.hasNext(); /**/) {
			
			final IJobChangeListener listener = itr.next();
			listener.aboutToRun(event);
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#awake(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void awake(final IJobChangeEvent event) {
		for (final Iterator<? extends IJobChangeListener> itr = listeners.iterator(); itr.hasNext(); /**/) {
			
			final IJobChangeListener listener = itr.next();
			listener.awake(event);
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void done(final IJobChangeEvent event) {
		for (final Iterator<? extends IJobChangeListener> itr = listeners.iterator(); itr.hasNext(); /**/) {
			
			final IJobChangeListener listener = itr.next();
			listener.done(event);
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#running(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void running(final IJobChangeEvent event) {
		for (final Iterator<? extends IJobChangeListener> itr = listeners.iterator(); itr.hasNext(); /**/) {
			
			final IJobChangeListener listener = itr.next();
			listener.running(event);
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#scheduled(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void scheduled(final IJobChangeEvent event) {
		for (final Iterator<? extends IJobChangeListener> itr = listeners.iterator(); itr.hasNext(); /**/) {
			
			final IJobChangeListener listener = itr.next();
			listener.scheduled(event);
			
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.IJobChangeListener#sleeping(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public void sleeping(final IJobChangeEvent event) {
		for (final Iterator<? extends IJobChangeListener> itr = listeners.iterator(); itr.hasNext(); /**/) {
			
			final IJobChangeListener listener = itr.next();
			listener.sleeping(event);
			
		}
	}
	

}