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

import org.eclipse.core.runtime.*;

/**
 * Progress monitor wrapper that throws an {@link OperationCanceledException} if
 * it finds that the wrapped monitor has been canceled. Cancellation checks
 * occur when any of the methods outside of {@link #isCanceled()} is called.
 * 
 */
public final class CancelableProgressMonitorWrapper extends ProgressMonitorWrapper {

	private static IProgressMonitor checkNullMonitor(final IProgressMonitor delegate) {
		return (delegate == null) ? new NullProgressMonitor() : delegate;
	}

	/**
	 * Creates a new {@link CancelableProgressMonitorWrapper} delegating to the specified monitor.
	 * @param delegate the {@link IProgressMonitor} instance to delegate calls to
	 */
	public CancelableProgressMonitorWrapper(final IProgressMonitor delegate) {
		super(checkNullMonitor(delegate));
	}

	private void throwIfCanceled() {
		if (isCanceled()) {
			throw new OperationCanceledException();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.ProgressMonitorWrapper#beginTask(java.lang.String, int)
	 */
	@Override public void beginTask(final String name, final int totalWork) {
		throwIfCanceled();
		super.beginTask(name, totalWork);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.ProgressMonitorWrapper#clearBlocked()
	 */
	@Override public void clearBlocked() {
		throwIfCanceled();
		super.clearBlocked();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.ProgressMonitorWrapper#done()
	 */
	@Override public void done() {
		throwIfCanceled();
		super.done();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.ProgressMonitorWrapper#internalWorked(double)
	 */
	@Override public void internalWorked(final double work) {
		throwIfCanceled();
		super.internalWorked(work);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.ProgressMonitorWrapper#setBlocked(org.eclipse.core.runtime.IStatus)
	 */
	@Override public void setBlocked(final IStatus reason) {
		throwIfCanceled();
		super.setBlocked(reason);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.ProgressMonitorWrapper#setCanceled(boolean)
	 */
	@Override public void setCanceled(final boolean b) {
		throwIfCanceled();
		super.setCanceled(b);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.ProgressMonitorWrapper#setTaskName(java.lang.String)
	 */
	@Override public void setTaskName(final String name) {
		throwIfCanceled();
		super.setTaskName(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.ProgressMonitorWrapper#subTask(java.lang.String)
	 */
	@Override public void subTask(final String name) {
		throwIfCanceled();
		super.subTask(name);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.ProgressMonitorWrapper#worked(int)
	 */
	@Override public void worked(final int work) {
		throwIfCanceled();
		super.worked(work);
	}
}