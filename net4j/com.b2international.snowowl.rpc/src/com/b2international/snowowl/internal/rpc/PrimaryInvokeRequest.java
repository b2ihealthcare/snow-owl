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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcProtocolConstants;

/**
 * Represents a client-side request for two-way RPC calls which includes a progress monitor.
 * 
 */
public class PrimaryInvokeRequest extends InvokeRequestWithMonitoring {

	public PrimaryInvokeRequest(final RpcProtocol protocol, final String serviceClassName, final Method serviceMethod, final Object[] params) {
		super(protocol, RpcProtocolConstants.SIGNAL_RPC_PRIMARY_METHOD_CALL, serviceClassName, serviceMethod, params);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.InvokeRequestWithMonitoring#handleProxyParameter(org.eclipse.net4j.util.io.ExtendedDataOutputStream, int, 
	 * com.b2international.snowowl.internal.rpc.ValueType)
	 */
	protected void handleProxyParameter(ExtendedDataOutputStream out, int paramIdx, ValueType type) throws IOException {
		
		if (type.equals(ValueType.INPUT)) {
			params[paramIdx] = new SenderInputWrapper((InputStream) params[paramIdx]);
		} else if (type.equals(ValueType.OUTPUT)) {
			params[paramIdx] = new SenderOutputWrapper((OutputStream) params[paramIdx]);
		}
	}	
}