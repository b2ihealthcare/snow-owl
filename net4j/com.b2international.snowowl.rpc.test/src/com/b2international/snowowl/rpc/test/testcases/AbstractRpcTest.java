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
package com.b2international.snowowl.rpc.test.testcases;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.junit.After;
import org.junit.Before;

import com.b2international.snowowl.rpc.RpcConfiguration;
import com.b2international.snowowl.rpc.RpcProtocol;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcUtil;
import com.b2international.snowowl.rpc.test.service.SingleServiceLookup;

/**
 * @param <T> the service interface type
 * @param <U> the service implementation type
 */
public abstract class AbstractRpcTest<T, U extends T> {

//	private static final long PROTOCOL_TIMEOUT_MILLIS = Long.MAX_VALUE;
	
	private static final long PROTOCOL_TIMEOUT_MILLIS = 500L;

	private static final String JVM_DESCRIPTION = "test";
	
	private final Class<T> serviceInterfaceClass;
	
	private IManagedContainer container;
	
	protected U serviceImplementation;

	protected AbstractRpcTest(Class<T> serviceInterfaceClass) {
		this.serviceInterfaceClass = serviceInterfaceClass;
	}

	@Before
	public void setUp() {
		container = ContainerUtil.createContainer();
		LifecycleUtil.activate(container);
		
		Net4jUtil.prepareContainer(container);
		JVMUtil.prepareContainer(container);
		RpcUtil.prepareContainer(container, new RpcConfiguration(), false);
		
		JVMUtil.getAcceptor(container, JVM_DESCRIPTION);
	}

	@After
	public void tearDown() {
		LifecycleUtil.deactivate(container);
		
		serviceImplementation = null;
		container = null;
	}

	protected T initializeService() {
	
		final IConnector connector = JVMUtil.getConnector(container, JVM_DESCRIPTION);
		serviceImplementation = createServiceImplementation();
		
		final SingleServiceLookup<U> singleServiceLookup = new SingleServiceLookup<U>(serviceImplementation);
		final RpcSession initialServerSession = RpcUtil.getInitialServerSession(container);
		initialServerSession.registerClassLoader(serviceInterfaceClass, serviceImplementation.getClass().getClassLoader());
		initialServerSession.registerServiceLookup(singleServiceLookup);

		final RpcProtocol protocol = RpcUtil.getRpcClientProtocol(container);
		protocol.registerClassLoader(serviceInterfaceClass, serviceInterfaceClass.getClassLoader());
		protocol.open(connector);
		protocol.setTimeout(PROTOCOL_TIMEOUT_MILLIS);
		
		return protocol.getServiceProxy(serviceInterfaceClass);
	}

	protected abstract U createServiceImplementation();
}
