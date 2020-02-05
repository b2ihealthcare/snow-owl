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

import org.eclipse.net4j.signal.wrapping.GZIPStreamWrapperInjector;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.spi.net4j.ClientProtocolFactory;

/**
 * 
 */
public class RpcUtil {

	/**
	 * Registers and returns a service proxy for the given serviceClass.
	 * 
	 * @param container
	 * @param serviceClass
	 * @return
	 */
	public static <T> T createProxy(IManagedContainer container, Class<T> serviceClass) {
		RpcProtocol protocol = getRpcClientProtocol(container);
		protocol.registerClassLoader(serviceClass, serviceClass.getClassLoader());
		return protocol.getServiceProxy(serviceClass);
	}
	
	/**
	 * 
	 * @param container
	 * @return
	 */
	public static synchronized RpcSession getInitialServerSession(final IManagedContainer container) {
		return (RpcSession) container.getElement(RpcSessionImpl.Factory.PRODUCT_GROUP, RpcSessionImpl.Factory.TYPE, "initialServerSession");
	}

	/**
	 * 
	 * @param container
	 * @return
	 */
	public static synchronized RpcProtocol getRpcClientProtocol(final IManagedContainer container) {
		return (RpcProtocol) container.getElement(ClientProtocolFactory.PRODUCT_GROUP, RpcProtocolConstants.TYPE, "client", false);
	}
	
	/**
	 * 
	 * @param serviceClass
	 * @return
	 */
	public static <T> T getRpcClientProxy(Class<T> serviceClass) {
		return getRpcClientProxy(IPluginContainer.INSTANCE, serviceClass);
	}
	
	/**
	 * 
	 * @param container
	 * @param serviceClass
	 * @return
	 */
	public static <T> T getRpcClientProxy(final IManagedContainer container, Class<T> serviceClass) {
		return getRpcClientProtocol(container).getServiceProxy(serviceClass);
	}

	/**
	 * 
	 * @param container
	 */
	public static void prepareContainer(final IManagedContainer container, RpcConfiguration configuration, boolean gzip) {
		container.registerFactory(new RpcProtocol.ClientFactory(configuration));
		container.registerFactory(new RpcProtocol.ServerFactory(configuration));
		container.registerFactory(new RpcSessionImpl.Factory());
		container.addPostProcessor(new RpcServerProtocolInjector());
		if (gzip) {
			container.addPostProcessor(new GZIPStreamWrapperInjector(RpcProtocolConstants.TYPE));
		}
	}
}