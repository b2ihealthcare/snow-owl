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
package com.b2international.snowowl.rpc;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.Signal;
import org.eclipse.net4j.signal.SignalFinishedEvent;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.signal.SignalScheduledEvent;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.spi.net4j.ClientProtocolFactory;
import org.eclipse.spi.net4j.ServerProtocolFactory;

import com.b2international.snowowl.internal.rpc.InvokeRequestWithMonitoring;
import com.b2international.snowowl.internal.rpc.PrimaryInvokeIndication;
import com.b2international.snowowl.internal.rpc.PrimaryInvokeRequest;
import com.b2international.snowowl.internal.rpc.RemoteInvocationTargetException;
import com.b2international.snowowl.internal.rpc.RpcClientStubFactory;
import com.b2international.snowowl.internal.rpc.SecondaryInvokeIndication;
import com.b2international.snowowl.internal.rpc.SecondaryInvokeRequest;

/**
 * The symmetric RPC protocol implementation.
 * 
 */
public class RpcProtocol extends SignalProtocol<RpcSession> {

	private final IListener signalListener = new IListener() {

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.net4j.util.event.IListener#notifyEvent(org.eclipse.net4j.util.event.IEvent)
		 */
		@SuppressWarnings("unchecked") public void notifyEvent(final IEvent event) {
			
			if (event instanceof SignalScheduledEvent) {
				handleSignalScheduled((SignalScheduledEvent<RpcSession>) event);
			} else if (event instanceof SignalFinishedEvent) {
				handleSignalFinished((SignalFinishedEvent<RpcSession>) event);
			}
		}
	};

	private final Map<Integer, InvokeRequestWithMonitoring> pendingRequests = Collections.synchronizedMap(new HashMap<Integer, InvokeRequestWithMonitoring>());
	
	private final RpcClientStubFactory stubFactory = new RpcClientStubFactory(this);

	private RpcConfiguration configuration;
	
	public RpcProtocol(final boolean skipInitialization, RpcConfiguration configuration) {
		super(RpcProtocolConstants.TYPE);
		CheckUtil.checkArg(configuration, "configuration");
		this.configuration = configuration;
		
		if (!skipInitialization) { 
			initializeRegistry(); 
		}

		addListener(signalListener);
	}
	
	/**
	 * @return the configuration
	 */
	public RpcConfiguration getConfiguration() {
		return configuration;
	}

	private void initializeRegistry() {
		final RpcSession serviceRegistry = new RpcSessionImpl();
		setInfraStructure(serviceRegistry);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.spi.net4j.Protocol#setInfraStructure(java.lang.Object)
	 */
	@Override
	public void setInfraStructure(final RpcSession infraStructure) {
		((RpcSessionImpl) infraStructure).setProtocol(this);
		super.setInfraStructure(infraStructure);
	}

	/**
	 * 
	 * @param serviceClass
	 */
	public void registerClassLoader(final Class<?> serviceClass, final ClassLoader classLoader) {
		getInfraStructure().registerClassLoader(serviceClass, classLoader);
	}
	
	/**
	 * 
	 * @param serviceClass
	 * @return
	 */
	public <T> T getServiceProxy(final Class<T> serviceClass) {
		return stubFactory.createClientStub(serviceClass);
	}

	private void handleSignalScheduled(final SignalScheduledEvent<RpcSession> event) {
		final Signal signal = event.getSignal();
		if (signal instanceof InvokeRequestWithMonitoring) {
			pendingRequests.put(signal.getCorrelationID(), (InvokeRequestWithMonitoring) signal);
		}
	}

	private void handleSignalFinished(final SignalFinishedEvent<RpcSession> event) {
		final Signal signal = event.getSignal();
		if (signal instanceof InvokeRequestWithMonitoring) {
			pendingRequests.remove(signal.getCorrelationID());
		}
	}

	@Override protected SignalReactor createSignalReactor(final short signalID) {

		switch (signalID) {
			case RpcProtocolConstants.SIGNAL_RPC_SECONDARY_METHOD_CALL:
				return new SecondaryInvokeIndication(this);
			case RpcProtocolConstants.SIGNAL_RPC_PRIMARY_METHOD_CALL:
				return new PrimaryInvokeIndication(this);
			default:
				return super.createSignalReactor(signalID);
		}
	}

	/**
	 * 
	 * @param correlationID
	 * @param paramIdx
	 * @return
	 */
	public Object getParameter(final int correlationID, final int paramIdx) {
		return pendingRequests.get(correlationID).getParameter(paramIdx);
	}

	/**
	 * 
	 * @param serviceClassName
	 * @param serviceMethod
	 * @param params
	 * @return
	 * @throws RpcException
	 */
	public Object sendClientRequestWithMonitoring(final String serviceClassName, final Method serviceMethod, final Object[] params) throws Exception {
		return send(new PrimaryInvokeRequest(this, serviceClassName, serviceMethod, params));
	}

	/**
	 * 
	 * @param serviceClassName
	 * @param serviceMethod
	 * @param params
	 * @param correlationID
	 * @param paramIdx
	 * @return
	 * @throws RpcException
	 */
	public Object sendServerRequestWithMonitoring(final String serviceClassName, final Method serviceMethod, final Object[] params, final int correlationID, final int paramIdx) throws Exception {
		return send(new SecondaryInvokeRequest(this, serviceClassName, serviceMethod, params, correlationID, paramIdx));
	}

	/**
	 * 
	 * @param serviceClassName
	 * @param serviceMethod
	 * @param params
	 * @return
	 */
	public Future<Object> sendAsync(final String serviceClassName, final Method serviceMethod, final Object[] params) {
		return sendAsync(new PrimaryInvokeRequest(this, serviceClassName, serviceMethod, params));
	}

	private <RESULT> RESULT send(final RequestWithMonitoring<RESULT> request) throws Exception {
		
		try {
			return request.send(null);
		} catch (final RemoteInvocationTargetException ex) {
			throw ex.getCause();
		} catch (final Exception ex) {
			throw new RpcException(ex);
		}
	}

	private <RESULT> Future<RESULT> sendAsync(final RequestWithMonitoring<RESULT> request) throws RpcException {
		
		try {
			return request.sendAsync(null);
		} catch (final Exception ex) {
			throw new RpcException(ex);
		}
	}

	/**
	 * The client-side RPC protocol factory for registering with an {@link IManagedContainer}.
	 * 
	 */
	public static final class ClientFactory extends ClientProtocolFactory {

		private RpcConfiguration configuration;

		public ClientFactory(RpcConfiguration configuration) {
			super(RpcProtocolConstants.TYPE);
			this.configuration = configuration;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.net4j.util.factory.IFactory#create(java.lang.String)
		 */
		public Object create(final String description) throws ProductCreationException {
			return new RpcProtocol(false, configuration);
		}
	}
	
	/**
	 * The server-side RPC protocol factory for registering with an {@link IManagedContainer}. 
	 *
	 */
	public static class ServerFactory extends ServerProtocolFactory {
		
		private RpcConfiguration configuration;
		
		public ServerFactory(final RpcConfiguration configuration) {
			super(RpcProtocolConstants.TYPE);
			this.configuration = configuration;
		}

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.net4j.util.factory.IFactory#create(java.lang.String)
		 */
		public Object create(final String description) throws ProductCreationException {
			return new RpcProtocol(true, configuration);
		}
	}
	
}