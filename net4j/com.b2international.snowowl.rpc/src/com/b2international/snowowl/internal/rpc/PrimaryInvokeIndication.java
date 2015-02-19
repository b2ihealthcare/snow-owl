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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcProtocolConstants;

/**
 * A server-side indication for a two-way RPC call which includes a progress monitor.
 * 
 */
public class PrimaryInvokeIndication extends InvokeIndicationWithMonitoring {

	/**
	 * 
	 * @param protocol
	 */
	public PrimaryInvokeIndication(final RpcProtocol protocol) {
		super(protocol, RpcProtocolConstants.SIGNAL_RPC_PRIMARY_METHOD_CALL);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.InvokeIndicationWithMonitoring#handleProxyParameter(org.eclipse.net4j.util.io.ExtendedDataInputStream, 
	 * com.b2international.snowowl.internal.rpc.ValueType, int)
	 */
	protected Object handleProxyParameter(final ExtendedDataInputStream in, final ValueType type, final int paramIdx) {
		
		final Class<?> proxyInterface = getProxyInterface(type);
		final RpcServerStubFactory serverStubFactory = new RpcServerStubFactory(getProtocol());
		final Object serverProxy = serverStubFactory.createServerStub(proxyInterface, -getCorrelationID(), paramIdx);
		
		return wrapProxy(type, serverProxy);
	}

	private Class<?> getProxyInterface(final ValueType type) {

		switch (type) {
			case PROGRESS_MONITOR:
				return IProgressMonitor.class;
			case INPUT:
				return RpcInput.class;
			case OUTPUT:
				return RpcOutput.class;
			default:
				throw new IllegalArgumentException("Can't get proxy interface for type '" + type + "'.");
		}
	}
	
	private Object wrapProxy(final ValueType type, final Object serverProxy) {

		switch (type) {
			case PROGRESS_MONITOR:
				return serverProxy;
			case INPUT:
				return new BufferedInputStream(new ReceiverInputWrapper((RpcInput) serverProxy));
			case OUTPUT:
				return new BufferedOutputStream(new ReceiverOutputWrapper((RpcOutput) serverProxy));
			default:
				throw new IllegalArgumentException("Can't get proxy interface for type '" + type + "'.");
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.internal.rpc.InvokeIndicationWithMonitoring#getServiceImplementation(java.lang.Class)
	 */
	protected Object getServiceImplementation(final Class<?> serviceClass) {
		return getSession().getServiceImplementation(serviceClass);
	}
}