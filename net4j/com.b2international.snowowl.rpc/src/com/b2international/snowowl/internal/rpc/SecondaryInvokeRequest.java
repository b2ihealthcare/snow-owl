/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.io.IOException;
import java.lang.reflect.Method;

import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcProtocolConstants;

/**
 * Represents a server-initiated RPC request that is confirmed by the receiving client.
 */
public class SecondaryInvokeRequest extends InvokeRequestWithMonitoring {

	private final int correlationID;
	
	private final int paramIdx;
	
	public SecondaryInvokeRequest(final RpcProtocol protocol, 
			final String serviceClassName, 
			final Method serviceMethod, 
			final Object[] params,
			final int correlationID,
			final int paramIdx) {
		
		super(protocol, RpcProtocolConstants.SIGNAL_RPC_SECONDARY_METHOD_CALL, serviceClassName, serviceMethod, params);
		this.correlationID = correlationID;
		this.paramIdx = paramIdx;
	}

	@Override
	protected void handleProxyParameter(ExtendedDataOutputStream out, int paramIdx, ValueType type) throws IOException {
		throw new UnsupportedOperationException("Can't use proxyable class in a server-initiated callback.");
	}
	
	@Override
	protected void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception {

		monitor.begin();

		try {
			out.writeInt(correlationID);
			out.writeInt(paramIdx);
			writeMethod(out);
		} finally {
			monitor.done();
		}
	}
}