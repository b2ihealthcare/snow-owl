/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.eventbus.netty;

import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.internal.eventbus.netty.EventBusNettyHandler;

import io.netty.channel.ChannelInboundHandler;

/**
 * @since 8.0
 */
public class EventBusNettyUtil {

	// TODO: configure the netty instance here like with Net4j?
	
//	/**
//	 * Prepares the given {@link IManagedContainer} to deliver message through {@link IEventBus} instances over the
//	 * network.
//	 * 
//	 * @param container
//	 * @param gzip - to enable gzip compression on the protocol or not
//	 * @param numberOfWorkers 
//	 */
//	public static final void prepareContainer(IManagedContainer container, boolean gzip, int numberOfWorkers) {
//		container.registerFactory(new EventBusProtocol.ClientFactory());
//		container.registerFactory(new EventBusProtocol.ServerFactory());
//		container.registerFactory(new EventBus.Factory());
//		container.addPostProcessor(new EventBusProtocolInjector(numberOfWorkers));
//		if (gzip) {
//			container.addPostProcessor(new GZIPStreamWrapperInjector(EventBusConstants.PROTOCOL_NAME));
//		}
//	}

	/**
	 * Returns the client channel handler for the event bus protocol. 
	 * 
	 * @param eventBus
	 * @return
	 */
	public static ChannelInboundHandler getClientHandler(IEventBus eventBus) {
		return new EventBusNettyHandler(true, eventBus);
	}
}
