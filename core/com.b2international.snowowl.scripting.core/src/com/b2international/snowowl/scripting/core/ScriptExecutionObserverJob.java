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
package com.b2international.snowowl.scripting.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.common.base.Preconditions;


/**
 * Class for observing a Groovy script execution process. If the wrapped {@link IProgressMonitor monitor}
 * is canceled the wrapped Groovy object instance will throw a runtime exception. 
 *
 */
public final class ScriptExecutionObserverJob extends Job {

		private final Thread thread;
		private final IProgressMonitor monitor;

		/**
		 * Job for observing a Groovy script execution process.
		 * @param thread the script execution thread.
		 * @param monitor the monitor for the script execution.
		 */
		public ScriptExecutionObserverJob(final Thread thread, final IProgressMonitor monitor) {
			super("com.b2international.snowowl.scripting.core.ScriptExecutionObserverJob");
			this.thread = Preconditions.checkNotNull(thread, "Script executor thread argument cannot be null.");
			this.monitor = Preconditions.checkNotNull(monitor, "Progress monitor argument cannot be null.");
			setUser(false); //do not reveal this job to the user
			setSystem(true);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			
			while (!monitor.isCanceled()) {
				
				try { //start polling and check if the user requested a script execution cancel
					Thread.sleep(200L);
				} catch (InterruptedException e) {
					return Status.CANCEL_STATUS;
				}
				
				if (this.monitor.isCanceled()) { //interrupt script execution
					thread.interrupt();
					return Status.OK_STATUS;
				}
				
			}
			
			return Status.OK_STATUS;
		}
		
	}