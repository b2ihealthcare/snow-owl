/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.net4j;

import org.eclipse.net4j.signal.Signal;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * Runnable for canceling an OMMonitor.
 * 
 */
public final class RequestCancelationRunnable implements Runnable {
	private final OMMonitor monitor;
	private final long cancelationPollInterval;
	private final Signal signal;

	/**
	 * @param monitor the monitor
	 * @param cancelationPollInterval the cancelation polling interval
	 * @param signal the Net4j signal
	 */
	public RequestCancelationRunnable(OMMonitor monitor, long cancelationPollInterval, Signal signal) {
		this.monitor = monitor;
		this.cancelationPollInterval = cancelationPollInterval;
		this.signal = signal;
	}

	@Override
	@SuppressWarnings("restriction")
	public void run() {
		while (monitor != null) {
			ConcurrencyUtil.sleep(cancelationPollInterval);
			if (monitor.isCanceled()) {
				try {
					/* Send negative correlation id to the server to be able to get our indication from the signal map. 
					 * See: SignalProtocol.handleMonitorCanceled() */
					new MonitorCanceledRequest(signal.getProtocol(), -signal.getCorrelationID()).sendAsync();
				} catch (final Exception ex) {
					org.eclipse.internal.net4j.bundle.OM.LOG.error(ex);
				}

				return;
			}
		}
	}
}