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
package com.b2international.snowowl.datastore.net4j;

import org.eclipse.net4j.signal.Request;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * Please refer to {@link org.eclipse.net4j.signal.MonitorCanceledRequest}. The orig class has package visibility, however because of a possible CDO bug we need to send a
 * cancel request to the server from our code. The class itself is not important (there is no explicit cast on the server side), only the
 * <code>SignalProtocol.SIGNAL_MONITOR_CANCELED</code> is important. On the server side signals are identified by negative/positive <i>correlation id</i> refer:
 * <code>SignalProtocol.handleMonitorCanceled()</code>, however the orig cancel request <code>RequestWithMonitoring.requesting(ExtendedDataOutputStream out)</code> sends
 * positive correlation id but our export indication has negative id in the signal map.
 * 
 */
public class MonitorCanceledRequest extends Request {
	private final int correlationID;

	public MonitorCanceledRequest(final SignalProtocol<?> protocol, final int correlationID) {
		super(protocol, SignalProtocol.SIGNAL_MONITOR_CANCELED);
		this.correlationID = correlationID;
	}

	@Override
	protected void requesting(final ExtendedDataOutputStream out) throws Exception {
		out.writeInt(correlationID);
	}
}