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
package com.b2international.snowowl.internal.rpc;

import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcProtocolConstants;

/**
 * A client-side indication for an RPC call from the server that requires a response.
 * 
 */
public class SecondaryInvokeIndication extends InvokeIndicationWithMonitoring {

	private int correlationID;
	
	private int paramIdx;
	
	public SecondaryInvokeIndication(final RpcProtocol protocol) {
		super(protocol, RpcProtocolConstants.SIGNAL_RPC_SECONDARY_METHOD_CALL);
	}

	@Override
	protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception {
		
		monitor.begin();
		Async async = null;
		
		try {
			async = monitor.forkAsync();
			correlationID = in.readInt();
			paramIdx = in.readInt();
			readMethod(in);
		} finally  {
			stopAsync(async);
			monitor.done();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.InvokeIndicationWithMonitoring#handleProxyParameter(org.eclipse.net4j.util.io.ExtendedDataInputStream, 
	 * com.b2international.snowowl.internal.rpc.ValueType, int)
	 */
	protected Object handleProxyParameter(final ExtendedDataInputStream in, final ValueType type, final int paramIdx) {
		throw new UnsupportedOperationException("No proxied parameters should be present in a server-initiated request.");
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.InvokeIndicationWithMonitoring#getServiceImplementation(java.lang.Class)
	 */
	protected Object getServiceImplementation(final Class<?> serviceClass) {
		return getProtocol().getParameter(correlationID, paramIdx);
	}
}