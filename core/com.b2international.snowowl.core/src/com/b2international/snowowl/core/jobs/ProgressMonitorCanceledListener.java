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
package com.b2international.snowowl.core.jobs;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Listener fired from a {@link ProgressMonitorStateObserverJob} when the observed monitor has been canceled.  
 *
 */
public interface ProgressMonitorCanceledListener {

	/**
	 * Notifies listener that the {@code observedMonitor} has been canceled.
	 * @param observedMonitor the observed monitor which was canceled.
	 * @param originalMonitor the monitor belongs to the {@link ProgressMonitorStateObserverJob}.
	 */
	void notify(final IProgressMonitor observedMonitor, final IProgressMonitor originalMonitor);

}